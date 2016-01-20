/*
 * This class responsible for starting the application of rummikub game
 */
package rummikubFX;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import rummikub.view.ScreensController;

public class Rummikub extends Application {

    //Constants:

    public static final String PLAY_SCREEN_ID = "playScreen";
    public static final String PLAY_SCREEN_FXML = "PlayScreen.fxml";
    public static final String SUBMENU_SCREEN_ID = "subMenu";
    public static final String SUBMENU_SCREEN_FXML = "SubMenu.fxml";
    public static final String RESULT_SCREEN_ID = "resultScreen";
    public static final String RESULT_SCREEN_FXML = "ResultScreen.fxml";
    public static final String GAME_SELECT_SCREEN_ID = "ServerSelectScreen";
    public static final String GAME_SELECT_SCREEN_FXML = "ServerSelect.fxml";
    public static final String LOGIN_SCREEN_ID = "loginScreen";
    public static final String LOGIN_SCREEN_FXML = "LogIn.fxml";
    
    @Override
    public void start(Stage primaryStage) {
        
        primaryStage.setOnCloseRequest((WindowEvent event) -> {
            Platform.exit();
            System.exit(0);
        });

        ScreensController screensController = new ScreensController();
        screensController.loadScreen(LOGIN_SCREEN_ID, LOGIN_SCREEN_FXML);
        screensController.loadScreen(PLAY_SCREEN_ID, PLAY_SCREEN_FXML);
        screensController.loadScreen(SUBMENU_SCREEN_ID, SUBMENU_SCREEN_FXML);
        screensController.loadScreen(RESULT_SCREEN_ID, RESULT_SCREEN_FXML);
        screensController.loadScreen(GAME_SELECT_SCREEN_ID, GAME_SELECT_SCREEN_FXML);
        screensController.setScreen(LOGIN_SCREEN_ID, ScreensController.NOT_RESETABLE);
        StackPane root = new StackPane();
        root.getChildren().addAll(screensController);
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
}

