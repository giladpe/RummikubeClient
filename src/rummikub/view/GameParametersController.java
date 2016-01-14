/*
 * this class is responsible for contolling the game parameter selection scene
 */

package rummikub.view;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.function.Consumer;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBox;
import rummikub.gameLogic.model.logic.Settings;
import rummikubFX.Rummikub;

public class GameParametersController implements Initializable, ControlledScreen, ResetableScreen {

    // Constants:
    public static final int TWO_PLAYERS = 2;
    public static final int THREE_PLAYERS = 3;
    public static final int FOUR_PLAYERS = 4;
    public static final int PLAYER1 = 0;
    public static final int PLAYER2 = 1;
    public static final int PLAYER3 = 2;
    public static final int PLAYER4 = 3;
    private static final String DUP_NAME_MSG = "Name is already exict!";
    private static final String INVALID_GAME_NAME_MSG = "Invalid game name!";
    private static final String NO_HUMAN_MSG = "Chose atleast one human player!";
    private static final String EMPTY_STRING="";
    private static final String CONTAINS_WHITE_SPACES_MSG="Name can not statr with whitespaces!";
    private static final String DEFUALT_GAME_NAME = "name";
    private static final boolean SELECTED = true;    
    private static final boolean ENABLED = true;    


    // FXML private members:
    @FXML private Button StartPlayingButton;
    @FXML private Label errorMsg;
    @FXML private TextField gameName;
    @FXML private HBox configPlayer1;
    @FXML private HBox configPlayer2;
    @FXML private HBox configPlayer3;
    @FXML private HBox configPlayer4;
    @FXML private TextField playerName1;
    @FXML private TextField playerName2;
    @FXML private TextField playerName3;
    @FXML private TextField playerName4;
    @FXML private CheckBox isComputer1;
    @FXML private CheckBox isComputer2;
    @FXML private CheckBox isComputer3;
    @FXML private CheckBox isComputer4;
    @FXML private ToggleGroup radioButtonGroup;
    @FXML private RadioButton B2;
    @FXML private RadioButton B3;
    @FXML private RadioButton B4;
    
    //Private members:
    private ScreensController myController;
    private final ArrayList<HBox> hBoxList = new ArrayList<>();
    private final ArrayList<CheckBox> checkBoxList = new ArrayList<>();
    private final ArrayList<TextField> playersNames = new ArrayList<>();
    private Settings gameSettings;
    

    //FXML Protected methods:
    
    @FXML
    protected void handleGameNameTextChange(ActionEvent event) {
        initStartPlayingButton();
    }

    @FXML
    protected void handlePlayerNameTextChange(ActionEvent event) {
        initStartPlayingButton();
    }

    @FXML
    protected void handleBackToMenuButtonAction(ActionEvent event) {
        this.myController.setScreen(Rummikub.MAINMENU_SCREEN_ID,this);
        //resetScreen();
    }

    @FXML
    protected void handleCheckBoxSelection(ActionEvent event) {
        int index = hBoxList.indexOf(((Node) event.getSource()).getParent());
        TextField checkBoxTextField = this.playersNames.get(index);

        if (((CheckBox) event.getSource()).isSelected()) {
            checkBoxTextField.setText(EMPTY_STRING);
            checkBoxTextField.setDisable(ENABLED);
        } else {
            checkBoxTextField.setDisable(!ENABLED);
        }
    }

    @FXML
    private void handleRadioButtonChanged() {
        int numberOfPlayer = getNumOfPlayers();
        switch (numberOfPlayer) {

            case FOUR_PLAYERS:
                handleFourPlayersButton();
                break;
            case THREE_PLAYERS:
                handleThreePlayersButton();
                break;
            case TWO_PLAYERS:
            default:
                handleTwoPlayersButton();
                break;

        }
        initStartPlayingButton();
    }

    @FXML
    private boolean isAllPlayersSet() {
        int numOfPlayers;
        boolean isLegalConditions = isNumOfPlayersSet();
        if (isLegalConditions) {
            numOfPlayers = getNumOfPlayers();
            for (int i = 0; i < numOfPlayers && isLegalConditions; i++) {
                isLegalConditions = this.isPlayerFieldSet(i);
            }
        }

        return atleastOnePlayerIsHuman() && isLegalConditions;
    }
    
