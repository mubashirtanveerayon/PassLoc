/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package helper;

import javafx.scene.layout.BorderPane;

/**
 *
 * @author admin
 */
public class State {
    
    
    public enum AppState{
        MASTER_LOGIN,DB_LOGIN,PG,SYNC,VIEW,LOGOUT,
        EDIT;
    }
    
    public static AppState currentState=AppState.MASTER_LOGIN;


    public static boolean showAllNotifications = true;

}
