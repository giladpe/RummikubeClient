/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rummikub.view;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;
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
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.util.Duration;
import org.xml.sax.SAXException;
import rummikub.client.ws.DuplicateGameName_Exception;
import rummikub.client.ws.GameDetails;
import rummikub.client.ws.GameDoesNotExists_Exception;
import rummikub.client.ws.InvalidParameters_Exception;
import rummikub.client.ws.InvalidXML_Exception;
import rummikub.client.ws.PlayerDetails;
import rummikub.client.ws.RummikubWebService;
import rummikub.client.ws.RummikubWebServiceService;
import rummikub.gameLogic.model.logic.GameLogic;
import rummikub.gameLogic.view.ioui.JaxBXmlParser;
import rummikubFX.Rummikub;

/**
 * FXML Controller class
 *
 * @author giladPe
 */
public class ServerSelectController implements ServerConnection, Initializable, ControlledScreen, ResetableScreen {

    private static final String NAME_PROMPT = "Insert Player Name";
    private static final String INVALID_GAME_NAME_MSG = "Invalid game name!";
    private static final String CONTAINS_WHITE_SPACES_MSG = "Name can not statr with whitespaces!";
    private static final String INVALID_NUMBER = "Invalid number!";
    private static final String INVALID_COMPUTERS_NUMBER = "Invalid computer players number";
    private static final String INVALID_PLAYERS_NUMBER = "Invalid players number";
    private static final String INVALID_HUMANS_NUMBER = "Invalid human players number";
    private final static String FAIL_LOADING_FILE_MSG = "Error was not able to load file!";
    private static final boolean DAEMON_THREAD = true;
    private static final long UPDATE_TIME = 3000;
    private RummikubWebServiceService service;
    private RummikubWebService rummikubWebService;
    private int playerID;
    //may not need : private SimpleBooleanProperty isServerSelectSceeneShow;
    private Timer timer;
    @FXML
    private Button loadGameButton;
    private static final boolean ENABLED = true;

    public RummikubWebServiceService getService() {
        return service;
    }

    @FXML
    private GridPane GamesSettings;
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
    private final ArrayList<Button> buttonsList = new ArrayList<>();

    @FXML
    private void handleBackToMenuButtonAction(ActionEvent event) {
        this.myController.setScreen(Rummikub.LOGIN_SCREEN_ID, this);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //    sasa
        //"joinedHumanPlayers",
        //"loadedFromXML",
        //this.isServerSelectSceeneShow.set(false);
        this.buttonsList.add(this.joinButton);
        this.buttonsList.add(this.loadGameButton);
        this.buttonsList.add(this.addButton);
        this.timer = new Timer();
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

        gamesTableView.getSelectionModel().getSelectedItems().addListener((Change<? extends GameDetails> change) -> {
            onTableViewChange();
        });
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

        Platform.runLater(() -> (this.addButton.setDisable(true)));
        String gameName = gameNameInput.getText();
        String playerName = this.playerNameInput.getText();
        Thread thread = new Thread(() -> {
            try {
                rummikubWebService.createGame(getPlayerName(), getNumOfHumansPlayers(), getNumOfComputerPlayers());
                initPlayScrenAndSubMenu(gameName, playerName);
            } catch (DuplicateGameName_Exception | GameDoesNotExists_Exception | InvalidParameters_Exception ex) {
                Platform.runLater(() -> {
                    showErrorMsg(errorMsg, ex.getMessage());
                    Platform.runLater(() -> (clearInputFiled()));
                });
            } catch (Exception ex) {
                onServerLostException();
            }
        });
        thread.setDaemon(DAEMON_THREAD);
        thread.start();

    }

    @FXML
    public void joinButtonClicked() {

        String gameName = this.gamesTableView.getSelectionModel().getSelectedItem().getName();
        String playerName = this.playerNameInput.getText();
        this.playerNameInput.clear();
        Thread thread = new Thread(() -> {
            try {
                Platform.runLater(() -> (this.joinButton.setDisable(true)));
                initPlayScrenAndSubMenu(gameName, playerName);
            } catch (DuplicateGameName_Exception | GameDoesNotExists_Exception | InvalidParameters_Exception ex) {
                Platform.runLater(() -> {
                    showErrorMsg(errorMsg, ex.getMessage());
                    clearInputFiled();
                });
            }
        });
        thread.setDaemon(DAEMON_THREAD);
        thread.start();
    }

