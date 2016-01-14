/*
 * this class represents the serie in game
 */
package rummikub.view.viewObjects;

import javafx.geometry.Orientation;
import javafx.scene.layout.FlowPane;

public class AnimatedSeriePane extends FlowPane {

    //Constants
    public final static double PREF_HIFGT = 40;
    private final static String CONSTRACTOR_STYLE = "-fx-border-color: gray; -fx-border-width: 1";
    
    //Constractor
    public AnimatedSeriePane() {
        setPrefHeight(PREF_HIFGT);
        setOrientation(Orientation.VERTICAL);
        setHgap(AnimatedTilePane.TILE_SPACING);
        setStyle(CONSTRACTOR_STYLE);
    }
    
    //Public methods
    public void setSize() {
        this.setMinWidth(this.getChildren().size() * (AnimatedTilePane.TILE_WIDTH + AnimatedGameBoardPane.TILE_SPACING));
        this.setPrefWidth(this.getChildren().size() * (AnimatedTilePane.TILE_WIDTH + AnimatedGameBoardPane.TILE_SPACING));
    }
}
