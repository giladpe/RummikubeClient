/*
 * this class comunocates with th io and the game logics, combines them 
 * and makes the work in harmony
 */
package rummikub.gameLogic.controller.rummikub;

import rummikub.gameLogic.controller.rummikub.SingleMove.SingleMoveResult;
import java.awt.Point;
import java.io.IOException;
import rummikub.gameLogic.view.ioui.InputOutputParser;
import java.util.ArrayList;
import javax.xml.bind.JAXBException;
import rummikub.gameLogic.model.gameobjects.Board;
import rummikub.gameLogic.model.gameobjects.Serie;
import rummikub.gameLogic.model.logic.GameLogic;
import rummikub.gameLogic.model.logic.Settings;
import rummikub.gameLogic.model.logic.SeriesGenerator;
import rummikub.gameLogic.model.logic.PlayersMove;
import rummikub.gameLogic.model.player.ComputerSingleMoveGenerator;
import rummikub.gameLogic.model.player.Player;
import org.xml.sax.SAXException;
import rummikub.gameLogic.view.ioui.JaxBXmlParser;
import rummikub.gameLogic.view.ioui.Utils;
import rummikub.gameLogic.view.ioui.Utils.DealWithTileFromBoardOrHand;

// This is the main class in this package
// This class is the manager of the game
public class Rummikub {
    private final static int MIN_INDEX_IN_BOARD = 0;
    
    // Data Members
    private final GameLogic rummikubLogic;
    private final SeriesGenerator serieGenerator;
    private final ComputerSingleMoveGenerator newMoveGenerator;

    // Constructor
    public Rummikub() {
        this.rummikubLogic = new GameLogic();
        this.serieGenerator = new SeriesGenerator();
        this.newMoveGenerator = new ComputerSingleMoveGenerator();
    }
    
    // Private Methods
    // Initiates the basic game parameters the allow to start the game and starts it
    private void startNewGame() {
        boolean  hasToInitSettings = this.rummikubLogic.getGameSettings() == null;
        
        if (!hasToInitSettings) {
            hasToInitSettings = InputOutputParser.getNewSettingsOrUseOldSettings(); 
        } 
        
        if(hasToInitSettings){
            // Init settings
            Settings currGameSettings = initGameSetting();
            rummikubLogic.setGameSettings(currGameSettings);
            rummikubLogic.setGameOriginalInputedSettings(rummikubLogic.getGameSettings());
        }
        
        rummikubLogic.initGameFromUserSettings();      
        playGame();
        roundResualt();
    }
    
    // This method responsible to run the game loop iterations
    private void playGame() {
        // loop stops until game ended
        while (!(rummikubLogic.isGameOver() || rummikubLogic.isOnlyOnePlayerLeft())) { 
            //init variables in the statrt of the turn
            Board printableBoard = new Board(new ArrayList<>(rummikubLogic.getGameBoard().getListOfSerie()));
            boolean isFirstMoveDone = rummikubLogic.getCurrentPlayer().isFirstMoveDone();
            Player printablePlayer = rummikubLogic.getCurrentPlayer().clonePlayer();
            PlayersMove currentPlayerMove = new PlayersMove(printablePlayer.getListPlayerTiles(), printableBoard, isFirstMoveDone);
            ArrayList<Player> printablePlayersList = new ArrayList<>(rummikubLogic.getPlayers());
            printablePlayersList.remove(rummikubLogic.getCurrentPlayer());
            printablePlayersList.add(printablePlayer);
            
            //print board before the turn starts
            InputOutputParser.printGameScreen(printablePlayer, printableBoard, printablePlayersList);
            
            // recives input from player
            Utils.TurnMenuResult turnResult = getMoveFromPlayer(currentPlayerMove, printablePlayer, printablePlayersList);
            
            //check the player move
            if(turnResult != Utils.TurnMenuResult.EXIT_GAME){
                rummikubLogic.playSingleTurn(currentPlayerMove);
            }
            
            // Swap players
            if (!rummikubLogic.isGameOver()) {
                rummikubLogic.swapTurns();
            }
        }
    }
    
