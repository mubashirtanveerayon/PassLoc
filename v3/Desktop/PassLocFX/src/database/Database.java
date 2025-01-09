package database;

import commons.model.Entry;
import commons.utils.HelperFunctions;
import commons.utils.cipher.AES256;
import commons.utils.db.DatabaseAction;
import commons.utils.db.DatabaseInterface;
import commons.utils.db.DatabaseListener;
import commons.utils.db.DatabaseResponse;
import commons.utils.monitor.PasswordMonitor;
import commons.utils.monitor.PasswordMonitorListener;
import commons.utils.strings.SQLCom;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

public class Database implements DatabaseInterface, PasswordMonitorListener {

    public static int LOCKDOWN_TIMEOUT = 80;

    public PasswordMonitor getPasswordMonitor() {
        return monitor;
    }

    private final PasswordMonitor monitor;


    // Static variable to hold the single instance of the class
    private static Database instance;
    private static final ArrayList<DatabaseListener> listeners=new ArrayList<>();

    private final Connection connection;

    private boolean processing=false;

    private Database(String fullPath,String password)throws Exception {
        processing=true;
        String dbPassword = HelperFunctions.sha256( AES256.PBKDF2(password.toCharArray(),AES256.FIXED_SALT,65000,256));

        connection= DriverManager.getConnection("jdbc:sqlite:"+fullPath+"?cipher=sqlcipher&key="+ dbPassword+"&legacy=4");

        byte[] key= AES256.PBKDF2(password.toCharArray(),dbPassword.getBytes(),65000,256);
        monitor = new PasswordMonitor(new String(key).toCharArray(),LOCKDOWN_TIMEOUT);
        monitor.addListener(this);
        monitor.start();
        this.initialize();
        processing=false;
    }

    public static void addListener(DatabaseListener listener) {
        listeners.add(listener);
    }

    public static void removeListener(DatabaseListener listener) {
        listeners.add(listener);
    }

    public static boolean isProcessing(){
        return !isOffline() && instance.processing;
    }


    public void initialize() throws Exception{
        processing = true;
        PreparedStatement statement = null;
        Exception e=null;

        statement = connection.prepareStatement(SQLCom.createDataTable());
        statement.execute();
        statement.close();
        monitor.resetTimer();
        processing = false;
    }


    // Public method to provide access to the instance
    public static synchronized Database getInstance() {
        return instance;
    }

    public static synchronized void establishConnection(String fullPath,String masterPassword){
        if(instance != null)return;

        Exception e=null;
        try {
            instance = new Database(fullPath, masterPassword);
        } catch (Exception ex) {
            e=ex;
            e.printStackTrace();
        }finally {
            for(DatabaseListener listener : listeners)
                if(e==null)listener.onInitialized();
                else listener.onFailure(new DatabaseResponse(DatabaseAction.INIT,e.getMessage(),false));
        }
    }
    public static synchronized void disconnect() {
        if(instance == null)return;
        Exception ex=null;
        try {
            instance.close();
            instance = null;
        } catch (Exception e) {
            ex= e;
        }finally{
            for(DatabaseListener listener : listeners)
                if(ex==null)listener.onDisconnected();
                else listener.onFailure(new DatabaseResponse(DatabaseAction.CLOSE,ex.getMessage(),false));
        }

    }

    public static boolean isOffline(){
        return instance == null;
    }


    @Override
    public void insert(Entry e) {
        // Implementation code
        processing = true;
        PreparedStatement statement = null;
        Exception exception=null;
        try{
            statement = connection.prepareStatement(SQLCom.insertIntoDataTable());
            statement.setString(1,Long.toString(e.creationTime));
            statement.setString(2,  AES256.encrypt(  e.toString(),monitor.getPassword()));
            statement.execute();
            statement.close();
            monitor.resetTimer();
        }catch(Exception ex){
            ex.printStackTrace();
            exception  = ex;
        }finally{
            for(DatabaseListener listener : listeners)
                if(exception==null)listener.onSuccess(new DatabaseResponse(DatabaseAction.INSERT,"Entry inserted",true));
                else listener.onFailure(new DatabaseResponse(DatabaseAction.INSERT,exception.getMessage(),false));
        }
        processing =false;
    }

    @Override
    public ArrayList<Entry> getAll() {
        processing = true;
        ArrayList<Entry>entries = new ArrayList<>();
        PreparedStatement statement = null;
        ResultSet rs = null;
        Exception e=null;
        try{
            statement = connection.prepareStatement(SQLCom.getAllData());
            rs = statement.executeQuery();
            while(rs.next()){
                String decrypted = AES256.decrypt(rs.getString(2),monitor.getPassword());
                Entry entry = Entry.fromString(decrypted);
                entries.add(entry);
            }
            rs.close();
            statement.close();
            monitor.resetTimer();
        }catch(Exception ex){
            e=ex;
            e.printStackTrace();
        }finally{
            for(DatabaseListener listener : listeners)
                if(e==null)listener.onSuccess(new DatabaseResponse(DatabaseAction.GET_ALL,"Entries retrieved",true));
                else listener.onFailure(new DatabaseResponse(DatabaseAction.GET_ALL,e.getMessage(),false));
        }
        processing =false;
        return entries;
    }