    @FXML
    protected void handleStartPlayingButtonAction(ActionEvent event) {
        String gameNameString = this.gameName.getText();
        int numOfPlayers = getNumOfPlayers();
        int numOfComputerPlayers = getNumOfComputerPlayers();
        ArrayList<String> playersNamesList = getPlayersTextFieldList();
        PlayScreenController gameScreen = (PlayScreenController)this.myController.getControllerScreen(Rummikub.PLAY_SCREEN_ID);
        this.gameSettings = new Settings(gameNameString,numOfPlayers, numOfComputerPlayers, playersNamesList);
        gameScreen.createNewGame(gameSettings);
        
        this.myController.setScreen(Rummikub.PLAY_SCREEN_ID,ScreensController.NOT_RESETABLE);
        Platform.runLater(gameScreen::initAllGameComponents);
    }
    
    // Private methods:
    private void resetPlayerField(int index) {
        this.checkBoxList.get(index).setSelected(!SELECTED);
        this.playersNames.get(index).setText(EMPTY_STRING);
        this.playersNames.get(index).setDisable(!ENABLED);
    }

    private void initPlayersField() {
        this.hBoxList.add(configPlayer1);
        this.hBoxList.add(configPlayer2);
        this.hBoxList.add(configPlayer3);
        this.hBoxList.add(configPlayer4);
        this.checkBoxList.add(isComputer1);
        this.checkBoxList.add(isComputer2);
        this.checkBoxList.add(isComputer3);
        this.checkBoxList.add(isComputer4);
        this.playersNames.add(playerName1);
        this.playersNames.add(playerName2);
        this.playersNames.add(playerName3);
        this.playersNames.add(playerName4);
    }
    
    private boolean isNumOfPlayersSet() {
        return this.radioButtonGroup.getSelectedToggle() != null;
    }

    private int getNumOfPlayers() {
        return Integer.parseInt(((RadioButton)radioButtonGroup.getSelectedToggle()).getText());
    }

    private boolean isPlayerFieldSet(int index) {
        boolean result = false;
        String name = playersNames.get(index).getText();
        if (this.checkBoxList.get(index).isSelected()) {
            result = true;
        } else if (name != null && name.length() > 0) {
            result = (isValidTextField(name));
        }
        return result;
    }

    private boolean isValidTextField(String str) {
        return !(str.trim().isEmpty() || isInvalidFirstChar(str));
    }
    private boolean isInvalidFirstChar(String str){
        boolean retVal=false;
        if(Character.isWhitespace(str.charAt(0)))
        {
            errorMsg.setText(CONTAINS_WHITE_SPACES_MSG);
            retVal=true;
        }
        else if(isCurrMsgSameAs(CONTAINS_WHITE_SPACES_MSG)){
            clearMsg();
        }
        return retVal;
            
    }

    private void initStartPlayingButton() {
        this.StartPlayingButton.setDisable(!isAllFieldSet());
    }

    private boolean isAllFieldSet() {
        return isNumOfPlayersSet() && isGameNameSet() && isDiffNames() && isAllPlayersSet();
    }

    private boolean isGameNameSet() {
        if (isCurrMsgSameAs(INVALID_GAME_NAME_MSG)) {
            clearMsg();
        }
        if (gameName.getText() != null) {
            if (isValidTextField(gameName.getText())) {
                return true;
            }
        }
        this.errorMsg.setText(INVALID_GAME_NAME_MSG);
        return false;
    }

    private boolean isCurrMsgSameAs(String msg) {
        return this.errorMsg.getText().equals(msg);

    }

    private void clearMsg() {
        errorMsg.setText(EMPTY_STRING);
    }

    private void handleTwoPlayersButton() {
        resetPlayerField(PLAYER1);
        resetPlayerField(PLAYER2);
        resetPlayerField(PLAYER3);
        resetPlayerField(PLAYER4);
        setVisableFildsAfterRadioButtonSelection(TWO_PLAYERS);
    }