    // Deals with the basic inputs from the user about the game board and his hand
    private Utils.TurnMenuResult getMoveFromPlayer(PlayersMove currentPlayerMove, Player printablePlayer, ArrayList<Player> printablePlayersList) {
        Utils.TurnMenuResult turnResult = null;
        SingleMove singleMove;
        boolean keepPlaying, isTurnSkipped = false, isFirstMoveForPlayerInCurrTurn = true;
        
        do{
            if (rummikubLogic.getCurrentPlayer().getIsHuman()) {
                if(isFirstMoveForPlayerInCurrTurn){
                    turnResult = InputOutputParser.askTurnMenuWithSave();
                    isFirstMoveForPlayerInCurrTurn = false;
                }
                else {
                    turnResult = InputOutputParser.askTurnMenuWithoutSave();
                }
                singleMove = dealWithHumanPlayer(turnResult, currentPlayerMove, isTurnSkipped);
            }
            else {  
                singleMove = dealWithComputerPlayer(currentPlayerMove, isTurnSkipped); 
                turnResult = Utils.TurnMenuResult.CONTINUE;
            }
            keepPlaying = turnResult != Utils.TurnMenuResult.EXIT_GAME && !currentPlayerMove.getIsTurnSkipped();
            if(keepPlaying){
                if(singleMove != null) {
                    try {
                       dealWithSingleMoveResualt(turnResult,singleMove,currentPlayerMove);        
                       InputOutputParser.printGameScreen(printablePlayer, currentPlayerMove.getBoardAfterMove(), printablePlayersList);
                    }
                    catch (Exception ex) {
                         currentPlayerMove.setIsTurnSkipped(PlayersMove.USER_WANT_SKIP_TRUN);
                    }
                }
            }
            keepPlaying = keepPlaying && isPlayerWantsToContinueHisTurn(); 
        }while (keepPlaying); 
        
        return turnResult;
    }
    
    // Deals with the human  player and allows him to makes his inputs
    private SingleMove dealWithHumanPlayer(Utils.TurnMenuResult turnResult, 
                                           PlayersMove currentPlayerMove , 
                                           boolean isTurnSkipped) {
        SingleMove singleMove = null;

        switch(turnResult){
            // Continue in game
            case CONTINUE:{
                // check turn skipped
                isTurnSkipped = InputOutputParser.isSkipTurnOrPlay();
                if (!isTurnSkipped) {
                    singleMove = getSingleMove(currentPlayerMove);
                    currentPlayerMove.setIsTurnSkipped(!PlayersMove.USER_WANT_SKIP_TRUN);
                }
                else{
                    currentPlayerMove.setIsTurnSkipped(PlayersMove.USER_WANT_SKIP_TRUN);
                }
                
                break;
            }
            // Save to xml
            case SAVE_GAME:{
                saveGame();
                
                break;
            }
            // Player wants to get out from this match
            case EXIT_GAME:
            default:{
                InputOutputParser.playerIsRemoving(this.rummikubLogic.getCurrentPlayer().getName());
                rummikubLogic.removeCurrentPlayerFromTheGame();
                break;
            }
        }
        return singleMove;
    }

    // Deals with the computer player and allows him to makes his inputs
    private SingleMove dealWithComputerPlayer(PlayersMove currentPlayerMove,boolean isTurnSkipped) {
        SingleMove singleMove;
        Serie serie;
        
        if (newMoveGenerator.isFinishedGeneratingLastSerie()) {
            serie = this.serieGenerator.generateSerieMove(currentPlayerMove.getHandAfterMove(), isTurnSkipped);
            this.newMoveGenerator.setSerieToPlaceOnBoard(serie);
        
            if(serie != null){
                this.newMoveGenerator.setBoardSizeBeforeMove(currentPlayerMove.getBoardAfterMove().boardSize());
            }
        }
        
        singleMove = newMoveGenerator.generateSingleMove(currentPlayerMove.getHandAfterMove(), currentPlayerMove.getBoardAfterMove()); 

        if(this.newMoveGenerator.isTurnSkipped()) {
            currentPlayerMove.setIsTurnSkipped(this.newMoveGenerator.isTurnSkipped());
        }
        
        return singleMove;
    }
    
    // Asks player if he want to continue his turn
    private boolean isPlayerWantsToContinueHisTurn() {
        boolean resualt;
        
        if (this.rummikubLogic.getCurrentPlayer().getIsHuman()) {
            resualt = !InputOutputParser.isFinishYourTurn();
        }
        else {
            resualt = !this.newMoveGenerator.isTurnFinnised();
            if(this.newMoveGenerator.isTurnFinnised()) {
                this.newMoveGenerator.initComputerSingleMoveGenerator();
            }
        }
        
        return resualt;
    }
    
