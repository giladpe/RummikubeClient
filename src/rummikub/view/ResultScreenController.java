/*
 * this class controlls therusalt screen
 */

package rummikub.view;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import rummikub.gameLogic.model.logic.Settings;
import rummikubFX.Rummikub;
import rummikub.gameLogic.view.ioui.Utils;

public class ResultScreenController implements Initializable, ControlledScreen {

    //FXML members
    @FXML private Button mainMenu;
    @FXML private Button restartGame;
    @FXML private Label resultMsg;
    
    //Private members
    private ScreensController myController;

    //FXML methods
    @FXML
    private void handleMainMenuButtonAction(ActionEvent event) {
         this.myController.setScreen(Rummikub.MAINMENU_SCREEN_ID,(PlayScreenController)myController.getControllerScreen(Rummikub.PLAY_SCREEN_ID));
    }

    @FXML
    private void handleRestartGameButtonAction(ActionEvent event) {
        PlayScreenController gameScreen = (PlayScreenController) this.myController.getControllerScreen(Rummikub.PLAY_SCREEN_ID);
        gameScreen.createNewGame(new Settings(gameScreen.getRummikubLogic().getGameOriginalInputedSettings()));
        this.myController.setScreen(Rummikub.PLAY_SCREEN_ID, gameScreen);
        Platform.runLater(gameScreen::initAllGameComponents);
        //gameScreen.initAllGameComponents();
    }

    //Public methods

    public void updatedGameResultMsg() {
        PlayScreenController gameScreen = (PlayScreenController) this.myController.getControllerScreen(Rummikub.PLAY_SCREEN_ID);
        
        if(gameScreen.getRummikubLogic().isGameOver() && !gameScreen.getRummikubLogic().isTie()) {
            this.resultMsg.setText(Utils.Constants.QuestionsAndMessagesToUser.WINNER_IS + 
                                   Utils.Constants.END_LINE + gameScreen.getRummikubLogic().getWinner().getName());
        }
        else {
            this.resultMsg.setText(Utils.Constants.QuestionsAndMessagesToUser.TIE);
        }
    }

    @Override
    public void setScreenParent(ScreensController parentScreen) {
        this.myController = parentScreen;
    }
        
    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }
}
