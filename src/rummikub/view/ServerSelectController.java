/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rummikub.view;

import com.sun.javafx.binding.BindingHelperObserver;
import com.sun.javafx.property.adapter.PropertyDescriptor;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener.Change;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import rummikub.client.ws.DuplicateGameName_Exception;
import rummikub.client.ws.GameDetails;
import rummikub.client.ws.GameDoesNotExists_Exception;
import rummikub.client.ws.InvalidParameters_Exception;
import rummikub.client.ws.RummikubWebService;
import rummikub.client.ws.RummikubWebServiceService;
import rummikubFX.Rummikub;
//import rummikub.view.viewObjects.GameProp;

/**
 * FXML Controller class
 *
 * @author giladPe
 */
public class ServerSelectController implements ServerConnection, Initializable, ControlledScreen, ResetableScreen {

    private static final String INVALID_GAME_NAME_MSG = "Invalid game name!";
    private static final String NO_HUMAN_MSG = "Chose atleast one human player!";
    private static final String EMPTY_STRING = "";
    private static final String CONTAINS_WHITE_SPACES_MSG = "Name can not statr with whitespaces!";
    private static final String INVALID_NUMBER = "Invalid number!";
    private static final String INVALID_COMPUTERS_NUMBER = "Invalid computer players number";
    private static final String INVALID_PLAYERS_NUMBER = "Invalid players number";
    private static final String INVALID_HUMANS_NUMBER = "Invalid human players number";
    private String GAME_DOES_NOT_EXISTS = "Game does not exists";
    private String INVALID_PARAMETERS = "Invalid parameters input ";
    private String DUP_NAME = "Duplicate game name insert new name";
    private RummikubWebServiceService service;
    private RummikubWebService rummikubWebService;
    private int playerID;

    public RummikubWebServiceService getService() {
        return service;
    }
    

    @FXML
    private GridPane GamesSettings;
    @FXML
    private Button StartPlayingButton;
    @FXML
    private Label errorMsg;
    @FXML
    private TableView<GameDetails> gamesTableView;
    @FXML
    private TableColumn<GameDetails, String> gameNameColumn;

    @FXML
    private TableColumn<GameDetails, Integer> computerPlayersColumn;
    @FXML
    TextField gameNameInput;

    @FXML
    TextField numOfHumansInput, numOfCopmputersInput;
    @FXML
    private TableColumn<GameDetails, Integer> numOfHumanColumn;
    private ScreensController myController;
    @FXML
    private VBox tableVBox;
    @FXML
    private Button joinButton;
    @FXML
    private Button addButton;
    @FXML
    private TableColumn<?, ?> gameStatus;
    @FXML
    private TextField playerNameInput;
    @FXML
    private TableColumn<?, ?> joinedColumn;

    @FXML
    private void handleBackToMenuButtonAction(ActionEvent event) {
        this.myController.setScreen(Rummikub.MAINMENU_SCREEN_ID,this);
    }

    @FXML
    private void handleStartPlayingButtonAction(ActionEvent event) {
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //    sasa
        //"joinedHumanPlayers",
        //"loadedFromXML",

        this.joinedColumn.setCellValueFactory(new PropertyValueFactory<>("joinedHumanPlayers"));
        this.gameNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        this.numOfHumanColumn.setCellValueFactory(new PropertyValueFactory<>("humanPlayers"));
        this.computerPlayersColumn.setCellValueFactory(new PropertyValueFactory<>("computerizedPlayers"));
        this.gameStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        this.joinButton.setDisable(true);

        this.addButton.setOnAction(e -> addButtonClicked());
        this.joinButton.setOnAction(e -> joinButtonClicked());
        initAddButton();
        //this.gamesTableView.selectionModelProperty().addListener(new PropertyDescriptor.Listener<>);
        gamesTableView.getSelectionModel().getSelectedItems().addListener((Change<? extends GameDetails> change) -> joinButton.setDisable(this.playerNameInput.getText().isEmpty()));
        ///add listener to game name 
        this.gameNameInput.textProperty().addListener((ObservableValue<? extends String> ov, String t, String t1) -> {
            isValidGameName();
            initAddButton();
        });
        this.numOfHumansInput.textProperty().addListener((ObservableValue<? extends String> ov, String t, String t1) -> {
            isNumericChar(this.numOfHumansInput);
            initAddButton();
        });
        this.numOfCopmputersInput.textProperty().addListener((ObservableValue<? extends String> ov, String t, String t1) -> {
            isNumericChar(this.numOfCopmputersInput);
            initAddButton();
        });
        this.playerNameInput.textProperty().addListener((ObservableValue<? extends String> ov, String t, String t1) -> {
            initAddButton();
            initJoinButton();
        });

    }

