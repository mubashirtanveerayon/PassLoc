package constants;

public class EntryModel {

    public String id,username,password,tag;
    public boolean toInsert;

    public EntryModel(String id,String username,String password,String tag,boolean insertMode){
        this.id = id;
        this.username=username;
        this.password= password;
        this.tag = tag;
        toInsert = insertMode;
    }


    public static EntryModel createInsertModel() {
        return new EntryModel("","","","" ,true);
    }
}
