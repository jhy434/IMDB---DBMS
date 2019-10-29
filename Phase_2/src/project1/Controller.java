package project1;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.lang.Thread;

public class Controller {

    @FXML
    // The reference of inputText will be injected by the FXML loader
    private TextField nameInput;

    @FXML
    private TextField costarInput;

    // The reference of outputText will be injected by the FXML loader
    @FXML
    private TextArea outputText;

    @FXML
    private Button button1;

    @FXML
    private Button button2;

    @FXML
    private Button button3;

    @FXML
    private Button button4;

    @FXML
    private Button submitButton;

    @FXML
    private ProgressIndicator progressIndicator;

    @FXML
    private AnchorPane appearancesInput;

    @FXML
    private AnchorPane submitAnchor;

    @FXML
    private Label nameInputLabel;

    @FXML
    private Tooltip nameInputLabelTooltip;

    private void setNameLabelActor() {
        nameInputLabel.setText("Actor Name");
        nameInputLabelTooltip.setText("Enter the actor's name here");
    }

    private void setNameLabelRole() {
        nameInputLabel.setText("Role name");
        nameInputLabelTooltip.setText("Enter the role name here");
    }

    boolean buttonActive1 = false;
    boolean buttonActive2 = false;
    boolean buttonActive3 = false;
    boolean buttonActive4 = false;

    private void setAllButtonsFalse() {
        buttonActive1 = false;
        buttonActive2 = false;
        buttonActive3 = false;
        buttonActive4 = false;
        oneInputMode();
    }

    @FXML
    private void hideAppearancesInput() {
        appearancesInput.setVisible(false);
    }

    @FXML
    private void showAppearancesInput() {
        appearancesInput.setVisible(true);
    }

    private void oneInputMode() {
        hideAppearancesInput();
        costarInput.clear();
        submitButtonTopPos();
    }

    private void twoInputMode() {
        submitButtonBotPos();
        showAppearancesInput();
    }

    private void submitButtonBotPos() {
        AnchorPane.setBottomAnchor(submitAnchor, 15.0);
        AnchorPane.setTopAnchor(submitAnchor, 275.0);
    }

    private void submitButtonTopPos() {
        AnchorPane.setBottomAnchor(submitAnchor, 35.0);
        AnchorPane.setTopAnchor(submitAnchor, 255.0);
    }

    @FXML
    private void toggle1() {
        if (buttonActive1)
            setAllButtonsFalse();
        else {
            setAllButtonsFalse();
            buttonActive1 = true;
            setNameLabelActor();
        }
        updateButtons();
    }

    @FXML
    private void toggle2() {
        if (buttonActive2) {
            setAllButtonsFalse();
        } else {
            setAllButtonsFalse();
            buttonActive2 = true;
            setNameLabelActor();
            twoInputMode();
        }
        updateButtons();
    }

    @FXML
    private void toggle3() {
        if (buttonActive3)
            setAllButtonsFalse();
        else {
            setAllButtonsFalse();
            buttonActive3 = true;
            setNameLabelActor();
        }
        updateButtons();
    }

    @FXML
    private void toggle4() {
        if (buttonActive4)
            setAllButtonsFalse();
        else {
            setAllButtonsFalse();
            buttonActive4 = true;
            setNameLabelRole();
        }
        updateButtons();
    }

    private void updateButtons() {
        if(!buttonActive1) {
            button1.getStyleClass().clear();
            button1.getStyleClass().add("regular-button-1");
        }
        if (!buttonActive2) {
            button2.getStyleClass().clear();
            button2.getStyleClass().add("regular-button-2");
        }
        if (!buttonActive3) {
            button3.getStyleClass().clear();
            button3.getStyleClass().add("regular-button-3");
        }
        if (!buttonActive4) {
            button4.getStyleClass().clear();
            button4.getStyleClass().add("regular-button-4");
        }
    }

    // Add a public no-args constructor
    public Controller() {

    }

    boolean outputLock = true;
    @FXML
    private void printOutput() {
        if (buttonActive1) {
            new Thread(() -> {
                showLoading();
                while(!outputLock) {}
                outputLock = false;
                try {
                    typecastingOutput();
                    outputLock = true;
                    hideLoading();
                    outputText.setEditable(false);
                } catch (Exception e) { outputText.setText(errorText());}
                finally { return; }
            }).start();
        } else if (buttonActive2) {
            new Thread(() -> {
                showLoading();
                while(!outputLock) {}
                outputLock = false;
                try {
                    costarAppearancesOutput();
                    outputLock = true;
                    hideLoading();
                    outputText.setEditable(false);
                } catch (Exception e) { outputText.setText(errorText());}
                finally { return; }
            }).start();
        } else if (buttonActive3) {
            new Thread(() -> {
                showLoading();
                while(!outputLock) {}
                outputLock = false;
                try {
                    bestOfWorstOfOutput();
                    outputLock = true;
                    hideLoading();
                    outputText.setEditable(false);
                } catch (Exception e) { outputText.setText(errorText());}
                finally { return; }
            }).start();
        } else if (buttonActive4) {
            new Thread(() -> {
                showLoading();
                while(!outputLock) {}
                outputLock = false;
                try {
                    coverRolesOutput();
                    outputLock = true;
                    hideLoading();
                    outputText.setEditable(false);
                } catch (Exception e) { outputText.setText(errorText()); }
                finally { return; }
            }).start();
        }
    }

