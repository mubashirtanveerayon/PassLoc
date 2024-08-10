package controllers;

import constants.EntryModel;
import elements.PanelActionHandler;

import encryption.SecureEncryption;
import sqlite.SQLiteCommandGenerator;
import sqlite.SQLiteConnection;
import panels.EntryPanel;
import constants.AutoLoad;
import toast.JToast;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;

public class EntryPanelActionHandler implements PanelActionHandler {
    EntryPanel entryPanel;



    public EntryPanelActionHandler(EntryPanel entryPanel){
        this.entryPanel = entryPanel;
        this.entryPanel.setComponentListener(this);
    }


    @Override
    public void actionPerformed(ActionEvent e) {

    }

    @Override
    public void onMouseClicked(MouseEvent e) {

    }

    @Override
    public void onActionPerformed(ActionEvent e) {

        if(e.getSource() == entryPanel.tagField){
            entryPanel.usernameField.requestFocus();
        }else if(e.getSource() == entryPanel.usernameField){
            entryPanel.passwordArea.requestFocus();
        }else if(e.getSource() == entryPanel.actionButton){

            if(AutoLoad.entryModel == null){
                if(JToast.isAvailable())JToast.sendToastMessage(JToast.TYPE.ERROR,"Action unavailable","No database is available right now",JToast.HORIZONTAL_POSITION.RIGHT,JToast.VERTICAL_POSITION.BOTTOM);
                return;
            }

            String tag=entryPanel.tagField.getText();
            String username = entryPanel.usernameField.getText();
            String password = entryPanel.passwordArea.getText();

            if(tag.isEmpty() || username.isEmpty() || password.isEmpty()){

                if(JToast.isAvailable())JToast.sendToastMessage(JToast.TYPE.ERROR,"You cannot leave any field empty",JToast.HORIZONTAL_POSITION.RIGHT,JToast.VERTICAL_POSITION.BOTTOM);

                return;
            }

            SQLiteCommandGenerator databaseCommunication = SQLiteCommandGenerator.getInstance();
            if(databaseCommunication == null)return;
            Connection connection = SQLiteConnection.getConnection();
            try{
                if(connection == null || connection.isClosed())return;
                PreparedStatement statement = null;
                SecureEncryption textEncryptor = databaseCommunication.getTextEncryptor();
                if(AutoLoad.entryModel.toInsert){
                    statement = connection.prepareStatement(databaseCommunication.insertIntoDataTable());
                    statement.setString(1,textEncryptor.encode(tag));
                    statement.setString(2,textEncryptor.encode(username));
                    statement.setString(3,textEncryptor.encode(password));
                }else{
                    statement = connection.prepareStatement(databaseCommunication.updateEntry());
                    statement.setString(1,textEncryptor.encode(username));
                    statement.setString(2,textEncryptor.encode(password));
                    statement.setString(3,AutoLoad.entryModel.id);

                    AutoLoad.entryModel = EntryModel.createInsertModel();
                    entryPanel.loadContent();
                }


                statement.execute();
                statement.close();

                entryPanel.usernameField.setText("");
                entryPanel.passwordArea.setText("");

                if(JToast.isAvailable())

                    JToast.sendToastMessage(JToast.TYPE.SUCCESS,"Data push successful", "Search by the same tag in the view panel to see changes.",JToast.HORIZONTAL_POSITION.RIGHT,JToast.VERTICAL_POSITION.BOTTOM);


                entryPanel.usernameField.requestFocus();

            }catch (Exception ex){
//                JOptionPane.showMessageDialog(null,ex.toString(),"Might be the result of incorrect pin",JOptionPane.ERROR_MESSAGE);
                if(JToast.isAvailable())
                    JToast.sendToastMessage(JToast.TYPE.ERROR,ex.getMessage(),JToast.HORIZONTAL_POSITION.RIGHT,JToast.VERTICAL_POSITION.BOTTOM);


                ex.printStackTrace();
            }






        }

    }
}
