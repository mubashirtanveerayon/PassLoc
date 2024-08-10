package elements;

import constants.AutoLoad;
import panels.EntryPanel;
import toast.JToast;

import javax.swing.*;
import javax.swing.text.AbstractDocument;
import java.awt.*;

public class ContentLayout extends CardLayout {

    JLabel titleLabel;
    JPanel contentPanel;

    EntryPanel entryPanel;

    public ContentLayout(JPanel panel, JLabel label, EntryPanel entryPanel){
        titleLabel = label;
        contentPanel=panel;
        this.entryPanel=entryPanel;
    }

    public void show(String name){
        super.show(contentPanel,name);
        titleLabel.setText(name);
        if(name.equals("Entry")){
            entryPanel.loadContent();
        }

        if(name.equals("Create") && AutoLoad.createTutorial){
            if(JToast.isAvailable()){
                JToast toast = JToast.getInstance();
                toast.setType(JToast.TYPE.INFO);
                toast.setTitle("Create a database to get started");
                toast.setBody("Give your database a name, and enter a new and unique password and pin!");
                toast.setPosition(JToast.HORIZONTAL_POSITION.CENTER, JToast.VERTICAL_POSITION.TOP);
                toast.setDuration(2);
                toast.setAnimationSpeed(0.5f);
                toast.send();
            }
            AutoLoad.createTutorial=false;
        }else if(name.equals("View") && AutoLoad.viewTutorial){

            if(JToast.isAvailable()){
                JToast toast = JToast.getInstance();
                toast.setType(JToast.TYPE.INFO);
                toast.setTitle("The view panel");
                toast.setBody("Search by tag to reveal an entire group!");
                toast.setPosition(JToast.HORIZONTAL_POSITION.CENTER, JToast.VERTICAL_POSITION.TOP);
                toast.setDuration(1.5f);
                toast.setAnimationSpeed(0.5f);
                toast.send();
            }

            AutoLoad.viewTutorial=false;
        }else if(name.equals("Entry") && AutoLoad.insertTutorial){
            if(JToast.isAvailable()){
                JToast toast = JToast.getInstance();
                toast.setType(JToast.TYPE.INFO);
                toast.setTitle("The entry panel");
                toast.setBody("Credentials are grouped into tags. Think of them as categories!");
                toast.setPosition(JToast.HORIZONTAL_POSITION.CENTER, JToast.VERTICAL_POSITION.TOP);
                toast.setDuration(1.5f);
                toast.setAnimationSpeed(0.5f);
                toast.send();
            }

            AutoLoad.insertTutorial=false;
        }else if(name.equals("Access") && AutoLoad.accessTutorial){
            if(JToast.isAvailable()){
                JToast toast = JToast.getInstance();
                toast.setType(JToast.TYPE.INFO);
                toast.setTitle("The access panel");
                toast.setBody("Access your databases here!");
                toast.setPosition(JToast.HORIZONTAL_POSITION.CENTER, JToast.VERTICAL_POSITION.TOP);
                toast.setDuration(1.5f);
                toast.setAnimationSpeed(0.7f);
                toast.send();
            }


            AutoLoad.accessTutorial=false;
        }

    }
}
