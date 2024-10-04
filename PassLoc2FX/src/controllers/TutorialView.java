package controllers;

import helper.Info;
import helper.State;
import io.github.palexdev.materialfx.controls.MFXScrollPane;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import org.controlsfx.control.PopOver;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.ResourceBundle;

public class TutorialView extends PopOver implements Initializable {

    @FXML
    VBox rootView;

    @FXML
    private MFXScrollPane scrollPane;//--> contains 1 vbox of 230 px width




    @FXML
    private Label pageLabel;


    ArrayList<VBox> tutorials = new ArrayList<>();
    int index = 0;


    @FXML
    void onLeftAction(ActionEvent event) {
        index = Math.max(index-1,0);
        setTutorial();
    }

    @FXML
    void onRightAction(ActionEvent event) {

        index = Math.min(index+1,tutorials.size()-1);
        setTutorial();
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {


        setAutoFix(false);
        setArrowLocation(PopOver.ArrowLocation.TOP_CENTER);
        setContentNode(rootView);


        tutorials.clear();
        for(String[] tutorialText:Info.TUTORIALS)
            tutorials.add(getTutorialBox(tutorialText));
        index = 0;

        setTutorial();

    }

    private void setTutorial(){
        scrollPane.setContent(tutorials.get(index));
        pageLabel.setText(Info.VIEW_TITLES[index]);
    }

    private VBox getTutorialBox(String[] lines){
        VBox container = getContainer();



        for(String line : lines)
            container.getChildren().add( getTextContainer(getTutorialLabel(line)));




        return container;
    }

    private Label getTutorialLabel(String tutorialText){
        Label label = new Label(tutorialText);
        HBox.setHgrow(label, Priority.ALWAYS);
        label.setFont(new Font("System",18));
        label.setWrapText(true);
        return label;
    }

    private HBox getTextContainer(Label label){
        HBox container = new HBox();
        container.setAlignment(Pos.CENTER_LEFT);
        container.setPadding(new Insets(5));
        container.setSpacing(10);

        Pane ribbon = new Pane();
        ribbon.setPrefWidth(5);
        ribbon.setMinWidth(Region.USE_PREF_SIZE);
        ribbon.setPrefHeight(Region.USE_COMPUTED_SIZE);

        ribbon.setStyle("-fx-background-color: green;" +
                "-fx-background-radius: 10px;");

        container.getChildren().add(ribbon);
        container.getChildren().add(label);


        return container;
    }

    private VBox getContainer(){
        VBox container = new VBox();
        container.setAlignment(Pos.TOP_CENTER);
        container.setPrefWidth(230);
        container.setSpacing(10);
        return container;
    }
}
