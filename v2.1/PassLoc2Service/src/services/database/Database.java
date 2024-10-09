package services.database;


import commons.services.model.SimpleEntry;
import commons.services.sqlcomm.CommandGenerator;
import commons.utils.HelperFunctions;
import commons.utils.dbInterface.DatabaseInterface;
import commons.utils.dbInterface.DatabaseListener;
import commons.utils.Identifier;

import java.io.File;
import java.sql.*;
import java.util.ArrayList;

public class Database implements DatabaseInterface {


    private final Connection sqlConnection;
    private DatabaseListener listener;
    private static Database instance;


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
        String extension = Identifier.DATABASE_FULL_EXTENSION;
        if(!dbName.endsWith(extension))
            dbName += extension;
        String fullPath = dbPath.endsWith(File.separator)?dbPath+dbName:dbPath+File.separator + dbName;
        sqlConnection= DriverManager.getConnection("jdbc:sqlite:"+fullPath+"?cipher=sqlcipher&key="+ HelperFunctions.sha256(dbPassword)+"&legacy=4");

        initialize();
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





    public void initialize() {
        PreparedStatement statement = null;
        Exception e=null;
        CommandGenerator commandGenerator = CommandGenerator.getInstance();
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
        CommandGenerator.getInstance().clear();

    }




    public void insert(SimpleEntry entryModel) {
        PreparedStatement statement = null;
        Exception e=null;

        CommandGenerator commandGenerator = CommandGenerator.getInstance();

        try{
            statement = sqlConnection.prepareStatement(commandGenerator.insertIntoDataTable());
            statement.setString(1,commandGenerator.encryptEntry(entryModel));

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


    public ArrayList<SimpleEntry> getAllData() {
        ArrayList<SimpleEntry> entries = new ArrayList<>();
        Exception e=null;
        PreparedStatement statement = null;
        ResultSet rs = null;
        CommandGenerator commandGenerator = CommandGenerator.getInstance();
        try{
            statement = sqlConnection.prepareStatement(commandGenerator.getAllData());
            rs = statement.executeQuery();
            while(rs.next()) {
                String id = rs.getString(1);
                SimpleEntry entry = commandGenerator.decryptEntry(rs.getString(2));
                entry.setId(id);
                entries.add(entry);
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


    public void update(SimpleEntry entryModel) {
        String id = entryModel.getId();
        if(id == null)
            return;
        PreparedStatement statement = null;
        Exception e=null;
        CommandGenerator commandGenerator = CommandGenerator.getInstance();
        try{
            statement = sqlConnection.prepareStatement(commandGenerator.updateEntry());
            statement.setString(1,commandGenerator.encryptEntry(entryModel));
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


    public void setListener(DatabaseListener databaseListener) {
        this.listener = databaseListener;
    }


    public void delete(String id) {
        PreparedStatement statement = null;
        Exception e=null;
        CommandGenerator commandGenerator = CommandGenerator.getInstance();
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

    public String getTableName(){
        return CommandGenerator.getInstance().getTableName();
    }


    public boolean checkIfIdAlreadyExists(String id){

        PreparedStatement statement = null;
        ResultSet rs = null;
        boolean exists = false;
        CommandGenerator commandGenerator = CommandGenerator.getInstance();
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
