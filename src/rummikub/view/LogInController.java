/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rummikub.view;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
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
import javafx.util.Duration;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
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
    private String addressF;
    private String portF;
    public static final String RUMMIKUB_API = "/RummikubApi/RummikubWebServiceService?wsdl";
    public static final String RESOURCES_FOLDER = "./src/resources/";
    public static final String CONIGURATION_FILE = "serverConfig.xml";
    private static final String ROOT_ELEMENT = "serverUrl";
    private static final String ADDRESS_ELEMENT = "address";
    private static final String PORT_ELEMENT = "port";
    private static final String HTTP = "http://";
    private static final String CREATE_FILE_ERROR="Can not create file";
    private static final String INVALID_URL="Invalid Url";
    private static final boolean DAEMON_THREAD = true;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        init();

        this.address.textProperty().addListener((ObservableValue<? extends String> ov, String t, String t1) -> {
            addressF = this.address.getText();

            initLoginButton();
        });

        this.port.textProperty().addListener((ObservableValue<? extends String> ov, String t, String t1) -> {
            this.portF = this.port.getText();

            initLoginButton();
        });
        changeServerCheckBox.selectedProperty().addListener((ObservableValue<? extends Boolean> ov, Boolean old_val, Boolean new_val) -> {
            initServerChanges(new_val);
        });
    }

    @Override
    public void setScreenParent(ScreensController parentScreen) {
        this.myController = parentScreen;
    }

    @Override
    public void resetScreen() {
        init();
    }

    @FXML
    private void handleLoginButtonAction(ActionEvent event) {
        this.loginButton.setDisable(true);
        try {
            if (!address.isDisable()) {
                String[] urlAttribute = {this.addressF, portF};
                String[] elements = {ADDRESS_ELEMENT, PORT_ELEMENT};

                Thread newThread = new Thread(() -> {
                    try {
                        CreateXMLDoc(ROOT_ELEMENT, elements, urlAttribute);
                    } catch (Exception ex) {
                        showErrorMsg(errorMsg, CREATE_FILE_ERROR);
                    }
                });
                newThread.setDaemon(DAEMON_THREAD);
                newThread.start();
            }
            URL location = new URL(HTTP + addressF + ":" + portF + RUMMIKUB_API);
            GameSelectController gameSelectScene = (GameSelectController) this.myController.getControllerScreen(Rummikub.GAME_SELECT_SCREEN_ID);

            gameSelectScene.setService(new RummikubWebServiceService(location));
            this.myController.setScreen(Rummikub.GAME_SELECT_SCREEN_ID, gameSelectScene);
            resetScreen();
            this.loginButton.setDisable(false);
        } catch (MalformedURLException ex) {
            this.errorMsg.setText(INVALID_URL);
        } catch (Exception ex) {
            this.errorMsg.setText(CREATE_FILE_ERROR);
        }
        loginButton.setDisable(false);
        
    }

    private void loadServerFromFile() {
        try {
            File fXmlFile = new File(RESOURCES_FOLDER + CONIGURATION_FILE);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(fXmlFile);
            doc.getDocumentElement().normalize();
            NodeList nList = doc.getElementsByTagName(ROOT_ELEMENT);
            Node nNode = nList.item(0);
            if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                Element eElement = (Element) nNode;
                this.addressF = eElement.getElementsByTagName(ADDRESS_ELEMENT).item(0).getTextContent();
                this.portF = eElement.getElementsByTagName(PORT_ELEMENT).item(0).getTextContent();
            }
            Platform.runLater(() -> {
                this.address.setText(addressF);
                this.port.setText(portF);
            });
        } catch (Exception ex) {
            Platform.runLater(() -> {
                this.errorMsg.setText("cannot open file");
                this.addressF = this.portF = "";
                this.address.setDisable(false);
                this.port.setDisable(false);
            });
        }
    }

    private void initServerChanges(Boolean new_val) {
        this.address.setDisable(!new_val);
        this.port.setDisable(!new_val);
        if (!new_val) {
            Thread thread = new Thread(() -> {
                loadServerFromFile();
            });
            thread.setDaemon(DAEMON_THREAD);
            thread.start();
        }
    }

    @FXML
    private void handleServerAddressTextChange(ActionEvent event) {
    }

    @FXML
    private void handleExitButtonAction(ActionEvent event) {
        closeGameAppScene(event);
    }

    private void closeGameAppScene(ActionEvent event) {
        (((javafx.scene.Node) event.getSource()).getScene().getWindow()).hide();
    }

    @FXML
    private void handleServrtPortTextChange(ActionEvent event) {
    }

    public static void CreateXMLDoc(String root, String[] elements, String[] children) throws TransformerConfigurationException, TransformerException, ParserConfigurationException {
        File dir = new File(RESOURCES_FOLDER + CONIGURATION_FILE);
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
        Document doc = docBuilder.newDocument();
        Element rootElement = doc.createElement(root);
        doc.appendChild(rootElement);

        for (int i = 0; i < children.length; i++) {
            Element element = doc.createElement(elements[i]);
            element.appendChild(doc.createTextNode(children[i]));
            rootElement.appendChild(element);
        }
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        DOMSource source = new DOMSource(doc);

        StreamResult result = new StreamResult(dir);
        transformer.transform(source, result);
    }

    private void init() {
        this.address.setDisable(true);
        this.port.setDisable(true);

        Thread newThread = new Thread(() -> {
            loadServerFromFile();
        });
        newThread.setDaemon(DAEMON_THREAD);
        newThread.start();
    }

    private void initLoginButton() {
        this.loginButton.setDisable(this.address.getText().isEmpty() || this.port.getText().isEmpty());
    }
    public static void disappearAnimation(javafx.scene.Node node) {
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

