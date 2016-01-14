/*
 * deals with the inout and output in the game
 */
package rummikub.gameLogic.view.ioui;

import rummikub.gameLogic.model.gameobjects.Board;
import java.util.ArrayList;
import java.util.Scanner;
import java.io.File;
import rummikub.gameLogic.model.player.Player;

/**
 *
 * @author roiesti
 */
final public class InputOutputParser {
    // Constants
    static private final boolean ADD_EXTRA_INDEX = true;
    
    // Private Methods
    private static boolean simmilarToOneName(ArrayList<String> playerNames, String sPlayerName){
        boolean bIsFound = false;
        
        for (int i = 0; i < playerNames.size() && !bIsFound; i++) {
            bIsFound = playerNames.get(i).equals(sPlayerName);
        }
        
        return bIsFound;
    }
    
    private static  String getInputFromUser(String question, String errorMessage, String pattern){
        showUser(question);
        Scanner scanIn = new Scanner(System.in);         
        
        while(scanIn.hasNext() && !scanIn.hasNext(pattern)){
            scanIn.next();
            showUser(errorMessage);
        }
        
        String returnValue = scanIn.next(pattern);
                
        return returnValue;
    }
    
    private static Utils.DealWithTileFromBoardOrHand boardOrHandQuestion(String question){
        String answer;
        
        answer = getInputFromUser(question, 
                                  Utils.Constants.ErrorMessages.ILEGAL_INPUT_1_2, 
                                  Utils.Constants.ControlersPattern.ONE_TWO_OPTIONS);
        
        if(Utils.DealWithTileFromBoardOrHand.TILES_IN_BOARD.getBoardOrHandValue() == Integer.valueOf(answer)){
            return Utils.DealWithTileFromBoardOrHand.TILES_IN_BOARD;
        }
        else if(Utils.DealWithTileFromBoardOrHand.TILES_IN_HAND.getBoardOrHandValue() == Integer.valueOf(answer)){
            return Utils.DealWithTileFromBoardOrHand.TILES_IN_HAND;
        }
            
        return null;
    }
    
    private static boolean yesNoQuestion(String question){
        boolean resualt;
        String answer = getInputFromUser(question, 
                                         Utils.Constants.ErrorMessages.ILEGAL_INPUT_Y_N, 
                                         Utils.Constants.ControlersPattern.Y_N_ANSWER_OPTIONS);
        
        resualt = answer.toLowerCase().equals(Utils.Constants.ControlersPattern.Y_ANSWER);
        
        return resualt;
    }
    
    private static int getLocationInBoardFromUser(int max, String question){
        int nLoc;
        
        String answerRow = getInputFromUser(question, 
                                            String.format(Utils.Constants.ErrorMessages.ILEGAL_INPUT_INT_BETWEEN, "0", String.valueOf(max)), 
                                            String.format(makePatteren(max)));
                
        nLoc = Integer.valueOf(answerRow);
        
        return nLoc;
    }
    
    private static StringBuilder printHandBuilder(Player CurrentPlayer) {     
        StringBuilder toPrint = new StringBuilder();
        
        toPrint.append(printIndexLineBuilder(CurrentPlayer.getListPlayerTiles().size(),Player.WHITESPACE_LINE, !ADD_EXTRA_INDEX));
        toPrint.append(CurrentPlayer.PlayerTilesString());
        toPrint.append(Utils.Constants.END_LINE);
        
        return toPrint;
    }

    private static StringBuilder printIndexLineBuilderForBoard(int sizeOfElement){
        StringBuilder tempStr = new StringBuilder();
       
        tempStr.append("   ");
                
        for (int i = 0; i <= sizeOfElement && sizeOfElement!=0; i++) {
            
            if(i==sizeOfElement) {
                tempStr.append(String.format("%d",i));
            }
            else {
                if (i < 10) {
                    tempStr.append(String.format("%d |",i));
                } 
                else {
                    tempStr.append(String.format("%d|",i));
                }
            }
        }
        
        tempStr.append(Utils.Constants.END_LINE);
        
        return tempStr;
    }
    
