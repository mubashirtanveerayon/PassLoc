package elements;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

public class LabelGroup implements MouseListener {

    ArrayList<Label> labels = new ArrayList<>();

    public void add(Label label){
        if(labels.contains(label))return;
        label.addMouseListener(this);

        labels.add(label);
    }



    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {
        Label source =(Label) e.getSource();
        source.setBackground(source.pressedColor);

    }

    @Override
    public void mouseReleased(MouseEvent e) {
        Label source =(Label) e.getSource();
        source.setBackground(source.hoverColor);

    }

    @Override
    public void mouseEntered(MouseEvent e) {
        Object source = e.getSource();
        for(Label label:labels){
            if(source == label){
                label.setBackground(label.hoverColor);
                label.setForeground(label.hoverTextColor);
                if(label.hoverIcon != null)label.setIcon(label.hoverIcon);

            }else{
                label.setBackground(label.defaultColor);
                label.setForeground(label.textColor);
                if(label.normalIcon != null)label.setIcon(label.normalIcon);

            }

        }


    }

    @Override
    public void mouseExited(MouseEvent e) {
        Label label=(Label)e.getSource();
        label.setBackground(label.defaultColor);
        label.setForeground(label.textColor);
        if(label.normalIcon != null)label.setIcon(label.normalIcon);

    }
}
