/*
 * this class controlls the sub menu
 */
package rummikub.view;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import rummikub.client.ws.GameDetails;
import rummikub.client.ws.GameDoesNotExists_Exception;
import rummikub.client.ws.GameStatus;
import rummikub.client.ws.InvalidParameters_Exception;
import rummikub.client.ws.PlayerDetails;
import rummikub.client.ws.RummikubWebService;
import rummikub.client.ws.RummikubWebServiceService;
import rummikub.gameLogic.model.logic.Settings;
import rummikubFX.Rummikub;

public class SubMenuController implements Initializable, ControlledScreen, ServerConnection {

    @FXML
    private Button resumeGame;

    //Private methods
    private ScreensController myController;
    @FXML
    private Button ResignedGame;
    @FXML
    private Button Exit;
    private RummikubWebServiceService service;
    private RummikubWebService rummikubWebService;
    private int playerID;
    private String gameName;

    @FXML
    private void handleResumeGameButtonAction(ActionEvent event) {

        this.myController.setScreen(Rummikub.PLAY_SCREEN_ID, ScreensController.NOT_RESETABLE);
    }

    //Public methods
    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }

    @Override
    public void setScreenParent(ScreensController parentScreen) {
        this.myController = parentScreen;
    }

    @FXML
    private void handlePlayerResignedGameButtonAction(ActionEvent event) {
        //PlayScreenController playScreen = (PlayScreenController) this.myController.getControllerScreen(Rummikub.PLAY_SCREEN_ID);
        try {
            GameDetails game = this.rummikubWebService.getGameDetails(gameName);
            GameStatus st=game.getStatus();
            if (st == GameStatus.ACTIVE) {
                this.rummikubWebService.resign(playerID);
            }

        } catch (InvalidParameters_Exception | GameDoesNotExists_Exception ex) {
            int i=0;
        }
        //ServerSelectController gameSelectScene = (ServerSelectController) this.myController.getControllerScreen(Rummikub.SERVER_SELECT_SCREEN_ID);
        //this.myController.setScreen(Rummikub.SERVER_SELECT_SCREEN_ID, gameSelectScene);
        //playScreen.resetScreen();
    }

    @FXML
    private void handleExitButtonAction(ActionEvent event) {
        try {
            this.rummikubWebService.resign(playerID);
            closeGameAppScene(event);
        } catch (InvalidParameters_Exception ex) {
            closeGameAppScene(event);
        }
    }

    private void closeGameAppScene(ActionEvent event) {
        (((Node) event.getSource()).getScene().getWindow()).hide();
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

    @Override
    public RummikubWebServiceService getService() {
        return service;
    }
    public void initWsSetting(RummikubWebServiceService service, String gameName, int playerID) {
        setService(service);
        this.gameName = gameName;
        this.playerID = playerID;
        //getRummikubeWsEvents();
    }
}