    private static StringBuilder printIndexLineBuilder(int sizeOfElement , String whiteSpaces , boolean addExtraIndex) {
        StringBuilder tempStr = new StringBuilder();
        
        if(sizeOfElement !=0 && addExtraIndex) {
            sizeOfElement++;

            for (int i = 0; i < String.valueOf(sizeOfElement+2).length(); i++) {
                tempStr.append(' ');
            }
        }

        tempStr.append(whiteSpaces);
            
                
                
        for (int i = 0; i < sizeOfElement; i++) {
            
            if(i==sizeOfElement-1) {
                tempStr.append(String.format("%d",i));
            }
            else {
                if (i < 10) {
                    tempStr.append(String.format("%d |",i));
                } 
                else {
                    tempStr.append(String.format("%d|",i));
                }
            }
        }
        
            tempStr.append(System.getProperty("line.separator"));
        
        return tempStr;
        }
    
    private static String makePatteren(int maxIndex){
        StringBuilder  patternBuilder= new StringBuilder();
        int num = 1;
        String digit =  String.valueOf(maxIndex % 10);
        boolean found = maxIndex <= 9;
        
        if(found) {
            patternBuilder.append(String.format("[0-%s]",digit));
        }
        
        while(!found){
            patternBuilder.append(String.format("[0-9]|%d",num));
            num++;
            maxIndex-=10;
            found = maxIndex <= 9;
            
            if (found) {
                patternBuilder.append(String.format("[0-%s]",digit));
            }
        }
        
        return patternBuilder.toString();
    }
     
    private static StringBuilder printBoardBuilder(Board currentBoard , int sizeOfLongestSerie) {
        StringBuilder tempStr = new StringBuilder();
        
        tempStr.append("Board: ").append(Utils.Constants.END_LINE);
        tempStr.append(printIndexLineBuilderForBoard(sizeOfLongestSerie));
        tempStr.append(currentBoard.toString());
        
        return tempStr;
    }
    
    private static StringBuilder printRivalsBuilder(ArrayList<Player> players) {
        StringBuilder tempStr = new StringBuilder();
        int lineNum=1;
        tempStr.append(Utils.Constants.RIVALS).append(Utils.Constants.END_LINE);

        for (Player player : players) {
            tempStr.append(String.format("%d. %s: %d card/s",lineNum
                                                            ,player.toString()
                                                            ,player.getListPlayerTiles().size()));
            lineNum++;
            tempStr.append(Utils.Constants.END_LINE);
        }
        
        tempStr.append(Utils.Constants.END_LINE);
        
        return tempStr;
    }

    private static StringBuilder printSeperateLineBuilder(int sizeOfLongestSerie) {
        
        StringBuilder seperateLine= new StringBuilder();
        
        for (int i = 0; i < sizeOfLongestSerie + Utils.Constants.NUMBER_OF_SEPERATE_CHARS; i++) {
            seperateLine.append(Utils.Constants.SEPERATE_CHAR);
        }
        seperateLine.append(Utils.Constants.END_LINE);
        
        return seperateLine;
    }
    
    private static String getPlayerName(ArrayList<String> playerNames){
        String answer = null;
        boolean found = false;
        
        showUser(Utils.Constants.QuestionsAndMessagesToUser.PLAYER_NAME_QUESTION);
        Scanner scanIn = new Scanner(System.in);         
        
        while(!found && scanIn.hasNext()){
            answer = scanIn.next();
            //A: i changed it
            if(simmilarToOneName(playerNames, answer) || 
               answer.matches(Utils.Constants.ControlersPattern.COMPUTER_NAME)){
                showUser(Utils.Constants.ErrorMessages.ILEGAL_PLAYER_NAME);
            }
            else{
                found = true;
            }
        }
        
        return answer;
    }
    
    // Public Methods
    public static  void showUser(String message){
        System.out.format(message);
    }

    public static int getLocationInHandFromUser(int maxIndex){
        String answer = getInputFromUser(Utils.Constants.QuestionsAndMessagesToUser.PLAYER_TILE_INDEX,
                                        String.format(Utils.Constants.ErrorMessages.ILEGAL_INPUT_INT_BETWEEN, "0", String.valueOf(maxIndex-1)), 
                                        String.format(makePatteren(maxIndex-1)));
        
        return Integer.valueOf(answer);
    }
    
    public static int getRowWhereToPlaceNewPoint(int maxRow) {
        showUser("enter row number where to put the new value in board ");
        
        return getLocationInBoardFromUser(maxRow, Utils.Constants.QuestionsAndMessagesToUser.ROW_QUESTION);
    }
    
