/*
 * this class represnts the move of the player before it is 
 * changed by the logic unit that decides whether it is valid move or not
 */
package rummikub.gameLogic.model.logic;

import rummikub.gameLogic.controller.rummikub.SingleMove;
import rummikub.gameLogic.controller.rummikub.SingleMove.SingleMoveResult;
import java.util.ArrayList;
import java.util.Collections;
import rummikub.gameLogic.model.gameobjects.Board;
import rummikub.gameLogic.model.gameobjects.Serie;
import rummikub.gameLogic.model.gameobjects.Tile;

public class PlayersMove {
    // Constants
    public static final boolean USER_WANT_SKIP_TRUN = true;
    private static final int AT_LEAST_ONE_TILE_DROPPED_FROM_HAND = 1;
    private static final int MIN_SCORE_FOR_STARTING_PLAYING = 30;  
    private static final int INDEX_NORMALIZATION = 1;
    
    // Data Members
    private final int sizeOfHandBeforeMove;
    private final int numOfTilesOnBoardBeforeTheMove;
    private final int numOfLinesInBoardBeforeMove;
    private final ArrayList<Tile> handAfterMove;
    private final Board boardAfterMove;
    private final ArrayList<Tile> tilesTakenFromHand;
    private final ArrayList<Integer> indexPlayerUsedForFirstMove; 
    
    private boolean isValidMove;
    private boolean isFirstMoveDone;
    private boolean isTurnSkipped;
    private int moveCounter;
    
    // Constructor
    public PlayersMove(ArrayList<Tile> HandAfterMove, Board BoardAfterMove, boolean isFirstMoveDone) {
        this.sizeOfHandBeforeMove = HandAfterMove.size();
        this.numOfTilesOnBoardBeforeTheMove = BoardAfterMove.getNumOfTilesInBoard();
        
        this.handAfterMove = HandAfterMove;
        this.boardAfterMove = BoardAfterMove;
        this.numOfLinesInBoardBeforeMove = boardAfterMove.boardSize();
        
        this.isValidMove = false;
        this.tilesTakenFromHand = new ArrayList<>();
        this.isTurnSkipped = false;
        this.moveCounter=0;
        this.isFirstMoveDone = isFirstMoveDone;
        this.indexPlayerUsedForFirstMove = new ArrayList<>(); 
    }
    
    // Getters & Setters
    public ArrayList<Tile> getHandAfterMove() {
        return this.handAfterMove;
    }

    public Board getBoardAfterMove() {
        return this.boardAfterMove;
    }

    public boolean getIsValidMove() {
        return this.isValidMove;
    }

    public boolean getIsTurnSkipped() {
        return this.isTurnSkipped;
    }
    
    public boolean getIsFirstMoveDone() {
        return this.isFirstMoveDone;
    }
    
    public void setIsTurnSkipped(boolean newTurnSkippedState) {
        this.isTurnSkipped = newTurnSkippedState;
    }
    
    // Private Methods:
    // This method implements a single move in the game
    private SingleMoveResult implementSingleNormalMove(SingleMove move) {
        SingleMoveResult result;
        
        switch(move.getMoveType()){
            
            case BOARD_TO_HAND:
                result = implementBoardToHandMove(move);
                break;
            
            case HAND_TO_BOARD:
                result = implementHandToBoardMove(move);
                break;
                
            case BOARD_TO_BOARD:
                
            default:
                result = implementBoardToBoardMove(move);
                break;
        }
        
        return result;        
    }
    
    // This method implements the first step of the player
    // His first step have to be 30 points and more
    private SingleMoveResult implementSingleFirstMove(SingleMove move) {
        SingleMoveResult result;
        
        switch(move.getMoveType()){
            
            case BOARD_TO_HAND:
                result = implementBoardToHandFirstMove(move);
                break;
            
            case HAND_TO_BOARD:
                result = implementHandToBoardFirstMove(move);
                break;
                
            case BOARD_TO_BOARD:
                
            default:
                result = implementBoardToBoardFirstMove(move);
                break;
        }
        
        return result;        
    }
    
