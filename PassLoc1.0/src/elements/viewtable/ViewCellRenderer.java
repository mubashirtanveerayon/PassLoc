package elements.viewtable;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

public class ViewCellRenderer extends DefaultTableCellRenderer {

    public Component getTableCellRendererComponent(JTable table, Object o,boolean isSelected,boolean isFocused,int row,int column){
        Component component = super.getTableCellRendererComponent(table,o,isSelected,isFocused,row,column);
        ViewEditPanel panel = new ViewEditPanel();
        panel.setBackground(component.getBackground());
        return panel;
    }
}
