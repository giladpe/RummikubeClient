/*
 * this class is responsible to represent a Board of series in the game
 */
package rummikub.view.viewObjects;

import java.awt.Point;
import javafx.collections.ListChangeListener;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.input.DataFormat;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.FlowPane;
import rummikub.gameLogic.controller.rummikub.SingleMove;
import rummikub.view.ResetableScreen;


public class AnimatedGameBoardPane extends FlowPane implements ResetableScreen {

    //Constants
    public final static int NORMALIZATON_INDEX_OF_NEW_SERIE_ADDING_ARREA = 1;
    public final static double TILE_SPACING = 1.5;
    public final static double BOARD_MIN_SIZE = 300;
    public final static double HORIZONTAL_GAP = 30;
    public final static double VERTICAL_GAP = 20;
    public final static double PADDING_VALUE = 25;
    public final static double HIGHT_OF_NEW_SERIE_ADDING_AREA = 40;
    public final static double WIDTH_OF_NEW_SERIE_ADDING_AREA = 120;
    public final static String LABEL_OF_NEW_SERIE_ADDING_AREA = "Add New Series";
    public final static String STYLE_OF_NEW_SERIE_ADDING_AREA_NORMAL_VIEW = "-fx-border-color: gray; -fx-border-width: 1";
    public final static String STYLE_OF_NEW_SERIE_ADDING_AREA_DRAG_ENTER_VIEW = "-fx-background-color: red";
    public final static String STYLE_OF_NEW_SERIE_ADDING_AREA_DRAG_EXITED_VIEW = "-fx-background-color: none; -fx-border-color: gray; -fx-border-width: 1";

    //Constaractor
    public AnimatedGameBoardPane() {
        super();
        createFlowPane();
    }

    //Private methods
    private void createFlowPane() {
        this.setMinSize(BOARD_MIN_SIZE, BOARD_MIN_SIZE);
        this.setHgap(HORIZONTAL_GAP);
        this.setVgap(VERTICAL_GAP);
        this.setAlignment(Pos.CENTER);
        this.setPadding(new Insets(PADDING_VALUE));
        createNewSerieAddinArea();
    }

    private void createNewSerieAddinArea() {
        FlowPane newSerieAddingArea = new FlowPane();
        Label label = new Label();
        label.setText(LABEL_OF_NEW_SERIE_ADDING_AREA);
        newSerieAddingArea.getChildren().add(label);
        newSerieAddingArea.setMinSize(WIDTH_OF_NEW_SERIE_ADDING_AREA, HIGHT_OF_NEW_SERIE_ADDING_AREA);
        newSerieAddingArea.setMaxSize(WIDTH_OF_NEW_SERIE_ADDING_AREA, HIGHT_OF_NEW_SERIE_ADDING_AREA);
        newSerieAddingArea.setPrefSize(WIDTH_OF_NEW_SERIE_ADDING_AREA, HIGHT_OF_NEW_SERIE_ADDING_AREA);
        newSerieAddingArea.setAlignment(Pos.CENTER);
        newSerieAddingArea.setStyle(STYLE_OF_NEW_SERIE_ADDING_AREA_NORMAL_VIEW);
        this.getChildren().add(newSerieAddingArea);
        setNewSeriesEvents(newSerieAddingArea);
    }

    private void setNewSeriesEvents(FlowPane newSerieAddingArea) {
       
        newSerieAddingArea.setOnDragEntered((event) -> {
            if (event.getDragboard().getContent(DataFormat.RTF).getClass() == AnimatedTilePane.class) {
                newSerieAddingArea.setStyle(STYLE_OF_NEW_SERIE_ADDING_AREA_DRAG_ENTER_VIEW);
            }
            event.consume();
        });

        newSerieAddingArea.setOnDragExited((event) -> {
            newSerieAddingArea.setStyle(STYLE_OF_NEW_SERIE_ADDING_AREA_DRAG_EXITED_VIEW);
            event.consume();
        });

        newSerieAddingArea.setOnDragOver(this::onDragOver);
        newSerieAddingArea.setOnDragDropped(this::onDragDropped);
    }
    
    private void onDragOver(DragEvent event) {
        if (event.getDragboard().getContent(DataFormat.RTF).getClass() == AnimatedTilePane.class) {
            event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
        }
        event.consume();    
    }
    
    private void onDragDropped(DragEvent event) {
        Dragboard db = event.getDragboard();
        AnimatedTilePane currTile = (AnimatedTilePane) db.getContent(DataFormat.RTF);
        SingleMove singleMove;
        boolean success = db.getContent(DataFormat.RTF).getClass() == AnimatedTilePane.class;
        
        if (success) {
            AnimatedSeriePane newSerie = new AnimatedSeriePane();
        
            newSerie.getChildren().addListener((ListChangeListener.Change<? extends Node> c) -> {
                onAddingAndRemovingSerie(newSerie);
            });

            int xTarget = this.getChildren().isEmpty() ? 0 : this.getChildren().size() - 1;
            int yTarget = 0;
            Point pTarget = new Point(xTarget, yTarget);
            
            if (currTile.isTileParentIsSerie()) {
                //board to new serie in board
                Point pSource=new Point(currTile.getSerieIndexFromTile(currTile), currTile.getIndexOfTileInSerie(currTile));
                singleMove = new SingleMove(pTarget, pSource, SingleMove.MoveType.BOARD_TO_BOARD);
            }
            else {
                singleMove = new SingleMove(pTarget, currTile.getIndexOfTileInHand(currTile), SingleMove.MoveType.HAND_TO_BOARD);
            }
            
            currTile.setSingleMove(singleMove);
            success = currTile.getIsLegalMove();
        }
        event.setDropCompleted(success);
        event.consume();        
    }
    
    private void onAddingAndRemovingSerie(AnimatedSeriePane newSerie) {
        if (newSerie.getChildren().isEmpty()) {
            removeEmptySerie(newSerie);
        } else {
            newSerie.setSize();
        }
    }
    
    //Public methods
    @Override
    public void resetScreen() {
        this.getChildren().clear();
        createNewSerieAddinArea();
    }

    public void removeEmptySerie(FlowPane holdingSerie) {
        this.getChildren().remove(holdingSerie);
    }
}


//************************Test Zone*****************************//

//***************************END********************************//

