package controllers;

import com.formdev.flatlaf.extras.FlatSVGIcon;
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
                accessPanel.showPassword.normalIcon=new FlatSVGIcon(getClass().getResource("/res/hide.svg"));
                accessPanel.showPassword.hoverIcon=new FlatSVGIcon(getClass().getResource("/res/hide_hover.svg"));

                showingPassword=false;

                accessPanel.passwordField.setEchoChar((char)8226);

            }else{
                accessPanel.showPassword.normalIcon=new FlatSVGIcon(getClass().getResource("/res/show.svg"));
                accessPanel.showPassword.hoverIcon=new FlatSVGIcon(getClass().getResource("/res/show_hover.svg"));
                showingPassword=true;
                accessPanel.passwordField.setEchoChar((char)0);
            }
            accessPanel.showPassword.setIcon(accessPanel.showPassword.hoverIcon);

        }else if(source == accessPanel.showPin){
            if(showingPin){
                accessPanel.showPin.normalIcon=new FlatSVGIcon(getClass().getResource("/res/hide.svg"));
                accessPanel.showPin.hoverIcon=new FlatSVGIcon(getClass().getResource("/res/hide_hover.svg"));

                showingPin=false;
                accessPanel.pinField.setEchoChar((char)8226);

            }else{
                accessPanel.showPin.normalIcon=new FlatSVGIcon(getClass().getResource("/res/show.svg"));
                accessPanel.showPin.hoverIcon=new FlatSVGIcon(getClass().getResource("/res/show_hover.svg"));
                showingPin=true;
                accessPanel.pinField.setEchoChar((char)0);
            }
            accessPanel.showPin.setIcon(accessPanel.showPin.hoverIcon);

        }else if(source == accessPanel.accessButton){
            String path = accessPanel.filePathField.getText();

            String password = accessPanel.passwordField.getText();
            char[] pin = accessPanel.pinField.getPassword();

            if (!new File(path).exists() ) {
                if(JToast.isAvailable())
                    JToast.sendToastMessage(JToast.TYPE.ERROR,"No such file exists",JToast.HORIZONTAL_POSITION.RIGHT, JToast.VERTICAL_POSITION.BOTTOM);
                return;
            }else if(password.length() < AutoLoad.PASSWORD_LENGTH_MINIMUM ){
                if(JToast.isAvailable())
                    JToast.sendToastMessage(JToast.TYPE.ERROR,"Password must be at least "+AutoLoad.PASSWORD_LENGTH_MINIMUM+" characters long",JToast.HORIZONTAL_POSITION.RIGHT,JToast.VERTICAL_POSITION.BOTTOM);
                return;
            }else if(pin.length<AutoLoad.PIN_LENGTH_MINIMUM || pin.length>AutoLoad.PIN_LENGTH_MAXIMUM){
                if(JToast.isAvailable())
                    JToast.sendToastMessage(JToast.TYPE.ERROR,"Pin must be in between "+AutoLoad.PIN_LENGTH_MINIMUM+" and "+AutoLoad.PIN_LENGTH_MAXIMUM+" digits.",JToast.HORIZONTAL_POSITION.RIGHT,JToast.VERTICAL_POSITION.BOTTOM);
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
//                JOptionPane.showMessageDialog(null,ex.toString(),"Incorrect password",JOptionPane.ERROR_MESSAGE);

                if(JToast.isAvailable())
                    JToast.sendToastMessage(JToast.TYPE.ERROR,"Probably due to incorrect password",ex.getMessage(),JToast.HORIZONTAL_POSITION.RIGHT,JToast.VERTICAL_POSITION.BOTTOM);

                return;
            }

            accessPanel.filePathField.setText("");
            accessPanel.passwordField.setText("");
            accessPanel.pinField.setText("");


            AutoLoad.entryModel = EntryModel.createInsertModel();
            layout.show("View");


            if(JToast.isAvailable())
                JToast.sendToastMessage(JToast.TYPE.SUCCESS,"Database connection established",JToast.HORIZONTAL_POSITION.RIGHT,JToast.VERTICAL_POSITION.BOTTOM);


        }else if(source== accessPanel.filePathField){
            accessPanel.passwordField.requestFocus();
            accessPanel.passwordField.setCaretPosition(accessPanel.passwordField.getPassword().length);
        }else if(source == accessPanel.passwordField){
            accessPanel.pinField.requestFocus();
            accessPanel.pinField.setCaretPosition(accessPanel.pinField.getPassword().length);
        }

    }
}