    @Override
    public void update(Entry entry) {
        // Implementation code
        processing = true;
        Exception e=null;
        PreparedStatement statement = null;
        try{
            statement = connection.prepareStatement(SQLCom.updateEntry());
            statement.setString(1,AES256.encrypt( entry.toString(),monitor.getPassword()));
            statement.setString(2,Long.toString(entry.creationTime));
            statement.execute();
            statement.close();
            monitor.resetTimer();
        }catch(Exception ex){
            e=ex;
            ex.printStackTrace();
        }finally{

            for(DatabaseListener listener : listeners)
                if(e==null)listener.onSuccess(new DatabaseResponse(DatabaseAction.UPDATE,"Entry updated",true));
                else listener.onFailure(new DatabaseResponse(DatabaseAction.UPDATE,e.getMessage(),false));
        }
        processing =false;
    }

    @Override
    public void delete(String id) {
        processing = true;
        PreparedStatement statement = null;
        Exception e=null;

        try{
            statement = connection.prepareStatement(SQLCom.deleteFromDataTable());
            statement.setString(1,id);
            statement.execute();
            statement.close();
            monitor.resetTimer();
        } catch (Exception ex) {
            ex.printStackTrace();
            e= ex;
        }finally{
            for(DatabaseListener listener : listeners)
                if(e==null)listener.onSuccess(new DatabaseResponse(DatabaseAction.DELETE,"Entry deleted",true));
                else listener.onFailure(new DatabaseResponse(DatabaseAction.DELETE,e.getMessage(),false));
        }
        processing =false;
    }

    @Override
    public ArrayList<Entry> getEntries(String... tags) {
        ArrayList<Entry>entries = getAll();
        ArrayList<Entry>filtered = new ArrayList<>();
        for(Entry entry:entries){
            if(entry.hasTags(tags))filtered.add(entry);
        }
        return filtered;
    }

    @Override
    public void close() throws Exception{
        processing = true;
        monitor.resetPassword();
        connection.close();
        processing = false;
    }

    @Override
    public void dropTable() {
        processing = true;
        Exception e=null;
        PreparedStatement statement = null;
        try{
            statement = connection.prepareStatement(SQLCom.deleteAllData());
            statement.execute();
            statement.close();


            monitor.resetTimer();
        } catch (Exception ex) {
            ex.printStackTrace();
            e= ex;
        }finally{
            for(DatabaseListener listener : listeners)
                if(e==null)listener.onSuccess(new DatabaseResponse(DatabaseAction.DATABASE_DELETE,"Database deleted",true));
                else listener.onFailure(new DatabaseResponse(DatabaseAction.DATABASE_DELETE,e.getMessage(),false));
        }
        processing =false;
    }

    @Override
    public Entry getEntry(long id) {
        processing = true;
        Exception e=null;
        PreparedStatement statement = null;
        ResultSet rs = null;

        try{
            statement = connection.prepareStatement(SQLCom.getEntry());
            statement.setString(1,Long.toString(id));
            rs = statement.executeQuery();
            Entry entry = Entry.fromString(AES256.decrypt(rs.getString(2),monitor.getPassword()));
            rs.close();
            statement.close();
            return entry;
        }catch(Exception ex){
            e=ex;
            ex.printStackTrace();
            return null;
        }finally{
            processing = false;
            for(DatabaseListener listener : listeners)
                if(e==null)listener.onSuccess(new DatabaseResponse(DatabaseAction.GET,"Entry retrieved",true));
                else listener.onFailure(new DatabaseResponse(DatabaseAction.GET,e.getMessage(),false));

        }
    }

    @Override
    public void requirePassword() {
        while(processing){
            System.out.print("");
        }
        disconnect();
    }

    @Override
    public void passwordProvided() {

    }

    public boolean exists(long id) {
        processing = true;
        PreparedStatement statement = null;
        ResultSet rs = null;

        try{
            statement = connection.prepareStatement(SQLCom.hasEntry());
            statement.setString(1,Long.toString(id));
            rs = statement.executeQuery();
            return rs.getString(1).equals("1");
        }catch(Exception ex){
            ex.printStackTrace();
            return false;
        }finally{
            processing = false;
        }
    }

    public void put(Entry entry){

        if(exists(entry.creationTime)){
            Entry existing = getEntry(entry.creationTime);
            if(existing.modifiedRecently(entry) == existing)return;
            update(entry);
        }else{
            insert(entry);
        }

    }
}