    private void handleThreePlayersButton() {
        resetPlayerField(PLAYER4);
        setVisableFildsAfterRadioButtonSelection(THREE_PLAYERS);
    }

    private void handleFourPlayersButton() {
        setVisableFildsAfterRadioButtonSelection(FOUR_PLAYERS);
    }
    
    private void setVisableFildsAfterRadioButtonSelection(int numOfFildsToSetVisable) {
        for (int i = 0; i < numOfFildsToSetVisable; i++) {
            this.hBoxList.get(i).setVisible(ENABLED);
        }
        
        for (int i = numOfFildsToSetVisable; i < this.hBoxList.size(); i++) {
            this.hBoxList.get(i).setVisible(!ENABLED);
        }
    }

    private boolean atleastOnePlayerIsHuman() {
        boolean foundHuman = false;
        if (isCurrMsgSameAs(NO_HUMAN_MSG)) {
            clearMsg();
        }
        for (int i = 0; i < getNumOfPlayers() && !foundHuman; i++) {
            foundHuman = !this.checkBoxList.get(i).isSelected();
        }
        if (!foundHuman) {
            this.errorMsg.setText(NO_HUMAN_MSG);
        }
        return foundHuman;
    }

    private ArrayList<String> getPlayersTextFieldList() {
        ArrayList<String> sPlayersNames = new ArrayList<>();
        this.playersNames.stream().forEach((fName) -> {
            String sName = fName.getText();
            if (this.isValidTextField(sName)) {
                sPlayersNames.add(fName.getText());
            } });
        return sPlayersNames;
    }

    boolean isDiffNames() {
        //////////////////need to check if there is methode 
        boolean isDiffNames = Settings.isValidPlayersNames(getPlayersTextFieldList());
        if (isCurrMsgSameAs(DUP_NAME_MSG)) {
            clearMsg();
        }
        if (!isDiffNames) {
            this.errorMsg.setText(DUP_NAME_MSG);
        }
        return isDiffNames;
    }

    private int getNumOfComputerPlayers() {
        int numOfComputerPlayers = 0;
        numOfComputerPlayers = this.checkBoxList.stream().filter((cb) -> (cb.isSelected())).map((_item) -> 1).reduce(numOfComputerPlayers, Integer::sum);
        
        return numOfComputerPlayers;
    }

    private void resetFeilds (Iterable collectionToReset, Consumer action) {
        collectionToReset.forEach(action);
    }
    
    //Public methods:
    @Override
    public void setScreenParent(ScreensController parentScreen) {
        this.myController = parentScreen;
    }

    @Override
    public void resetScreen() {
        this.errorMsg.setText(EMPTY_STRING);
        B2.setSelected(true);
        handleTwoPlayersButton();
        
        this.gameName.setText(DEFUALT_GAME_NAME);       
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        initPlayersField();
        
        this.checkBoxList.stream().forEach((cb) -> {
            cb.selectedProperty().addListener((ObservableValue<? extends Boolean> ov, Boolean old_val, Boolean new_val) -> {
                initStartPlayingButton();});
        });
        
        this.playersNames.stream().forEach((nameTextField) -> {
            nameTextField.textProperty().addListener((ObservableValue<? extends String> ov, String t, String t1) -> {
                initStartPlayingButton();});
        });

        this.gameName.textProperty().addListener((ObservableValue<? extends String> ov, String t, String t1) -> {
            initStartPlayingButton();
        });

        this.radioButtonGroup.selectedToggleProperty().addListener((ObservableValue<? extends Toggle> ov, Toggle old_toggle, Toggle new_toggle) -> {
            if (radioButtonGroup.getSelectedToggle() != null) {
                handleRadioButtonChanged();
            }
        });

        this.radioButtonGroup.selectedToggleProperty().addListener((ObservableValue<? extends Toggle> ov, Toggle old_toggle, Toggle new_toggle) -> {
            if (radioButtonGroup.getSelectedToggle() != null) {
                handleRadioButtonChanged();
            }
        });
        
    }
    
    public Settings getGameSettings() {
        return gameSettings;
    }
}


