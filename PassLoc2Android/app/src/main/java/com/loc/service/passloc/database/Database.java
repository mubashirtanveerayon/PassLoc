package com.loc.service.passloc.database;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.loc.service.passloc.model.EntryModel;
import com.loc.service.passloc.secure.Credential;
import com.loc.service.utils.HelperFunctions;
import com.loc.service.utils.Identifier;
import com.loc.service.utils.dbInterface.DatabaseInterface;
import com.loc.service.utils.dbInterface.DatabaseListener;

import net.zetetic.database.sqlcipher.SQLiteDatabase;
import net.zetetic.database.sqlcipher.SQLiteOpenHelper;
import net.zetetic.database.sqlcipher.SQLiteStatement;

import java.util.ArrayList;


public class Database extends SQLiteOpenHelper implements DatabaseInterface {

    private static final int VERSION = 1;

    private DatabaseListener listener;
    private final String databaseName;
    private static Database instance;
    private CommandGenerator commandGenerator;

    private Database(Credential credential,Context context,String dbName,String password){
        super(context,dbName+"."+ Identifier.DATABASE_EXTENSION, HelperFunctions.sha256(password),null,VERSION,VERSION,null,null,false);

        databaseName = dbName;

        commandGenerator = new CommandGenerator(credential,password);


    }

    public static boolean online(){
        return instance != null;
    }

    public static void disconnect() {
        if(online())
            instance.close();
        instance = null;

    }

    public static void establishConnection( Credential credential,Context context, String dbName,  String databasePassword){
        if(online())
            disconnect();

        instance = new Database (credential,context, dbName, databasePassword);
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


        try (SQLiteStatement statement = db.compileStatement(commandGenerator.insertIntoDataTable())){

            statement.bindString(1, commandGenerator.encryptText( entry.getTag()));
            statement.bindString(2, commandGenerator.encryptText(entry.getUsername()));
            statement.bindString(3, commandGenerator.encryptText(entry.getPassword()));

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

        SQLiteDatabase db = getWritableDatabase();
        Cursor c = db.rawQuery(commandGenerator.getAllData(),null);
        while(c.moveToNext()){
            String id = c.getString(0);


            String tag = commandGenerator.decryptText(c.getString(1));
            String username = commandGenerator.decryptText( c.getString(2));
            String password = commandGenerator.decryptText( c.getString(3));

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



        try (SQLiteStatement statement = db.compileStatement(commandGenerator.updateEntry())){
            statement.bindString(1, commandGenerator.encryptText(entry.getUsername()));
            statement.bindString(2, commandGenerator.encryptText(entry.getPassword()));

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


        Cursor c = db.rawQuery(commandGenerator.getTags(), null);;


        while(c.moveToNext()) {
            String tag = commandGenerator.decryptText( c.getString(0));
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
    public String encryptText(String text) {
        return commandGenerator.encryptText(text);
    }

    @Override
    public String decryptText(String encryptedText) {
        return commandGenerator.decryptText(encryptedText);
    }

    @Override
    public String getName() {
        return databaseName;
    }

    @Override
    public boolean alreadyExists(String id) {
        SQLiteDatabase db = getWritableDatabase();
        Cursor c = db.rawQuery(commandGenerator.checkIfEntryExists());

        //TODO

        return false;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
