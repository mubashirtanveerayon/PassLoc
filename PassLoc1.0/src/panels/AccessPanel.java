package panels;

import constants.AutoLoad;
import elements.PanelActionHandler;
import elements.RoundedButton;
import elements.RoundedPanel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;

public class AccessPanel extends RoundedPanel  {


    public JTextField filePathField;
    public JPasswordField passwordField,pinField;

    public RoundedButton openFile,showPassword,showPin;
    public RoundedButton accessButton;
//    public ImageIcon showIcon,hideIcon;
    public AccessPanel() {
        super(10, AutoLoad.THEME_COLOR_LIGHT);

        ((FlowLayout)getLayout()).setVgap(30);
        setBorder(new EmptyBorder(50,20,20,20));


//        JLabel titleLabel = new JLabel("Access Database");
//        titleLabel.setFont(AutoLoad.BOLD_FONT.deriveFont(45.0f));
//        titleLabel.setForeground(AutoLoad.THEME_TEXT_COLOR);
//
//        titleLabel.setPreferredSize(new Dimension(800,70));



        filePathField = new JTextField(AutoLoad.DATABASE_DIRECTORY);
        filePathField.setPreferredSize(new Dimension(750,60));
        filePathField.setFont(AutoLoad.REGULAR_FONT.deriveFont(25.0f));
        filePathField.setBorder(AutoLoad.getTitledBorer("Database File"));
        filePathField.setBackground(AutoLoad.THEME_COLOR_LIGHT);
        filePathField.setForeground(AutoLoad.THEME_TEXT_COLOR);
        filePathField.setCaretColor(AutoLoad.THEME_COLOR);


        passwordField = new JPasswordField();
        passwordField.setPreferredSize(new Dimension(750,60));
        passwordField.setFont(AutoLoad.REGULAR_FONT.deriveFont(25.0f));
        passwordField.setBorder(AutoLoad.getTitledBorer("Password"));
        passwordField.setBackground(AutoLoad.THEME_COLOR_LIGHT);
        passwordField.setForeground(AutoLoad.THEME_TEXT_COLOR);
        passwordField.setCaretColor(AutoLoad.THEME_COLOR);

        pinField = new JPasswordField();
        pinField.setPreferredSize(new Dimension(750,60));
        pinField.setFont(AutoLoad.REGULAR_FONT.deriveFont(25.0f));
        pinField.setBorder(AutoLoad.getTitledBorer("Pin"));
        pinField.setBackground(AutoLoad.THEME_COLOR_LIGHT);
        pinField.setForeground(AutoLoad.THEME_TEXT_COLOR);
        pinField.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
                if(AutoLoad.INVALID_PIN.indexOf(e.getKeyChar())>=0){

                    e.consume();
                }
            }

            @Override
            public void keyPressed(KeyEvent e) {

            }

            @Override
            public void keyReleased(KeyEvent e) {

            }
        });
        pinField.setCaretColor(AutoLoad.THEME_COLOR);

//        showIcon=new ImageIcon(getClass().getResource("/res/show.svg"));
//        hideIcon = new ImageIcon(getClass().getResource("/res/hide.svg"));

//        openFile = new JLabel(new ImageIcon(getClass().getResource("/res/open.svg")));
//
//        showPassword = new JLabel(hideIcon);
//        showPin = new JLabel(hideIcon);

        showPassword=new RoundedButton("/res/hide_hover.svg", "/res/hide.svg",50);
        showPin=new RoundedButton("/res/hide_hover.svg", "/res/hide.svg",50);
        openFile=new RoundedButton("/res/open_hover.svg", "/res/open.svg",50);
        showPassword.setPreferredSize(new Dimension(40,40));
        showPin.setPreferredSize(new Dimension(40,40));
        openFile.setPreferredSize(new Dimension(40,40));

        accessButton = new RoundedButton("Access",false,35,20);
        accessButton.setPreferredSize(new Dimension(250,50));

        JPanel accessPanel = new JPanel();
        accessPanel.setBackground(AutoLoad.THEME_COLOR_LIGHT);
        accessPanel.setBorder(new EmptyBorder(50,0,30,0));

//        add(titleLabel);
        add(filePathField);
        add(openFile);
        add(passwordField);
        add(showPassword);
        add(pinField);
        add(showPin);
        accessPanel.add(accessButton);
        add(accessPanel);




    }


    public void setComponentListener(PanelActionHandler eventHandler){
        openFile.addActionHandler(eventHandler);
        showPassword.addActionHandler(eventHandler);
        showPin.addActionHandler(eventHandler);
        accessButton.addActionHandler(eventHandler);
    }

//    @Override
//    public void mouseClicked(MouseEvent e) {
//        Object source = e.getSource();
//        if(source == openFile){
//            JFileChooser fileChooser = new JFileChooser(AutoLoad.DATABASE_DIRECTORY);
//            fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
//            int response = fileChooser.showSaveDialog(null);
//            if(response == JFileChooser.APPROVE_OPTION){
//                String fullPath = fileChooser.getSelectedFile().getPath();
//                filePathField.setText(fullPath);
//            }
//
//        }else if(source == showPassword){
//            if(showPassword.getIcon() == hideIcon){
//                showPassword.setIcon(showIcon);
//                passwordField.setEchoChar((char)0);
//            }else {
//                showPassword.setIcon(hideIcon);
//                passwordField.setEchoChar((char)8226);
//            }
//        }else if(source == showPin){
//            if(showPin.getIcon() == hideIcon){
//                showPin.setIcon(showIcon);
//                pinField.setEchoChar((char)0);
//            }else {
//                showPin.setIcon(hideIcon);
//                pinField.setEchoChar((char)8226);
//            }
//        }
//
//    }
//
//    @Override
//    public void mousePressed(MouseEvent e) {
//
//    }
//
//    @Override
//    public void mouseReleased(MouseEvent e) {
//
//    }
//
//    @Override
//    public void mouseEntered(MouseEvent e) {
//
//    }
//
//    @Override
//    public void mouseExited(MouseEvent e) {
//
//    }
}
