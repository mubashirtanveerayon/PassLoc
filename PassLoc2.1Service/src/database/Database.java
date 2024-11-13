package database;

import commons.model.SimpleEntry;
import commons.sqlcomm.SQLCom;
import commons.utils.Identifier;
import commons.utils.dbInterface.DatabaseInterface;
import commons.utils.dbInterface.DatabaseListener;

import java.io.File;
import java.sql.*;
import java.util.ArrayList;

public class Database implements DatabaseInterface {

    private static Database instance;
    private static boolean classFound=false;


    private DatabaseListener listener;
    private Connection connection;

    private Database(String fullPath,String password)throws SQLException {

        connection= DriverManager.getConnection("jdbc:sqlite:"+fullPath+"?cipher=sqlcipher&key=["+ password+"]&legacy=4");
        initialize();
    }

    @Override
    public void initialize() {
        PreparedStatement statement = null;
        Exception e=null;
        SQLCom commandGenerator = SQLCom.getInstance();
        try {
            statement = connection.prepareStatement(commandGenerator.createDataTable());
            statement.execute();
            statement.close();
        } catch (SQLException ex) {

            ex.printStackTrace();
            e= ex;
        }finally{
            if(listener != null) {
                if (e == null)
                    listener.onSuccess("Table created");
                else
                    listener.onFailure(e.getMessage());
            }

        }
    }

    @Override
    public void close(){
        if(connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                if(listener != null)
                    listener.onFailure(e.getMessage());
                throw new RuntimeException(e);
            }
        }
        
    }

    @Override
    public void insert(SimpleEntry entry) {
        PreparedStatement statement = null;
        Exception e=null;

        SQLCom commandGenerator = SQLCom.getInstance();

        try{
            statement = connection.prepareStatement(commandGenerator.insertIntoDataTable());
            statement.setString(1,commandGenerator.encryptEntry((entry)));

            statement.execute();
            statement.close();
        } catch (Exception ex) {
            ex.printStackTrace();
            e= ex;
        }finally{
            if(listener != null) {
                if (e == null)
                    listener.onSuccess("SimpleEntry inserted");
                else
                    listener.onFailure(e.getMessage());
            }

        }
    }

    @Override
    public ArrayList<SimpleEntry> getAllData() {
        ArrayList<SimpleEntry> entries = new ArrayList<>();
        Exception e=null;
        PreparedStatement statement = null;
        ResultSet rs = null;
        SQLCom commandGenerator = SQLCom.getInstance();
        try{
            statement = connection.prepareStatement(commandGenerator.getAllData());
            rs = statement.executeQuery();
            while(rs.next()) {
                String id = rs.getString(1);
                SimpleEntry entry = commandGenerator.decryptEntry(rs.getString(2));
                entry.setId(id);
                entries.add(entry);
            }
            rs.close();
            statement.close();
        }catch(SQLException ex){
            ex.printStackTrace();
            e = ex;
        }finally{
            if(listener != null) {
                if (e == null)
                    listener.onSuccess("Entries loaded");
                else
                    listener.onFailure(e.getMessage());
            }
        }
        return entries;
    }

    @Override
    public void update(SimpleEntry entry) {
        String id = entry.getId();
        if(id == null)
            return;
        PreparedStatement statement = null;
        Exception e=null;
        SQLCom commandGenerator = SQLCom.getInstance();
        try{
            statement = connection.prepareStatement(commandGenerator.updateEntry());
            statement.setString(1,commandGenerator.encryptEntry(entry));
            statement.setString(2,id);


            statement.execute();
            statement.close();
        } catch (Exception ex) {
            ex.printStackTrace();
            e= ex;
        }finally{
            if(listener != null) {
                if (e == null)
                    listener.onSuccess("Entry updated");
                else
                    listener.onFailure(e.getMessage());
            }

        }
    }

    @Override
    public void setListener(DatabaseListener listener) {
        this.listener = listener;
    }

    @Override
    public void delete(String id) {
        PreparedStatement statement = null;
        Exception e=null;
        SQLCom commandGenerator = SQLCom.getInstance();
        try{
            statement = connection.prepareStatement(commandGenerator.deleteFromDataTable());

            statement.setString(1,id);
            statement.execute();
            statement.close();
        } catch (Exception ex) {
            ex.printStackTrace();
            e= ex;
        }finally{
            if(listener != null) {
                if (e == null)
                    listener.onSuccess("Entry deleted");
                else
                    listener.onFailure(e.getMessage());
            }

        }
    }

    @Override
    public ArrayList<SimpleEntry> getEntries(String tag) {
        ArrayList<SimpleEntry> entries = getAllData();
        ArrayList<SimpleEntry> filteredEntries = new ArrayList<>();
        for(SimpleEntry entry : entries) {
            if(entry.containsTag(tag)) {
                filteredEntries.add(entry);
            }
        }
        entries.clear();
        return filteredEntries;
    }

    @Override
    public boolean checkIfIdAlreadyExists(String id) {
        PreparedStatement statement = null;
        ResultSet rs = null;
        boolean exists = false;
        SQLCom commandGenerator = SQLCom.getInstance();
        try{
            statement = connection.prepareStatement(commandGenerator.checkIfEntryExists());
            statement.setString(1,id);
            rs = statement.executeQuery();

            while(rs.next()) {
                exists = true;
                break;
            }

            statement.close();


        }catch(Exception e){
            e.printStackTrace();
        }

        return exists;
    }


    public static void disconnect(){
        if(Database.offline())return;

        instance.close();
        instance=null;
    }

    public static boolean offline(){
        return getInstance()==null;
    }


    public static Database getInstance(){
        return instance;
    }

    public static void establishConnection(String name,String path,String password) throws Exception{
        if(!classFound) {
            Class.forName("org.sqlite.JDBC");
            classFound=true;
        }

        File directory = new File(path);
        if(!directory.exists() || !directory.isDirectory())
            directory.mkdir();


        if(!offline())

            disconnect();
        StringBuffer sb = new StringBuffer(path);
        if(!path.endsWith(File.separator))sb.append(File.separator);

        sb.append(name).append(Identifier.DATABASE_FULL_EXTENSION);

        instance = new Database(sb.toString(), password);
    }


}
