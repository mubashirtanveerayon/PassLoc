package services.model;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class EntryModel {


    private String id = null;
    private final String tag,username,password;


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


    public static ArrayList<EntryModel> fromJSONArray(JSONArray jsonArray){
        ArrayList<EntryModel> entries = new ArrayList<EntryModel>();
        for(int i=0;i<jsonArray.length(); i++){
            JSONObject jo = jsonArray.getJSONObject(i);
            String tag = jo.getString("tag");
            String username = jo.getString("username");
            String password = jo.getString("password");
            String id = jo.has("id") ? jo.getString("id"):null;
            if(id == null)
                entries.add(new EntryModel(tag,username,password));
            else
                entries.add(new EntryModel(id,tag,username,password));
        }

        return entries;
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



}
