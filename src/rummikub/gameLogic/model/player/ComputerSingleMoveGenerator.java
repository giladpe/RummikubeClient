/*
 * generates single move of computer player
 */
package rummikub.gameLogic.model.player;

import rummikub.gameLogic.controller.rummikub.SingleMove;
import java.awt.Point;
import java.util.ArrayList;
import rummikub.gameLogic.model.gameobjects.Board;
import rummikub.gameLogic.model.gameobjects.Serie;
import rummikub.gameLogic.model.gameobjects.Tile;

public class ComputerSingleMoveGenerator {

    //Constants
    public static final long SLEEP_TIME_IN_MILLISECOUNDS = 0;
    private static final int NOT_FOUND = -1;
    
    //Members
    private Board gameBoard;
    private int sizeOfBoardBeforeMove;
    private  ArrayList<Tile> computerPlayerHand;
    private ArrayList<Tile> serieToPlaceOnBoard;
    private boolean isTurnFinnised;
    private boolean isTurnSkipped;
    private int moveCounter;
    
    //Constructor
    public ComputerSingleMoveGenerator() {
        initComputerSingleMoveGenerator();
    }
    
    //Private Methods
    private SingleMove generateSingleMoveOfFullSerie() {
        SingleMove singleMove = null;
        int indexInHand, lineInBoard, indexInSerie;
        final int indexOfFirstTile = 0;
        Point pWhereToPlaceInBoard;
        
        if (this.serieToPlaceOnBoard != null)
        {
            if (!this.serieToPlaceOnBoard.isEmpty()) {
                indexInHand = this.computerPlayerHand.indexOf(this.serieToPlaceOnBoard.remove(indexOfFirstTile));
                
                if (indexInHand == NOT_FOUND) {
                    this.serieToPlaceOnBoard.clear();
                }
                else {
                    lineInBoard = this.sizeOfBoardBeforeMove;
                    indexInSerie = this.gameBoard.getSeries(lineInBoard).getSizeOfSerie();
                    pWhereToPlaceInBoard = new Point(lineInBoard, indexInSerie);
                    singleMove = new SingleMove(pWhereToPlaceInBoard, indexInHand, SingleMove.MoveType.HAND_TO_BOARD);
                }
            }
        }
        
        return singleMove;
    }

    private void initMembers(ArrayList<Tile> playerHand, Board gameBoard) {
        this.computerPlayerHand = new ArrayList<>(playerHand);
        this.gameBoard = new Board(gameBoard.getListOfSerie());
        this.isTurnFinnised = false;
    }

    // Public members
    public final void initComputerSingleMoveGenerator() {
        this.computerPlayerHand = new ArrayList<>();
        this.gameBoard = new Board();
        this.sizeOfBoardBeforeMove = gameBoard.boardSize();
        this.serieToPlaceOnBoard = new ArrayList<>();
        this.isTurnFinnised = false;
        this.isTurnSkipped = false;
        this.moveCounter = 0;    
    }
    
    public SingleMove generateSingleMove(ArrayList<Tile> playerHand, Board gameBoard) {
        SingleMove singleMove;
        
        initMembers(playerHand, gameBoard);
        singleMove = generateSingleMoveOfFullSerie();
        
        if(singleMove != null){
            this.moveCounter++;
        }
        
        this.isTurnFinnised = singleMove == null;
        this.isTurnSkipped = (this.moveCounter == 0 && singleMove == null);
        
        try {
            if (SLEEP_TIME_IN_MILLISECOUNDS > 0) {
                Thread.sleep(SLEEP_TIME_IN_MILLISECOUNDS);
            }
        }
        catch (InterruptedException ex) {
        }
        
        return singleMove;
    }

    public boolean isFinishedGeneratingLastSerie() {
        return this.serieToPlaceOnBoard.isEmpty();
    }
    
    public boolean isTurnFinnised() {
        return this.isTurnFinnised;
    }
    
    public boolean isTurnSkipped() {
        return this.isTurnSkipped;
    }

    public void setSerieToPlaceOnBoard(Serie serie) {
        if(serie != null && !serie.isEmptySeries()) {
            this.serieToPlaceOnBoard = new ArrayList<>(serie.getSerieOfTiles());
        }
    }

    public void setBoardSizeBeforeMove(int sizeOfBoardBeforeMove) {
        this.sizeOfBoardBeforeMove = sizeOfBoardBeforeMove;
    }
}
