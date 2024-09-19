package panels;
import constants.AutoLoad;
import elements.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class CreatePanel extends RoundedPanel  {

    public JTextField nameField;
    public JTextField saveDirectoryField;
    public JPasswordField passwordField;
    public JPasswordField pinField;

    public RoundedButton openFolder;
    public RoundedButton showPassword;
    public RoundedButton showPin;

    public RoundedButton createButton;

//    public ImageIcon showIcon,hideIcon;
    private PanelActionHandler event;



    public CreatePanel(){
        super(10, AutoLoad.THEME_COLOR_LIGHT);

        ((FlowLayout)getLayout()).setVgap(30);
        setBorder(new EmptyBorder(20,20,20,20));



//        JLabel titleLabel = new JLabel("Create Database");
//        titleLabel.setFont(AutoLoad.BOLD_FONT.deriveFont(45.0f));
//        titleLabel.setForeground(AutoLoad.THEME_TEXT_COLOR);
//
//        titleLabel.setPreferredSize(new Dimension(800,60));


        nameField = new JTextField();
        nameField.setPreferredSize(new Dimension(800,60));
        nameField.setFont(AutoLoad.REGULAR_FONT.deriveFont(25.0f));
        nameField.setBorder(AutoLoad.getTitledBorer("Database name"));
        nameField.setBackground(AutoLoad.THEME_COLOR_LIGHT);
        nameField.setForeground(AutoLoad.THEME_TEXT_COLOR);
        nameField.setCaretColor(AutoLoad.THEME_COLOR);

        saveDirectoryField = new JTextField(AutoLoad.DATABASE_DIRECTORY);
        saveDirectoryField.setPreferredSize(new Dimension(750,60));
        saveDirectoryField.setFont(AutoLoad.REGULAR_FONT.deriveFont(25.0f));
        saveDirectoryField.setBorder(AutoLoad.getTitledBorer("Save directory"));
        saveDirectoryField.setBackground(AutoLoad.THEME_COLOR_LIGHT);
        saveDirectoryField.setForeground(AutoLoad.THEME_TEXT_COLOR);
        saveDirectoryField.setCaretColor(AutoLoad.THEME_COLOR);

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
        pinField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {

                if(AutoLoad.INVALID_PIN.indexOf(e.getKeyChar())>=0){

                    e.consume();
                }

            }
        });

        pinField.setCaretColor(AutoLoad.THEME_COLOR);
//        showIcon=new ImageIcon(getClass().getResource("/res/show_hover.svg"));
//        hideIcon = new ImageIcon(getClass().getResource("/res/hide.svg"));

//        openFolder = new JLabel(new ImageIcon(getClass().getResource("/res/open.svg")));
//
//        showPassword = new JLabel(hideIcon);
//        showPin = new JLabel(hideIcon);

        showPassword=new RoundedButton("/res/hide_hover.svg", "/res/hide.svg",50);
        showPin=new RoundedButton("/res/hide_hover.svg", "/res/hide.svg",50);
        openFolder=new RoundedButton("/res/open_hover.svg", "/res/open.svg",50);


        showPassword.setPreferredSize(new Dimension(40,40));
        showPin.setPreferredSize(new Dimension(40,40));
        openFolder.setPreferredSize(new Dimension(40,40));


        createButton = new RoundedButton("Create",false,35,20);
        createButton.setPreferredSize(new Dimension(250,50));



//        add(titleLabel);
        add(nameField);
        add(saveDirectoryField);
        add(openFolder);
        add(passwordField);
        add(showPassword);
        add(pinField);
        add(showPin);
        add(createButton);




    }

    public void setComponentListener(PanelActionHandler handler){
        event=handler;

        showPassword.addActionHandler(event);
        showPin.addActionHandler(event);
        openFolder.addActionHandler(event);
        createButton.addActionHandler(event);
//        openFolder.addMouseListener(this);
//        showPassword.addMouseListener(this);
//        showPin.addMouseListener(this);
    }


//    @Override
//    public void mouseClicked(MouseEvent e) {
//        Object source = e.getSource();
//        if(source == openFolder){
//            JFileChooser fileChooser = new JFileChooser(AutoLoad.DATABASE_DIRECTORY);
//            fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
//            int response = fileChooser.showSaveDialog(null);
//            if(response == JFileChooser.APPROVE_OPTION){
//                String fullPath = fileChooser.getSelectedFile().getPath();
//                saveDirectoryField.setText(fullPath+ File.separator);
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
