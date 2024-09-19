package com.example.database;




import com.example.encryption.SecureEncryption;

import java.util.HashMap;

public class SQLiteCommandGenerator  {


    private SecureEncryption keySe,textSe;


    private HashMap<String,String>dbKeys;

    private boolean encryptedDatabase;


    private static SQLiteCommandGenerator sqliteCommunication;

    private SQLiteCommandGenerator(long keySeed,long textSeed,boolean isEncrypted){
        encryptedDatabase=isEncrypted;
        keySe = new SecureEncryption(keySeed);
        textSe = new SecureEncryption(textSeed);
        dbKeys=new HashMap<>();

        if(isEncrypted) {

            dbKeys.put("password", keySe.encode("password"));
            dbKeys.put("data", keySe.encode("data"));
            dbKeys.put("username", keySe.encode("username"));
            dbKeys.put("tag", keySe.encode("tag"));
            dbKeys.put("id",keySe.encode("id"));
        }else{
            dbKeys.put("password", ("password"));
            dbKeys.put("data", ("data"));
            dbKeys.put("username", ("username"));
            dbKeys.put("tag", ("tag"));
            dbKeys.put("id","id");
        }


    }

    public String encodeText(String text){return textSe.encode(text);}
    public String decodeText(String text){return textSe.decode(text);}
    public SecureEncryption getKeyEncryptor(){
        return keySe;
    }


    public static void instantiate(long keySeed,long textSeed){
        if(sqliteCommunication != null)return;
        sqliteCommunication = new SQLiteCommandGenerator(keySeed,textSeed,true);
    }

    public static SQLiteCommandGenerator getInstance(){
        return sqliteCommunication;
    }

    public static void close(){
        sqliteCommunication=null;
    }


    public static boolean isAvailable(){return sqliteCommunication != null;}


    public String getTags(){
        return "select distinct ["+dbKeys.get("tag")+"] from ["+dbKeys.get("data")+"];";

    }

    public String createDataTable(){
        return "create table ["+dbKeys.get("data")+ "](["+dbKeys.get("id")+"] integer primary key autoincrement, ["+dbKeys.get("tag")+"] varchar(255),["+dbKeys.get("username")+"] text,["+dbKeys.get("password")+"] text);";
    }

    public String getFromDataTable(String tag){
        return "select * from ["+dbKeys.get("data")+"] where ["+dbKeys.get("tag")+"]='"+ (encryptedDatabase ? textSe.encode(tag):tag)+"';";
    }

    public String deleteFromDataTable(){
        return "delete from ["+dbKeys.get("data")+"] where ["+dbKeys.get("id")+"] = ?;";
    }

    public String deleteAllData(){
        return "drop table ["+dbKeys.get("data")+"];";
    }


    public String insertIntoDataTable(){
//        return "insert into ["+dbKeys.get("data")+"] (["+dbKeys.get("tag")+"],["+dbKeys.get("username")+"],["+dbKeys.get("password")+"]) values ('"+(encryptedDatabase ? textSe.encode(tag):tag)+"','"+(encryptedDatabase ? textSe.encode(username):username)+"','"+(encryptedDatabase ? textSe.encode(password):password)+"');";

        return "insert into ["+dbKeys.get("data")+"] (["+dbKeys.get("tag")+"],["+dbKeys.get("username")+"],["+dbKeys.get("password")+"]) values (?,?,?);";
    }


    public String getEncodedKey(String tag) {
        return dbKeys.get(tag);
    }

    public String getAllData() {
        return "select * from ["+dbKeys.get("data")+"];";
    }

    public String getIdColumn() {
        return "select ["+dbKeys.get("id")+"] from ["+dbKeys.get("data")+"];";
    }

    public String updateEntry() {
        return "update ["+dbKeys.get("data")+"] set ["+dbKeys.get("username")+"]=?, ["+dbKeys.get("password")+"]=? where ["+dbKeys.get("id")+"]=?";
    }
}
