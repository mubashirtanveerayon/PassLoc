package commons.services.model;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Base64;

public class SimpleEntry extends Entry {
    //         mandatory           optional
    // fields: username, password, url, note(multi-line), extras(i.e. icon index, ui stuff)

    String username;
    String password;

    String url;
    String note;

    String id;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        if(this.id == null){
            this.id = id;
        }
    }




    public SimpleEntry(String username, String password, String url, String note){


        super(4);
        this.username = username == null ? "" : username;
        this.password = password == null ? "":password;
        this.url = url == null ? "":url;
        this.note = note == null ? "":note;

        addField(this.username );
        addField(this.password);
        addField(this.url);
        addField(this.note);

    }





    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }


    public String getUrl(){
        return url;
    }

    public String getNote(){
        return note;    
    }



    @Override
    public boolean equals(Object obj) {
        if(obj instanceof SimpleEntry){
            return hashCode() == ((Entry)obj).hashCode();
        }else return false;
    }

    public void clear(){
        super.clear();
        username = null;
        password = null;
        url = null;
        note = null;
    }







    public String toString(){

        StringBuilder sb = new StringBuilder();
        sb.append("Tags: ");
        for(int i=0;i<tags.size();i++) {
            sb.append(tags.get(i));
            if (i < tags.size() - 1)
                sb.append(", ");
        }

        sb.append("\nFields: ");
        for(int i=0;i<fields.length;i++) {
            sb.append(fields[i]);
            if(i<fields.length-1)
                sb.append(", ");
        }

        sb.append("\n-end-");
        return sb.toString();
    }



    public static SimpleEntry decode(String data) {


        String[] segments = data.split(Character.toString(Entry.SEPARATOR));

        if(segments.length != 2)
            return null;


        String separator = Character.toString(Entry.DATA_SEPARATOR);

        String[] fields = segments[1].split(separator);

        SimpleEntry entry;

        if(fields.length != 4 && !segments[1].endsWith(separator))
            return null;

        entry = new SimpleEntry(fields[0],fields[1],fields[2],(segments[1].endsWith(separator) ?"": fields[3]));



        String[] tags = segments[0].split(separator);



        for(String tag : tags)
            entry.addTag(tag);
        return entry;
    }
    public static String convertToJSONString(ArrayList<SimpleEntry> entries){
        StringBuilder jsonArray = new StringBuilder();
        jsonArray.append("[");

        Base64.Encoder encoder = Base64.getEncoder();

        for(int i=0;i<entries.size();i++){
            String encoded = encoder.encodeToString(entries.get(i).encode().getBytes());
            String arrayElement = String.format("\"%s\"",encoded);
            jsonArray.append(arrayElement);
            if(i<entries.size()-1)
                jsonArray.append(",");
        }

        return jsonArray.toString();

    }

    public static ArrayList<SimpleEntry> fromJSONArray(String jsonArrayStr){

        JSONArray array = new JSONArray(jsonArrayStr);

        ArrayList<SimpleEntry>entries = new ArrayList<>();

        Base64.Decoder decoder = Base64.getDecoder();

        for(int i=0;i<array.length();i++){
            String element = array.get(i).toString();
            byte[] encoded = decoder.decode(element);
            entries.add(SimpleEntry.decode(new String(encoded)));

        }

        return entries;


    }


}
