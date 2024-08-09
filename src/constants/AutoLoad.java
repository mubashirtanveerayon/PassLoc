package constants;

import sqlite.SQLiteCommandGenerator;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Random;

public class AutoLoad {




    public static boolean createTutorial,accessTutorial,insertTutorial,viewTutorial;
    public static EntryModel entryModel;

    public static final int PIN_LENGTH_MINIMUM = 6;
    public static final int PIN_LENGTH_MAXIMUM = 38;

    public static final int PASSWORD_LENGTH_MINIMUM = 6;

    public static final void compile(){}
    public static final Dimension DEFAULT_APP_DIMENSION = new Dimension(1120,630);



    public static final String DATABASE_DIRECTORY = System.getProperty("user.home")+File.separator+"passloc"+File.separator;


    public static final Font REGULAR_FONT,BOLD_FONT;

    public static final Color THEME_COLOR = new Color(0x002F03);
    public static final Color THEME_TEXT_COLOR = new Color(0x095E0F);

    public static final Color THEME_BUTTON_TEXT_COLOR = new Color(0x76EC7E);

    public static final Color THEME_COLOR_LIGHT = new Color(0x46E353);

    public static final Color THEME_COLOR_DARK = new Color (0x2B9833);


    public static final String VERSION = "PassLoc1.1";

    public static final String GITHUB = "github.com/mubashirtanveerayon/PassLoc";

    public static final String INVALID_PIN = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz \t";


    public static ArrayList<Exception> exceptions=new ArrayList<>();


    public static boolean firstLaunch;

    public static final String wordFromTheDev = "<html>Hello, "+System.getProperty("user.name")+".<br>" +
            "This is to let you know that <b>PassLoc</b> is an <b>open-source</b> password manager app that lets you store anything<br> " +
            "that is important and confidential to you in <i>text format</i> locally on your device's storage with strong and reliable encryption.<br>" +
            "Nobody can retrieve the content of the generated database file on your device without the <b>password and pin code</b> that you set when<br>" +
            "you created the database. So, even if your device storage is <b>compromised, your secrets are safe</b>. Again, this app does not rely on<br>" +
            "an internet connection, and the source code is available publicly for inspection at "+GITHUB+"</html>";


    static{

        REGULAR_FONT = new Font("SansSerif",Font.PLAIN,20);
        BOLD_FONT = REGULAR_FONT.deriveFont(Font.BOLD);

        File folder = new File(DATABASE_DIRECTORY);

        try {

            Class.forName("org.sqlite.JDBC");

            if(!folder.exists() || !folder.isDirectory()){
                folder.mkdir();
                createTutorial=true;
                accessTutorial=true;
                viewTutorial=true;
                insertTutorial=true;
                firstLaunch=true;
            }

        } catch (Exception e) {
            exceptions.add(e);
            throw new RuntimeException(e);

        }


//        File fontFile;




//        try {
//            fontFile = new File("res/font/Ubuntu-Regular.ttf");
//
//            REGULAR_FONT=Font.createFont(Font.TRUETYPE_FONT,fontFile);
//            fontFile = new File("res/font/Ubuntu-Bold.ttf");
//            BOLD_FONT=Font.createFont(Font.TRUETYPE_FONT,fontFile);
//        } catch (FontFormatException e) {
//            throw new RuntimeException(e);
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
//        ge.registerFont(REGULAR_FONT);
//        ge.registerFont(BOLD_FONT);







    }

    public static TitledBorder getTitledBorer(String title){
        TitledBorder titledBorder = BorderFactory.createTitledBorder(BorderFactory.createMatteBorder(3,3,3,3,AutoLoad.THEME_TEXT_COLOR),title);
        titledBorder.setTitleColor(AutoLoad.THEME_TEXT_COLOR);
        titledBorder.setTitleFont(AutoLoad.REGULAR_FONT.deriveFont(15.0f));
        return titledBorder;
    }

    public static int[] getSeeds(char[] pinArray){
        String keySeed="";
        for(int i=0;i<pinArray.length/2;i++)keySeed += pinArray[i];
        String textSeed = "";
        for(int i=pinArray.length/2;i<pinArray.length;i++)textSeed+=pinArray[i];
        return new int[]{Integer.parseInt(keySeed),Integer.parseInt(textSeed)};
    }



}