    public static int getColWhereToPlaceNewPoint(int maxCol) {
        showUser("enter col number where to put the new value in board ");
        
        return getLocationInBoardFromUser(maxCol, Utils.Constants.QuestionsAndMessagesToUser.COL_QUESTION);
    }
    
    public static int getRowWhereToTakeFromBoard(int maxRow) {
        showUser("enter row number where to take from board ");
        
        return getLocationInBoardFromUser(maxRow, Utils.Constants.QuestionsAndMessagesToUser.ROW_QUESTION);
    }
    
    public static int getColWhereToTakeFromBoard(int maxCol){
        showUser("enter col number where to take from board ");
        
        return getLocationInBoardFromUser(maxCol, Utils.Constants.QuestionsAndMessagesToUser.COL_QUESTION);
    }
    
    public static boolean isFinishYourTurn(){
        return yesNoQuestion(Utils.Constants.QuestionsAndMessagesToUser.CONTINUE_IN_TURN_QUESTION);
    }
    
    public static Utils.DealWithTileFromBoardOrHand isTakenFromBoardOrPlayerList(){
        return boardOrHandQuestion(Utils.Constants.QuestionsAndMessagesToUser.TAKE_BOARD_OR_PLAYERLIST_QUESTION);
    }
    
    public static Utils.DealWithTileFromBoardOrHand isPutInBoardOrPlayerHand(){
        return boardOrHandQuestion(Utils.Constants.QuestionsAndMessagesToUser.PUT_BOARD_OR_PLAYERLIST_QUESTION);
    }
        
    public static int getTotalNumOfPlayers(){
        String answer;
        
        answer = getInputFromUser(Utils.Constants.QuestionsAndMessagesToUser.TOTAL_PLAYERS_NUMBER_QUESTION, 
                                  Utils.Constants.ErrorMessages.ILEGAL_TOTAL_PLAYER_NUMBER, 
                                  Utils.Constants.ControlersPattern.PLAYER_TOTAL_NUMBER_OPTIONS);
            
        return Integer.valueOf(answer);
    }
    
    public static int getComputerPlayersNumber(int totalNumberOfPlayers){
        String answer;
        
        answer = getInputFromUser(String.format(Utils.Constants.QuestionsAndMessagesToUser.COMPUTER_PLAYERS_NUMBER_QUESTION, String.valueOf(totalNumberOfPlayers)), 
                                  String.format(Utils.Constants.ErrorMessages.ILEGAL_COMPUTER_PLAYER_NUMBER, String.valueOf(totalNumberOfPlayers)), 
                                  String.format(makePatteren(totalNumberOfPlayers)));
            
        return Integer.valueOf(answer);
    }
    
    public static ArrayList<String> getPlayerNames(int numOfHumanPlayers) {
        ArrayList<String> playerNames = new ArrayList<>();
        String playerName;
        
        for (int i = 0; i < numOfHumanPlayers; i++) {
            playerName = getPlayerName(playerNames); 
            playerNames.add(playerName);
        }
        
        return playerNames;
    }
        
    public static Utils.MainMenuResult showMainMenu() {
        String answer;
        Utils.MainMenuResult returnValue = null;
        
        answer = getInputFromUser(Utils.Constants.QuestionsAndMessagesToUser.MAIN_MENU,
                                  Utils.Constants.ErrorMessages.ILEGAL_INPUT_MAIN_MENU, 
                                  Utils.Constants.ControlersPattern.ONE_TWO_THREE_OPTIONS);
        
        if(Utils.MainMenuResult.START_NEW_GAME.getMainMenuResultValue() == Integer.valueOf(answer)){
            returnValue = Utils.MainMenuResult.START_NEW_GAME;
        }
        else if(Utils.MainMenuResult.LOAD_GAME.getMainMenuResultValue() == Integer.valueOf(answer)){
            returnValue = Utils.MainMenuResult.LOAD_GAME;
        }
        else if(Utils.MainMenuResult.EXIT_GAME.getMainMenuResultValue() == Integer.valueOf(answer)){
            returnValue = Utils.MainMenuResult.EXIT_GAME;
        }
            
        return returnValue;
    }