    @FXML
    public void addButtonClicked() {
        new Thread(() -> {
            try {
                rummikubWebService.createGame(gameNameInput.getText(), getNumOfHumansPlayers(), getNumOfComputerPlayers());
                this.setPlayerId(rummikubWebService.joinGame(gameNameInput.getText(), playerNameInput.getText()));
                    ObservableList<GameDetails> gameList = getListOfWaittingGames();
                    Platform.runLater(() -> {
                        gamesTableView.setItems(gameList);
                        
                    });
                        PlayScreenController gameScreen = (PlayScreenController)this.myController.getControllerScreen(Rummikub.PLAY_SCREEN_ID);
                        gameScreen.setService(service);
                        gameScreen.setPlayerId(playerID);
                        gameScreen.initButtons(true);
                        gameScreen.getEvents();
                        this.myController.setScreen(Rummikub.PLAY_SCREEN_ID,ScreensController.NOT_RESETABLE);
                        //Platform.runLater(gameScreen::initAllGameComponents);
                        
            } catch (DuplicateGameName_Exception ex) {
                Platform.runLater(() -> showErrorMsg(errorMsg, ex.getMessage()));
            } catch (InvalidParameters_Exception | GameDoesNotExists_Exception ex) {
                Platform.runLater(() -> {
                    showErrorMsg(errorMsg, ex.getMessage());
                });
            }
            Platform.runLater(() -> (clearInputFiled()));
        }).start();
    }
    @FXML
    public void joinButtonClicked() {
        new Thread(() -> {
        RummikubWebService rummikubWebService = service.getRummikubWebServicePort();
        try {
            setPlayerId(rummikubWebService.joinGame(this.gamesTableView.getSelectionModel().getSelectedItem().getName(), this.playerNameInput.getText()));
            Platform.runLater(() -> {
                 clearInputFiled();
            });
                 PlayScreenController gameScreen = (PlayScreenController)this.myController.getControllerScreen(Rummikub.PLAY_SCREEN_ID);
                 gameScreen.setService(service);
                 gameScreen.setPlayerId(playerID);
                 gameScreen.initButtons(true);
                 gameScreen.getEvents();
                 this.myController.setScreen(Rummikub.PLAY_SCREEN_ID,ScreensController.NOT_RESETABLE);
                 //Platform.runLater(gameScreen::initAllGameComponents);
            //////TODO 
        } catch (GameDoesNotExists_Exception | InvalidParameters_Exception ex) {
            Platform.runLater(() -> {showErrorMsg(errorMsg, ex.getMessage());});
        }
        Platform.runLater(() -> {this.gamesTableView.getSelectionModel().clearSelection();});
        }).start();
    }


    private synchronized ObservableList<GameDetails> getListOfWaittingGames() {
        //new Threa
        RummikubWebService rummikubWebService = service.getRummikubWebServicePort();
        List<String> stringGames = rummikubWebService.getWaitingGames();
        ObservableList<GameDetails> gamesDetails = FXCollections.observableArrayList();
        for (String stringGame : stringGames) {
            try {
                gamesDetails.add(rummikubWebService.getGameDetails(stringGame));
            } catch (GameDoesNotExists_Exception ex) {
                Platform.runLater(() -> {
                    showErrorMsg(errorMsg, ex.getFaultInfo().getMessage());
                });
            }
        }
        return gamesDetails;
    }

    public int getNumOfHumansPlayers() {
        int num = 0;
        if (!this.numOfHumansInput.getText().isEmpty()) {
            num = Integer.parseInt(this.numOfHumansInput.getText());
        }
        return num;
    }

    public int getNumOfComputerPlayers() {
        int num = 0;
        if (!this.numOfCopmputersInput.getText().isEmpty()) {
            num = Integer.parseInt(this.numOfCopmputersInput.getText());
        }
        return num;
    }

    //Delete button clicked
    
    @Override
    public void setScreenParent(ScreensController parentScreen) {
        this.myController = parentScreen;
    }

    @Override
    public void resetScreen() {
        this.gamesTableView.getItems().clear();////////////
        new Thread(() -> {
            ObservableList<GameDetails> gameList = getListOfWaittingGames();
            Platform.runLater(() -> {
                gamesTableView.setItems(gameList);
            });
        }).start();
        //this.gamesTableView.setItems(getListOfWaittingGames());
    }