    // Implememts single move when the move was from board tp hand
    private SingleMoveResult implementBoardToHandFirstMove(SingleMove move) {
        SingleMoveResult result;
        int chosenLine = (int)move.getpSource().getX();
        
        if (this.indexPlayerUsedForFirstMove.contains(chosenLine)) {
            result = implementBoardToHandMove(move);
            
            if(result == SingleMoveResult.LEGAL_MOVE && this.boardAfterMove.getSeries(chosenLine).isEmptySeries()){
                this.indexPlayerUsedForFirstMove.remove((Integer)chosenLine);
            }
        }
        else {
            result = SingleMoveResult.CAN_NOT_TOUCH_BOARD_IN_FIRST_MOVE;
        }
        
        return result;
    }

    // Implements when the single move was from hand to board
    private SingleMoveResult implementHandToBoardFirstMove(SingleMove move) {
        int chosenLine = (int)move.getpTarget().getX();
        SingleMoveResult result;

        if (isInLegalBoardRangeOfFirstMove(chosenLine)) {
            result = implementHandToBoardMove(move);
            
            if(result == SingleMoveResult.LEGAL_MOVE && !this.indexPlayerUsedForFirstMove.contains(chosenLine)){
                this.indexPlayerUsedForFirstMove.add(chosenLine);
            }
        } 
        else {
            result = SingleMoveResult.CAN_NOT_TOUCH_BOARD_IN_FIRST_MOVE;
        }
        
        return result;
    }
    
    // Implements board to board move
    private SingleMoveResult implementBoardToBoardFirstMove(SingleMove move) {
        int fromLine = (int)move.getpSource().getX();
        int toLine = (int)move.getpTarget().getX();
        SingleMoveResult result;

        if (isInLegalBoardRangeOfFirstMove(fromLine) && isInLegalBoardRangeOfFirstMove(toLine)) {
            result = implementBoardToBoardMove(move);
            
            if(result == SingleMoveResult.LEGAL_MOVE && !this.indexPlayerUsedForFirstMove.contains(toLine)){
                this.indexPlayerUsedForFirstMove.add(toLine);
            }
        } 
        else {
            result = SingleMoveResult.CAN_NOT_TOUCH_BOARD_IN_FIRST_MOVE;
        }
        
        return result;
    }
        
    private SingleMoveResult implementBoardToBoardMove(SingleMove move) {
        boolean isValid;
        int fromLine = (int)move.getpSource().getX(), whatTileInFromLine = (int)move.getpSource().getY();
        int toLine = (int)move.getpTarget().getX(), whatTileInToLine = (int)move.getpTarget().getY();
        SingleMoveResult result = SingleMoveResult.LEGAL_MOVE;
        Tile tileToMove = this.boardAfterMove.getSpecificTile(fromLine, whatTileInFromLine);

        isValid = this.boardAfterMove.getSeries(toLine).isLegalPlaceOfTile(tileToMove, whatTileInToLine);
        
        if(!isValid) {
            result = SingleMoveResult.NOT_IN_THE_RIGHT_ORDER;
        }
        else {
            checkIfToAddNewSeriesToBoard(move);
            setTilesAfterChange(move);		
        }
        
        return result;
    }

    private SingleMoveResult implementHandToBoardMove(SingleMove move) {
        boolean isValid;
        SingleMoveResult result = SingleMoveResult.LEGAL_MOVE;
        int whatTileInHand = move.getnSource();
        int toLine = (int)move.getpTarget().getX(), whatTileInToLine = (int)move.getpTarget().getY();
        Tile tileToMove = this.handAfterMove.get(whatTileInHand);

        isValid = this.boardAfterMove.getSeries(toLine).isLegalPlaceOfTile(tileToMove, whatTileInToLine);
        
        if(!isValid){
            result = SingleMoveResult.NOT_IN_THE_RIGHT_ORDER;
        }
        else{
            checkIfToAddNewSeriesToBoard(move);
            this.tilesTakenFromHand.add(tileToMove);
            this.boardAfterMove.setSpecificTile(tileToMove, toLine, whatTileInToLine);
            this.moveCounter++;
            this.handAfterMove.remove(whatTileInHand);
        }
        
        return result;
    }
    
