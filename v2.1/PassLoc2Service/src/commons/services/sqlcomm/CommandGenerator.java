package commons.services.sqlcomm;

import commons.services.model.SimpleEntry;
import commons.services.secure.AES256WithPassword;
import commons.services.secure.AES256WithSaltAndIV;
import commons.utils.HelperFunctions;

import java.util.HashMap;

public class CommandGenerator {



    private static CommandGenerator instance;

    static final String DATA_TABLE_NAME = "data";
    static final String KEY_ID = "id";
    static final String KEY_ENTRY = "entry";

    HashMap<String,String> keys;

    char[] password;



    private CommandGenerator(String databasePassword,String masterPassword) {

        password = HelperFunctions.derivePasswordKey(HelperFunctions.sha256(databasePassword).toCharArray(),masterPassword).toCharArray();

        keys = new HashMap<>();

        AES256WithPassword keyEncryptor = new AES256WithPassword(HelperFunctions.derivePasswordKey(masterPassword.toCharArray(),databasePassword));

        keys.put(KEY_ID,HelperFunctions.sha256( keyEncryptor.encrypt(KEY_ENTRY)));
        keys.put(DATA_TABLE_NAME,HelperFunctions.sha256( keyEncryptor.encrypt(DATA_TABLE_NAME)));



    }


    public static CommandGenerator getInstance() {
        return instance;
    }

    public void clear(){
        keys.clear();
        for(int i=0;i<password.length;i++)
            password[i] = 0;
    }


    public static void initialize(String databasePassword,String masterPassword){
        if(instance != null)
            instance.clear();

        instance = new CommandGenerator(databasePassword,masterPassword);
    }

    public String encrypt(String text){
        try {
            return AES256WithSaltAndIV.encrypt(text, password);
        }catch(Exception e){
            throw new RuntimeException(e);
        }
    }


    public String decrypt(String text){
        try {
            return AES256WithSaltAndIV.decrypt(text, password);
        }catch(Exception e){
            throw new RuntimeException(e);
        }
    }

    public String encryptEntry(SimpleEntry entry){
        return encrypt(entry.encode());
    }

    public SimpleEntry decryptEntry(String data){
        return SimpleEntry.decode(decrypt(data));
    }



    public static void disconnect(){
        if(instance != null)
            instance.clear();
        instance = null;
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
