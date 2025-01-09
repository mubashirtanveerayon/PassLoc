package commons.utils.db;

import commons.model.Entry;

import java.util.ArrayList;

public interface DatabaseInterface  {


    void insert(Entry e);
    ArrayList<Entry> getAll();
    void update(Entry e);
    void delete(String id);
    ArrayList<Entry>getEntries(String ...tags);
    void close() throws Exception;
    void initialize()throws Exception;
    void dropTable();
    Entry getEntry(long id);
    boolean exists(long id);
    void put(Entry entry);
}