    // Checks if can add another sequence
    private void checkIfToAddNewSeriesToBoard(SingleMove move) {
        boolean doAddNewSiresToBoard;
        int chosenLine = (int)move.getpTarget().getX();
        
        doAddNewSiresToBoard = this.boardAfterMove.isNewLineForTheBoard(chosenLine);
        if (doAddNewSiresToBoard){
            this.boardAfterMove.addSeries(new Serie());
        }
    }
    
    // Checks legal move
    private boolean isInLegalBoardRangeOfFirstMove(int index) {
        return index >= this.numOfLinesInBoardBeforeMove && 
               index <= this.numOfLinesInBoardBeforeMove + this.indexPlayerUsedForFirstMove.size();
    }
    
    
    private SingleMoveResult implementBoardToHandMove(SingleMove move) {
        boolean isValid;
        SingleMoveResult result = SingleMoveResult.LEGAL_MOVE;
        int fromLine = (int)move.getpSource().getX(), whatTileInFromLine = (int)move.getpSource().getY();
        Tile tileToMove = this.boardAfterMove.getSpecificTile(fromLine, whatTileInFromLine);
        
        isValid = this.tilesTakenFromHand.contains(tileToMove); 
        
        if (isValid) {
            this.handAfterMove.add(tileToMove);
            Collections.sort(this.handAfterMove);
            this.moveCounter--;
            this.boardAfterMove.removeSpecificTile(fromLine, whatTileInFromLine);
        }
        else{
            result = SingleMoveResult.TILE_NOT_BELONG_HAND;
        }
        
        return result;
    }
    
    private Tile getTileToSwap(int toLine, int fromLine, int whatTileInToLine) {
        Tile tileToSwap = null;
        Serie serieTarget;
        
        if (fromLine == toLine || toLine != this.boardAfterMove.boardSize() - INDEX_NORMALIZATION) {
            serieTarget = this.boardAfterMove.getSeries(toLine);

            if (serieTarget.getSizeOfSerie() == whatTileInToLine) {
                tileToSwap = this.boardAfterMove.getSpecificTile(toLine, serieTarget.getSizeOfSerie()-INDEX_NORMALIZATION);
            } 
            else {
                tileToSwap = this.boardAfterMove.getSpecificTile(toLine, whatTileInToLine);
            }
        }
        
        return tileToSwap;
    }
    
    private void setTilesAfterChange(SingleMove move) {
        int fromLine = (int)move.getpSource().getX(), whatTileInFromLine = (int)move.getpSource().getY();
        int toLine = (int)move.getpTarget().getX(), whatTileInToLine = (int)move.getpTarget().getY();
        Tile tileToMove = this.boardAfterMove.getSpecificTile(fromLine, whatTileInFromLine);
        Tile tileToSwap = getTileToSwap(toLine, fromLine, whatTileInToLine);

        if (fromLine == toLine && tileToMove.isEqualTiles(tileToSwap)) {
            if (whatTileInFromLine > whatTileInToLine) {
                this.boardAfterMove.setSpecificTile(tileToMove, toLine, whatTileInToLine);	
                whatTileInFromLine++;
                this.boardAfterMove.removeSpecificTile(fromLine, whatTileInFromLine);
            }
            else {
                if (whatTileInToLine == this.boardAfterMove.getSeries(toLine).getSizeOfSerie()) {
                    this.boardAfterMove.getSeries(toLine).addSpecificTileToSerie(tileToMove);
                    this.boardAfterMove.removeSpecificTile(toLine, whatTileInFromLine);
                }
                else {
                    if (whatTileInToLine != this.boardAfterMove.getSeries(toLine).getSizeOfSerie()-INDEX_NORMALIZATION) {
                        whatTileInToLine++;
                    }
                    this.boardAfterMove.setSpecificTile(tileToMove, toLine, whatTileInToLine);
                    this.boardAfterMove.removeSpecificTile(fromLine, whatTileInFromLine);
                }
            }
        }
        else {
            this.boardAfterMove.setSpecificTile(tileToMove, toLine, whatTileInToLine);	
            this.boardAfterMove.removeSpecificTile(fromLine, whatTileInFromLine);
        }
    }
    
