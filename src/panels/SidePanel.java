package panels;

import com.formdev.flatlaf.extras.FlatSVGIcon;
import elements.Label;
import elements.PanelActionHandler;
import elements.RoundedPanel;
import constants.AutoLoad;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;


public class SidePanel extends RoundedPanel  {



    public Label createLabel,accessLabel,viewLabel,entryLabel,exitLabel;


    public SidePanel(){
        super(10,AutoLoad.THEME_COLOR_DARK);

        ((FlowLayout)getLayout()).setVgap(0);


        setPreferredSize(new Dimension(200,AutoLoad.DEFAULT_APP_DIMENSION.height));


        JLabel logo = new JLabel(new FlatSVGIcon(getClass().getResource("/res/logo.svg")));
        logo.setText(AutoLoad.VERSION);
        logo.setFont(AutoLoad.BOLD_FONT.deriveFont(28.0f));
        logo.setForeground(new Color(0xCAFFBF));
        logo.setToolTipText(AutoLoad.VERSION+" source code is here!");
        logo.setHorizontalTextPosition(JLabel.CENTER);
        logo.setVerticalTextPosition(JLabel.BOTTOM);
//        logo.addMouseListener(new MouseAdapter(){
//            public void mouseClicked(MouseEvent e){
//                Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
//                if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
//                    try {
//                        desktop.browse(new URL(AutoLoad.GITHUB).toURI());
//
//                    } catch (Exception ex) {
//                        ex.printStackTrace();
//                    }
//                }
//            }
//        });


//        logo.setBackground(Color.white);
//        logo.setPreferredSize(new Dimension(200,200));

        logo.setBorder(new EmptyBorder(20,0,30,0));

        add(logo);


        createLabel = new Label("Create",false,20);
        createLabel.setPreferredSize(new Dimension(200,60));
//        createLabel.setBorder(new EmptyBorder(0,20,0,20));

        accessLabel = new Label("Access",false,20);
        accessLabel.setPreferredSize(createLabel.getPreferredSize());
//        accessLabel.setBorder(new EmptyBorder(0,20,0,20));

        viewLabel = new Label("View",false,20);
        viewLabel.setPreferredSize(createLabel.getPreferredSize());
//        viewLabel.setBorder(new EmptyBorder(0,20,0,20));

        entryLabel = new Label("Insert",false,20);
        entryLabel.setPreferredSize(createLabel.getPreferredSize());
//        entryLabel.setBorder(new EmptyBorder(0,20,0,20));

        exitLabel = new Label("Exit",false,20);
        exitLabel.setPreferredSize(createLabel.getPreferredSize());
//        exitLabel.setBorder(new EmptyBorder(0,20,0,20));


        add(createLabel);
        add(accessLabel);
        add(viewLabel);
        add(entryLabel);
        add(exitLabel);








    }

    public void setComponentListener(PanelActionHandler handler){
        createLabel.addActionHandler(handler);
        accessLabel.addActionHandler(handler);
        viewLabel.addActionHandler(handler);
        entryLabel.addActionHandler(handler);
        exitLabel.addActionHandler(handler);
    }



}
