package elements.viewtable;

import elements.RoundedButton;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ViewEditPanel extends JPanel {

    public RoundedButton copyUsernameButton,copyPasswordButton, editButton,deleteButton;


    public ViewEditPanel(){

//        ((FlowLayout)getLayout()).setHgap(10);
        setOpaque(true);
        initComponents();


    }

    private void initComponents() {


        copyUsernameButton = new RoundedButton("/res/username_hover.png", "/res/username.png",20);
        copyPasswordButton = new RoundedButton("/res/password_hover.png", "/res/password.png",20);
        editButton = new RoundedButton("/res/edit_black.png", "/res/edit_white.png",20);
        deleteButton = new RoundedButton("/res/delete_black.png", "/res/delete_white.png",20);


        copyUsernameButton.setToolTipText("Copy username");
        copyPasswordButton.setToolTipText("Copy password");
        editButton.setToolTipText("Edit entry");
        deleteButton.setToolTipText("Delete entry");

        copyUsernameButton.setPreferredSize(new Dimension(40,40));
        copyPasswordButton.setPreferredSize(copyUsernameButton.getPreferredSize());
        editButton.setPreferredSize(copyUsernameButton.getPreferredSize());
        deleteButton.setPreferredSize(copyUsernameButton.getPreferredSize());

        add(copyUsernameButton);
        add(copyPasswordButton);
        add(editButton);
        add(deleteButton);

    }

    public void triggerEvent(TableActionEvent event, int row){

        ActionListener listener = new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                Object source = e.getSource();
                if(source == editButton)event.onEdit(row);
                else if(source == deleteButton)event.onDelete(row);
                else if(source == copyUsernameButton)event.onCopyUsername(row);
                else if(source == copyPasswordButton)event.onCopyPassword(row);
            }
        };

        copyUsernameButton.addActionListener(listener);
        copyPasswordButton.addActionListener(listener);

        editButton.addActionListener(listener);

        deleteButton.addActionListener(listener);
    }
}