    // Deals with round end resualt
    private void roundResualt() {
        InputOutputParser.printGameOverMsg();

        if (this.rummikubLogic.isGameOver() && this.rummikubLogic.isTie()) {
            InputOutputParser.printTieMsg();
        }
        else if(this.rummikubLogic.isGameOver() && !this.rummikubLogic.isTie()) {
            InputOutputParser.ShowWinner(rummikubLogic.getWinner().toString());
        }
    }
    
    
    // This method should return the basic move of the current Player
    // It can be one of the three options:
    // 1: hand to board; 2: board to board; board to hand
    private SingleMove getSingleMove(PlayersMove playersMove) {
        Utils.DealWithTileFromBoardOrHand answer;
        SingleMove singleMove;
        
        answer = askUserFromWhereToTakeTileBoardOrHand(playersMove.isBoardStillEmpty());
        
        switch (answer) {
            case TILES_IN_HAND:
                singleMove = handToBoardSingleMove(playersMove);
                break;
                
            case TILES_IN_BOARD:
            default:
                singleMove = BoardToHandOrToBoardSingleMove(playersMove);
                break;
        }
        
        return singleMove;
    }        
    
    // Deals with input about move from hand to baord
    private SingleMove handToBoardSingleMove(PlayersMove playersMove) {
        SingleMove singleMove;
        int nRowLoc, nColLoc , indexWhatToTakeFromPlayerList;
        Point plcWhereToAdd;
        final boolean  insertNewTile = true;
        
        nRowLoc = InputOutputParser.getRowWhereToPlaceNewPoint(playersMove.sizeOfTheBoardDruingTheMove());
        nColLoc = InputOutputParser.getColWhereToPlaceNewPoint(playersMove.getBoardMaxColSizeForSpecificRow(nRowLoc, insertNewTile));
        
        // init singleMove with source from list to target in board
        plcWhereToAdd = new Point(nRowLoc, nColLoc);
        indexWhatToTakeFromPlayerList = InputOutputParser.getLocationInHandFromUser(playersMove.sizeOfTheHandDruingTheMove());
        singleMove = new SingleMove(plcWhereToAdd, indexWhatToTakeFromPlayerList, SingleMove.MoveType.HAND_TO_BOARD);
        
        return  singleMove;
    }

    // Deals with input about move from board to baord or to the hand
    private SingleMove BoardToHandOrToBoardSingleMove(PlayersMove playersMove) {
        DealWithTileFromBoardOrHand answer;
        SingleMove singleMove;
        int nRowLoc, nColLoc, maxRow;
        Point pointToTakeFromBoard , plcWhereToAdd;
        final boolean  insertNewTile = true;
        
        maxRow = playersMove.isBoardStillEmpty() ?  MIN_INDEX_IN_BOARD : playersMove.sizeOfTheBoardDruingTheMove()-1;
        nRowLoc = InputOutputParser.getRowWhereToTakeFromBoard(maxRow);
        nColLoc = InputOutputParser.getColWhereToTakeFromBoard(playersMove.getBoardMaxColSizeForSpecificRow(nRowLoc, !insertNewTile));
        answer  = InputOutputParser.isPutInBoardOrPlayerHand();
        
        pointToTakeFromBoard = new Point(nRowLoc, nColLoc);

        switch (answer) {
            case TILES_IN_BOARD:{
                nRowLoc = InputOutputParser.getRowWhereToPlaceNewPoint(playersMove.sizeOfTheBoardDruingTheMove());
                nColLoc = InputOutputParser.getColWhereToPlaceNewPoint(playersMove.getBoardMaxColSizeForSpecificRow(nRowLoc, insertNewTile));
                
                plcWhereToAdd = new Point(nRowLoc, nColLoc);
                singleMove = new SingleMove(plcWhereToAdd, pointToTakeFromBoard, SingleMove.MoveType.BOARD_TO_BOARD);               
                break;
            }
            case TILES_IN_HAND:
                
            default:{
                singleMove = new SingleMove(pointToTakeFromBoard, SingleMove.MoveType.BOARD_TO_HAND);
                break;
            }
        }

        return singleMove;
    }
            
    // This method reasponsible to init the game basic properties
    // the properties are taken from the user using the InputUser class
    private Settings initGameSetting() {
        // Local Varibles
        String gameName = InputOutputParser.getGameName();
        int totalNumberOfPlayer = InputOutputParser.getTotalNumOfPlayers();
        int numOfComputerPlayers = InputOutputParser.getComputerPlayersNumber(totalNumberOfPlayer);
        int numOfHumanPlayers = totalNumberOfPlayer - numOfComputerPlayers;
        ArrayList<String> playerNames = InputOutputParser.getPlayerNames(numOfHumanPlayers);
        
        Settings currGameSettings = new Settings(gameName, totalNumberOfPlayer, numOfComputerPlayers, playerNames);
        
        return currGameSettings;
    }

