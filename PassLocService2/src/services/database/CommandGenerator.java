package services.database;

import org.jasypt.util.text.AES256TextEncryptor;
import services.secure.Credential;
import utils.HelperFunctions;
import utils.Identifier;

import java.util.HashMap;

public class CommandGenerator {


    Credential credential;


    AES256TextEncryptor textEncryptor;


    static final String DATA_TABLE_NAME = "data";
    static final String KEY_TAG = "tag";
    static final String KEY_USERNAME = "username";
    static final String KEY_PASSWORD = "password";
    static final String KEY_ID = "id";

    HashMap<String,String> keys;

    public CommandGenerator(Credential credential,String databasePassword) {
        this.credential = credential;

        textEncryptor = new AES256TextEncryptor();
        textEncryptor.setPassword(credential.derivePasswordKey(databasePassword));

        keys = new HashMap<>();
        keys.put(KEY_ID,HelperFunctions.sha256(KEY_ID));
        keys.put(KEY_TAG,HelperFunctions.sha256(KEY_TAG));
        keys.put(KEY_USERNAME,HelperFunctions.sha256(KEY_USERNAME));
        keys.put(KEY_PASSWORD,HelperFunctions.sha256(KEY_PASSWORD));
        keys.put(DATA_TABLE_NAME,credential.derivePasswordKey(DATA_TABLE_NAME));
    }


    public boolean verifyDatabasePassword(String password){
        AES256TextEncryptor testTextEncryptor = new AES256TextEncryptor();
        testTextEncryptor.setPassword(HelperFunctions.sha256( password));
        String sample = Identifier.VERSIONS[Identifier.VERSIONS.length-1];
        try{

            textEncryptor.decrypt(testTextEncryptor.encrypt(sample)).equals(sample);
            return true;
        }catch(Exception ex){
            return false;
        }
    }


    public String encryptText(String text){

        return textEncryptor.encrypt(text);
    }

    public String decryptText(String encryptedText){
        return textEncryptor.decrypt(encryptedText);
    }

    
    public String getTags(){
        return "select distinct ["+keys.get (KEY_TAG)+"] from ["+keys.get (DATA_TABLE_NAME)+"];";
    }

    public String createDataTable(){
        return "create table ["+keys.get (DATA_TABLE_NAME)+ "](["+keys.get (KEY_ID)+"] integer primary key autoincrement, ["+keys.get (KEY_TAG)+"] varchar(255),["+keys.get (KEY_USERNAME)+"] text,["+keys.get (KEY_PASSWORD)+"] text);";
    }

    public String getFromDataTable(){
        return "select * from ["+keys.get (DATA_TABLE_NAME)+"] where ["+keys.get (KEY_TAG)+"]= ?;";
    }

    public String deleteFromDataTable(){
        return "delete from ["+keys.get (DATA_TABLE_NAME)+"] where ["+keys.get (KEY_ID)+"] = ?;";
    }

    public String deleteAllData(){
        return "drop table ["+keys.get (DATA_TABLE_NAME)+"];";
    }


    public String insertIntoDataTable(){
        return "insert into ["+keys.get (DATA_TABLE_NAME)+"] (["+keys.get (KEY_TAG)+"],["+keys.get (KEY_USERNAME)+"],["+keys.get (KEY_PASSWORD)+"]) values (?,?,?);";
    }


    public String getAllData() {
        return "select * from ["+keys.get (DATA_TABLE_NAME)+"];";
    }

    public String getIdColumn() {
        return "select ["+keys.get (KEY_ID)+"] from ["+keys.get (DATA_TABLE_NAME)+"];";
    }

    public String updateEntry() {
        return "update ["+keys.get (DATA_TABLE_NAME)+"] set ["+keys.get (KEY_USERNAME)+"]=?, ["+keys.get (KEY_PASSWORD)+"]=? where ["+keys.get (KEY_ID)+"]=?;";
    }

    public String checkIfTableExists(){
        return "select ["+keys.get (KEY_ID)+"],["+keys.get(KEY_TAG)+"],["+keys.get(KEY_USERNAME)+"],["+keys.get(KEY_PASSWORD)+"] from ["+keys.get (DATA_TABLE_NAME)+"];";
    }

    public String checkIfEntryExists(){
        return "select ["+keys.get (KEY_ID)+"] from ["+keys.get(DATA_TABLE_NAME)+"] where ["+keys.get (KEY_ID)+"] = ?;";
    }






}
