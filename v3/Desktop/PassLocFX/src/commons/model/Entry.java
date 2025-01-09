package commons.model;

import commons.exceptions.EmptyStringException;
import commons.exceptions.InvalidEntryException;
import commons.utils.strings.StringUtils;

import java.util.*;


public class Entry {

    public static final String USERNAME_KEY = "username", PASSWORD_KEY = "password",URL_KEY="url";

    public static final String SEPARATOR =":";
    public static final String VALUE_SEPARATOR = ",";
    public static final String KEY_VALUE_SEPARATOR = "->";
    public final HashSet<String> tags;
    public final HashMap<String, String> fields;
    public final long creationTime;
    private long lastModified;

    public Entry() {
        this.tags = new HashSet<>();
        this.fields = new HashMap<>();

        creationTime = System.currentTimeMillis();
        lastModified = creationTime;
    }

    public Entry(long creationTime,long lastModified){
        this.tags = new HashSet<>();
        this.fields = new HashMap<>();

        this.creationTime = creationTime;
        this.lastModified = lastModified;
    }



    public Set<String> getFieldKeys() {
        return fields.keySet();
    }

    public boolean hasField(String fieldKey) {
        return fields.containsKey(fieldKey);
    }

    public boolean hasTag(String tag) {

        return tags.contains(tag);
    }

    public String getFieldValue(String fieldKey) {
        return hasField(fieldKey) ? fields.get(fieldKey):"";
    }

    public Entry addField(String key, String value) {
        fields.put(key, value);
        lastModified = System.currentTimeMillis();
        return this;
    }

    public void removeField(String fieldKey) {
        if(fields.containsKey(fieldKey)) lastModified = System.currentTimeMillis();
        fields.remove(fieldKey);
    }

    public void removeTag(String tag){
        if(!hasTag(tag))return;
        lastModified = System.currentTimeMillis();
        tags.remove(tag);
    }

    public void addTag(String tag){
        if(hasTag(tag))return;
        tags.add(tag);
        lastModified = System.currentTimeMillis();
    }


    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Entry)) {
            return false;
        }

        Entry other = (Entry) o;

        return creationTime == other.creationTime;

//        if (fields.size() != other.fields.size() || tags.size() != other.tags.size()) {
//            return false;
//        }
//        for (Map.Entry<Integer, HashSet<String>> entry : tags.entrySet()) {
//            HashSet<String> otherValues = other.tags.get(entry.getKey());
//            if (!entry.getValue().equals(otherValues)) {
//                return false;
//            }
//        }
//        return fields.equals(other.fields);
    }

    @Override
    public int hashCode() {
//        int result = 17;
//
//        // Compute hash for tags
//        result = 31 * result + tags.hashCode();
//
//        // Compute hash for fields
//        result = 31 * result + fields.hashCode();
//
//        return result;
        return Long.hashCode(creationTime);
    }

    public void addTags(String ...tags){
        for(String tag : tags){
            addTag(tag);
        }
    }

    // assuming creationTime == other.creationTime
    public Entry modifiedRecently(Entry other){
        return lastModified>other.lastModified?this:other;
    }

    public String toString(){
        if(tags.isEmpty()) throw new InvalidEntryException("Entry must have at least one tag");
        StringBuffer sb = new StringBuffer();

        for(String tag:tags)
            if(tag.isEmpty())
                throw new EmptyStringException("Tag must not be empty");
            else
                sb.append(StringUtils.toB64(tag)).append(VALUE_SEPARATOR);

        sb = new StringBuffer(sb.substring(0, sb.length()-1));
        sb.append(SEPARATOR);
        for(Map.Entry<String,String>entry:fields.entrySet())
            if(entry.getKey().isEmpty() || entry.getValue().isEmpty())throw new EmptyStringException("Field key and value must not be empty.");
            else sb.append(StringUtils.toB64(entry.getKey())).append(KEY_VALUE_SEPARATOR).append(StringUtils.toB64(entry.getValue())).append(VALUE_SEPARATOR);

        sb = new StringBuffer(sb.substring(0, sb.length()-1));

        sb.append(SEPARATOR).append(creationTime).append(SEPARATOR).append(lastModified);
        return StringUtils.toB64(StringUtils.compress( sb.toString() ));

    }

    public static ArrayList<Entry> fromCompressedArray(byte[] compressedArray) {
        String array = StringUtils.decompress(compressedArray);
        ArrayList<Entry>entries = new ArrayList<>();
        String []parts = array.split(VALUE_SEPARATOR);

        for(String part:parts)

            entries.add(fromString(part));


        return entries;
    }

    public static byte[] toCompressedArray(ArrayList<Entry>entries) {
        StringBuilder sb = new StringBuilder();
        for(Entry entry:entries)
            sb.append(entry.toString()).append(VALUE_SEPARATOR);
        return StringUtils.compress( sb.substring(0, sb.length()-1));
    }

    public static Entry fromString(String str){
        try {

            String decompressed = StringUtils.decompress(StringUtils.fromB64(str));

            String[] parts = decompressed.split(SEPARATOR);
            Entry entry = new Entry(Long.parseLong(parts[2]), Long.parseLong(parts[3]));
            String[] tags = parts[0].split(VALUE_SEPARATOR);
            String[] fields = parts[1].split(VALUE_SEPARATOR);
            for (String tag : tags)
                entry.addTag(new String(StringUtils.fromB64(tag)));


            for (String field : fields) {
                String[] keyValuePair = field.split(KEY_VALUE_SEPARATOR);
                entry.addField(new String(StringUtils.fromB64(keyValuePair[0])), new String(StringUtils.fromB64(keyValuePair[1])));
            }
            return entry;
        } catch (Exception e) {
            throw new InvalidEntryException(e.getMessage());
        }

    }


    public boolean merge(Entry other) {

        // Check if the current Entry's lastModified timestamp is more recent
        if (lastModified > other.lastModified) return false;

        // Clear existing tags and fields
        tags.clear();
        fields.clear();

        // Add tags and fields from the other Entry
        tags.addAll(other.tags);
        fields.putAll(other.fields);

        // Update the lastModified timestamp
        lastModified = other.lastModified;
        return true;
    }

    public String getUsername(){
        return getFieldValue(USERNAME_KEY);
    }

    public String getPassword(){
        return getFieldValue(PASSWORD_KEY);
    }

    public String getURL(){
        return getFieldValue(URL_KEY);
    }


    public void reset() {
        tags.clear();
        fields.clear();
    }

    public void setUsername(String username) {
        addField(USERNAME_KEY, username);
    }
    public void setPassword(String password) {
        addField(PASSWORD_KEY, password);
    }
    public void setURL(String url) {
        addField(URL_KEY, url);
    }

    public boolean hasTags(String ...tags) {
        for(String tag:tags)
            if(!hasTag(tag))return false;
        return true;
    }
}
