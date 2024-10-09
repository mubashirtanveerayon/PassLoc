package utils.dbInterface;

import services.model.SimpleEntry;

import java.util.ArrayList;

public interface DatabaseInterface {

    public void initialize();
    public void insert(SimpleEntry entry);
    public ArrayList<SimpleEntry> getAllData();
    public void update(SimpleEntry entry);
    public void setListener(DatabaseListener listener);
    public void delete(String id);
    public ArrayList<SimpleEntry> getEntries(String tag);
    public String getTableName();

    public boolean checkIfIdAlreadyExists(String id);

}
