/*
 * utils class of the input parser
 */
package rummikub.gameLogic.view.ioui;

import rummikub.gameLogic.model.logic.Settings;

/**
 *
 * @author roiesti
 */

public class Utils {
    public static class Constants{
        // Constants
        public static final String END_LINE = System.getProperty("line.separator");
        public static final String EMPTY_STRING = "";
        static final String RIVALS = "Rivals: "; 
        static final int NUMBER_OF_SEPERATE_CHARS = 30; 
        static final char SEPERATE_CHAR = '_';
        
        // Controlers
        public static class ControlersPattern{
        static final String ONE_TWO_THREE_OPTIONS = "[1]|[2]||[3]";
        static final String ONE_TWO_OPTIONS = "[1]|[2]";
        static final String Y_ANSWER = "y";
        static final String PLAYER_TOTAL_NUMBER_OPTIONS = "[2]||[3]||[4]";
        static final String Y_N_ANSWER_OPTIONS = "[y]|[n]|[Y]|[N]";
        static final String GAME_NAME_OPTION = "[a-z].*|[A-Z].*|[0-9].*";
        public static final String COMPUTER_NAME = "^[Cc]{1}[Oo]{1}[mM]{1}[pP]{1}[0-9]";
        static final String LOAD_XML_FILE_OPTION = ".*[\\.][x]{1}[m]{1}[l]{1}";
        }
        
        // questions
        public static class QuestionsAndMessagesToUser{
            static final String TOTAL_PLAYERS_NUMBER_QUESTION = String.format("Please enter the players total number (%d-%d): ",Settings.MIN_NUMBER_OF_PLAYERS,Settings.MAX_NUMBER_OF_PLAYERS);
            static final String COMPUTER_PLAYERS_NUMBER_QUESTION = "Please enter the computer players number (0-%s): ";
            static final String PLAYER_NAME_QUESTION = "Please enter the player name (non duplicate name): ";
            static final String TAKE_BOARD_OR_PLAYERLIST_QUESTION = "would you take tile from your hand or from the board?" + END_LINE+ "press 1 from board" + END_LINE + "press 2 form hand"+ END_LINE;
            static final String PUT_BOARD_OR_PLAYERLIST_QUESTION = "would you put the tile in the hand or in the board?" + END_LINE + "press 1 to put in the board" + END_LINE + "press 2 to put into the hand" + END_LINE;
            static final String ROW_QUESTION = "plaese enter row (starting from 0): ";
            static final String COL_QUESTION = "plaese enter col (starting from 0): ";
            static final String PLAYER_TILE_INDEX = "plaese enter the tile you chose (starting from index 0, count from left): ";
            static final String CONTINUE_IN_TURN_QUESTION = "would u like to finish your turn? (Y/N): ";
            static final String NEW_SETTINGS_OR_OLD_SETTINGS_QUESTION = "would you like to get new settings or old settings? press" + END_LINE +
                                                                        "1. To use old settings. " + END_LINE +
                                                                        "2. To set new settings. " + END_LINE;;
            static final String GAME_NAME_QUESTION = "Please enter a the game name: ";
            static final String LOAD_XML_PATH_QUESTION = "Please enter the path of the xml file to load: ";
            static final String MAIN_MENU =    "1. Start new game." + END_LINE +
                                             "2. Load game. " + END_LINE +
                                             "3. To exit game. " + END_LINE;
            static final String TURN_MENU_WITH_SAVE = "1. Continue. " + END_LINE +
                                                      "2. Save game. " + END_LINE +
                                                      "3. To exit game. " + END_LINE;
            static final String TURN_MENU_WITHOUT_SAVE = "1. Continue. " + END_LINE +
                                                         "2. To exit game. " + END_LINE;
            static final String SAVE_OR_SAVEAS = "1. SAVE. " + END_LINE
                                             + "2. SAVE AS. " + END_LINE;           
            static final String SKIP_TURN = "would u like to skip your turn? (Y/N) ";
            public static final String GAME_OVER = "Game over... ";
            public static final String TIE = "We have a tie ";
            public static final String WINNER_IS = "The winner is: ";
            static final String PLAYER_IS_REMOVING = "%s is leaving the game. Bye Bye!!" + END_LINE;
            public static final String SUCCSESSFUL_MOVE = "Move done";

        }
        
