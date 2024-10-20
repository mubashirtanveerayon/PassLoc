package commons.services.model;

import java.util.ArrayList;
import java.util.Base64;

public abstract class Entry {

    protected ArrayList<String> tags;

    protected String[] fields;
    int fieldSet = 0;

    public static final char DATA_SEPARATOR = ',';
    public static final char SEPARATOR = ':';


    public Entry(int fieldSize) {
        tags = new ArrayList<>();
        fields = new String[fieldSize];
    }

    public final void addTag(String tag){
        tags.add(tag);
    }
    protected void addField(String field){
        if(fieldSet>=fields.length)
            return;
        fields[fieldSet++] = field;
    }

    public abstract boolean equals(Object obj);

    public int hashCode(){
        int code = 0;

        for(String field:fields)
            code ^= field.hashCode();

        return code;
    }

    public final int getTagSize (){
        return tags.size();
    }

    public final int getFieldSize(){
        return fields.length;
    }

    public final String getTag(int index){
        if(index >= tags.size())
            return "";
        return tags.get(index);

    }

    public final boolean containsTag(String tag){

        return tags.contains(tag);

    }

    public final String getField(int index){
        if(index >= fields.length)
            return "";
        return fields[index];
    }

    public final String encode(){
        StringBuilder builder = new StringBuilder();
        Base64.Encoder encoder = Base64.getEncoder();
        for(int i=0;i<tags.size();i++) {

            builder.append(encoder.encodeToString( tags.get(i).getBytes()));
            if (i < tags.size() - 1)
                builder.append(DATA_SEPARATOR);
        }

        builder.append(SEPARATOR);
        for(int i=0;i<fields.length;i++){
            builder.append(encoder.encodeToString( fields[i].getBytes()));
            if(i < fields.length-1)
                builder.append(DATA_SEPARATOR);
        }

        return builder.toString();
    }




    public void clear(){
        tags.clear();
        for(int i=0;i<fields.length;i++)
            fields[i] = "";
    }


}