    public static String getGameName() {
        String gameNameToReturn;
        
        gameNameToReturn = getInputFromUser(Utils.Constants.QuestionsAndMessagesToUser.GAME_NAME_QUESTION, 
                         Utils.Constants.ErrorMessages.ILEGAL_GAME_NAME, 
                         Utils.Constants.ControlersPattern.GAME_NAME_OPTION);
        
        return gameNameToReturn;
    }
    
    public static  boolean isSkipTurnOrPlay() {
        return yesNoQuestion(Utils.Constants.QuestionsAndMessagesToUser.SKIP_TURN);    
    }
    
    public static void printGameScreen(Player currentPlayer, Board currentBoard , ArrayList<Player> players) /**/
    {
        StringBuilder gameScreen = new StringBuilder();
        int sizeOfLongestSerie = currentBoard.getSizeOfLongestSerie();
        
        gameScreen.append(Utils.Constants.END_LINE);
        gameScreen.append("current player is: ").append(currentPlayer.getName()).append(System.getProperty("line.separator"));
        gameScreen.append(printHandBuilder(currentPlayer));
        gameScreen.append(printBoardBuilder(currentBoard, sizeOfLongestSerie));
        gameScreen.append(printRivalsBuilder(players));
        gameScreen.append(printSeperateLineBuilder(sizeOfLongestSerie));
        
        showUser(gameScreen.toString());
    }    

    public static boolean getNewSettingsOrUseOldSettings() {
        String answer;
        boolean isNewSettings;
        
        answer = getInputFromUser(Utils.Constants.QuestionsAndMessagesToUser.NEW_SETTINGS_OR_OLD_SETTINGS_QUESTION,
                                  Utils.Constants.ErrorMessages.ILEGAL_INPUT_1_2, 
                                  Utils.Constants.ControlersPattern.ONE_TWO_OPTIONS);
        
        isNewSettings = (Utils.NewSettingsOrOldSettings.NEW_SETTINGS.getNewSettingsOrOldSettingsValue() == Integer.valueOf(answer));
            
        return isNewSettings;
    }

    public static void printTileNotBelongToTheHand() {
        showUser(Utils.Constants.ErrorMessages.ILEGAL_TILE_IS_NOT_BELONG_TO_HAND);
    }

    public static void printTileInsertedNotInRightOrder() {
        showUser(Utils.Constants.ErrorMessages.ILEGAL_TILE_INSERTED_NOT_IN_RIGHT_ORDER);
    }

    public static void printCantTuchBoardInFirstMove() {
        showUser(Utils.Constants.ErrorMessages.ILEGAL_CANT_TUCH_BOARD_IN_FIRST_MOVE);
    }

    public static void printGameOverMsg() {
        showUser(Utils.Constants.QuestionsAndMessagesToUser.GAME_OVER + "\n\n");

    }

    public static void printTieMsg() {
        showUser(Utils.Constants.QuestionsAndMessagesToUser.TIE + "\n\n");

    }

    public static void ShowWinner(String winnersName) {
        showUser(Utils.Constants.QuestionsAndMessagesToUser.WINNER_IS + winnersName + "\n\n");

    }

    public static String getXmlFilePathForLoad() {
        String path;
        File file;
        
        path = getInputFromUser(Utils.Constants.QuestionsAndMessagesToUser.LOAD_XML_PATH_QUESTION, 
                    Utils.Constants.ErrorMessages.ILEGAL_LOAD_GAME_XML_PATH, 
                    Utils.Constants.ControlersPattern.LOAD_XML_FILE_OPTION);
        file = new File(path);
            
        while(!file.isFile()){
            showUser(Utils.Constants.ErrorMessages.ILEGAL_LOAD_GAME_XML_PATH + Utils.Constants.END_LINE);
            path = getInputFromUser(Utils.Constants.QuestionsAndMessagesToUser.LOAD_XML_PATH_QUESTION, 
                    Utils.Constants.ErrorMessages.ILEGAL_LOAD_GAME_XML_PATH, 
                    Utils.Constants.ControlersPattern.LOAD_XML_FILE_OPTION);
            file = new File(path);
        }
        
        return path;
    }
    
