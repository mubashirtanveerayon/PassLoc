package commons.sqlcomm;

import commons.model.SimpleEntry;
import commons.secure.AES;
import commons.secure.AES256WithPassword;
import commons.utils.exception.SQLComAlreadyExistsException;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;

public class SQLCom {

    private static SQLCom instance;

    static final String DATA_TABLE_NAME = "data";
    static final String KEY_ID = "id";
    static final String KEY_ENTRY = "entry";

    HashMap<String,String> keys;
    AES aes;


    private SQLCom(String masterPassword){
        keys = new HashMap<>();
        aes = new AES(masterPassword);

        AES256WithPassword aesWithPassword = new AES256WithPassword(masterPassword);
        keys.put(DATA_TABLE_NAME, aesWithPassword.encrypt(KEY_ID));
        keys.put(KEY_ENTRY,aesWithPassword.encrypt(KEY_ENTRY));
        keys.put(KEY_ID,aesWithPassword.encrypt(KEY_ID));

    }



    public static SQLCom getInstance(){
        return instance;
    }

    public static void initialize(String masterPassword){
        if(instance != null)
            throw new SQLComAlreadyExistsException();
        instance = new SQLCom(masterPassword);
    }


    public static void disconnect(){
        if(instance==null)return;
        instance.reset();
        instance=null;
    }

    public void reset(){
        keys.clear();
        aes.clearPassword();
    }

    public SimpleEntry decryptEntry(String text){
        try {
            return SimpleEntry.decode(new String(aes.decrypt(Base64.getDecoder().decode(text.getBytes())), StandardCharsets.UTF_8));
        }catch(Exception e){
            throw new RuntimeException(e);
        }
    }

    public String encryptEntry(SimpleEntry entry){
        try {
            return Base64.getEncoder().encodeToString(aes.encrypt(entry.encode().getBytes()));
        }catch(Exception e){
            throw new RuntimeException(e);
        }
    }

    public byte[] encrypt(byte[]data){
        try{
            return aes.encrypt(data);
        }catch(Exception e){
            throw new RuntimeException(e);
        }
    }

    public byte[] decrypt(byte[]data){
        try{
            return aes.decrypt(data);
        }catch(Exception e){
            throw new RuntimeException(e);
        }
    }

    public String createDataTable(){
        return "create table if not exists ["+keys.get (DATA_TABLE_NAME)+ "](["+keys.get (KEY_ID)+"] integer primary key autoincrement, ["+keys.get (KEY_ENTRY)+"] text);";
    }


    public String deleteFromDataTable(){
        return "delete from ["+keys.get (DATA_TABLE_NAME)+"] where ["+keys.get (KEY_ID)+"] = ?;";
    }

    public String deleteAllData(){
        return "drop table ["+keys.get (DATA_TABLE_NAME)+"];";
    }


    public String insertIntoDataTable(){
        return "insert into ["+keys.get (DATA_TABLE_NAME)+"] (["+keys.get (KEY_ENTRY)+"]) values (?);";
    }


    public String getAllData() {
        return "select * from ["+keys.get (DATA_TABLE_NAME)+"];";
    }


    public String updateEntry() {
        return "update ["+keys.get (DATA_TABLE_NAME)+"] set ["+keys.get (KEY_ENTRY)+"]=? where ["+keys.get (KEY_ID)+"]=?;";
    }


    public String checkIfEntryExists(){
        return "select ["+keys.get (KEY_ID)+"] from ["+keys.get(DATA_TABLE_NAME)+"] where ["+keys.get (KEY_ID)+"] = ?;";
    }


    public String getTableName() {
        return keys.get(DATA_TABLE_NAME);
    }


}
