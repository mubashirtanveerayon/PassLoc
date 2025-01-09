package auto;

import java.io.File;

public class Preload {


    public static String DB_SAVE_LOCATION = System.getProperty("user.home")+ File.separator + "passloc";
    static{
        try {
            Class.forName("org.sqlite.JDBC");
            File folder = new File(DB_SAVE_LOCATION);
            if(!folder.exists())folder.mkdir();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static final String  FILE_EXTENSION = ".db";



    public static void load() {

    }
}
