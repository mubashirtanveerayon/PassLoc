package com.loc.service.passloc.database;

import android.database.Cursor;
import android.util.Log;

import com.loc.passloc2android.PassLoc2;

import android.os.storage.StorageManager;
import android.os.storage.StorageVolume;
import net.zetetic.database.sqlcipher.SQLiteDatabase;
import net.zetetic.database.sqlcipher.SQLiteOpenHelper;
import net.zetetic.database.sqlcipher.SQLiteStatement;

import java.io.File;
import java.util.ArrayList;

import services.database.CommandGenerator;
import services.model.EntryModel;
import services.secure.Credential;
import utils.HelperFunctions;
import utils.Identifier;
import utils.dbInterface.DatabaseInterface;
import utils.dbInterface.DatabaseListener;


public class Database extends SQLiteOpenHelper implements DatabaseInterface {

    private static final int VERSION = 1;

    private DatabaseListener listener;
    private final String databasePath,databaseName,fullPath;
    private static Database instance;
    private CommandGenerator commandGenerator;




    private Database(String dbName,String dbPath,String password){
        super(PassLoc2.getContext(), dbName+"."+ Identifier.DATABASE_EXTENSION, HelperFunctions.sha256(password),null,VERSION,VERSION,null,null,false);


        databaseName = dbName+"."+ Identifier.DATABASE_EXTENSION;
        databasePath = dbPath.endsWith(File.separator) ? dbPath : dbPath+File.separator;


        fullPath = databasePath + databaseName;

        commandGenerator = new CommandGenerator(password);


    }

//    @Override
//    public SQLiteDatabase getReadableDatabase() {
//        // Open the database with custom path
//        return SQLiteDatabase.openDatabase(fullPath, null, SQLiteDatabase.OPEN_READONLY);
//    }
//
//    @Override
//    public SQLiteDatabase getWritableDatabase() {
//        // Open the database with custom path
//        return SQLiteDatabase.openDatabase(fullPath, null, SQLiteDatabase.OPEN_READWRITE);
//    }

    public static boolean online(){
        return instance != null;
    }

    public static void disconnect() {
        if(online())
            instance.close();
        instance = null;

    }

    public static void establishConnection( String dbName, String databasePassword){
        if(online())
            disconnect();




        instance = new Database (dbName,"" ,databasePassword);
        SQLiteDatabase db = instance.getWritableDatabase();
        if(instance.requireInitialization())
            instance.initialize();
    }

    public static Database getInstance() {
        return instance;
    }

    public void close(){
        super.close();
        commandGenerator = null;
    }

    @Override
    public void initialize() {
        try {

            getWritableDatabase().execSQL(commandGenerator.createDataTable());

            if(listener != null)listener.onSuccess("Table created");
        }catch (Exception ex){
            if(listener!=null)listener.onFailure(ex.getMessage());
            Log.e("exception",ex.getMessage());
        }
    }

    @Override
    public void insert(EntryModel entry) {
        SQLiteDatabase db = getWritableDatabase();


        Credential credential = Credential.getInstance();
        try (SQLiteStatement statement = db.compileStatement(commandGenerator.insertIntoDataTable())){

            statement.bindString(1, credential.encrypt( entry.getTag()));
            statement.bindString(2, credential.encrypt(entry.getUsername()));
            statement.bindString(3, credential.encrypt(entry.getPassword()));

            statement.execute();
            statement.close();
            if(listener!=null)listener.onSuccess("Insert operation successful");
        }catch(Exception ex){
            if(listener!=null)listener.onFailure(ex.getMessage());
            Log.e("exception",ex.getMessage());
        }

    }

    @Override
    public ArrayList<EntryModel> getAllData() {
        ArrayList<EntryModel> data = new ArrayList<>();
        Credential credential = Credential.getInstance();
        SQLiteDatabase db = getWritableDatabase();
        Cursor c = db.rawQuery(commandGenerator.getAllData(),null);
        while(c.moveToNext()){
            String id = c.getString(0);


            String tag = credential.decrypt(c.getString(1));
            String username = credential.decrypt( c.getString(2));
            String password = credential.decrypt( c.getString(3));

            data.add(new EntryModel(id,tag,username,password));




        }
        c.close();


        return data;
    }

    @Override
    public void update(EntryModel entry) {
        String id = entry.getId();
        if(id == null)
            return;

        SQLiteDatabase db = getWritableDatabase();

        Credential credential = Credential.getInstance();

        try (SQLiteStatement statement = db.compileStatement(commandGenerator.updateEntry())){
            statement.bindString(1, credential.encrypt(entry.getUsername()));
            statement.bindString(2, credential.encrypt(entry.getPassword()));

            statement.bindString(3,id);

            statement.execute();
            statement.close();
            if(listener!=null)listener.onSuccess("Update operation successful");
        }catch(Exception ex){
            if(listener!=null)listener.onFailure(ex.getMessage());
            Log.e("exception",ex.getMessage());
        }

    }

    @Override
    public void setListener(DatabaseListener listener) {
        this.listener =listener;
    }

    @Override
    public void delete(String id) {
        SQLiteDatabase db = getWritableDatabase();


        try(SQLiteStatement statement=db.compileStatement(commandGenerator.deleteFromDataTable())){
            statement.bindString(1,id);
            statement.execute();
        }catch (Exception ex){
            if(listener!=null)listener.onFailure(ex.getMessage());
            Log.e("exception",ex.getMessage());
        }
    }

    @Override
    public ArrayList<String> getAllTags() {
        ArrayList<String> tags = new ArrayList<>();

        SQLiteDatabase db = getWritableDatabase();


        Cursor c = db.rawQuery(commandGenerator.getTags(), null);

        Credential credential = Credential.getInstance();

        while(c.moveToNext()) {
            String tag = credential.decrypt( c.getString(0));
            tags.add(tag);
        }

        c.close();
        return tags;
    }

    @Override
    public ArrayList<EntryModel> getEntries(String tag) {
        ArrayList<EntryModel> filtered = new ArrayList<>();
        for(EntryModel entry:getAllData())
            if(entry.getTag().equals(tag))
                filtered.add(entry);

        return filtered;
    }

    @Override
    public boolean requireInitialization() {
        try{
            SQLiteStatement statement = getWritableDatabase().compileStatement(commandGenerator.checkIfTableExists());
            statement.execute();
            statement.close();
            return false;
        }catch(Exception ex) {

            return true;
        }
    }

    @Override
    public String getTableName() {
        return commandGenerator.getTableName();
    }

    @Override
    public boolean alreadyExists(String id) {
        SQLiteDatabase db = getWritableDatabase();





        Cursor c=db.rawQuery(commandGenerator.checkIfEntryExists(),id);
        boolean exists = c.moveToNext();
        c.close();




        return exists;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
