package controllers;

import com.jfoenix.controls.JFXSlider;
import com.jfoenix.controls.JFXToggleButton;
import services.commons.password.PasswordStrengthEvaluator;
import services.commons.password.UniquePasswordGenerator;
import services.commons.model.PasswordElements;
import services.commons.model.PasswordUnit;
import helper.NotificationCenter;
import helper.State;
import io.github.palexdev.materialfx.beans.NumberRange;
import io.github.palexdev.materialfx.controls.MFXProgressBar;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.util.Duration;

import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class PasswordGeneratorView  extends View implements Initializable {

    @FXML
    private JFXToggleButton digitToggleSwitch;

    @FXML
    private Label lengthLabel;

    @FXML
    private JFXSlider lengthSlider;

    @FXML
    private JFXToggleButton lowerCaseToggleSwitch;

    @FXML
    private Label passwordLabel;

    @FXML
    private JFXToggleButton specialToggleSwitch;

    @FXML
    private MFXProgressBar strengthBar;

    @FXML
    private JFXToggleButton upperCaseToggleSwitch;


    Timeline progressTimeline=null;

    boolean isAnimating = false;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        State.currentState = State.AppState.PG;
        if(progressTimeline == null)
            progressTimeline=new Timeline();

        progressTimeline.setOnFinished(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                isAnimating=false;
            }
        });
        strengthBar.getRanges1().add(NumberRange.of(0.0, 0.30));
        strengthBar.getRanges2().add(NumberRange.of(0.31, 0.60));
        strengthBar.getRanges3().add(NumberRange.of(0.61, 1.0));

        lengthSlider.valueProperty().addListener(new ChangeListener<Number>() {

            @Override
            public void changed(
                    ObservableValue<? extends Number> observableValue,
                    Number oldValue,
                    Number newValue) {
                lengthLabel.setText("Length: "+String.valueOf(newValue.intValue()));

                generatePassword(newValue.intValue());
            }
        });
    }

    private void generatePassword(int length) {

//        strengthBar.setProgress(0);
        int activeCount = 0;
        if(lowerCaseToggleSwitch.isSelected()) activeCount++;
        if(specialToggleSwitch.isSelected()) activeCount++;
        if(upperCaseToggleSwitch.isSelected()) activeCount++;
        if(digitToggleSwitch.isSelected()) activeCount++;

        if(activeCount == 0)
            return;
        int countPerUnit = length/activeCount;


        ArrayList<PasswordUnit> units = new ArrayList<>();
        if(upperCaseToggleSwitch.isSelected())
            units.add(new PasswordUnit(PasswordElements.CharacterType.UPPERCASE_LETTER,countPerUnit,true));
        if(lowerCaseToggleSwitch.isSelected())
            units.add(new PasswordUnit(PasswordElements.CharacterType.LOWERCASE_LETTER,countPerUnit,true));
        if(digitToggleSwitch.isSelected())
            units.add(new PasswordUnit(PasswordElements.CharacterType.DIGIT,countPerUnit,true));
        if(specialToggleSwitch.isSelected())
            units.add(new PasswordUnit(PasswordElements.CharacterType.SPECIAL,countPerUnit,true));

        int remaining = length - countPerUnit * units.size();
        if(remaining != 0){
            PasswordUnit first = units.get(0);
            PasswordUnit newUnit = new PasswordUnit(first.getType(),remaining+countPerUnit,false);
            units.remove(first);
            units.add(newUnit);
        }


        String allCharacters = UniquePasswordGenerator.generateStringFromPasswordUnits(units);
        String password = UniquePasswordGenerator.shuffle(allCharacters);
        passwordLabel.setText(password);

        float score = PasswordStrengthEvaluator.evaluatePasswordStrength(password)/100.0f;
        double startingValue = strengthBar.getProgress();


        if(isAnimating)
            progressTimeline.stop();

        // strengthBar.setProgress(score/100.0);

        progressTimeline.getKeyFrames().clear();
        progressTimeline.getKeyFrames().addAll(
                new KeyFrame(Duration.ZERO, new KeyValue(strengthBar.progressProperty(), startingValue)),
                new KeyFrame(Duration.millis(300), new KeyValue(strengthBar.progressProperty(), score))
        );
        progressTimeline.setCycleCount(1);
        progressTimeline.play();
        isAnimating=true;




    }

    @FXML
    public void onCopyPasswordImageClicked(MouseEvent me){
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(passwordLabel.getText()), null);
        NotificationCenter.sendSuccessNotification("Password copied to clipboard");
    }

    @FXML
    void onRegenerateButtonAction(ActionEvent event) {
        generatePassword((int)(lengthSlider.getValue()));
    }





}
