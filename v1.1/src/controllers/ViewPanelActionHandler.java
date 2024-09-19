package controllers;

import constants.AutoLoad;
import constants.EntryModel;
import elements.ContentLayout;
import elements.PanelActionHandler;
import elements.viewtable.TableActionEvent;
import encryption.SecureEncryption;
import panels.EntryPanel;
import panels.ViewPanel;
import sqlite.SQLiteCommandGenerator;
import sqlite.SQLiteConnection;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;

public class ViewPanelActionHandler implements PanelActionHandler, TableActionEvent {
    ViewPanel viewPanel;

    ContentLayout layout;

    public ViewPanelActionHandler(ViewPanel viewPanel, ContentLayout contentLayout){

        this.viewPanel = viewPanel;
        viewPanel.setComponentListener(this);

        layout = contentLayout;
    }


    @Override
    public void onMouseClicked(MouseEvent e) {

    }

    @Override
    public void onActionPerformed(ActionEvent e) {
        if(e.getSource() == viewPanel.tagField || e.getSource() == viewPanel.searchButton){

            viewPanel.removeAllRows();

            String searchedTag = viewPanel.tagField.getText();
            if(searchedTag.isEmpty())return;
            ArrayList<String[] > data = loadData().get(searchedTag);

            if(data!=null){
                viewPanel.putDataRows(data);
                data.clear();
            }


            data=null;
        }
    }

    public HashMap<String,ArrayList<String[]> > loadData(){
        HashMap<String,ArrayList<String[]> >data = new HashMap<>();
        SQLiteCommandGenerator databaseCommunication = SQLiteCommandGenerator.getInstance();
        if(databaseCommunication == null)return null;

        Connection connection = SQLiteConnection.getConnection();
        try {
            if (connection == null || connection.isClosed()) return null;





            PreparedStatement statement = connection.prepareStatement(databaseCommunication.getAllData());
            ResultSet result = statement.executeQuery();
            SecureEncryption textEncryptor = databaseCommunication.getTextEncryptor();
            while(result.next()){

                String id = (result.getString(1));
                String tag = textEncryptor.decode( result.getString(2));

                String username = textEncryptor.decode( result.getString(3));
                String password = textEncryptor.decode( result.getString(4));

                String [] entry = new String[]{id,username,password,tag};

                ArrayList<String[]>entries;
                if(data.containsKey(tag)){
                    entries=data.get(tag);
                    entries.add(entry);
                }else{
                    entries = new ArrayList<>();
                    entries.add(entry);
                    data.put(tag,entries);
                }






            }
            result.close();
            statement.close();


        }catch(Exception ex){
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null,"Could not load data.","Might be the result of incorrect pin",JOptionPane.ERROR_MESSAGE);
            return null;
        }
        return data;
    }

    @Override
    public void onDelete(int row) {
        if(viewPanel.dataTable.getSelectedRow()<0)return;
        String id = viewPanel.tableModel.getValueAt(viewPanel.dataTable.getSelectedRow(),0).toString();


        Connection connection = SQLiteConnection.getConnection();
        SQLiteCommandGenerator databaseCommunication = SQLiteCommandGenerator.getInstance();
        try{
            if(connection == null || connection.isClosed() || databaseCommunication == null)return;
            PreparedStatement statement = connection.prepareStatement(databaseCommunication.deleteFromDataTable());
            statement.setString(1,id);
            statement.execute();
            statement.close();
        }catch(Exception ex){
            ex.printStackTrace();
            return;
        }



        viewPanel.ignoreTableAction=true;
        viewPanel.tableModel.removeRow(viewPanel.dataTable.getSelectedRow());
        viewPanel.ignoreTableAction=false;

    }

    @Override
    public void onEdit(int row) {
        if(viewPanel.dataTable.getSelectedRow()<0)return;
        String username=null,password=null,tag=null, id = viewPanel.tableModel.getValueAt(viewPanel.dataTable.getSelectedRow(),0).toString();

        HashMap<String,ArrayList<String[]>> data = loadData();

        for(ArrayList<String[]> entries:data.values()) {
            boolean found=false;
            for (String[] entry : entries) {
                if (entry[0].equals(id)) {
                    username = entry[1];
                    password = entry[2];
                    tag = entry[3];
                    found=true;
                    break;
                }
            }
            if(found)break;
        }

        data.clear();
        data=null;
        AutoLoad.entryModel = new EntryModel(id,username,password,tag,false);

        layout.show("Entry");

    }

    @Override
    public void onCopyUsername(int row) {
        if(viewPanel.dataTable.getSelectedRow()<0)return;
        String content = viewPanel.dataTable.getValueAt(viewPanel.dataTable.getSelectedRow(),1).toString();
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(content),null);



    }

    @Override
    public void onCopyPassword(int row) {
        if(viewPanel.dataTable.getSelectedRow()<0)return;
        String id = viewPanel.tableModel.getValueAt(viewPanel.dataTable.getSelectedRow(),0).toString();

        HashMap<String,ArrayList<String[]>> data = loadData();

        for(ArrayList<String[]> entries:data.values()) {
            boolean found=false;
            for (String[] entry : entries) {
                if (entry[0].equals(id)) {
                    Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(entry[2]),null);
                    found=true;
                    break;
                }
            }
            if(found)break;
        }
        data.clear();
        data=null;


    }

//    @Override
//    public void valueChanged(ListSelectionEvent e) {
//        if(viewPanel.ignoreTableAction || viewPanel.dataTable.getSelectedRow()<0)return;
//        String id = viewPanel.tableModel.getValueAt(viewPanel.dataTable.getSelectedRow(),0).toString();
//        String content = "";
//
//        for(String[] entry:entries){
//            if(entry[0].equals(id)){
//                content=entry[viewPanel.dataTable.getSelectedColumn()];
//            }
//        }
//
//        for(String[] entry: entries){
//            if(entry[0].equals(id)){
//                Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(content),null);
//                break;
//            }
//        }
//
//    }
}