    @FXML
    private void submitPressed() {
        submitButton.getStyleClass().clear();
        submitButton.getStyleClass().add("pressed-submit-button");
        showLoading();
    }

    @FXML
    private void submitReleased() {
        submitButton.getStyleClass().clear();
        submitButton.getStyleClass().add("regular-submit-button");
        hideLoading();
    }

    @FXML
    private void button1Pressed() {
        button1.getStyleClass().clear();
        button1.getStyleClass().add("pressed-button-1");
    }

    @FXML
    private void button1Released() {
        button1.getStyleClass().clear();
        button1.getStyleClass().add("regular-button-1");
        if (buttonActive1) button1.getStyleClass().add("selected1");
    }

    @FXML
    private void button2Pressed() {
        button2.getStyleClass().clear();
        button2.getStyleClass().add("pressed-button-2");
    }

    @FXML
    private void button2Released() {
        button2.getStyleClass().clear();
        button2.getStyleClass().add("regular-button-2");
        if (buttonActive2) button2.getStyleClass().add("selected2");
    }

    @FXML
    private void button3Pressed() {
        button3.getStyleClass().clear();
        button3.getStyleClass().add("pressed-button-3");
    }

    @FXML
    private void button3Released() {
        button3.getStyleClass().clear();
        button3.getStyleClass().add("regular-button-3");
        if (buttonActive3) button3.getStyleClass().add("selected3");
    }

    @FXML
    private void button4Pressed() {
        button4.getStyleClass().clear();
        button4.getStyleClass().add("pressed-button-4");
    }

    @FXML
    private void button4Released() {
        button4.getStyleClass().clear();
        button4.getStyleClass().add("regular-button-4");
        if (buttonActive4) button4.getStyleClass().add("selected4");
    }

    private void showLoading() {
        // submitButton.setDisable(true);
        progressIndicator.setVisible(true);
    }

    private void hideLoading() {
        // submitButton.setDisable(false);
        progressIndicator.setVisible(false);
    }

    private String errorText() {
        return "ERROR: Something bad happened";
    }

    private String cleanName(String name) {
        return name.trim().replaceAll(" ", "_");
    }
    private String reverseUnderscore(String name) {
        return name.trim().replaceAll("_", " ");
    }

    private void typecastingOutput() {    // Query 3 : Button 1
        outputText.clear();
        String name = cleanName(nameInput.getText());
        String screen = reverseUnderscore(name);
        Queries queries = new Queries();
        ArrayList<String> output = new ArrayList<>();
        try {
            output = queries.QueryThree(name);
        } catch (IOException e) {
            e.printStackTrace();
        }
        outputText.setText("The most common genre played\nby " + screen + "\nis " + reverseUnderscore(output.get(0)));
        for (int i = 1; i < output.size(); i++)
            outputText.setText(outputText.getText() + "\n and" + reverseUnderscore(output.get(i)));
    }

    private void costarAppearancesOutput() { // Query 2 : Button 2
        outputText.clear();
        String name = cleanName(nameInput.getText());
        Queries queries = new Queries();
        ArrayList<String> output = new ArrayList<>();
        int costarAppearances = -1;
        try {
            costarAppearances = Integer.parseInt(costarInput.getText().trim());
        } catch (NumberFormatException a) {
            System.out.println("Integer format issue in costar appearances function");
        }
        try {
            if (costarAppearances > -1) {
                String screen = reverseUnderscore(name);
                output = queries.QueryTwo(name, costarAppearances);
                outputText.setText("List of co-stars that have\nstarred " + costarAppearances + " times\nwith " + screen + ":");
            } else
                outputText.setText(errorText());
            for (String s : output)
                outputText.appendText("\n - " + reverseUnderscore(s));
        } catch (IOException e) { e.printStackTrace(); }
    }

    private void bestOfWorstOfOutput() { // Query 5 : Button 3
        outputText.clear();
        String name = cleanName(nameInput.getText());
        String screen = reverseUnderscore(name);
        Queries queries = new Queries();
        ArrayList<String> output = new ArrayList<>();
        try {
            output = queries.QueryFive(name);
        } catch (IOException e) {
            e.printStackTrace();
        }
        outputText.setText("The director of " + screen + "'s"+
                "\nbest-ranked movie is " + reverseUnderscore(output.get(0)) +
                "\nand " +reverseUnderscore(output.get(0)) +"'s worst movie" +
                "\nis " + reverseUnderscore(output.get(1))
                    );
        for (int i = 2; i < output.size(); i++)
            outputText.appendText("\n and " + reverseUnderscore(output.get(i)));
    }

    private void coverRolesOutput() { // Query 3 : Button 4
        outputText.clear();
        String name = cleanName(nameInput.getText());
        String screen = reverseUnderscore(name);
        Queries queries = new Queries();
        ArrayList<String> output = new ArrayList<>();
        try {
            output = queries.QueryFour(name);
        } catch (IOException e) {
            e.printStackTrace();
        }
        outputText.setText("Names of actors who played\nthe role of " + screen + ":");
        for (String s : output)
            outputText.appendText("\n - " + reverseUnderscore(s));
    }
}