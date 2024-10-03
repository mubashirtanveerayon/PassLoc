package services.model;

import org.json.JSONArray;
import org.json.JSONObject;
import utils.HelperFunctions;

import java.util.ArrayList;
import java.util.Base64;

public class EntryModel {


    private String id = null;
    private final String tag,username,password;


    public static final char PROPERTY_SEPARATOR = ':';

    public EntryModel(String tag, String username, String password) {
        this.tag = tag;
        this.username = username;
        this.password = password;
    }

    public EntryModel(String id,String tag, String username, String password) {
        this.tag = tag;
        this.username = username;
        this.password = password;
        this.id = id;
    }

    public EntryModel(EntryModel model){
        this(model.getId(),model.getTag(),model.getUsername(),model.getPassword());
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public String getTag() {
        return tag;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public static EntryModel fromJSONObject(JSONObject jsonObject){
        if(jsonObject.has("id"))
            return new EntryModel(jsonObject.getString("id"), jsonObject.getString("tag"), jsonObject.getString("username"), jsonObject.getString("password"));

        return new EntryModel(jsonObject.getString("tag"), jsonObject.getString("username"), jsonObject.getString("password"));
    }


    public static ArrayList<EntryModel> fromJSONArray(String jsonArrayStr){

        Base64.Decoder decoder = Base64.getDecoder();

        JSONArray jsonArray = new JSONArray(jsonArrayStr);

        ArrayList<EntryModel> entries = new ArrayList<EntryModel>();
        for(int i=0;i<jsonArray.length(); i++){
//            JSONObject jo = jsonArray.getJSONObject(i);
//            String tag = jo.getString("tag");
//            String username = jo.getString("username");
//            String password = jo.getString("password");
//            String id = jo.has("id") ? jo.getString("id"):null;
//            if(id == null)
//                entries.add(new EntryModel(tag,username,password));
//            else
//                entries.add(new EntryModel(id,tag,username,password));


            byte[] decoded = decoder.decode(jsonArray.get(i).toString());
            entries.add(decode(decoded));


        }

        return entries;
    }

    public static String convertToJsonString(ArrayList<EntryModel> entries){
        StringBuilder json = new StringBuilder();
        json.append("[");
        Base64.Encoder encoder = Base64.getEncoder();

        for(int i=0;i<entries.size();i++){
            EntryModel entry = entries.get(i);
//            json.append("{\"tag\":\"").append(entry.getTag()).append("\",\"username\":\"").append(entry.getUsername()).append("\",\"password\":\"").append(entry.getPassword()).append("\"");
//            if(entry.getId() != null)
//                json.append(",\"id\":\"").append(entry.getId()).append("\"");
//            json.append("}");

            String arrayElement = String.format("\"%s\"",encoder.encodeToString(entry.encode()));

            json.append(arrayElement);

            if(i < entries.size()-1) json.append(",");
        }
        json.append("]");
        return json.toString();
    }


    public String toString(){
        StringBuilder builder = new StringBuilder();
        if(id != null)
            builder.append("id: ").append(id).append(",");
        builder.append("tag: ").append(tag).append(",");
        builder.append("username: ").append(username).append(",");
        builder.append("password: ").append(password);
        return builder.toString();
    }

    public byte[] encode(){
        Base64.Encoder b64 = Base64.getEncoder();
        StringBuilder builder = new StringBuilder();
        if(id != null)
            builder.append(b64.encodeToString(id.getBytes())).append(PROPERTY_SEPARATOR);

        builder.append(b64.encodeToString(tag.getBytes())).append(PROPERTY_SEPARATOR);
        builder.append(b64.encodeToString(username.getBytes())).append(PROPERTY_SEPARATOR);
        builder.append(b64.encodeToString(password.getBytes()));

        return HelperFunctions.compress(builder.toString());
    }

    public static EntryModel decode(byte[] compressed){
        String b64Encoded = HelperFunctions.decompress(compressed);
        Base64.Decoder b64 = Base64.getDecoder();
        String[] parts = b64Encoded.split(Character.toString(PROPERTY_SEPARATOR));
        String id = null;
        String tag, username, password;
        int index = 0;
        if(parts.length == 4) {
            id = new String(b64.decode(parts[index++]));
            tag = new String(b64.decode(parts[index++]));
        }else
            tag = new String(b64.decode(parts[index++]));

        username = new String(b64.decode(parts[index++]));
        password = new String(b64.decode(parts[index++]));
        return new EntryModel(id,tag,username,password);

    }



}
