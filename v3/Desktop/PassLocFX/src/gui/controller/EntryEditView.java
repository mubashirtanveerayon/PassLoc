package gui.controller;

import commons.model.Entry;
import database.Database;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import gui.features.CenterView;
import gui.features.Component;
import gui.features.ComponentListener;
import gui.features.View;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.ArrayList;
import java.util.Map;
import java.util.ResourceBundle;

public class EntryEditView extends CenterView implements ComponentListener {



    ArrayList<TagChip>tags;
    ArrayList<FieldContainer>fields;

    @FXML
    private PasswordField URLPasswordField;

    @FXML
    private TextField URLTextField;

    @FXML
    private FontAwesomeIconView URLVisibilityIcon;

    @FXML
    private Button addFieldButton;

    @FXML
    private VBox fieldContainer;

    @FXML
    private PasswordField passwordField;

    @FXML
    private FontAwesomeIconView passwordVisibilityIcon;


    @FXML
    private HBox tagContainer;

    @FXML
    private TextField tagField;

    @FXML
    private TextField textField;

    @FXML
    private PasswordField usernamePasswordField;

    @FXML
    private TextField usernameTextField;

    @FXML
    private FontAwesomeIconView usernameVisibilityIcon;

    private long idOfEntryTobeUpdated=-1;


    private void loadField(Map.Entry<String,String>entry){
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/res/view/field-container.fxml"));
        try{
            Parent root = loader.load();
            FieldContainer controller = loader.getController();
            controller.addComponentListener(this);
            controller.setField(entry);
            controller.setParent(this);
            fields.add(controller);
            fieldContainer.getChildren().add(root);
        } catch (Exception e) {
            e.printStackTrace();
        }
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
            resetIdleTimeout();

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void resetIdleTimeout(){
        Database.getInstance().getPasswordMonitor().resetTimer();
    }



    @FXML
    void onAddFieldPressed(ActionEvent event) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/res/view/field-container.fxml"));
        try{
            Parent root = loader.load();
            FieldContainer controller =loader.getController();
            controller.addComponentListener(this);
            fields.add(controller);
            fieldContainer.getChildren().add(root);
            resetIdleTimeout();
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    @FXML
    void onAddTagClicked(MouseEvent event) {
        addTag();
    }

    private void addTag() {
        String tag = tagField.getText();
        loadTag(tag);
        tagField.clear();
    }

    @FXML
    void onAddTagFieldAction(ActionEvent event) {
        addTag();
    }
    @FXML
    void onDonePressed(ActionEvent event) {

        if(Database.isOffline()){
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Locked database");
            alert.setContentText("Database is locked. Login to complete this action.");
            alert.show();
            return;
        }

        if(isUpdating()){
            Entry entry = new Entry(idOfEntryTobeUpdated,System.currentTimeMillis());
            setContent(entry);


            Database.getInstance().update(entry);

        }else{
            Entry entry = new Entry();
            setContent(entry);
            Database.getInstance().insert(entry);

        }
    }

    public boolean isUpdating(){
        return idOfEntryTobeUpdated>0;
    }

    private void setContent(Entry entry) {
        entry.setUsername(getUsername());
        entry.setPassword(getPassword());
        entry.setURL(getURL());
        for(TagChip tagController:tags)
            entry.addTag(tagController.getTag());
        for(FieldContainer f : fields)
            entry.addField(f.getKey(),f.getValue());
    }

    private String getUsername(){
        return usernameTextField.isVisible()?usernameTextField.getText():usernamePasswordField.getText();
    }

    private String getPassword(){
        return textField.isVisible()?textField.getText():passwordField.getText();
    }

    private String getURL(){
        return URLTextField.isVisible()?URLTextField.getText():URLPasswordField.getText();
    }
    @FXML
    void onPasswordVisibilityIconClicked(MouseEvent event) {
        onPasswordVisibilityIconClicked(textField,passwordField,passwordVisibilityIcon);
    }

    @FXML
    void onURLVisibilityIconClicked(MouseEvent event) {
        onPasswordVisibilityIconClicked(URLTextField,URLPasswordField,URLVisibilityIcon);
    }

    @FXML
    void onUsernameVisibilityIconClicked(MouseEvent event) {
        onPasswordVisibilityIconClicked(usernameTextField,usernamePasswordField,usernameVisibilityIcon);
    }



    public void setEntry(Entry entry) {

        idOfEntryTobeUpdated=entry.creationTime;

        for(String tag:entry.tags)
            loadTag(tag);


        for(Map.Entry<String,String>e:entry.fields.entrySet()){
            if(e.getKey().equalsIgnoreCase(Entry.USERNAME_KEY)){
                usernamePasswordField.setText(e.getValue());
            }else if(e.getKey().equalsIgnoreCase(Entry.PASSWORD_KEY)){
                passwordField.setText(e.getValue());
            }else if(e.getKey().equalsIgnoreCase(Entry.URL_KEY)){
                URLPasswordField.setText(e.getValue());
            }else{
                loadField(e);
            }
        }

    }




    @Override
    public void onRemoved(Component component, Parent root) {
        fieldContainer.getChildren().remove(root);
        tagContainer.getChildren().remove(root);
        fields.remove(component);
        tags.remove(component);
    }

    @Override
    public void resetView() {
        if(isUpdating()){
            tags.clear();
            tagContainer.getChildren().clear();
            tagField.clear();
            idOfEntryTobeUpdated=-1;
        }
        fields.clear();

        ObservableList<Node> fieldNodes = fieldContainer.getChildren();
        fieldNodes.remove(3,fieldNodes.size());


        usernamePasswordField.clear();
        usernameTextField.clear();
        textField.clear();
        passwordField.clear();
        URLTextField.clear();
        URLPasswordField.clear();

        usernamePasswordField.setVisible(true);
        usernameTextField.setVisible(false);
        textField.setVisible(false);
        passwordField.setVisible(true);
        URLTextField.setVisible(false);
        URLPasswordField.setVisible(true);

        usernameVisibilityIcon.setGlyphName("EYE_SLASH");
        passwordVisibilityIcon.setGlyphName("EYE_SLASH");
        URLVisibilityIcon.setGlyphName("EYE_SLASH");

    }



    @Override
    public void initialize(URL location, ResourceBundle resources) {
        tags = new ArrayList<>();
        fields = new ArrayList<>();
    }
}
