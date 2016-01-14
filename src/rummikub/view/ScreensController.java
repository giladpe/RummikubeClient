/*
 * this class is the controller of all screens in the application
 */
package rummikub.view;

import java.util.HashMap;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.DoubleProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;


public class ScreensController extends StackPane {

    //Constatnts:
    private static final double TIME_OF_EFFECT = 800;
    private static final double ZERO_VALUE = 0.0;
    private static final double EFFECT_VALUE = 1.5;    
    private static final int FIRST_CHILD_LOCATION = 0;
    public static final  ResetableScreen NOT_RESETABLE = null;
    
    //Private members:
    private final KeyFrame fadeFrameValue;
    private final KeyFrame fadeInFrameEffect1;
    private final KeyFrame fadeInFrameEffect2;
    private final KeyValue ZeroValueOpacity;
    private final KeyValue opacityValue;
    private final Timeline fadeInEffect;
    private final Duration durationOfEffect;
    private final HashMap<String, Node> gameScreens = new HashMap<>();
    private final HashMap<String, ControlledScreen> screensController = new HashMap<>();
    
    //Constractor:
    public ScreensController() {
        super();
        final DoubleProperty opacity = opacityProperty();
        this.ZeroValueOpacity = new KeyValue(opacity, ZERO_VALUE);
        this.opacityValue = new KeyValue(opacity, EFFECT_VALUE);
        this.durationOfEffect = new Duration(TIME_OF_EFFECT);
        this.fadeFrameValue = new KeyFrame(Duration.ZERO, this.opacityValue);
        this.fadeInFrameEffect1 = new KeyFrame(Duration.ZERO,  this.ZeroValueOpacity);
        this.fadeInFrameEffect2 = new KeyFrame(this.durationOfEffect, this.opacityValue);
        this.fadeInEffect = new Timeline(this.fadeInFrameEffect1, this.fadeInFrameEffect2);
    }

    //Private methods:
    private KeyFrame makeKeyFrameEffect(final String name,ResetableScreen resetableScreen) {
        KeyFrame keyFrameEffect = new KeyFrame(this.durationOfEffect, (ActionEvent event) -> {
            if (resetableScreen!=null){
                resetableScreen.resetScreen();
            }
            getChildren().remove(FIRST_CHILD_LOCATION);
            getChildren().add(FIRST_CHILD_LOCATION, gameScreens.get(name));
            this.fadeInEffect.play();
        },this.ZeroValueOpacity);
                
        return keyFrameEffect;
    }
    
    //Public methods:
    public void addScreen(String name, Node screen) {
        this.gameScreens.put(name, screen);
    }
    public void addScreenController(String name, ControlledScreen controller) {
        this.screensController.put(name, controller);
    }
    public Node getScreen(String name) {
        return this.gameScreens.get(name);
    }

    public void loadScreen(String name, String resource) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(resource));
            Parent loadedScreen = (Parent) fxmlLoader.load();
            ControlledScreen myScreen = ((ControlledScreen) fxmlLoader.getController());
            myScreen.setScreenParent(this);
            addScreenController(name,myScreen);
            addScreen(name, loadedScreen);
        } 
        catch (Exception e) {}
    }
    public ControlledScreen getControllerScreen(String name){
        return screensController.get(name);
    }
    public void setScreen(final String name, ResetableScreen resetableScreen) {
//        if (resetableScreen!=null){
//            resetableScreen.resetScreen();
//        }
        if (!getChildren().isEmpty()) {
            Timeline fade = new Timeline(this.fadeFrameValue, makeKeyFrameEffect(name,resetableScreen));
            fade.play();
        } 
        else {
            setOpacity(ZERO_VALUE);
            getChildren().add(gameScreens.get(name));
            this.fadeInEffect.play();
        }
    }
}

//************************Test Zone*****************************//

//        public void setScreen(final String name,Consumer action) {
//        
//        if (!getChildren().isEmpty()) {
//            Timeline fade = new Timeline(this.fadeFrameValue, makeKeyFrameEffect(name,action));
//            fade.play();
//        } 
//        else {
//            setOpacity(ZERO_VALUE);
//            getChildren().add(gameScreens.get(name));
//            this.fadeInEffect.play();
//        }
//    }

    
//    private KeyFrame makeKeyFrameEffect(final String name,Consumer action) {
//        KeyFrame keyFrameEffect = new KeyFrame(this.durationOfEffect, (ActionEvent event) -> {
//            action.accept(this);
//            getChildren().remove(FIRST_CHILD_LOCATION);
//            getChildren().add(FIRST_CHILD_LOCATION, gameScreens.get(name));
//            this.fadeInEffect.play();
//        },this.ZeroValueOpacity);
//                
//        return keyFrameEffect;
//    }
    
//OLD COPPY OF THE CLASS (COPPY FROM INTERNET):


//public class ScreensController extends StackPane {
//
//    private final HashMap<String, Node> gameScreens = new HashMap<>();
//
//    public ScreensController() {
//        super();
//    }
//
//    public void addScreen(String name, Node screen) {
//        this.gameScreens.put(name, screen);
//    }
//
//    public Node getScreen(String name) {
//        return this.gameScreens.get(name);
//    }
//
//    public boolean loadScreen(String name, String resource) {
//        try {
//            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(resource));
//            Parent loadedScreen = (Parent) fxmlLoader.load();
//            ControlledScreen myScreen = ((ControlledScreen) fxmlLoader.getController());
//            myScreen.setScreenParent(this);
//            addScreen(name, loadedScreen);
//            return true;
//        } catch (Exception e) {
//            return false;
//        }
//    }
//
//    public boolean setScreen(final String name) {
//        if (gameScreens.get(name) != null) {
//            final DoubleProperty opacity = opacityProperty();
//            if (!getChildren().isEmpty()) {
//                Timeline fade = new Timeline(new KeyFrame(Duration.ZERO, new KeyValue(opacity, 1.0)),
//                               new KeyFrame(new Duration(1000), (ActionEvent event) -> {
//                            getChildren().remove(0);
//                            getChildren().add(0, gameScreens.get(name));
//                            Timeline fadeIn = new Timeline(
//                                    new KeyFrame(Duration.ZERO, new KeyValue(opacity, 0.0)),
//                                    new KeyFrame(new Duration(800), new KeyValue(opacity, 1.0)));
//                            fadeIn.play();
//                }, new KeyValue(opacity, 0.0)));
//                fade.play();
//            } else {
//                setOpacity(0.0);
//                getChildren().add(gameScreens.get(name));
//                Timeline fadeIn = new Timeline(
//                        new KeyFrame(Duration.ZERO, new KeyValue(opacity, 0.0)),
//                        new KeyFrame(new Duration(1000), new KeyValue(opacity, 1.0)));
//                fadeIn.play();
//            }
//            return true;
//        } else {
//            //////Exepppppppppppppppppppppppppppppppp
//            System.out.println("Screen Error");
//            return false;
//
//        }
//
//    }
//    
//
//    public boolean unloadScreen(String name) {
//        if (gameScreens.remove(name) == null) {
//            System.out.println("Screen not Exict");
//            return false;
//        } else {
//            return true;
//        }
//    }
//}

//*****