package sqlite;


import constants.AutoLoad;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SQLiteConnection {

    private static Connection sqLiteConnection;

    public static void establishConnection(String databasePath, String password) throws Exception{
        if(sqLiteConnection != null && !sqLiteConnection.isClosed()){
            return;
        }


        sqLiteConnection=DriverManager.getConnection("jdbc:sqlite:"+ databasePath,"",password);
    }

    public static void disconnect() throws SQLException {

        if (sqLiteConnection == null || sqLiteConnection.isClosed())return ;
        sqLiteConnection.close();


    }

    public static Connection getConnection(){
        return sqLiteConnection;
    }


}
