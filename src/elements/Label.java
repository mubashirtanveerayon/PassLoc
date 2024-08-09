package elements;

import constants.AutoLoad;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;


public class Label extends JLabel implements MouseListener {




    public Color hoverColor,pressedColor,defaultColor,textColor,hoverTextColor;



    public ImageIcon normalIcon,hoverIcon;
    private PanelActionHandler handler;

    public Label(Color hover, Color pressed, Color normal, Color hoverText, Color textColor, String text, String hoverImagePath, String normalImagePath, boolean regularFont, int textSize){
        super(text);
        normalIcon = new ImageIcon(normalImagePath);
        hoverIcon = new ImageIcon(hoverImagePath);
        setIcon(normalIcon);


        hoverColor=hover;
        pressedColor=pressed;
        defaultColor=normal;
        hoverTextColor=hoverText;
        this.textColor=textColor;
        setForeground(textColor);
        setBackground(normal);
        setOpaque(true);


        if(regularFont)setFont(AutoLoad.REGULAR_FONT.deriveFont((float)textSize));
        else setFont(AutoLoad.BOLD_FONT.deriveFont((float)textSize));
        addMouseListener(this);
        
    }

    public Label(Color hover, Color pressed, Color normal, Color hoverText, Color textColor, String text, boolean regularFont, int textSize){
        super(text);


        hoverColor=hover;
        hoverTextColor = hoverText;
        pressedColor=pressed;
        defaultColor=normal;
        this.textColor=textColor;
        setHorizontalTextPosition(JLabel.CENTER);
        setForeground(textColor);
        setBackground(normal);
        setOpaque(true);
//        setFont(AutoLoad.BOLD_FONT);



        setHorizontalAlignment(JLabel.CENTER);
        setVerticalAlignment(JLabel.CENTER);

        if(regularFont)setFont(AutoLoad.REGULAR_FONT.deriveFont((float)textSize));
        else setFont(AutoLoad.BOLD_FONT.deriveFont((float)textSize));


        addMouseListener(this);
        


    }

    public Label(String text, boolean regularFont, int fontSize){
        this(Color.white,new Color(0x8A8A8A),AutoLoad.THEME_COLOR,Color.black,AutoLoad.THEME_BUTTON_TEXT_COLOR,text,regularFont,fontSize);
    }

    public Label(String text, String hoverImagePath, String normalImagePath, boolean regularFont, int fontSize){
        this(Color.white,new Color(0x8A8A8A),AutoLoad.THEME_COLOR,Color.black,AutoLoad.THEME_BUTTON_TEXT_COLOR,text,hoverImagePath,normalImagePath,regularFont,fontSize);
    }



    public void addActionHandler(PanelActionHandler handler) {
        this.handler=handler;

    }


    @Override
    public void mouseClicked(MouseEvent e) {
        if(handler!= null)handler.onMouseClicked(e);
    }

    @Override
    public void mousePressed(MouseEvent e) {

            setBackground(pressedColor);

    }

    @Override
    public void mouseReleased(MouseEvent e) {


           setBackground(hoverColor);

    }

    @Override
    public void mouseEntered(MouseEvent e) {


        setBackground(hoverColor);
        setForeground(hoverTextColor);
        if(hoverIcon != null)setIcon(hoverIcon);
    }

    @Override
    public void mouseExited(MouseEvent e) {
        setBackground(defaultColor);
        setForeground(textColor);
        if(normalIcon != null)setIcon(normalIcon);
    }
}
