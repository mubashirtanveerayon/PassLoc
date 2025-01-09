package gui.controller;

import commons.model.Entry;
import database.Database;
import gui.features.CenterView;
import gui.features.Component;
import gui.features.ComponentListener;
import gui.features.View;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class ShowEntriesView extends CenterView implements ComponentListener {

    ArrayList<TagChip> tags;
    ArrayList<EntryView> entries;

    @FXML
    private VBox entryContainer;


    @FXML
    private HBox tagContainer;

    @FXML
    private TextField tagField;

    @FXML
    void onAddTagClicked(MouseEvent event) {
        addTagAndFilter();

    }

    private void addTagAndFilter() {
        String tag = tagField.getText();
        loadTag(tag);
        tagField.clear();
        filter();
    }

    @FXML
    void onAddTagFieldAction(ActionEvent event) {
        addTagAndFilter();
    }

    @Override
    public void resetView() {
        tags.clear();
        tagContainer.getChildren().clear();
        entryContainer.getChildren().clear();
        tagField.clear();
        entries.clear();
    }



    private void loadTag(String tag){
        if(tag.isEmpty())return;
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/res/view/tag-chip.fxml"));
        try{
            Parent root = loader.load();
            TagChip controller = loader.getController();
            tags.add(controller);
            controller.setTag(tag);
            controller.setParent(this);
            controller.addComponentListener(this);
            tagContainer.getChildren().add(root);
        }catch (Exception e){
            e.printStackTrace();
        }
    }



    @Override
    public void initialize(URL location, ResourceBundle resources) {

        tags = new ArrayList<>();
        entries = new ArrayList<>();
        resetView();
    }

    @Override
    public void onRemoved(Component component, Parent root) {
        tags.remove(component);
        tagContainer.getChildren().remove(root);

        entryContainer.getChildren().remove(root);
        entries.remove(component);

        if(component instanceof EntryView){
            Entry entry = ((EntryView)component).getEntry();
            Database.getInstance().delete(Long.toString(entry.creationTime));
        }else filter();
    }

    private void filter(){
        if(tags.isEmpty())return;
        String[] tagArray= new String[tags.size()];
        for(int i=0;i<tagArray.length;i++)tagArray[i] = tags.get(i).getTag();
        ArrayList<Entry>filteredEntries = Database.getInstance().getEntries(tagArray);
        ArrayList<Parent >entryViews = new ArrayList<>();
        for(Entry e: filteredEntries){
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/res/view/entry.fxml"));

            try{

                entryViews.add(loader.load());
                EntryView view = loader.getController();
                view.setParent(this);
                view.addComponentListener(this);
                view.setEntry(e);
                entries.add(view);


            }catch(Exception ex){
                ex.printStackTrace();
            }
        }

        entryContainer.getChildren().clear();
        entryContainer.getChildren().addAll(entryViews);
    }


}
