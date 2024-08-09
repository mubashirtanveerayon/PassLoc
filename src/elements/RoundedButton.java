package elements;

import constants.AutoLoad;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class RoundedButton extends JButton implements MouseListener, ActionListener {

    Color hoverColor;
    public Color defaultColor;
    Color pressedColor;
    Color hoverTextColor;
    Color textColor;
    public Color borderColor;
    int radius=2;
    public ImageIcon normalIcon,hoverIcon;



    PanelActionHandler eventHandler;

    public RoundedButton(Color hover,Color pressed,Color normal,Color border,Color hoverText,Color textColor,String text,String hoverImagePath,String normalImagePath,boolean regularFont,int textSize, int radius){
        super(text);
        this.radius=radius;
        normalIcon = new ImageIcon(normalImagePath);
        hoverIcon = new ImageIcon(hoverImagePath);
        setIcon(normalIcon);

        borderColor=border;

        hoverColor=hover;
        pressedColor=pressed;
        defaultColor=normal;
        hoverTextColor=hoverText;
        this.textColor=textColor;
        setForeground(textColor);




        if(regularFont)setFont(AutoLoad.REGULAR_FONT.deriveFont((float)textSize));
        else setFont(AutoLoad.BOLD_FONT.deriveFont((float)textSize));
        configure();

    }

    public RoundedButton(Color hover,Color pressed,Color normal,Color border,Color hoverText,Color textColor,String text,boolean regularFont,int textSize, int radius){
        super(text);

            borderColor = border;
            this.radius = radius;
            hoverColor = hover;
            hoverTextColor = hoverText;
            pressedColor = pressed;
            defaultColor = normal;
            this.textColor = textColor;
            setHorizontalTextPosition(JLabel.CENTER);
            setForeground(textColor);


//        setFont(AutoLoad.BOLD_FONT);


            if (regularFont) setFont(AutoLoad.REGULAR_FONT.deriveFont((float) textSize));
            else setFont(AutoLoad.BOLD_FONT.deriveFont((float) textSize));

            configure();



    }



    public RoundedButton(Color hoverColor, Color pressedColor, Color defaultColor, Color borderColor, String hoverIcon, String normalIcon, int radius) {
        this.hoverColor = hoverColor;
        this.defaultColor = defaultColor;
        this.pressedColor = pressedColor;
        this.borderColor = borderColor;
        this.radius = radius;
        this.normalIcon = new ImageIcon(getClass().getResource(normalIcon));
        this.hoverIcon = new ImageIcon(getClass().getResource(hoverIcon));
        setIcon(this.normalIcon);
        configure();

    }


    public RoundedButton(String text,boolean regularFont,int fontSize,int radius){
        this(Color.white,new Color(0x8A8A8A),AutoLoad.THEME_COLOR,AutoLoad.THEME_TEXT_COLOR,Color.black,AutoLoad.THEME_BUTTON_TEXT_COLOR,text,regularFont,fontSize,radius);
    }

    public RoundedButton(String hoverIcon, String normalIcon, int radius){
        this(Color.white,new Color(0x484848),AutoLoad.THEME_TEXT_COLOR,AutoLoad.THEME_COLOR,hoverIcon,normalIcon,radius);
    }




    private void configure() {
        setHorizontalAlignment(JLabel.CENTER);
        setVerticalAlignment(JLabel.CENTER);
        setBackground(defaultColor);
        setContentAreaFilled(false);
        setFocusPainted(false);
        setFocusable(false);
        setBorder(new EmptyBorder(0,0,0,0));
        addMouseListener(this);
        addActionListener(this);

    }


    @Override
    public void paintComponent(Graphics g){

        Graphics2D g2d = (Graphics2D)g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setColor(borderColor);
        g2d.fillRoundRect(0,0, getWidth(),getHeight(),radius,radius );
        g2d.setColor(getBackground());
        g2d.fillRoundRect( 2,2,getWidth()-4,getHeight()-4,radius,radius);
        super.paintComponent(g);

    }

    @Override
    public void mouseClicked(MouseEvent e) {

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


    public void addActionHandler(PanelActionHandler handler) {
        this.eventHandler=handler;

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(eventHandler!= null)eventHandler.onActionPerformed(e);
    }
}