//************************Test Zone*****************************//
//    @Override
//    public void resetScreen() {
//        this.errorMsg.setText(EMPTY_STRING);
////        resetFeilds(this.playersNames, (Consumer) (Object playerName) -> {
////            ((TextField)playerName).setText(EMPTY_STRING);
////            ((TextField)playerName).setDisable(false);
////        });
//        //resetFeilds(this.hBoxList, (Consumer)(Object hBox) -> {((HBox)hBox).setVisible(false);});
//        //resetFeilds(this.radioButtonGroup.getToggles(), (Consumer)(Object rButton) -> {((RadioButton)rButton).setSelected(false);});
//        //resetFeilds(this.checkBoxList, (Consumer)(Object cBox) -> {((CheckBox)cBox).setSelected(false);});
//        B2.setSelected(true);
//        handleTwoPlayersButton();
//        
//        this.gameName.setText("name");       
//    }



//    private int getNumOfComputerPlayers() {
//        int numOfComputerPlayers = 0;
//        for (CheckBox cb : this.checkBoxList) {
//            if (cb.isSelected()) {
//                numOfComputerPlayers++;
//            }
//        }
//        return numOfComputerPlayers;
//    }

//    private void handleTwoPlayersButton() {
//        resetPlayerField(Utility.PLAYER3);
//        resetPlayerField(Utility.PLAYER4);
//        this.hBoxList.get(PLAYER1).setVisible(true);
//        this.hBoxList.get(PLAYER2).setVisible(true);
//        this.hBoxList.get(PLAYER3).setVisible(false);
//        this.hBoxList.get(PLAYER4).setVisible(false);
//    }

//    private void handleThreePlayersButton() {
//        resetPlayerField(Utility.PLAYER4);
//        this.hBoxList.get(PLAYER1).setVisible(true);
//        this.hBoxList.get(PLAYER2).setVisible(true);
//        this.hBoxList.get(PLAYER3).setVisible(true);
//        this.hBoxList.get(PLAYER4).setVisible(false);
//    }
//
//    private void handleFourPlayersButton() {
//        this.hBoxList.get(PLAYER1).setVisible(true);
//        this.hBoxList.get(PLAYER2).setVisible(true);
//        this.hBoxList.get(PLAYER3).setVisible(true);
//        this.hBoxList.get(PLAYER4).setVisible(true);
//    }

//    @Override
//    public void resetScreen() {
//        this.errorMsg.setText(Utility.EMPTY_STRING);
//        //this.hBoxList.forEach(null);
//        this.playersNames.forEach((playerName)-> {playerName.setText(Utility.EMPTY_STRING);});
//        this.radioButtonGroup.getToggles().forEach((rButton) -> {rButton.setSelected(false);});
//        this.checkBoxList.stream().forEach((cBox) -> { cBox.setSelected(false);});
//        this.gameName.setText(Utility.EMPTY_STRING);
//    }



//    private void handelErrorMsg() {
//        String stringMsg;
//        
//            case Utility.EMPTY_GAME_NAME:
//                stringMsg = EMPTY_GAME_NAME_MSG;
//                break;
//            case Utility.NO_ERROR:
//            default:
//                stringMsg = Utility.EMPTY_STRING;
//                break;
//        }
//        this.errorMsg.setText(stringMsg);
//    }
//    private int hasError() {
//        int hasError = Utility.NO_ERROR;
//        if (!isDiffNames()) {
//            hasError = Utility.DUP_NAME;
//        } else if (!isValidTextField(this.gameName.getText())) {
//            hasError = Utility.EMPTY_GAME_NAME;
//        }
//        return hasError;
//
//    }


//        @FXML
//    protected void handleBackToMenuButtonAction(ActionEvent event) throws IOException {
//        Stage primaryStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
//        primaryStage.setTitle("Main Menu");
//        URL url = this.getClass().getResource("MainMenu.fxml");
//        FXMLLoader fxmlLoader = new FXMLLoader();
//        fxmlLoader.setLocation(url);
//        Parent root = (Parent) fxmlLoader.load(url.openStream());
//        Scene scene = new Scene(root);
//        (((Node) event.getSource()).getScene().getWindow()).hide();
//        primaryStage.setScene(scene);
//        primaryStage.show();
//    }

//************************END*****************************//
