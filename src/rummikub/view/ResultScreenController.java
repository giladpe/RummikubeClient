/*
 * this class controlls therusalt screen
 */

package rummikub.view;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import rummikub.gameLogic.view.ioui.Utils;
import rummikubFX.Rummikub;

public class ResultScreenController implements Initializable, ControlledScreen {

    @FXML private Label resultMsg;
    
    //Private members
    private ScreensController myController;
    @FXML
    private Button gameSelect;
    @FXML
    private Button Exit;

    //FXML methods


    //Public methods

    public void updatedGameResultMsg(String winner) {
        resultMsg.setText(Utils.Constants.QuestionsAndMessagesToUser.WINNER_IS + Utils.Constants.END_LINE + winner);
    }

    @Override
    public void setScreenParent(ScreensController parentScreen) {
        this.myController = parentScreen;
    }
        
    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }

    @FXML
    private void handleGameSelectButtonAction(ActionEvent event) {
        this.myController.setScreen(Rummikub.SERVER_SELECT_SCREEN_ID, ScreensController.NOT_RESETABLE);
    }

    @FXML
    private void handleExitButtonAction(ActionEvent event) {
        closeGameAppScene(event);
    }
    
    private void closeGameAppScene(ActionEvent event) {
        (((Node) event.getSource()).getScene().getWindow()).hide();
    }
}
