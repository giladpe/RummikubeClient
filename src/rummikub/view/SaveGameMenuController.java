/*
 * this class controlles the save game menu
 */
package rummikub.view;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.FileChooser;
import javax.xml.bind.JAXBException;
import org.xml.sax.SAXException;
import rummikubFX.Rummikub;
import rummikub.gameLogic.model.logic.GameLogic;
import rummikub.gameLogic.view.ioui.JaxBXmlParser;

public class SaveGameMenuController implements Initializable, ControlledScreen, ResetableScreen {
    //Constants
    private final String SAVED = "Game was saved!";
    private final String NOT_SAVED = "Error game was not saved!";
    private final boolean HIDE_MSG = false;
    private static final boolean ENABLED = false;


    //Private FXML members
    @FXML private Button save;
    @FXML private Button saveAs;
    @FXML private Button backToPrevMenu;
    @FXML private Label msg;
    
    //Private members
    private final ArrayList<Button> buttonsList = new ArrayList<>();


    //Private members
    private ScreensController myController;

    //Private FXML methods
    
    @FXML
    protected void handleBackToPrevMenuButtonAction(ActionEvent event) {
        this.myController.setScreen(Rummikub.SUBMENU_SCREEN_ID, ScreensController.NOT_RESETABLE);
    }

    @FXML
    protected void handleSaveGameButtonAction(ActionEvent event) {
        Platform.runLater(() -> { enableOrDisableButtonsControls(!ENABLED); });
        try {
            new Thread(() -> { saveGame(); }).start();
        }
        catch (Exception ex) {
            this.myController.setScreen(Rummikub.MAINMENU_SCREEN_ID, ScreensController.NOT_RESETABLE);
        }
        Platform.runLater(() -> { enableOrDisableButtonsControls(ENABLED); });
        this.myController.setScreen(Rummikub.PLAY_SCREEN_ID, ScreensController.NOT_RESETABLE);
    }

    @FXML
    protected void handleSaveAsButtonAction(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Resource File");
        FileChooser.ExtensionFilter extFilterXML = new FileChooser.ExtensionFilter("XML files (*.xml)", "*.XML");
        fileChooser.getExtensionFilters().add(extFilterXML);
        
        Platform.runLater(() -> { enableOrDisableButtonsControls(!ENABLED); });
        File file = fileChooser.showSaveDialog(((Button) event.getSource()).getContextMenu());

        if (file != null) {
            try {
                new Thread(() -> { saveAsGame(file.getPath()); }).start();
            }
            catch (Exception ex) {
                this.myController.setScreen(Rummikub.MAINMENU_SCREEN_ID, ScreensController.NOT_RESETABLE);
            }
        }
        Platform.runLater(() -> { enableOrDisableButtonsControls(ENABLED); });
    }

    //Public methods
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        this.buttonsList.add(this.backToPrevMenu);
        this.buttonsList.add(this.save);
        this.buttonsList.add(this.saveAs);
    }
    
    @Override
    public void setScreenParent(ScreensController parentScreen) {
        this.myController = parentScreen;
    }
    
    @Override
    public void resetScreen() {
        PlayScreenController gameScreen = (PlayScreenController) this.myController.getControllerScreen(Rummikub.PLAY_SCREEN_ID);
        boolean canSave, canSaveAs;
        
        canSaveAs = !gameScreen.getIsUserMadeFirstMoveInGame();
        canSave = isGameSavedBefor() && canSaveAs;
        this.save.setDisable(!canSave);
        this.saveAs.setDisable(!canSaveAs);
        msg.setVisible(HIDE_MSG);
    }

    //Private methods
    private void saveAsGame(String filePath) {
        PlayScreenController gameScreen = (PlayScreenController) this.myController.getControllerScreen(Rummikub.PLAY_SCREEN_ID);
        boolean succedSavingFile = false;
        GameLogic rummikubLogic = gameScreen.getRummikubLogic();

        try {
            succedSavingFile = JaxBXmlParser.saveAsSettingsToXml(filePath, rummikubLogic.getPlayers(),
                    rummikubLogic.getGameBoard(),
                    rummikubLogic.getGameSettings().getGamesName(),
                    rummikubLogic.getCurrentPlayer().getName());
        } 
        catch (SAXException | JAXBException | IOException ex) {
            succedSavingFile = false;
        } 
        finally {
            final boolean saveResult = succedSavingFile;
            Platform.runLater(() -> { handleMsg(saveResult); });
        }
    }

    private void saveGame() {
        PlayScreenController gameScreen = (PlayScreenController) this.myController.getControllerScreen(Rummikub.PLAY_SCREEN_ID);
        boolean succedSavingFile = false;
        GameLogic rummikubLogic = gameScreen.getRummikubLogic();
                
        try {
            succedSavingFile = JaxBXmlParser.saveSettingsToXml(rummikubLogic.getPlayers(),
                                                               rummikubLogic.getGameBoard(),
                                                               rummikubLogic.getGameSettings().getGamesName(),
                                                               rummikubLogic.getCurrentPlayer().getName());

        } catch (SAXException | JAXBException | IOException ex) {
            succedSavingFile = false;
        } 
        finally {
            final boolean saveResult = succedSavingFile;
            Platform.runLater(() -> { handleMsg(saveResult); });
            Thread.currentThread().stop();
        }
    }
    
    private void enableOrDisableButtonsControls(boolean state) {
        this.buttonsList.stream().forEach((button) -> { button.setDisable(state); });
    }
    
    private boolean isGameSavedBefor() {
        boolean savedBefore;
    
        savedBefore = JaxBXmlParser.getLastPathSaved() != null; 
        
        return savedBefore;
    }

    private void handleMsg(boolean succedSavingFile) {
        resetScreen();
        
        if (!succedSavingFile) {
            msg.setText(this.NOT_SAVED);
            msg.setStyle("-fx-text-fill: #ff0000");
        } 
        else {
            msg.setText(this.SAVED);
            msg.setStyle("-fx-text-fill: blue");
        }
        msg.setVisible(!HIDE_MSG);

    }
}
