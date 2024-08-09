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


        copyUsernameButton = new RoundedButton("/res/username_hover.svg", "/res/username.svg",20);
        copyPasswordButton = new RoundedButton("/res/password_hover.svg", "/res/password.svg",20);
        editButton = new RoundedButton("/res/edit_black.svg", "/res/edit_white.svg",20);
        deleteButton = new RoundedButton("/res/delete_black.svg", "/res/delete_white.svg",20);


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
