package elements;

import constants.AutoLoad;
import panels.EntryPanel;

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

            AutoLoad.createTutorial=false;
        }else if(name.equals("View") && AutoLoad.viewTutorial){

            AutoLoad.viewTutorial=false;
        }else if(name.equals("Entry") && AutoLoad.insertTutorial){

            AutoLoad.insertTutorial=false;
        }else if(name.equals("Access") && AutoLoad.accessTutorial){

            AutoLoad.accessTutorial=false;
        }

    }
}
