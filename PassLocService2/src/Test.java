import org.json.JSONArray;
import org.json.JSONObject;
import services.model.EntryModel;

import java.util.ArrayList;

public class Test {

    public static void main(String[] args) {


        ArrayList<EntryModel> entries = new ArrayList<EntryModel>();

        entries.add(new EntryModel("1","sdfg","user","pass2"));
        entries.add(new EntryModel("ssdfg","user","passsa2"));

        System.out.println(EntryModel.fromJSONArray(EntryModel.convertToJsonString(entries)));
    }
}
