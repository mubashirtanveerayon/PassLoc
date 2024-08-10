package elements;


import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;

public interface PanelActionHandler extends ActionListener {

    public void actionPerformed(ActionEvent e);

    public void onMouseClicked(MouseEvent e);
    public void onActionPerformed(ActionEvent e);

}
