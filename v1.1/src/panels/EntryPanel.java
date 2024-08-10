package panels;

import constants.AutoLoad;
import constants.EntryModel;
import controllers.EntryPanelActionHandler;
import elements.PanelActionHandler;
import elements.RoundedButton;
import elements.RoundedPanel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class EntryPanel extends RoundedPanel implements ActionListener {

    public JTextField tagField,usernameField;
    public JTextArea passwordArea;
    public RoundedButton actionButton;

    PanelActionHandler handler;



    public EntryPanel(){
        super(10, AutoLoad.THEME_COLOR_LIGHT);
        setBorder(new EmptyBorder(10,10,10,10));

        tagField = new JTextField();
        tagField.setPreferredSize(new Dimension(800,60));
        tagField.setFont(AutoLoad.REGULAR_FONT.deriveFont(25.0f));
        tagField.setBorder(AutoLoad.getTitledBorer("Tag"));
        tagField.setBackground(AutoLoad.THEME_COLOR_LIGHT);
        tagField.setForeground(AutoLoad.THEME_TEXT_COLOR);
        tagField.setCaretColor(AutoLoad.THEME_COLOR);

        usernameField = new JTextField();
        usernameField.setPreferredSize(new Dimension(800,60));
        usernameField.setFont(AutoLoad.REGULAR_FONT.deriveFont(25.0f));
        usernameField.setBorder(AutoLoad.getTitledBorer("Username"));
        usernameField.setBackground(AutoLoad.THEME_COLOR_LIGHT);
        usernameField.setForeground(AutoLoad.THEME_TEXT_COLOR);
        usernameField.setCaretColor(AutoLoad.THEME_COLOR);

        passwordArea=new JTextArea();
        passwordArea.setFont(AutoLoad.REGULAR_FONT.deriveFont(25.0f));
        passwordArea.setLineWrap(true);
        passwordArea.setBackground(AutoLoad.THEME_COLOR_LIGHT);
        passwordArea.setForeground(AutoLoad.THEME_TEXT_COLOR);
//        passwordArea.addKeyListener(new KeyAdapter() {
//            @Override
//            public void keyPressed(KeyEvent e) {
//                if(e.getKeyChar() == '\n'|| e.getKeyChar()=='\t' )e.consume();
//            }
//        });
        passwordArea.setCaretColor(AutoLoad.THEME_COLOR);
        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setViewportView(passwordArea);
        scrollPane.setBorder(AutoLoad.getTitledBorer("Password"));
        scrollPane.setPreferredSize(new Dimension(800,300));

        actionButton = new RoundedButton("Done",false,35,20);
        actionButton.setPreferredSize(new Dimension(250,50));




        add(tagField);
        add(usernameField);
        add(scrollPane);
        add(actionButton);

        tagField.addActionListener(this);
        usernameField.addActionListener(this);





    }

    public void setComponentListener(PanelActionHandler actionHandler){
        handler=actionHandler;
        actionButton.addActionHandler(actionHandler);
    }


    @Override
    public void actionPerformed(ActionEvent e) {

        if(handler!= null)handler.onActionPerformed(e);
    }

    public void loadContent() {
        if(AutoLoad.entryModel == null)return;

        tagField.setText(AutoLoad.entryModel.tag);

        tagField.setEditable (AutoLoad.entryModel.toInsert);

        usernameField.setText(AutoLoad.entryModel.username);
        passwordArea.setText(AutoLoad.entryModel.password);

    }
}
