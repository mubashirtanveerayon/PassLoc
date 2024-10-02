package utils.dbInterface;

import services.model.EntryModel;

import java.util.ArrayList;

public interface DatabaseInterface {

    public void initialize();
    public void insert(EntryModel entry);
    public ArrayList<EntryModel> getAllData();
    public void update(EntryModel entry);
    public void setListener(DatabaseListener listener);
    public void delete(String id);
    public ArrayList<String> getAllTags();
    public ArrayList<EntryModel> getEntries(String tag);
    public boolean requireInitialization();
    public String getTableName();

    public boolean alreadyExists(String id);

}