    // Public Methods
    
    // Takes user single move and moves the tile acordong to i
    // @param move - the move given by player
    // @return the resualt of the single move
    public SingleMoveResult implementSingleMove(SingleMove move) {
        SingleMoveResult result;
        
        if(this.isFirstMoveDone){
            result = implementSingleNormalMove(move);
        }
        else{
            result = implementSingleFirstMove(move);
        }
        
        return result;
    }
    
    // Public Methods:
    public boolean isUsedAtleastOneTile(){
        boolean isUsedAtLeastOneTile;
        int numOfUsedCardsFromHand = this.sizeOfHandBeforeMove - this.handAfterMove.size();
        
        isUsedAtLeastOneTile = ((numOfUsedCardsFromHand > 0) && 
                                (this.moveCounter >= AT_LEAST_ONE_TILE_DROPPED_FROM_HAND));
        
        return isUsedAtLeastOneTile;
    }
    
    // Check player move
    public boolean isValidMove() {
        boolean isValidBoard, doesAllCardsAreUsed;
        int numOfUsedCardsFromHand = this.sizeOfHandBeforeMove - this.handAfterMove.size();
        int numOfUsedCardInMove = numOfUsedCardsFromHand + this.numOfTilesOnBoardBeforeTheMove;
        
        
        isValidBoard = this.boardAfterMove.validateBoard();
        doesAllCardsAreUsed = numOfUsedCardInMove == this.boardAfterMove.getNumOfTilesInBoard();
        this.isValidMove = isValidBoard && doesAllCardsAreUsed;
        return this.isValidMove;
    }
    
    public boolean has30PointsSeriesMove() {
        boolean has30Points;
        int sum = 0;
        
        sum = this.indexPlayerUsedForFirstMove.stream().map((usedLine) -> this.boardAfterMove.getSeries(usedLine).getScoreOfSerie()).reduce(sum, Integer::sum);
        has30Points = sum >= MIN_SCORE_FOR_STARTING_PLAYING;
        
        return has30Points;
    }
    
    public int getBoardMaxColSizeForSpecificRow (int row, boolean insertNewTile) {
        return this.boardAfterMove.getMaxColSizeForSpecificRow(row, insertNewTile);
    }
    
    public boolean isBoardStillEmpty() {
        return this.boardAfterMove.isEmpty();
    }
    
    public int sizeOfTheBoardDruingTheMove() {
        return this.boardAfterMove.boardSize();
    }
    
    public int sizeOfTheHandDruingTheMove() {
        return this.handAfterMove.size();
    }
}


    //Old Version
//    private SingleMoveResult implementBoardToBoardMove(SingleMove move) {
//        boolean isValid;
//        int fromLine = (int)move.getpSource().getX(), whatTileInFromLine = (int)move.getpSource().getY();
//        int toLine = (int)move.getpTarget().getX(), whatTileInToLine = (int)move.getpTarget().getY();
//        SingleMoveResult result = SingleMoveResult.LEGAL_MOVE;
//        Tile tileToMove = this.boardAfterMove.getSpecificTile(fromLine, whatTileInFromLine);
//
//        isValid = this.boardAfterMove.getSeries(toLine).isLegalPlaceOfTile(tileToMove, whatTileInToLine);
//        
//        if(!isValid) {
//            result = SingleMoveResult.NOT_IN_THE_RIGHT_ORDER;
//        }
//        else {
//            checkIfToAddNewSeriesToBoard(move);
//            this.boardAfterMove.setSpecificTile(tileToMove, toLine, whatTileInToLine);
//            this.boardAfterMove.removeSpecificTile(fromLine, whatTileInFromLine);
//        }
//        
//        return result;
//    }
