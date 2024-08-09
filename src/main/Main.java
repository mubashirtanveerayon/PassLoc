package main;

import javax.swing.*;


import com.formdev.flatlaf.IntelliJTheme;
import constants.AutoLoad;
import controllers.AccessPanelActionHandler;
import controllers.CreatePanelActionHandler;
import controllers.EntryPanelActionHandler;
import controllers.ViewPanelActionHandler;
import elements.*;
import elements.Label;

import panels.*;
import sqlite.SQLiteCommandGenerator;
import sqlite.SQLiteConnection;
import toast.JToast;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.ConcurrentModificationException;
import java.util.Enumeration;

public class Main extends JFrame implements PanelActionHandler, WindowListener {


    JPanel contentPanel;
    JLabel titleLabel;

    SidePanel sidePanel;
    CreatePanel createPanel;
    AccessPanel accessPanel;

    ViewPanel viewPanel;

    EntryPanel entryPanel;

    ContentLayout layout;
    private Main() {

        super(AutoLoad.VERSION);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setPreferredSize(AutoLoad.DEFAULT_APP_DIMENSION);

        RoundedPanel titlePanel = new RoundedPanel(10,AutoLoad.THEME_COLOR);



        titleLabel = new JLabel("Create");
        titleLabel.setFont(AutoLoad.BOLD_FONT.deriveFont(45.0f));
        titleLabel.setForeground(AutoLoad.THEME_BUTTON_TEXT_COLOR);

        titleLabel.setHorizontalAlignment(JLabel.CENTER);

        titlePanel.add(titleLabel);

        Container container=getContentPane();
        contentPanel = new JPanel();

        entryPanel = new EntryPanel();

        layout = new ContentLayout(contentPanel,titleLabel,entryPanel);
        contentPanel.setLayout(layout);


        sidePanel = new SidePanel();


        container.add(titlePanel,BorderLayout.NORTH);
        container.add(sidePanel,BorderLayout.WEST);


        createPanel = new CreatePanel();
        contentPanel.add(createPanel,"Create");
        viewPanel = new ViewPanel();
        contentPanel.add(viewPanel,"View");


        contentPanel.add(entryPanel,"Entry");

        accessPanel = new AccessPanel();
        contentPanel.add(accessPanel,"Access");





        container.add(contentPanel,BorderLayout.CENTER);


        sidePanel.setComponentListener(this);

        pack();


        EntryPanelActionHandler entryPanelActionHandler=new EntryPanelActionHandler(entryPanel);
        ViewPanelActionHandler viewPanelActionHandler=new ViewPanelActionHandler(viewPanel,layout);
        CreatePanelActionHandler createPanelActionHandler=new CreatePanelActionHandler(createPanel,layout);
        AccessPanelActionHandler accessPanelActionHandler=new AccessPanelActionHandler(accessPanel,layout);
    }

    public static void main(String[] args) {
        IntelliJTheme.setup(Main.class.getResourceAsStream("/themes/oceanic.theme.json"));

        AutoLoad.compile();

        Thread appThread= new Thread(new Runnable() {
            @Override
            public void run() {



                Main window = new Main();
                window.setVisible(true);

                JToast.setFrame(window);

            }
        });
        appThread.start();
    }


    @Override
    public void onMouseClicked(MouseEvent e) {
        Label button = (Label)e.getSource();

        if(button == sidePanel.createLabel){
            layout.show("Create");
        }else if(button == sidePanel.accessLabel){
            layout.show("Access");
        } else if (button == sidePanel.viewLabel) {
            layout.show("View");
        }else if(button == sidePanel.entryLabel){
            layout.show("Entry");
        }else if(button == sidePanel.exitLabel){
            try {
                SQLiteConnection.disconnect();
            } catch (SQLException ex) {
                ex.printStackTrace();
                throw new RuntimeException(ex);
            }
            System.exit(0);
        }
    }

    @Override
    public void onActionPerformed(ActionEvent e) {

    }

    @Override
    public void windowOpened(WindowEvent e) {

    }

    @Override
    public void windowClosing(WindowEvent e) {

    }

    @Override
    public void windowClosed(WindowEvent e) {
        try{
            SQLiteCommandGenerator.close();
            SQLiteConnection.disconnect();

        }catch(Exception ex){
            ex.printStackTrace();
        }
    }

    @Override
    public void windowIconified(WindowEvent e) {

    }

    @Override
    public void windowDeiconified(WindowEvent e) {

    }

    @Override
    public void windowActivated(WindowEvent e) {

    }

    @Override
    public void windowDeactivated(WindowEvent e) {

    }
}