    public static String getPathToSaveXmlFile() {
        String path;
        
        path = getInputFromUser(Utils.Constants.QuestionsAndMessagesToUser.LOAD_XML_PATH_QUESTION, 
                    Utils.Constants.ErrorMessages.ILEGAL_LOAD_GAME_XML_PATH, 
                    Utils.Constants.ControlersPattern.LOAD_XML_FILE_OPTION);
        
        return path;
    }
    
    public static Utils.TurnMenuResult askTurnMenuWithSave(){
        String answer;
        Utils.TurnMenuResult returnValue = null;
        
        answer = getInputFromUser(Utils.Constants.QuestionsAndMessagesToUser.TURN_MENU_WITH_SAVE,
                                  Utils.Constants.ErrorMessages.ILEGAL_INPUT_1_2_3, 
                                  Utils.Constants.ControlersPattern.ONE_TWO_THREE_OPTIONS);
        
        if(Utils.TurnMenuResult.CONTINUE.getTurnMenuResultValue() == Integer.valueOf(answer)){
            returnValue = Utils.TurnMenuResult.CONTINUE;
        }
        else if(Utils.TurnMenuResult.SAVE_GAME.getTurnMenuResultValue() == Integer.valueOf(answer)){
            returnValue = Utils.TurnMenuResult.SAVE_GAME;
        }
        else if(Utils.TurnMenuResult.EXIT_GAME.getTurnMenuResultValue() == Integer.valueOf(answer)){
            returnValue = Utils.TurnMenuResult.EXIT_GAME;
        }
            
        return returnValue;
    }
    
    public static Utils.TurnMenuResult askTurnMenuWithoutSave(){
        String answer;
        Utils.TurnMenuResult returnValue = null;
        
        answer = getInputFromUser(Utils.Constants.QuestionsAndMessagesToUser.TURN_MENU_WITHOUT_SAVE,
                                  Utils.Constants.ErrorMessages.ILEGAL_INPUT_1_2, 
                                  Utils.Constants.ControlersPattern.ONE_TWO_OPTIONS);
        
        if(Utils.TurnMenuResultWithOutSave.CONTINUE.getTurnMenuResultWithOutSaveValue() == Integer.valueOf(answer)){
            returnValue = Utils.TurnMenuResult.CONTINUE;
        }
        else if(Utils.TurnMenuResultWithOutSave.EXIT_GAME.getTurnMenuResultWithOutSaveValue() == Integer.valueOf(answer)){
            returnValue = Utils.TurnMenuResult.EXIT_GAME;
        }
            
        return returnValue;
    }
    
    public static void failLoadingFileMsg(String msg) {
        showUser(msg + Utils.Constants.END_LINE);
        failLoadingFileMsg();
    }

    public static void failLoadingFileMsg() {
        showUser(Utils.Constants.ErrorMessages.FAIL_LOADING_FILE_MSG);
    }
    
    public static Utils.SaveOrSaveas getSaveOrSaveAs() {
        String answer;
        Utils.SaveOrSaveas returnValue = null;

        answer = getInputFromUser(Utils.Constants.QuestionsAndMessagesToUser.SAVE_OR_SAVEAS,
                                  Utils.Constants.ErrorMessages.ILEGAL_INPUT_SAVE_OR_SAVE_AS, 
                                  Utils.Constants.ControlersPattern.ONE_TWO_OPTIONS);

        if(Utils.SaveOrSaveas.SAVE.getSaveOrSaveasValue() == Integer.valueOf(answer)){
            returnValue = Utils.SaveOrSaveas.SAVE;
        }
        else if(Utils.SaveOrSaveas.SAVEAS.getSaveOrSaveasValue() == Integer.valueOf(answer)){
            returnValue = Utils.SaveOrSaveas.SAVEAS;
        }
            
        return returnValue;

    }

    public static void theBoardIsEmptyMsg() {
        showUser(Utils.Constants.ErrorMessages.EMPTY_BOARD);
    }

    public static void failSavingFileMsg() {
        showUser(Utils.Constants.ErrorMessages.ERROR_SAVING_FILE);
    }

    public static void failSavingFileMsg(String msg) {
        showUser(msg + Utils.Constants.END_LINE);
        failSavingFileMsg();
    }

    public static void playerIsRemoving(String name) {
        showUser(String.format(Utils.Constants.QuestionsAndMessagesToUser.PLAYER_IS_REMOVING, name));
    }
}
 