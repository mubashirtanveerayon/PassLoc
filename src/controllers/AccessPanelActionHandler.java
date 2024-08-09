package controllers;

import constants.AutoLoad;
import constants.EntryModel;
import elements.ContentLayout;
import elements.PanelActionHandler;
import panels.AccessPanel;

import sqlite.SQLiteCommandGenerator;
import sqlite.SQLiteConnection;
import toast.JToast;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.io.File;

public class AccessPanelActionHandler implements PanelActionHandler {
    
    AccessPanel accessPanel;
    boolean showingPassword = false,showingPin=false;

    ContentLayout layout;

    
    public AccessPanelActionHandler(AccessPanel panel, ContentLayout contentLayout){
        accessPanel = panel;
        accessPanel.setComponentListener(this);
        this.layout = contentLayout;

    }
    
    
    
    @Override
    public void onMouseClicked(MouseEvent e) {
        
    }

    @Override
    public void onActionPerformed(ActionEvent e) {
        Object source = e.getSource();
        if(source == accessPanel.openFile){
            JFileChooser fileChooser = new JFileChooser(AutoLoad.DATABASE_DIRECTORY);
            fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            int response = fileChooser.showSaveDialog(null);
            if(response == JFileChooser.APPROVE_OPTION){
                String fullPath = fileChooser.getSelectedFile().getPath();
                accessPanel.filePathField.setText(fullPath);
            }

        } else if(source == accessPanel.showPassword){
            if(showingPassword){
                accessPanel.showPassword.normalIcon=new ImageIcon(getClass().getResource("/res/hide.png"));
                accessPanel.showPassword.hoverIcon=new ImageIcon(getClass().getResource("/res/hide_hover.png"));

                showingPassword=false;

                accessPanel.passwordField.setEchoChar((char)8226);

            }else{
                accessPanel.showPassword.normalIcon=new ImageIcon(getClass().getResource("/res/show.png"));
                accessPanel.showPassword.hoverIcon=new ImageIcon(getClass().getResource("/res/show_hover.png"));
                showingPassword=true;
                accessPanel.passwordField.setEchoChar((char)0);
            }
            accessPanel.showPassword.setIcon(accessPanel.showPassword.hoverIcon);

        }else if(source == accessPanel.showPin){
            if(showingPin){
                accessPanel.showPin.normalIcon=new ImageIcon(getClass().getResource("/res/hide.png"));
                accessPanel.showPin.hoverIcon=new ImageIcon(getClass().getResource("/res/hide_hover.png"));

                showingPin=false;
                accessPanel.pinField.setEchoChar((char)8226);

            }else{
                accessPanel.showPin.normalIcon=new ImageIcon(getClass().getResource("/res/show.png"));
                accessPanel.showPin.hoverIcon=new ImageIcon(getClass().getResource("/res/show_hover.png"));
                showingPin=true;
                accessPanel.pinField.setEchoChar((char)0);
            }
            accessPanel.showPin.setIcon(accessPanel.showPin.hoverIcon);

        }else if(source == accessPanel.accessButton){
            String path = accessPanel.filePathField.getText();

            String password = accessPanel.passwordField.getText();
            char[] pin = accessPanel.pinField.getPassword();

            if (!new File(path).exists() || password.length() < AutoLoad.PASSWORD_LENGTH_MINIMUM || pin.length<AutoLoad.PIN_LENGTH_MINIMUM || pin.length>AutoLoad.PIN_LENGTH_MAXIMUM) {
                return;
            }

            try{
                SQLiteConnection.disconnect();
                SQLiteConnection.establishConnection(path,password);
                SQLiteCommandGenerator.close();
                int[] seeds=AutoLoad.getSeeds(pin);
                SQLiteCommandGenerator.instantiate(seeds[0],seeds[1]);

            }catch(Exception ex){
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null,ex.toString(),"Incorrect password",JOptionPane.ERROR_MESSAGE);
                return;
            }

            accessPanel.filePathField.setText("");
            accessPanel.passwordField.setText("");
            accessPanel.pinField.setText("");


            AutoLoad.entryModel = EntryModel.createInsertModel();
            layout.show("View");


            if(JToast.isAvailable())
                JToast.sendToastMessage(JToast.TYPE.SUCCESS,"Database connection established",JToast.HORIZONTAL_POSITION.RIGHT,JToast.VERTICAL_POSITION.BOTTOM);


        }

    }
}