        // error messages
        public static class ErrorMessages{
            static final String ILEGAL_TOTAL_PLAYER_NUMBER = String.format("Please try again. You should enter an intger between %d-%d.",Settings.MIN_NUMBER_OF_PLAYERS,Settings.MAX_NUMBER_OF_PLAYERS);
            static final String ILEGAL_COMPUTER_PLAYER_NUMBER = "Please try again. You should enter an intger between 0-%s.";
            static final String ILEGAL_PLAYER_NAME = "Please try again. You should enter a non duplicate name. ";
            static final String ILEGAL_INPUT_MAIN_MENU = String.format("Please try again. You should enter int between %s.",MainMenuResult.menuOptions());
            static final String ILEGAL_INPUT_INT_BETWEEN = "please try again, You shoud enter int between (%s-%s).";
            static final String ILEGAL_INPUT_Y_N = "please try again, You shoud enter (Y/N).";
            static final String ILEGAL_INPUT_1_2 = "please try again, You shoud enter 1/2 ";
            static final String ILEGAL_INPUT_1_2_3 = "please try again, You shoud enter 1/2/3 ";
            static final String ILEGAL_INPUT_SAVE_OR_SAVE_AS = "Ilegal input: plaese enter 1 to save and 2 to save as." + END_LINE;
            static final String ILEGAL_NUMIRIC_INPUT = "Wrong selection, You should enter a valid option numer."; /**/
            static final String ILEGAL_GAME_NAME = "Ilegal input, plaese enter a name.";
            public static final String ILEGAL_TILE_IS_NOT_BELONG_TO_HAND = "Ilegal move: tile is not exists in player's hand";
            public static final String ILEGAL_TILE_INSERTED_NOT_IN_RIGHT_ORDER = "Ilegal move: sequence have to be in right order";
            public static final String ILEGAL_CANT_TUCH_BOARD_IN_FIRST_MOVE = "Ilegal move: first move have to be from player hand only";
            static final String ILEGAL_LOAD_GAME_XML_PATH = "ilegal path for xml file";
            public final static String FAIL_LOADING_FILE_MSG = "Error was not able to load file!"; 
            static final String ERROR_SAVING_FILE = "Error game was not saved!"; 
            static final String EMPTY_BOARD = "The board is empty, use tiles in your hand.";
        }
    }

    public enum DealWithTileFromBoardOrHand{
        TILES_IN_BOARD(1),
        TILES_IN_HAND(2);
        
        private final int boardOrHand;
        
        DealWithTileFromBoardOrHand(int boardOrList){
            this.boardOrHand = boardOrList;
        }
        
        public int getBoardOrHandValue(){
            return this.boardOrHand;
        }
    }
    
    public enum TurnMenuResult{
        CONTINUE(1),
        SAVE_GAME(2),
        EXIT_GAME(3);
        
        private final int turnMenuResult;
        
        TurnMenuResult(int turnMenuResult){
            this.turnMenuResult = turnMenuResult;
        }
        
        public int getTurnMenuResultValue(){
            return this.turnMenuResult;
        }
    }
        
    public enum TurnMenuResultWithOutSave{
        CONTINUE(1),
        EXIT_GAME(2);
        
        private final int turnMenuResultWithOutSave;
        
        TurnMenuResultWithOutSave(int turnMenuResultWithOutSave){
            this.turnMenuResultWithOutSave = turnMenuResultWithOutSave;
        }
        
        public int getTurnMenuResultWithOutSaveValue(){
            return this.turnMenuResultWithOutSave;
        }
    }
        
    public enum SaveOrSaveas{
        SAVE(1),
        SAVEAS(2);
        
        private final int SaveOrSaveas;
        
        SaveOrSaveas(int SaveOrSaveas){
            this.SaveOrSaveas = SaveOrSaveas;
        }
        
        public int getSaveOrSaveasValue(){
            return this.SaveOrSaveas;
        }
    }
        
    public enum NewSettingsOrOldSettings{
        OLD_SETTINGS(1),
        NEW_SETTINGS(2);
        
        private final int newSettingsOrOldSettings;
        
        NewSettingsOrOldSettings(int newSettingsOrOldSettings){
            this.newSettingsOrOldSettings = newSettingsOrOldSettings;
        }
        
        public int getNewSettingsOrOldSettingsValue(){
            return this.newSettingsOrOldSettings;
        }
    }
        
    public enum MainMenuResult{
        START_NEW_GAME(1),
        LOAD_GAME(2),
        EXIT_GAME(3);

        private final int mainMenuResult;
        
        public static String menuOptions() {
            return String.format("%d-%d",START_NEW_GAME.getMainMenuResultValue(), (int)EXIT_GAME.getMainMenuResultValue());
        }

        MainMenuResult(int mainMenuResult){
            this.mainMenuResult = mainMenuResult;
        }

        public int getMainMenuResultValue(){
            return this.mainMenuResult;
        }
    }
}
