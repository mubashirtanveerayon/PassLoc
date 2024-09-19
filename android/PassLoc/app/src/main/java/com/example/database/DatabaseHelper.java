package com.example.database;

import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.util.Log;

import com.example.constants.AutoLoad;
import com.example.constants.PassLoc;
import com.example.encryption.SecureEncryption;
import com.example.exception.SQLiteInternalError;
import com.example.passloc.MainActivity;

import net.zetetic.database.sqlcipher.SQLiteDatabase;
import net.zetetic.database.sqlcipher.SQLiteOpenHelper;
import net.zetetic.database.sqlcipher.SQLiteStatement;

import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.HashMap;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final int VERSION = 1;

    private static DatabaseHelper dbHelper;
    private static boolean databaseOnline=false;
    private DatabaseListener listener;


    private DatabaseHelper(Context context, String name, String password) {
        super(context, name, password, null, VERSION, VERSION, null, null,false);
    }

    public static void establishConnection(String name,String password,char[] pin) {

        if(databaseOnline)throw new SQLiteInternalError("Database in use");


        String keySeedStr = "";
        for (int i = 0; i < pin.length / 2; i++) keySeedStr += pin[i];
        String textSeedStr = "";
        for (int i = pin.length / 2; i < pin.length; i++) textSeedStr += pin[i];
        SQLiteCommandGenerator.close();
        SQLiteCommandGenerator.instantiate(Long.parseLong(keySeedStr), Long.parseLong(textSeedStr));

        dbHelper = new DatabaseHelper(PassLoc.getContext(), name, password);
        try {
            dbHelper.getWritableDatabase();
        }catch(Exception ex){
            Log.e("exception",ex.getMessage());
        }
        databaseOnline=true;
    }

    public static boolean isOnline(){
        return databaseOnline;
    }

    public void setListener(DatabaseListener listener) {
        this.listener=listener;
    }

    public void addDatabaseListener(DatabaseListener listener){
        this.listener=listener;
    }



    public static void disconnect(){
        if(dbHelper == null)return;
        dbHelper.close();
        SQLiteCommandGenerator.close();
        databaseOnline=false;
    }


    public static DatabaseHelper getInstance(){
        return dbHelper;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {


        if(!SQLiteCommandGenerator.isAvailable())throw new SQLiteInternalError("SQLiteCommandGenerator is unavailable.");

        try {

            db.execSQL(SQLiteCommandGenerator.getInstance().createDataTable());

            if(listener != null)listener.onSuccess("Database initialized");
        }catch (Exception ex){
            if(listener!=null)listener.onFailure(ex.getMessage());
            Log.e("exception",ex.getMessage());
        }


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        // do something
        if(listener != null)listener.onUpgrade("Database upgraded");

    }

    public void delete(String id){
        SQLiteDatabase db = getWritableDatabase();
        SQLiteCommandGenerator commandGenerator =  SQLiteCommandGenerator.getInstance();

        try(SQLiteStatement statement=db.compileStatement(commandGenerator.deleteFromDataTable())){
            statement.bindString(1,id);
            statement.execute();
            statement.close();
        }catch (Exception ex){
            if(listener!=null)listener.onFailure(ex.getMessage());
            Log.e("exception",ex.getMessage());
        }
    }

    public void insert(String tag,String username,String password ){


        SQLiteDatabase db = getWritableDatabase();

        SQLiteCommandGenerator commandGenerator = SQLiteCommandGenerator.getInstance();

        try (SQLiteStatement statement = db.compileStatement(commandGenerator.insertIntoDataTable())){

            statement.bindString(1, commandGenerator.encodeText(tag));
            statement.bindString(2, commandGenerator.encodeText(username));
            statement.bindString(3, commandGenerator.encodeText(password));

//            statement.bindString(1,(tag));
//            statement.bindString(2, (username));
//            statement.bindString(3, (password));
            statement.execute();
            statement.close();
            if(listener!=null)listener.onSuccess("Insert operation successful");
        }catch(Exception ex){
            if(listener!=null)listener.onFailure(ex.getMessage());
            Log.e("exception",ex.getMessage());
        }
    }

    public void update(String username,String password,String id){
        SQLiteDatabase db = getWritableDatabase();

        SQLiteCommandGenerator commandGenerator = SQLiteCommandGenerator.getInstance();


        try (SQLiteStatement statement = db.compileStatement(commandGenerator.updateEntry())){
            statement.bindString(1, commandGenerator.encodeText(username));
            statement.bindString(2, commandGenerator.encodeText(password));

            statement.bindString(3,id);

            statement.execute();
            statement.close();
            if(listener!=null)listener.onSuccess("Update operation successful");
        }catch(Exception ex){
            if(listener!=null)listener.onFailure(ex.getMessage());
            Log.e("exception",ex.getMessage());
        }
    }

    public HashMap<String, ArrayList<String[]>>loadData(){


        HashMap<String,ArrayList<String[]>>data = new HashMap<>();

        SQLiteDatabase db = getWritableDatabase();
        SQLiteCommandGenerator commandGenerator = SQLiteCommandGenerator.getInstance();

        Cursor c = db.rawQuery(commandGenerator.getAllData(),null);


        while(c.moveToNext()){
            String id = c.getString(0);

//            String encryptedTag = c.getString(1);
//            String encryptedUsername = c.getString(2);
//            String encryptedPassword = c.getString(3);

            String tag = commandGenerator.decodeText(c.getString(1));
            String username = commandGenerator.decodeText( c.getString(2));
            String password = commandGenerator.decodeText( c.getString(3));

            String [] entry = new String[]{id,tag,username,password};

            ArrayList<String[]>entries;
            if(data.containsKey(tag)){
                entries = data.get(tag);
                entries.add(entry);
            }else{
                entries = new ArrayList<>();
                entries.add(entry);
                data.put(tag,entries);
            }


        }

        c.close();


        return data;
    }




}
