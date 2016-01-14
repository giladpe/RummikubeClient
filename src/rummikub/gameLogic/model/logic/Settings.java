/*
 * this class represents the basic settings for the game gives by the player or by a file
 */
package rummikub.gameLogic.model.logic;

import java.util.ArrayList;
import static rummikub.gameLogic.view.ioui.Utils.Constants.ControlersPattern.COMPUTER_NAME;

public class Settings {
    //Constants

    public static final int MIN_NUMBER_OF_PLAYERS = 2;
    public static final int MAX_NUMBER_OF_PLAYERS = 4;

    // Data Members
    private String GamesName;
    private int NumOfCpuPlayers;
    private int NumOfHumanPlayers;
    private int NumOfPlayers;
    private ArrayList<String> HumanPlayersNames;

    // Constructor
    public Settings(String GamesName, int totalNumPlayer, int computerPlayersNumber, ArrayList<String> listOfPlayerNames) {
        this.GamesName = GamesName;
        this.NumOfCpuPlayers = computerPlayersNumber;
        this.NumOfHumanPlayers = totalNumPlayer - computerPlayersNumber;
        this.NumOfPlayers = totalNumPlayer;
        this.HumanPlayersNames = listOfPlayerNames;
    }
    
    public Settings(Settings settings) {
        this.GamesName = settings.getGamesName();
        this.NumOfCpuPlayers = settings.getNumOfCpuPlayers();
        this.NumOfHumanPlayers = settings.getNumOfPlayers() - settings.getNumOfCpuPlayers();
        this.NumOfPlayers = settings.getNumOfPlayers();
        this.HumanPlayersNames = settings.getHumanPlayersNames();
    }

    // Setters
    public void setGamesName(String GamesName) {
        this.GamesName = GamesName;
    }

    public void setNumOfCpuPlayers(int NumOfCpuPlayers) {
        this.NumOfCpuPlayers = NumOfCpuPlayers;
    }

    public void setNumOfHumanPlayers(int NumOfHumanPlayers) {
        this.NumOfHumanPlayers = NumOfHumanPlayers;
    }

    public void setNumOfPlayers(int NumOfPlayers) {
        this.NumOfPlayers = NumOfPlayers;
    }

    public void setHumanPlayersNames(ArrayList<String> HumanPlayersNames) {
        this.HumanPlayersNames = HumanPlayersNames;
    }

    // Getters
    public String getGamesName() {
        return GamesName;
    }

    public int getNumOfCpuPlayers() {
        return NumOfCpuPlayers;
    }

    public int getNumOfHumanPlayers() {
        return NumOfHumanPlayers;
    }

    public int getNumOfPlayers() {
        return NumOfPlayers;
    }

    public ArrayList<String> getHumanPlayersNames() {
        return HumanPlayersNames;
    }

    // Public Methods
    public boolean isPlayerNameExists(String playerName) {
        boolean Resualt;

        Resualt = HumanPlayersNames.contains(playerName);

        return Resualt;
    }

    public void addHumanPlayer(String playerName) {
        HumanPlayersNames.add(playerName);
    }

    public boolean isLegalTotalPlayerNumberSelected(int NumOfPlayers) {
        return NumOfPlayers <= MIN_NUMBER_OF_PLAYERS && NumOfPlayers >= MAX_NUMBER_OF_PLAYERS;
    }

    public boolean isLegalComputerPlayerNumberSelected(int NumOfCpuPlayers) {
        return this.NumOfPlayers - NumOfCpuPlayers >= 0 && NumOfCpuPlayers >= 0;
    }

    public void removePlayerFromGame(boolean isHuman) {
        this.NumOfPlayers--;
        if (isHuman) {
            this.NumOfHumanPlayers--;
        } else {
            this.NumOfCpuPlayers--;
        }
    }

    public boolean isLessThenTwoPlayers() {
        return this.NumOfPlayers < MIN_NUMBER_OF_PLAYERS;
    }

    static public boolean isValidPlayersNames(ArrayList<String> namesList) {
        return !isPlayersNamesHasCompterName(namesList) && hasDiffNames(namesList);
    }
static private boolean isPlayersNamesHasCompterName(ArrayList<String> namesList) {
            boolean hasComputerName = false;
            for (String name : namesList) {
                if (isPlayerNameHasCompterName(name)) {
                    hasComputerName = true;
                }
            }
            return hasComputerName;
        }

        static private boolean isPlayerNameHasCompterName(String name) {
            return name.matches(COMPUTER_NAME);
        }
    private static boolean hasDiffNames(ArrayList<String> playersNames) {
        boolean foundSimmilar = false;
        for (int i = 0; i < (playersNames.size() - 1) && !foundSimmilar; i++) {
            for (int j = i + 1; j < playersNames.size() && !foundSimmilar; j++) {
                foundSimmilar = playersNames.get(i).equals(playersNames.get(j));
            }
        }
        return !foundSimmilar;
    }
}
