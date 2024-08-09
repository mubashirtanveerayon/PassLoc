package controllers;

import constants.AutoLoad;
import constants.EntryModel;
import elements.ContentLayout;
import elements.PanelActionHandler;
import panels.CreatePanel;

import sqlite.SQLiteCommandGenerator;
import sqlite.SQLiteConnection;
import toast.JToast;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;

public class CreatePanelActionHandler implements PanelActionHandler {

    CreatePanel createPanel;
    boolean showingPassword,showingPin;

    ContentLayout layout;


    public CreatePanelActionHandler(CreatePanel panel, ContentLayout contentLayout){


        createPanel=panel;


        createPanel.setComponentListener(this);

        layout = contentLayout;

    }

    @Override
    public void onMouseClicked(MouseEvent e) {

    }

    @Override
    public void onActionPerformed(ActionEvent e) {
        Object source = e.getSource();
        if(source == createPanel.showPassword){
            if(showingPassword){
                createPanel.showPassword.normalIcon=new ImageIcon(getClass().getResource("/res/hide.png"));
                createPanel.showPassword.hoverIcon=new ImageIcon(getClass().getResource("/res/hide_hover.png"));

                showingPassword=false;

                createPanel.passwordField.setEchoChar((char)8226);

            }else{
                createPanel.showPassword.normalIcon=new ImageIcon(getClass().getResource("/res/show.png"));
                createPanel.showPassword.hoverIcon=new ImageIcon(getClass().getResource("/res/show_hover.png"));
                showingPassword=true;
                createPanel.passwordField.setEchoChar((char)0);
            }
            createPanel.showPassword.setIcon(createPanel.showPassword.hoverIcon);

        }else if(source == createPanel.showPin){
            if(showingPin){
                createPanel.showPin.normalIcon=new ImageIcon(getClass().getResource("/res/hide.png"));
                createPanel.showPin.hoverIcon=new ImageIcon(getClass().getResource("/res/hide_hover.png"));

                showingPin=false;
                createPanel.pinField.setEchoChar((char)8226);

            }else{
                createPanel.showPin.normalIcon=new ImageIcon(getClass().getResource("/res/show.png"));
                createPanel.showPin.hoverIcon=new ImageIcon(getClass().getResource("/res/show_hover.png"));
                showingPin=true;
                createPanel.pinField.setEchoChar((char)0);
            }
            createPanel.showPin.setIcon(createPanel.showPin.hoverIcon);

        }else if(source == createPanel.openFolder){
            JFileChooser fileChooser = new JFileChooser(AutoLoad.DATABASE_DIRECTORY);
            fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            int response = fileChooser.showSaveDialog(null);
            if(response == JFileChooser.APPROVE_OPTION){
                String fullPath = fileChooser.getSelectedFile().getPath();
                createPanel.saveDirectoryField.setText(fullPath+ File.separator);
            }

        }else if(source == createPanel.createButton){
            String fullPath = createPanel.saveDirectoryField.getText()+createPanel.nameField.getText();
            if(new File(fullPath).exists()){

//                JOptionPane.showMessageDialog(null,"Cannot create database","File already exists",JOptionPane.ERROR_MESSAGE);
                if(JToast.isAvailable())
                    JToast.sendToastMessage(JToast.TYPE.ERROR,"Cannot use this name",JToast.HORIZONTAL_POSITION.RIGHT,JToast.VERTICAL_POSITION.BOTTOM);

                return;
            }

            int pinLength = createPanel.pinField.getPassword().length;
            String password = createPanel.passwordField.getText();
            if(pinLength<AutoLoad.PIN_LENGTH_MINIMUM || pinLength>AutoLoad.PIN_LENGTH_MAXIMUM || password.length()<AutoLoad.PASSWORD_LENGTH_MINIMUM){
//                JOptionPane.showMessageDialog(null,"Password must be at least "+AutoLoad.PASSWORD_LENGTH_MINIMUM+" characters long.\nPin must be in between "+AutoLoad.PIN_LENGTH_MINIMUM+" and "+AutoLoad.PIN_LENGTH_MAXIMUM+" digits.","Invalid password or pin",JOptionPane.ERROR_MESSAGE);

                if(JToast.isAvailable())
                    JToast.sendToastMessage(JToast.TYPE.INFO,"Password must be at least "+AutoLoad.PASSWORD_LENGTH_MINIMUM+" characters long.\nPin must be in between "+AutoLoad.PIN_LENGTH_MINIMUM+" and "+AutoLoad.PIN_LENGTH_MAXIMUM+" digits.",JToast.HORIZONTAL_POSITION.RIGHT,JToast.VERTICAL_POSITION.BOTTOM);

                return;
            }


            try{
                SQLiteConnection.disconnect();
                SQLiteConnection.establishConnection(fullPath,password);


                int[] seeds = AutoLoad.getSeeds(createPanel.pinField.getPassword());
                SQLiteCommandGenerator.close();
                SQLiteCommandGenerator.instantiate(seeds[0],seeds[1]);

                SQLiteCommandGenerator commandGenerator=SQLiteCommandGenerator.getInstance();
                Connection connection = SQLiteConnection.getConnection();
                PreparedStatement statement=connection.prepareStatement(commandGenerator.createDataTable());
                statement.execute();
                statement.close();

            }catch(Exception ex){
                ex.printStackTrace();
                if(JToast.isAvailable())
                    JToast.sendToastMessage(JToast.TYPE.ERROR,ex.getMessage(),JToast.HORIZONTAL_POSITION.RIGHT,JToast.VERTICAL_POSITION.BOTTOM);
                return;
            }

            createPanel.nameField.setText("");
            createPanel.passwordField.setText("");
            createPanel.pinField.setText("");
            AutoLoad.entryModel = EntryModel.createInsertModel();
            layout.show("Entry");

            if(JToast.isAvailable())
                JToast.sendToastMessage(JToast.TYPE.SUCCESS,"Database initialized",JToast.HORIZONTAL_POSITION.RIGHT,JToast.VERTICAL_POSITION.BOTTOM);



        }
    }
}
