package gui.features;

import javafx.fxml.FXML;
import javafx.scene.Parent;

import java.util.ArrayList;

public abstract class Component {

    protected final ArrayList<ComponentListener>listeners;
    public Component() {
        listeners = new ArrayList<>();
    }

    @FXML
    Parent root;

    protected CenterView parent;

    public void setParent(CenterView parent) {
        this.parent = parent;
    }

    public void addComponentListener(ComponentListener listener) {
        listeners.add(listener);
    }

    public void removeComponentListener(ComponentListener listener) {
        listeners.remove(listener);
    }

    protected void remove(){
        for(ComponentListener listener : listeners){
            listener.onRemoved(this,root);
        }
    }


}