    // Deals with the resualt of player inputs
    private void dealWithSingleMoveResualt(Utils.TurnMenuResult turnResult, 
                                           SingleMove singleMove , 
                                           PlayersMove currentPlayerMove) {
        SingleMoveResult singleMoveResualt;

        if (turnResult == Utils.TurnMenuResult.CONTINUE) {

            singleMoveResualt = currentPlayerMove.implementSingleMove(singleMove);
            
            switch(singleMoveResualt) {
                case TILE_NOT_BELONG_HAND:{
                    InputOutputParser.printTileNotBelongToTheHand();
                    break;
                }
                case NOT_IN_THE_RIGHT_ORDER:{
                    InputOutputParser.printTileInsertedNotInRightOrder();
                    break;
                }
                case CAN_NOT_TOUCH_BOARD_IN_FIRST_MOVE:{
                    InputOutputParser.printCantTuchBoardInFirstMove();
                    break;
                }
                case LEGAL_MOVE:
                default:{
                    break;
                }
            }                    
        }
    }
 
    // Load game from xml
    private void loadGame() {
        boolean succedLoadingFile = false;
        
        try {
            succedLoadingFile = JaxBXmlParser.loadSettingsFromXml();

            if (succedLoadingFile) {
                rummikubLogic.initGameFromFile(JaxBXmlParser.getPlayerArray(),
                                               JaxBXmlParser.getBoard(),
                                               JaxBXmlParser.getCurrPlayer(), 
                                               JaxBXmlParser.getGameName());
                playGame();
                roundResualt();
            }
        }
        catch (SAXException | IOException ex) {
            succedLoadingFile = false;
        }
        finally{
            if(!succedLoadingFile){
                InputOutputParser.failLoadingFileMsg();
            }
        }
    } 
    
    // Save game to xml
    private void saveGame() {
        Utils.SaveOrSaveas selectedOption;
        boolean succedSavingFile = false;

        try {
            selectedOption = InputOutputParser.getSaveOrSaveAs();

            if(selectedOption == Utils.SaveOrSaveas.SAVE){
               succedSavingFile = JaxBXmlParser.saveSettingsToXml(rummikubLogic.getPlayers(),
                                                                  rummikubLogic.getGameBoard(),
                                                                  rummikubLogic.getGameSettings().getGamesName(),
                                                                  rummikubLogic.getCurrentPlayer().getName());
            }
            else {
                succedSavingFile = JaxBXmlParser.saveAsSettingsToXml(rummikubLogic.getPlayers(),
                                                                     rummikubLogic.getGameBoard(),
                                                                     rummikubLogic.getGameSettings().getGamesName(),
                                                                     rummikubLogic.getCurrentPlayer().getName());
            }
        }
        catch (SAXException | JAXBException | IOException ex) {
            succedSavingFile = false;
        }
        finally{
            if (!succedSavingFile) {
                InputOutputParser.failSavingFileMsg();
            }
        }            
    }
    
    // Asks user from where he wantto take his tiles
    private DealWithTileFromBoardOrHand askUserFromWhereToTakeTileBoardOrHand(boolean isBoardStillEmpty) {
        Utils.DealWithTileFromBoardOrHand answer;
        boolean canPutIntheBoard;

        do {
            answer = InputOutputParser.isTakenFromBoardOrPlayerList();
            canPutIntheBoard = !(answer == DealWithTileFromBoardOrHand.TILES_IN_BOARD && isBoardStillEmpty);
            
            if (!canPutIntheBoard) {
                InputOutputParser.theBoardIsEmptyMsg();
            }
        }
        while(!canPutIntheBoard);
        
        return answer;
    }
    
    // Public Methods
    public void startGame() {
        Utils.MainMenuResult userChoice;
        
        do {            
            userChoice = InputOutputParser.showMainMenu();
            
            switch(userChoice){
                case START_NEW_GAME:
                    startNewGame();
                    break;
                
                case LOAD_GAME:
                    loadGame();
                    break;
                
                case EXIT_GAME:
                default:
                break;
            }        
        } 
        while (userChoice != Utils.MainMenuResult.EXIT_GAME);
    }
}