    private void isNumericChar(TextField text) {
        if (!text.getText().isEmpty() && !Character.isDigit(text.getText().charAt(0))) {//.matches("\\d*")) {
            text.clear();
            showErrorMsg(errorMsg, INVALID_NUMBER);
        } else if (text.getText().length() > 1) {
            char[] charArr = new char[1];
            charArr[0] = text.getText().charAt(0);
            String num = new String(charArr);
            text.setText(num);
        }
    }
//        private void isNumericChar(TextField text) {
//        if (!text.getText().isEmpty()&&!text.getText().matches("\\d*")) {
//            text.clear();
//            showErrorMsg(errorMsg, INVALID_NUMBER);
//        }
//    }

    private void isValidNumOfComputerPlayers(String text) {
        //TODO
    }

    private boolean isValidTextField(String str) {
        return !(str.trim().isEmpty() || isInvalidFirstChar(str));
    }

    private boolean isInvalidFirstChar(String str) {
        boolean retVal = false;
        if (Character.isWhitespace(str.charAt(0))) {
            showErrorMsg(errorMsg, CONTAINS_WHITE_SPACES_MSG);

            retVal = true;
        }

        return retVal;

    }

    public static void disappearAnimation(Node node) {
        FadeTransition animation = new FadeTransition();
        animation.setNode(node);
        animation.setDuration(Duration.seconds(3));
        animation.setFromValue(1.0);
        animation.setToValue(0.0);
        animation.play();
    }

    public static void showErrorMsg(Label label, String msg) {
        label.setText(msg);
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(0.5), (ActionEvent event) -> {
            disappearAnimation(label);
        }));
        timeline.setCycleCount(1);
        timeline.play();
    }

    private boolean isValidGameName() {
        if (this.gameNameInput.getText() != null) {
            if (isValidTextField(gameNameInput.getText())) {
                return true;
            }
        }
        if(!allInputFieldEmpty()){
        Platform.runLater(() -> (showErrorMsg(errorMsg, INVALID_GAME_NAME_MSG)));
        
        }
        return false;
    }

    private void initAddButton() {
        this.addButton.setDisable(!isAllSet());
    }

    private boolean isAllSet() {
        boolean allSet = true;
        if (this.playerNameInput.getText().isEmpty() || this.gameNameInput.getText().isEmpty() || this.numOfCopmputersInput.getText().isEmpty() || this.numOfHumansInput.getText().isEmpty()) {
            allSet = false;
        }
        if (getNumOfHumansPlayers() > 4 || getNumOfHumansPlayers() < 1) {
            if (!this.numOfHumansInput.getText().isEmpty()) {
                showErrorMsg(errorMsg, INVALID_HUMANS_NUMBER);
            }
            allSet = false;
        } else if (getNumOfComputerPlayers() > 3) {
            showErrorMsg(errorMsg, INVALID_COMPUTERS_NUMBER);
            allSet = false;
        } else if (getNumOfComputerPlayers() + getNumOfHumansPlayers() > 4 || getNumOfComputerPlayers() + getNumOfHumansPlayers() < 2) {
            showErrorMsg(errorMsg, INVALID_PLAYERS_NUMBER);
            allSet = false;
        }

        return allSet;
    }

    private void initJoinButton() {
        this.joinButton.setDisable((this.playerNameInput.getText().isEmpty() || !this.gamesTableView.getSelectionModel().isSelected(0)));
    }
    @Override
    public void setService(RummikubWebServiceService service) {
        this.service = service;
        this.rummikubWebService = service.getRummikubWebServicePort();
    }
    
    @Override
    public void setPlayerId(int playerId) {
        this.playerID=playerId;
    }

    @Override
    public int getPlayerId() {
    return this.playerID;
    }

    private boolean allInputFieldEmpty() {
        return (this.numOfCopmputersInput.getText().isEmpty()&&this.numOfHumansInput.getText().isEmpty()&&this.gameNameInput.getText().isEmpty());
    }

    private void clearInputFiled() {
                gameNameInput.clear();
                numOfHumansInput.clear();
                numOfCopmputersInput.clear();
                playerNameInput.clear();

    }
    

}
//public class NumberTextField extends TextField
//{
//
//    @Override
//    public void replaceText(int start, int end, String text)
//    {
//        if (validate(text))
//        {
//            super.replaceText(start, end, text);
//        }
//    }
//
//    @Override
//    public void replaceSelection(String text)
//    {
//        if (validate(text))
//        {
//            super.replaceSelection(text);
//        }
//    }
//
//    private boolean validate(String text)
//    {
//        return text.matches("[0-9]*");
//    }
//}
