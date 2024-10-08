package services.database;

import services.model.EntryModel;
import services.secure.Credential;
import utils.HelperFunctions;
import utils.dbInterface.DatabaseInterface;
import utils.dbInterface.DatabaseListener;
import utils.Identifier;

import java.io.File;
import java.sql.*;
import java.util.ArrayList;

public class Database implements DatabaseInterface {


    private final Connection sqlConnection;
    private DatabaseListener listener;
    private static Database instance;
    private CommandGenerator commandGenerator;

    private static boolean classFound=false;



    public static void establishConnection(String dbName, String dbPath, String databasePassword) throws Exception {

        if(!classFound) {
            Class.forName("org.sqlite.JDBC");
            classFound=true;
        }

        File directory = new File(dbPath);
        if(!directory.exists() || !directory.isDirectory())
            directory.mkdir();


        if(online())
            disconnect();

        instance = new Database(dbName, dbPath, databasePassword);
    }

    private Database(String dbName,String dbPath,String dbPassword) throws SQLException {
        String extension = "."+ Identifier.DATABASE_EXTENSION;
        if(!dbName.endsWith(extension))
            dbName += extension;
        String fullPath = dbPath.endsWith(File.separator)?dbPath+dbName:dbPath+File.separator + dbName;
        sqlConnection= DriverManager.getConnection("jdbc:sqlite:"+fullPath+"?cipher=sqlcipher&key="+ HelperFunctions.sha256(dbPassword)+"&legacy=4");
        commandGenerator = new CommandGenerator(dbPassword);
    }

    public static boolean online(){
        return instance != null;
    }

    public static void disconnect() {
        if(online())
            instance.close();
        instance = null;

    }

    public static Database getInstance() {
        return instance;
    }



    public boolean requireInitialization(){
        PreparedStatement statement = null;
        try{
            statement = sqlConnection.prepareStatement(commandGenerator.checkIfTableExists());
            statement.execute();
            statement.close();
            return false;
        }catch(Exception e){
            return true;
        }
    }

    public void initialize() {
        PreparedStatement statement = null;
        Exception e=null;
        try {
            statement = sqlConnection.prepareStatement(commandGenerator.createDataTable());
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

    public void close(){
        if(sqlConnection != null) {
            try {
                sqlConnection.close();
            } catch (SQLException e) {
                if(listener != null)
                    listener.onFailure(e.getMessage());
                throw new RuntimeException(e);
            }
        }

        commandGenerator = null;
    }




    public void insert(EntryModel entryModel) {
        PreparedStatement statement = null;
        Exception e=null;
        Credential c = Credential.getInstance();
        
        try{
            statement = sqlConnection.prepareStatement(commandGenerator.insertIntoDataTable());
            statement.setString(1,c.encrypt(entryModel.getTag()));
            statement.setString(2,c.encrypt(entryModel.getUsername()));
            statement.setString(3,c.encrypt(entryModel.getPassword()));
            statement.execute();
            statement.close();
        } catch (Exception ex) {
            ex.printStackTrace();
            e= ex;
        }finally{
            if(listener != null) {
                if (e == null)
                    listener.onSuccess("Entry inserted");
                else
                    listener.onFailure(e.getMessage());
            }

        }
    }


    public ArrayList<EntryModel> getAllData() {
        ArrayList<EntryModel> entries = new ArrayList<>();
        Exception e=null;
        PreparedStatement statement = null;
        ResultSet rs = null;
        Credential c = Credential.getInstance();
        try{
            statement = sqlConnection.prepareStatement(commandGenerator.getAllData());
            rs = statement.executeQuery();
            while(rs.next()) {
                String id = rs.getString(1);
                String tag = c.decrypt( rs.getString(2));

                String username = c.decrypt( rs.getString(3));
                String password = c.decrypt( rs.getString(4));

                EntryModel entryModel = new EntryModel(id,tag,username,password);
                entries.add(entryModel);
            }
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


    public void update(EntryModel entryModel) {
        PreparedStatement statement = null;
        Exception e=null;
        Credential c = Credential.getInstance();
        try{
            statement = sqlConnection.prepareStatement(commandGenerator.updateEntry());
            statement.setString(1,c.encrypt(entryModel.getUsername()));
            statement.setString(2,c.encrypt(entryModel.getPassword()));
            statement.setString(3,entryModel.getId());
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


    public void setListener(DatabaseListener databaseListener) {
        this.listener = databaseListener;
    }


    public void delete(String id) {
        PreparedStatement statement = null;
        Exception e=null;
        try{
            statement = sqlConnection.prepareStatement(commandGenerator.deleteFromDataTable());

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


    public ArrayList<String> getAllTags() {
        ArrayList <String> tags = new ArrayList<>();
        String sqlCommand = commandGenerator.getTags();
        PreparedStatement statement = null;
        ResultSet rs = null;
        Exception e=null;
        Credential c = Credential.getInstance();
        try{
            statement = sqlConnection.prepareStatement(sqlCommand);
            rs = statement.executeQuery();

            while (rs.next()) {
                String tag = c.decrypt(rs.getString(1));
                if (!tags.contains(tag))
                    tags.add(tag);

            }
            statement.close();
        } catch (Exception ex) {
            ex.printStackTrace();
            e= ex;
        }finally{
            if(listener != null) {
                if (e == null)
                    listener.onSuccess("Tags loaded");
                else
                    listener.onFailure(e.getMessage());
            }

        }
        return tags;
    }


    public ArrayList<EntryModel> getEntries(String tag) {
        ArrayList<EntryModel> entries = getAllData();
        ArrayList<EntryModel> filteredEntries = new ArrayList<>();
        for(EntryModel entry : entries) {
            if(entry.getTag().equals(tag)) {
                filteredEntries.add(entry);
            }
        }
        entries.clear();
        return filteredEntries;
    }

    public String getTableName(){
        return commandGenerator.getTableName();
    }


    public boolean alreadyExists(String id){

        PreparedStatement statement = null;
        ResultSet rs = null;
        boolean exists = false;
        try{
            statement = sqlConnection.prepareStatement(commandGenerator.checkIfEntryExists());
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
}
