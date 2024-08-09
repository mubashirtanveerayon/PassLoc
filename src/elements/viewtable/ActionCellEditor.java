package elements.viewtable;

import javax.swing.DefaultCellEditor;
import javax.swing.JCheckBox;
import javax.swing.JTable;
import java.awt.Component;

public class ActionCellEditor extends DefaultCellEditor {

    TableActionEvent event;

    public ActionCellEditor(TableActionEvent ev) {
        super(new JCheckBox());
        event = ev;
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value,
                                                 boolean isSelected,
                                                 int row, int column) {
        ViewEditPanel viewEditPanel = new ViewEditPanel();
        viewEditPanel.setBackground(table.getSelectionBackground());
        viewEditPanel.triggerEvent(event,row);


        return viewEditPanel;
    }

}
