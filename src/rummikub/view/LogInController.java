/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rummikub.view;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import rummikub.client.ws.RummikubWebService;
import rummikub.client.ws.RummikubWebServiceService;
import rummikubFX.Rummikub;

/**
 * FXML Controller class
 *
 * @author giladPe
 */
public class LogInController implements Initializable, ControlledScreen, ResetableScreen {

    @FXML
    private GridPane GamesSettings;
    @FXML
    private TextField address;
    @FXML
    private Button exit;
    @FXML
    private HBox configPlayer1;
    @FXML
    private TextField port;
    @FXML
    private Label errorMsg;
    private ScreensController myController;
    @FXML
    private Button loginButton;
    @FXML
    private CheckBox changeServerCheckBox;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        this.address.setText("localhost");
        this.address.setDisable(true);
        this.port.setText("8080");
        this.port.setDisable(true);
        changeServerCheckBox.selectedProperty().addListener((ObservableValue<? extends Boolean> ov, Boolean old_val, Boolean new_val) -> {
                initServerChanges(new_val);});
        
// TODO
    }    


    @Override
    public void setScreenParent(ScreensController parentScreen) {
        this.myController = parentScreen;
    }

    @Override
    public void resetScreen() {
        
    }

    @FXML
    private void handleLoginButtonAction(ActionEvent event) {
     
        try {
            //create a URL
            URL location = new URL("http://" + address.getText() + ":" + port.getText() + "/RummikubApi/RummikubWebServiceService?wsdl");
            //create a new service with the URL
            ServerSelectController gameSeettingsScene = (ServerSelectController) this.myController.getControllerScreen(Rummikub.SERVER_SELECT_SCREEN_ID);
            this.myController.setScreen(Rummikub.SERVER_SELECT_SCREEN_ID, gameSeettingsScene);
            RummikubWebServiceService service = new RummikubWebServiceService(location);
            gameSeettingsScene.setService(service);
            this.myController.setScreen(Rummikub.SERVER_SELECT_SCREEN_ID,gameSeettingsScene);
            
            resetScreen();
        } catch (MalformedURLException ex) {
            this.errorMsg.setText("Invalid Url");/////to change
        }
        //ServerSelectController gameSeettingsScene = (ServerSelectController) this.myController.getControllerScreen(Rummikub.SERVER_SELECT_SCREEN_ID);
        //    this.myController.setScreen(Rummikub.SERVER_SELECT_SCREEN_ID, gameSeettingsScene);
        //    resetScreen();

    }

    private void initServerChanges(Boolean new_val) {
       this.address.setDisable(!new_val);
       this.port.setDisable(!new_val);
    }

    @FXML
    private void handleServerAddressTextChange(ActionEvent event) {
    }

    @FXML
    private void handleExitButtonAction(ActionEvent event) {
    }

    @FXML
    private void handleServrtPortTextChange(ActionEvent event) {
    }
}

// <editor-fold defaultstate="collapsed" desc="Web service Info">
// Web service Info - START //
//*Endpoint:
//  -Service Name: {http://rummikub.ws/}RummikubWebServiceService
//  -Port Name: {http://rummikub.ws/}RummikubWebServicePort
//
//*Information:
//  -Address: http://localhost:8080/RummikubApi/RummikubWebServiceService
//  -WSDL: http://localhost:8080/RummikubApi/RummikubWebServiceService?wsdl
//  -Implementation class: rummikub.ws.RummikubWS
// Web service Info - END //
// </editor-fold>


