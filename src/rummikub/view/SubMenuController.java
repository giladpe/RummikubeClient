/*
 * this class controlls the sub menu
 */
package rummikub.view;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import rummikub.gameLogic.model.logic.Settings;
import rummikubFX.Rummikub;

public class SubMenuController implements Initializable, ControlledScreen {

    //Private FXML methods
    @FXML private Button saveGame;
    @FXML private Button restartGame;
    @FXML private Button resumeGame;
    @FXML private Button exitToMainMenu;
    @FXML private Button playerQuit;
    
    //Private methods
    private ScreensController myController;

    //Private FXML methods
    @FXML
    private void handleSaveGameButtonAction(ActionEvent event) {
        SaveGameMenuController saveScreen = (SaveGameMenuController) this.myController.getControllerScreen(Rummikub.SAVE_GAME_SCREEN_ID);
        this.myController.setScreen(Rummikub.SAVE_GAME_SCREEN_ID, saveScreen);
    }

    @FXML
    private void handlePlayerQuitButtonAction(ActionEvent event) {
        PlayScreenController gameScreen = (PlayScreenController) this.myController.getControllerScreen(Rummikub.PLAY_SCREEN_ID);
        gameScreen.getRummikubLogic().removeCurrentPlayerFromTheGame();
        
        if (!gameScreen.getRummikubLogic().isGameOver()) {
            gameScreen.swapTurns();
        }
        
        if (isGameOver(gameScreen)) {
            //gameScreen.initAllGameComponents();
            Platform.runLater(gameScreen::initAllGameComponents);
            this.myController.setScreen(Rummikub.PLAY_SCREEN_ID, ScreensController.NOT_RESETABLE);
        }
        else {
            ResultScreenController resultScreen = (ResultScreenController) this.myController.getControllerScreen(Rummikub.RESULT_SCREEN_ID);
            resultScreen.updatedGameResultMsg();
            this.myController.setScreen(Rummikub.RESULT_SCREEN_ID, gameScreen);
        }
    }

    @FXML
    private void handleRestartGameButtonAction(ActionEvent event) {
        PlayScreenController gameScreen = (PlayScreenController) this.myController.getControllerScreen(Rummikub.PLAY_SCREEN_ID);
        gameScreen.createNewGame( new Settings(gameScreen.getRummikubLogic().getGameOriginalInputedSettings()));
        this.myController.setScreen(Rummikub.PLAY_SCREEN_ID, ScreensController.NOT_RESETABLE);
        Platform.runLater(gameScreen::initAllGameComponents);
        //gameScreen.initAllGameComponents();
    }

    @FXML
    private void handleResumeGameButtonAction(ActionEvent event) {
        PlayScreenController gameScreen = (PlayScreenController) this.myController.getControllerScreen(Rummikub.PLAY_SCREEN_ID);
        gameScreen.showCurrentGameBoardAndCurrentPlayerHand();
        this.myController.setScreen(Rummikub.PLAY_SCREEN_ID, ScreensController.NOT_RESETABLE);
    }

    @FXML
    private void handleExitToMainMenuButtonAction(ActionEvent event) {
        this.myController.setScreen(Rummikub.MAINMENU_SCREEN_ID, (PlayScreenController) myController.getControllerScreen(Rummikub.PLAY_SCREEN_ID));
    }
    
    //private methods
    
    private boolean isGameOver(PlayScreenController gameScreen) {
        return !(gameScreen.getRummikubLogic().isGameOver() || gameScreen.getRummikubLogic().isOnlyOnePlayerLeft() || !gameScreen.getRummikubLogic().isHumanPlayerLeftInGame());
    }
    
    //Public methods
    @Override
    public void initialize(URL url, ResourceBundle rb) { }

    @Override
    public void setScreenParent(ScreensController parentScreen) {
        this.myController = parentScreen;
    }
}
