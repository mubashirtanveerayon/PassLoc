package elements.viewtable;

public interface TableActionEvent {


    public void onDelete(int row);
    public void onEdit(int row);
    public void onCopyUsername(int row);
    public void onCopyPassword(int row);

}