    private synchronized ObservableList<GameDetails> getListOfWaittingGames() {
        ObservableList<GameDetails> gamesDetails = null;

        try {
            List<String> stringGames = rummikubWebService.getWaitingGames();
            gamesDetails = FXCollections.observableArrayList();

            for (String stringGame : stringGames) {
                gamesDetails.add(rummikubWebService.getGameDetails(stringGame));
            }

        } catch (GameDoesNotExists_Exception ex) {
            Platform.runLater(() -> {
                showErrorMsg(errorMsg, ex.getFaultInfo().getMessage());
            });
        } catch (Exception ex) {
            onServerLostException();
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
        initGameViewTable();
        initGameViewTableTimer();
        initAllButton();
        clearInputFiled();
//        new Thread(() -> {
//            ObservableList<GameDetails> gameList = getListOfWaittingGames();
//            Platform.runLater(() -> {
//                this.gamesTableView.getItems().clear();////////////
//                gamesTableView.setItems(gameList);
//            });
//        }).start();
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
        if (!allInputFieldEmpty()) {
            Platform.runLater(() -> (showErrorMsg(errorMsg, INVALID_GAME_NAME_MSG)));

        }
        return false;
    }

    private void initAddButton() {
        this.addButton.setDisable(!isAllSet());
    }

    private boolean isAllSet() {
        boolean allSet = true;
        if (this.playerNameInput.getText().isEmpty() || this.gameNameInput.getText().isEmpty()) {
            allSet = false;
        }
        if (getNumOfHumansPlayers() > 4 || getNumOfHumansPlayers() < 1) {
            if (getNumOfHumansPlayers() > 1) {
                showErrorMsg(errorMsg, INVALID_HUMANS_NUMBER);
            }
            allSet = false;
        } else if (!this.numOfCopmputersInput.getText().isEmpty() && getNumOfComputerPlayers() > 3) {
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
        this.playerID = playerId;
    }

    @Override
    public int getPlayerId() {
        return this.playerID;
    }

    private boolean allInputFieldEmpty() {
        return (this.numOfCopmputersInput.getText().isEmpty() && this.numOfHumansInput.getText().isEmpty() && this.gameNameInput.getText().isEmpty());
    }

    private void clearInputFiled() {
        gameNameInput.clear();
        numOfHumansInput.clear();
        numOfCopmputersInput.clear();
        playerNameInput.clear();
    }

    public void initGameViewTableTimer() {
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                initGameViewTable();
                initGameViewTableTimer();
            }
        }, UPDATE_TIME);
    }

    public void initGameViewTable() {

        Thread thread = new Thread(() -> {
            ObservableList<GameDetails> gameList = getListOfWaittingGames();
            Platform.runLater(() -> {
                int index = gamesTableView.getSelectionModel().getSelectedIndex();
                gamesTableView.setItems(gameList);
                if (index >= 0) {
                    gamesTableView.getSelectionModel().select(index);
                }
            });
        });
        thread.setDaemon(DAEMON_THREAD);
        thread.start();
    }

    private String getPlayerName() {
        return gameNameInput.getText();
    }

    private void initPlayScrenAndSubMenu(String gameName, String playerName) throws DuplicateGameName_Exception, GameDoesNotExists_Exception, InvalidParameters_Exception {
        try {
            SubMenuController subMenu = (SubMenuController) this.myController.getControllerScreen(Rummikub.SUBMENU_SCREEN_ID);
            subMenu.initWsSetting(service, gameName, playerID);

            this.playerID = rummikubWebService.joinGame(gameName, playerName);
            PlayScreenController gameScreen = (PlayScreenController) this.myController.getControllerScreen(Rummikub.PLAY_SCREEN_ID);
            PlayerDetails myDetails = rummikubWebService.getPlayerDetails(playerID);

            gameScreen.initWsSetting(service, gameName, playerID, myDetails);
            this.timer.cancel();
            this.myController.setScreen(Rummikub.PLAY_SCREEN_ID, gameScreen);

        } catch (Exception ex) {
            onServerLostException();
        }

    }

    private void onServerLostException() {
        Platform.runLater(() -> {
            showErrorMsg(errorMsg, "Lost connection to server");
            Platform.runLater(() -> (clearInputFiled()));
        });
    }

    @FXML
    private void loadGameButtonClicked(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extFilterXML = new FileChooser.ExtensionFilter("XML files (*.xml)", "*.XML");
        fileChooser.getExtensionFilters().add(extFilterXML);

        Platform.runLater(() -> (disableButtonsControls()));
        File file = fileChooser.showOpenDialog(((Button) event.getSource()).getContextMenu());
        if (file != null) {
            try {
                new Thread(() -> {
                    try {
                        loadGame(file);
                    } catch (DuplicateGameName_Exception | IOException | InvalidParameters_Exception | InvalidXML_Exception ex) {
                        showErrorMsg(errorMsg, FAIL_LOADING_FILE_MSG);
                    }
                }).start();
            } catch (Exception ex) {
                Platform.runLater(() -> {
                    showErrorMsg(errorMsg, FAIL_LOADING_FILE_MSG);
                    initAllButton();
                });

            }
        }
        Platform.runLater(() -> (initAllButton()));
    }

    private void loadGame(File file) throws DuplicateGameName_Exception, IOException, InvalidParameters_Exception, InvalidXML_Exception {
        boolean succedLoadingFile = false;
        String content;
        Platform.runLater(() -> {
            resetScreen();
        });
        content = new String(Files.readAllBytes(file.toPath()));
        this.rummikubWebService.createGameFromXML(content);
    }

    private void disableButtonsControls() {
        this.buttonsList.stream().forEach((button) -> {
            button.setDisable(true);
        });
    }

    private void initAllButton() {
        initAddButton();
        initJoinButton();
        this.loadGameButton.setDisable(false);
    }

    private void onTableViewChange() {
        GameDetails gameDetails = this.gamesTableView.getSelectionModel().getSelectedItem();
        String gameName = gameDetails.getName();
        String prompt = NAME_PROMPT;
        String playersOption = "Artur is gay";
        if (gameDetails.isLoadedFromXML()) {
            prompt = "Choose Player name: " + playersOption;
        }
        Platform.runLater(() -> {
            joinButton.setDisable(this.playerNameInput.getText().isEmpty());
        });
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
