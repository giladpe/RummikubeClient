/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rummikub.view;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import java.io.File;
import java.io.InputStream;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
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
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
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

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        //this.address.setText("http://localhost");
        //this.port.setText("8080");

        this.address.setDisable(true);
        this.port.setDisable(true);
        try {
            getAddressFromFile();
            this.address.setText(addressF);
            this.port.setText(portF);
        } catch (Exception ex) {
            this.address.setDisable(false);
            this.port.setDisable(false);
        }
        changeServerCheckBox.selectedProperty().addListener((ObservableValue<? extends Boolean> ov, Boolean old_val, Boolean new_val) -> {
            initServerChanges(new_val);
        });
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
            //URL location = new URL( address.getText() + ":" + port.getText() + "/RummikubApi/RummikubWebServiceService?wsdl");
            if (!address.isDisable()) {
                this.addressF=this.address.getText();
                this.portF=this.port.getText();
                String[] urlAttribute = {this.addressF,portF};
                String[] elements = {ADDRESS_ELEMENT, PORT_ELEMENT};
                CreateXMLDoc(ROOT_ELEMENT, elements, urlAttribute);
            }
            URL location = new URL(HTTP + addressF + ":" + portF + RUMMIKUB_API);
            //create a new service with the URL
            ServerSelectController gameSeettingsScene = (ServerSelectController) this.myController.getControllerScreen(Rummikub.SERVER_SELECT_SCREEN_ID);
            this.myController.setScreen(Rummikub.SERVER_SELECT_SCREEN_ID, gameSeettingsScene);
            gameSeettingsScene.setService(new RummikubWebServiceService(location));
            this.myController.setScreen(Rummikub.SERVER_SELECT_SCREEN_ID, gameSeettingsScene);
            resetScreen();
        } catch (MalformedURLException ex) {
            this.errorMsg.setText("Invalid Url");/////to change
        } catch (TransformerConfigurationException ex) {
            this.errorMsg.setText("Can not create file");
        } catch (Exception ex) {
            this.errorMsg.setText("Can not create file");
        }
        //ServerSelectController gameSeettingsScene = (ServerSelectController) this.myController.getControllerScreen(Rummikub.SERVER_SELECT_SCREEN_ID);
        //    this.myController.setScreen(Rummikub.SERVER_SELECT_SCREEN_ID, gameSeettingsScene);
        //    resetScreen();
        
    }

    private void getAddressFromFile() throws SAXException, IOException, ParserConfigurationException {
        File fXmlFile = new File(RESOURCES_FOLDER + CONIGURATION_FILE);
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(fXmlFile);
        //optional, but recommended
        //read this - http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work
        doc.getDocumentElement().normalize();
        NodeList nList = doc.getElementsByTagName(ROOT_ELEMENT);
        Node nNode = nList.item(0);
        if (nNode.getNodeType() == Node.ELEMENT_NODE) {
            Element eElement = (Element) nNode;
            this.addressF = eElement.getElementsByTagName(ADDRESS_ELEMENT).item(0).getTextContent();
            this.portF = eElement.getElementsByTagName(PORT_ELEMENT).item(0).getTextContent();
        }
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
        closeGameAppScene(event);
    }

    private void closeGameAppScene(ActionEvent event) {
        (((javafx.scene.Node) event.getSource()).getScene().getWindow()).hide();
    }

    @FXML
    private void handleServrtPortTextChange(ActionEvent event) {
    }

    public static void CreateXMLDoc(String root, String[] elements, String[] children) throws TransformerConfigurationException {

        File dir = new File(RESOURCES_FOLDER + CONIGURATION_FILE);

        try {
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
            
        } catch (ParserConfigurationException pce) {
            pce.printStackTrace();
        } catch (TransformerException tfe) {
            tfe.printStackTrace();
        }

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

