package commons.utils.strings;

public class SQLCom {
    static final String DATA_TABLE_NAME = "data";
    static final String KEY_ID = "id";
    static final String KEY_ENTRY = "entry";

    public static String createDataTable(){
        return "create table if not exists ["+(DATA_TABLE_NAME)+ "](["+(KEY_ID)+"] integer primary key, ["+(KEY_ENTRY)+"] text);";
    }


    public static String deleteFromDataTable(){
        return "delete from ["+(DATA_TABLE_NAME)+"] where ["+(KEY_ID)+"] = ?;";
    }

    public static String deleteAllData(){
        return "drop table ["+(DATA_TABLE_NAME)+"];";
    }


    public static String insertIntoDataTable(){
        return "insert into ["+(DATA_TABLE_NAME)+"] (["+(KEY_ID)+"],["+(KEY_ENTRY)+"]) values (?,?);";
    }


    public static String getAllData() {
        return "select * from ["+(DATA_TABLE_NAME)+"];";
    }

    public static String getEntry(){
        return "select "+(KEY_ENTRY)+" from ["+(DATA_TABLE_NAME)+"] where "+(KEY_ID)+" = ?;";
    }


    public static String updateEntry() {
        return "update ["+(DATA_TABLE_NAME)+"] set ["+(KEY_ENTRY)+"]=? where ["+(KEY_ID)+"]=?;";
    }


    public static String checkIfEntryExists(){
        return "select ["+(KEY_ID)+"] from ["+(DATA_TABLE_NAME)+"] where ["+(KEY_ID)+"] = ?;";
    }


    public static String hasEntry() {
        return "select exists (select 1 from ["+(DATA_TABLE_NAME)+"] where ["+(KEY_ID)+"]=?);";
    }
}
