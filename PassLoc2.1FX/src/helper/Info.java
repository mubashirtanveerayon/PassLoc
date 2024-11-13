package helper;

import java.io.File;

public class Info {

    public static final String DB_SAVE_LOCATION = System.getProperty("user.home")+ File.separator + "passloc";



    public static final String[] UNLOCK_VIEW_TUTORIAL = {

            "A master password is required to unlock any database file on your device.",
            "Master password is used to determine which data should be fetched and decrypted from the database.",
            "This password is used to uniquely identify a database table, in which the entries are stored.",
            "Therefore, it allows you to use different data tables in a single database file with multiple master passwords.",
            "However, it is appreciated to use one database file with a single master password.",
            "The lock button in the top bar locks you out of database (if a connection had been established) and brings you back to this page.",
            "Master password must contain at least 8 characters including 4 digits."
    };

    public static final String[] DB_LOGIN_VIEW_TUTORIAL = {
            "You are required to enter a name and password for the database file.",
            "The save location is the path to the directory in which the database file (will be created)/(is stored).",
            "The database password is used to encrypt the database file using AES256.",

            "Database password must contain at least 8 characters including 4 digits."
    };

    public static final String[] ABOUT = {
            "PassLoc is an open-source password manager application that uses sql-cipher to store data on your device.",
            "Needless to say that all the data stored in the database are encrypted with strong and reliable encryption.",
            "PassLoc does not store any data on the cloud. Hence, this app does not require an internet connection to function.",
            "The source code can be found at github.com/mubashirtanveerayon/PassLoc ."
    };

    public static final String[] ENCRYPTION = {
            "PassLoc uses AES256 as the encryption algorithm. The sql-cipher library also relies on AES256.",
            "Your data is first encrypted using a special combination of the master and database password. Next, sql-cipher uses just the database password to encrypt the whole database file.",
            "When you add a new entry, the tag, username and password are encrypted individually using that special combination of passwords and then the data is pushed into the database along with a  unique id that is kept hidden from the user.",
            "If someone gains access to the password-protected database file, they will not be able to decrypt your data without your master password.",
            "This also means that if you happen to forget any of your passwords the database file becomes unusable."
    };
    
    public static final String[] SYNC_VIEW_TUTORIAL = {
            "Syncing(importing or exporting data) is done primarily using QR codes.",
            "Based on the amount of saved user credentials in the database table you are logged into, the number of QRCodes will vary.",
            "You can generate (load all the saved user data from the database table and converts them to QRCodes), export (save the QRCodes into a folder) and import (import QR code images) QRCodes.",
            "To import or export data you are needed to log in.",
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


    public static final String[][] TUTORIALS = {
            UNLOCK_VIEW_TUTORIAL, DB_LOGIN_VIEW_TUTORIAL, DATA_VIEW_TUTORIAL,ENTRY_VIEW_TUTORIAL ,SYNC_VIEW_TUTORIAL,PG_VIEW_TUTORIAL,ABOUT,ENCRYPTION
    };

    public static final String[] VIEW_TITLES = {"Access Locker","Database Login","Data","Entry","Sync","Password Generator","About","Encryption"};


    public static final int MIN_PASSWORD_LENGTH = 4;
}
