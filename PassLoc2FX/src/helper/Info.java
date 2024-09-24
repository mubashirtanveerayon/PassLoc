package helper;

import services.model.EntryModel;

import java.io.File;

public class Info {

    public static final String DB_SAVE_LOCATION = System.getProperty("user.home")+ File.separator + "passloc";



    public static final String[] UNLOCK_VIEW_TUTORIAL = {

            "A master password is required to unlock any database file in your device.",
            "Master password is used to determine which data should be fetched and decrypted from the database.",
            "This password is used to uniquely identify a database table, in which the entries are stored.",
            "Therefore, it allows you to use a singe database file with multiple master password to store different kinds of data (text).",
            "However, it is appreciated to use one database file with a single master password.",
            "The lock button in the top bar locks you out of database (if a connection had been established) and brings you back to this page.",
            "See password policy for master password on the last page."
    };

    public static final String[] DB_LOGIN_VIEW_TUTORIAL = {
            "You are required to enter a name and password for the database file.",
            "The save location is the path to the directory in which the database file (will be created)/(is stored).",
            "The database password is used to encrypt the database file using AES256.",
            "This password along with your master password is then used to encrypt your data using AES256.",
            "See password policy for database password on the last page."
    };
    
    public static final String[] SYNC_VIEW_TUTORIAL = {
            "You can sync your data to other devices using QRCodes here.",
            "Based on the amount of saved user credentials in the database table you are logged into, the number of QRCodes will vary.",
            "You can load (load all the saved user data from the database table and converts them to QRCodes), export (save the QRCodes into a folder) and import (load QRCodes from device) QRCodes.",
            "Use the app on your phone to scan the QRCodes one by one.",
            "The QRCodes encode encrypted data, so there is no chance of any data leak."
            
    };

    public static final String[] DATA_VIEW_TUTORIAL = {
            "Search for tags in the search box to view all the entries under that tag.",
            "You can edit, copy and delete a specific entry.",
            "Add new entries by clicking the 'Add New' button."

    };


    public static final String[] ENTRY_VIEW_TUTORIAL = {
            "Provide a tag to the entry (username, password) you want to save.",
            "Tags are used to group entries to make them easily discoverable.",
            "A tag can be anything (website name, date, type of the account, any text)."
    };

    public static final String[] PG_VIEW_TUTORIAL = {
            "Use the password generator to generate unique and unguessable passwords.",
            "The bar under the generated password represents strength of that password.",
            "Strength of a password is evaluated considering the following aspects: length, entropy and diversity."
    };

    public static final String[] PASSWORD_POLICY = {
            "A master password or database password must contain at least 8 characters.",
            "The password must contain at least 4 digits."

    };

    public static final String[][] TUTORIALS = {
            UNLOCK_VIEW_TUTORIAL, DB_LOGIN_VIEW_TUTORIAL, DATA_VIEW_TUTORIAL,ENTRY_VIEW_TUTORIAL ,SYNC_VIEW_TUTORIAL,PG_VIEW_TUTORIAL,PASSWORD_POLICY
    };

    public static final String[] VIEW_TITLES = {"Access Locker","Database Login","Data","Entry","Sync","Password Generator","Password Policy"};
}
