package panels;

import constants.AutoLoad;
import elements.PanelActionHandler;
import elements.RoundedButton;
import elements.RoundedPanel;
import elements.viewtable.ActionCellEditor;
import elements.viewtable.TableActionEvent;
import elements.viewtable.ViewCellRenderer;
import elements.viewtable.ViewEditPanel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class ViewPanel extends RoundedPanel implements ActionListener {

    private static final int ACTION_COLUMN_INDEX = 3;
    public JTable dataTable;
    public JTextField tagField;
    public DefaultTableModel tableModel;

    PanelActionHandler event;

    public boolean ignoreTableAction=true;
    public RoundedButton searchButton;

    public ViewPanel(){
        super(10, AutoLoad.THEME_COLOR_LIGHT);
        setBorder(new EmptyBorder(20,20,20,20));
//        JLabel titleLabel = new JLabel("View Data");
//        titleLabel.setFont(AutoLoad.BOLD_FONT.deriveFont(45.0f));
//        titleLabel.setForeground(AutoLoad.THEME_TEXT_COLOR);
//
//        titleLabel.setPreferredSize(new Dimension(800,60));



        tagField = new JTextField();
        tagField.setPreferredSize(new Dimension(735,60));
        tagField.setFont(AutoLoad.REGULAR_FONT.deriveFont(25.0f));
        tagField.setBorder(AutoLoad.getTitledBorer("Search by tag"));
        tagField.setBackground(AutoLoad.THEME_COLOR_LIGHT);
        tagField.setForeground(AutoLoad.THEME_TEXT_COLOR);
        tagField.setCaretColor(AutoLoad.THEME_COLOR);


        tableModel = new DefaultTableModel(new String[]{"ID","Username","Password","Actions"},0){
            public boolean isCellEditable(int row,int col){
                return col == ACTION_COLUMN_INDEX;
            }
        };


        searchButton =new RoundedButton("/res/search_hover.svg", "/res/search.svg",40);
//        searchButton.defaultColor = AutoLoad.THEME_COLOR_LIGHT;
//        searchButton.borderColor=AutoLoad.THEME_COLOR_LIGHT;
//        searchButton.setBackground(AutoLoad.THEME_COLOR_LIGHT);
        searchButton.setPreferredSize(new Dimension(52,52));


        dataTable = new JTable (tableModel);
        dataTable.setFont(AutoLoad.REGULAR_FONT.deriveFont(25.0f));
        dataTable.setRowHeight(50);
        dataTable.setSelectionBackground(AutoLoad.THEME_COLOR);

        JTableHeader header = dataTable.getTableHeader();

        header.setBackground(AutoLoad.THEME_COLOR);
        header.setForeground(AutoLoad.THEME_BUTTON_TEXT_COLOR);
        dataTable.getTableHeader().setFont(AutoLoad.BOLD_FONT.deriveFont(30.0f));
        dataTable.setBackground(AutoLoad.THEME_COLOR_DARK);
        dataTable.setForeground(Color.white);
        dataTable.getColumnModel().getColumn(ACTION_COLUMN_INDEX).setCellRenderer(new ViewCellRenderer());
        dataTable.setShowVerticalLines(true);

        DefaultTableCellRenderer cell = new DefaultTableCellRenderer();
        cell.setHorizontalAlignment(JTextField.CENTER);
        dataTable.getColumnModel().getColumn(0).setCellRenderer(cell);
        dataTable.getColumnModel().getColumn(0).setMaxWidth(80);



        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setViewportView(dataTable);
        scrollPane.setBorder(AutoLoad.getTitledBorer("Data table"));

        scrollPane.setPreferredSize(new Dimension(800,400));



        tagField.addActionListener(this);
//        add(titleLabel);
        add(tagField);
        add(searchButton);
        add(scrollPane);




    }

    public void setComponentListener(PanelActionHandler handler){
        event=handler;
        searchButton.addActionHandler(handler);

        dataTable.getColumnModel().getColumn(ACTION_COLUMN_INDEX).setCellEditor(new ActionCellEditor((TableActionEvent) handler));

    }



    @Override
    public void actionPerformed(ActionEvent e) {
        if(event != null)event.onActionPerformed(e);
    }

    public void removeAllRows() {
        ignoreTableAction=true;
        for(int i=tableModel.getRowCount()-1;i>=0;i--){
            tableModel.removeRow(i);
        }
        ignoreTableAction=false;
    }

    public void putDataRows(ArrayList<String[]>data){
        ignoreTableAction=true;
        for(String[] entry:data){
            String password = "";
            for(int i=0;i<entry[2].length();i++)password += "*";
            tableModel.addRow(new Object[]{entry[0],entry[1],password,new ViewEditPanel()});
        }
        ignoreTableAction=false;
    }
}
