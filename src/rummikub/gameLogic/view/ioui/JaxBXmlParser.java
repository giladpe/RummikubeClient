/*
 * deals with files in the game
 */

package rummikub.gameLogic.view.ioui;

import XmlClasses.Board;
import XmlClasses.Color;
import static XmlClasses.Color.BLACK;
import static XmlClasses.Color.BLUE;
import static XmlClasses.Color.RED;
import static XmlClasses.Color.YELLOW;
import XmlClasses.PlayerType;
import XmlClasses.Players;
import XmlClasses.Rummikub;
import XmlClasses.Tile;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;           
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import rummikub.gameLogic.model.gameobjects.Serie;
import rummikub.gameLogic.model.player.ComputerPlayer;
import rummikub.gameLogic.model.player.HumanPlayer;
import rummikub.gameLogic.model.player.Player;
import org.xml.sax.SAXException;


public class JaxBXmlParser {
    // Constants
    private static String lastPathSaved = null;
    private static final String RESOURCES = "/resources/";
    private static final Integer MAX_TILE_SAME = 2;
    private static final Integer MIN_TILE_SAME = 1;
    private static final Integer MAX_JOKERS_IN_MATCH_ONE_COLOR = 1;
    
    static private ArrayList<Player> playerArray;
    static private rummikub.gameLogic.model.gameobjects.Board board;
    static private rummikub.gameLogic.model.player.Player currPlayer;
    static private String gameName;
    
    // Getter
    public static String getLastPathSaved() {
        return lastPathSaved;
    }

    public static ArrayList<Player> getPlayerArray() {
        return playerArray;
    }

    public static rummikub.gameLogic.model.gameobjects.Board getBoard() {
        return board;
    }

    public static Player getCurrPlayer() {
        return currPlayer;
    }

    public static String getGameName() {
        return gameName;
    }
        
    // Private methods
    private static ArrayList<Player> copyGeneratedPlayersToPlayers(XmlClasses.Players players, int sum) {
        ArrayList<Player> playersArray = new ArrayList<>();
                
        for (XmlClasses.Players.Player currGeneratedPlayer : players.getPlayer()) {
            rummikub.gameLogic.model.player.Player currentPlayer = copyGeneratedPlayerToPlayer(currGeneratedPlayer, sum);
            playersArray.add(currentPlayer);
        }
        
        return playersArray;
    }

    private static Player getCurrPlayerFromGeneratedPlayers(Players players, String currentPlayer, int sum) {
        rummikub.gameLogic.model.player.Player playerToReturn = null;
        
        for (XmlClasses.Players.Player player : players.getPlayer()) {
            if(player.getName().equals(currentPlayer)){
                playerToReturn = copyGeneratedPlayerToPlayer(player, sum);
            }
        }
        
        return playerToReturn;
    }

    private static rummikub.gameLogic.model.player.Player copyGeneratedPlayerToPlayer(Players.Player generatedPlayer, int sum) {
        Player playerToReturn = null;
        boolean isPlayerHuman = false;
        ArrayList<rummikub.gameLogic.model.gameobjects.Tile> tileList = new ArrayList<>();
        
        isPlayerHuman = (generatedPlayer.getType().equals(PlayerType.HUMAN));
        
        if(isPlayerHuman){
            playerToReturn = new HumanPlayer(generatedPlayer.getName());
        }
        else{
            playerToReturn = new ComputerPlayer();
            playerToReturn.setName(generatedPlayer.getName());
        }
        
        playerToReturn.setIsHuman(isPlayerHuman);
        playerToReturn.setFirstMoveDone(generatedPlayer.isPlacedFirstSequence()); // check this by default
        playerToReturn.setNormalMoveDone(false); // check this by default
        playerToReturn.setScore(Player.FIRST_SCOORE); // chech this by default
        
        copyGeneratedTileListToTileList(generatedPlayer.getTiles().getTile(), tileList);
         
        playerToReturn.setListPlayerTiles(tileList);
        
        return playerToReturn;
    }

    private static void copyGeneratedTileListToTileList(List<XmlClasses.Tile> generatedTileList, ArrayList<rummikub.gameLogic.model.gameobjects.Tile> tileList) {
        rummikub.gameLogic.model.gameobjects.Tile realTile = null;
        for (XmlClasses.Tile generatedTile : generatedTileList) {
            realTile = copyGeneratedTileToTile(generatedTile);
            tileList.add(realTile);
        }
    }

    private static rummikub.gameLogic.model.gameobjects.Tile copyGeneratedTileToTile(XmlClasses.Tile generatedTile) {
        rummikub.gameLogic.model.gameobjects.Tile.Color color = null;
        rummikub.gameLogic.model.gameobjects.Tile.TileNumber value = null;
        rummikub.gameLogic.model.gameobjects.Tile realTile = null;
                
        switch(generatedTile.getColor()){
            case BLACK:{
                color = rummikub.gameLogic.model.gameobjects.Tile.Color.BLACK;
                
                break;
            }
            case BLUE:{
                color = rummikub.gameLogic.model.gameobjects.Tile.Color.BLUE;
                
                break;
            }
            case RED:{
                color = rummikub.gameLogic.model.gameobjects.Tile.Color.RED;
                
                break;
            }
            case YELLOW:{
                color = rummikub.gameLogic.model.gameobjects.Tile.Color.YELLOW;
                
                break;
            }
        }
        
        value = rummikub.gameLogic.model.gameobjects.Tile.TileNumber.getTileNumberByValue(generatedTile.getValue());
        
        realTile = new rummikub.gameLogic.model.gameobjects.Tile(color, value);
        
        return realTile;
    }

    private static rummikub.gameLogic.model.gameobjects.Board copyGenratedBoardToBoard(XmlClasses.Board generatedBoard) {
        Serie currSerie = new Serie();
        rummikub.gameLogic.model.gameobjects.Board realBoard = new rummikub.gameLogic.model.gameobjects.Board(new ArrayList<Serie>());
        
        for (XmlClasses.Board.Sequence seq : generatedBoard.getSequence()) {
            currSerie = copyGeneratedSequenceToSerie(seq);
            realBoard.addSeries(currSerie);
        }
        
        return realBoard;
    }

    private static Serie copyGeneratedSequenceToSerie(Board.Sequence seq) {
        Serie currSerie = null;
        rummikub.gameLogic.model.gameobjects.Tile realTile = null;
        ArrayList<rummikub.gameLogic.model.gameobjects.Tile> arrayOfTiles = new ArrayList<>();
        
        for (XmlClasses.Tile generatedTile : seq.getTile()) {
            realTile = copyGeneratedTileToTile(generatedTile);
            arrayOfTiles.add(realTile);
        }
        
        currSerie = new Serie(arrayOfTiles);
        
        return currSerie;
    }
    
    
    private static boolean saveSettinngToXmlInSpecificFile(String strPathGameFileName,
                                          ArrayList<Player> playerArray, 
                                          rummikub.gameLogic.model.gameobjects.Board board, 
                                          String gameName, 
                                          String currPlayerName) throws SAXException, JAXBException{
        // Get players
        Players generatedPlayers = new Players();
        copyPlayerToGeneratedPlayer(generatedPlayers, playerArray);

        // Get board
        XmlClasses.Board generatedBoard = new Board();
        copyBoardToGeneratedBoard(generatedBoard, board);

        // Get rummikub
        XmlClasses.Rummikub rummi = new Rummikub();
        rummi.setBoard(generatedBoard);
        rummi.setCurrentPlayer(currPlayerName);
        rummi.setName(gameName);
        rummi.setPlayers(generatedPlayers);

        //get the Schema from the XSD file
        URL csdURL = JaxBXmlParser.class.getResource(RESOURCES + "rummikub.xsd");
        SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        Schema schema = schemaFactory.newSchema(csdURL);


        JAXBContext context = JAXBContext.newInstance(Rummikub.class);
        Marshaller marshaller = context.createMarshaller();

        //attach the Schema to the unmarshaller so it will use it to run validations
        //on the content of the XML
        marshaller.setSchema(schema);
        
        try{
            // Get the file
            File f = new File(strPathGameFileName);

        
            marshaller.marshal(rummi, f);

            return true;
        }
        catch (JAXBException exception){

              return false;
        }
    }
            
    private static void copyPlayerToGeneratedPlayer(Players players, ArrayList<Player> playerArray) {
        XmlClasses.Tile generatedTile = null;
        Players.Player.Tiles generatedTiles = null;
        
        for (Player playerToAdd : playerArray) {
            // Get Player
            Players.Player generatedPlayer = new Players.Player();
            generatedPlayer.setName(playerToAdd.getName());
            generatedPlayer.setPlacedFirstSequence(playerToAdd.isFirstMoveDone());
            if(playerToAdd.getIsHuman()){
                generatedPlayer.setType(PlayerType.HUMAN);
            }
            else{
                generatedPlayer.setType(PlayerType.COMPUTER);
            }
            
            generatedTiles = new Players.Player.Tiles();
            for (rummikub.gameLogic.model.gameobjects.Tile tile : playerToAdd.getListPlayerTiles()) {
                generatedTile = new Tile();
                copyTileToGeneratedTile(generatedTile, tile);
                generatedTiles.getTile().add(generatedTile);
            }
            generatedPlayer.setTiles(generatedTiles);
            
            // add player to players
            players.getPlayer().add(generatedPlayer);
        }
    }

    private static void copyBoardToGeneratedBoard(Board generatedBoard, rummikub.gameLogic.model.gameobjects.Board board) {
        Board.Sequence sequence = null;
        XmlClasses.Tile generatedTile = null;
        
        // Pass all sequences
        for (Serie serie : board.getListOfSerie()) {
            sequence = new Board.Sequence();
            
            // Pass all tiles in sequences
            for (rummikub.gameLogic.model.gameobjects.Tile t : serie.getSerieOfTiles()) {
                generatedTile = new Tile();
                copyTileToGeneratedTile(generatedTile, t);
                sequence.getTile().add(generatedTile);
            }
            generatedBoard.getSequence().add(sequence);
        }
    }

    private static void copyTileToGeneratedTile(Tile generatedTile, rummikub.gameLogic.model.gameobjects.Tile tile) {
        switch(tile.getTileColor()){
            case BLACK:{
                generatedTile.setColor(Color.BLACK);
                break;
            }
            case BLUE:{
                generatedTile.setColor(Color.BLUE);
                break;
            }
            case RED:{
                generatedTile.setColor(Color.RED);
                break;
            }
            case YELLOW:{
                generatedTile.setColor(Color.YELLOW);
                break;
            }
            default:{
                break;
            }
        }
        
        generatedTile.setValue(tile.getEnumTileNumber().getTileNumberValue());
    }
    
    
    private static boolean checkNotDuplicateTilesValid(Rummikub rummikub) {
        boolean isLegal = true;
        
        Map<XmlClasses.Tile, Integer> tilesMapCounter = new HashMap<>();
        
        // Lambda expression pass all tiles in board and checks duplicate
        isLegal = (rummikub.getBoard().getSequence().stream().allMatch(seq->seq.getTile().stream().allMatch(tile -> checkTileAlreadyExistsValid(tilesMapCounter, tile)))) &&
                (rummikub.getPlayers().getPlayer().stream().allMatch(player->player.getTiles().getTile().stream().allMatch(tile -> checkTileAlreadyExistsValid(tilesMapCounter, tile))));
        
        return isLegal;
    }

    private static boolean checkTileAlreadyExistsValid(Map<Tile, Integer> tilesMapCounter, Tile tile) {
        Iterator it = tilesMapCounter.entrySet().iterator();
        boolean isFound = false;
        
        // Pass hasmap (cant use hashmap get beacause netbeans didn't generate this)
        while(it.hasNext()){
            Map.Entry<Tile, Integer> pair = (Map.Entry<Tile, Integer>)it.next();
            
            if(pair.getKey().getColor().equals(tile.getColor()) && 
               pair.getKey().getValue() == tile.getValue()){
                isFound = true;
                
                if(pair.getValue() < MAX_TILE_SAME){
                    pair.setValue(pair.getValue() + 1);
                }
                else{
                    return false;
                }
            }
        }    
         
        if(!isFound){
            tilesMapCounter.put(tile, MIN_TILE_SAME);
        }
        
        return true;
    }
    
    private static boolean checkCurrPlayerIsExistValid(String currentPlayer, List<Players.Player> player) {
        boolean result = player.stream().anyMatch(p1 -> (p1.getName().equals(currentPlayer)));        
        return result;
    }
        
    private static boolean checkMorethan2Jokers(Rummikub rummikub) {
        int countJokersRed = 0;
        int countJokersBlack = 0;
        boolean ilegalColorForJoker = false;
                
        for (Players.Player player : rummikub.getPlayers().getPlayer()) {
            for (Tile tile : player.getTiles().getTile()) {
                if(tile.getValue() == 0){
                    if(tile.getColor().equals(Color.BLACK)){
                        countJokersBlack++;
                    }
                    else if(tile.getColor().equals(Color.RED)){
                        countJokersRed++;
                    }
                    else{
                        ilegalColorForJoker = true;
                    }
                }
                
                if(countJokersBlack > MAX_JOKERS_IN_MATCH_ONE_COLOR ||
                   countJokersRed > MAX_JOKERS_IN_MATCH_ONE_COLOR ||
                   ilegalColorForJoker){
                    return false;
                }
            }
        }
        
        for (Board.Sequence seq : rummikub.getBoard().getSequence()) {
            for (Tile tile : seq.getTile()) {
                if(tile.getValue() == 0){
                    if(tile.getColor().equals(Color.BLACK)){
                        countJokersBlack++;
                    }
                    else if(tile.getColor().equals(Color.RED)){
                        countJokersRed++;
                    }
                    else{
                        ilegalColorForJoker = true;
                    }
                }
                
                if(countJokersBlack > MAX_JOKERS_IN_MATCH_ONE_COLOR ||
                   countJokersRed > MAX_JOKERS_IN_MATCH_ONE_COLOR ||
                   ilegalColorForJoker){
                    return false;
                }
            }
        }
        
        return true;
    }
        
    private static boolean checkGenerateValidation(Rummikub rummikub) {
        boolean isNotDuplicateTile = checkNotDuplicateTilesValid(rummikub);
        boolean isCurrentPlayerExists = checkCurrPlayerIsExistValid(rummikub.getCurrentPlayer(), rummikub.getPlayers().getPlayer());
        boolean isMoreThan2Jokers = checkMorethan2Jokers(rummikub);
        boolean isGameNameExists = (rummikub.getName()!= null) && (!rummikub.getName().equals(""));
        
        return (isGameNameExists &&
                isCurrentPlayerExists &&
                isMoreThan2Jokers &&
                isNotDuplicateTile);
    }

    private static int getSumOfPointsInBoard(rummikub.gameLogic.model.gameobjects.Board board) {
        int sum = 0;
        
        for (Serie currSerie : board.getListOfSerie()) {
            if(currSerie.isValidSerie()){
                sum += currSerie.getScoreOfSerie();
            }
            else{
                return -1;
            }
        }
        
        return sum;
    }
        
    //Public methods
    public static boolean loadSettingsFromXml() throws SAXException, IOException{
        //get the Schema from the XSD file
        URL csdURL = JaxBXmlParser.class.getResource(RESOURCES + "rummikub.xsd");
        SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        Schema schema = schemaFactory.newSchema(csdURL);

        // Get the file chosen from here
        String strXmlFilePath = InputOutputParser.getXmlFilePathForLoad();
        File source = new File(strXmlFilePath);
    
        try {
            JAXBContext context = JAXBContext.newInstance(Rummikub.class);
            Unmarshaller unmarshaller = context.createUnmarshaller();

            //attach the Schema to the unmarshaller so it will use it to run validations
            //on the content of the XML
            unmarshaller.setSchema(schema);

            Rummikub rummikub = (Rummikub) unmarshaller.unmarshal(source); //unmarshal(xmlInputStream);
            
            if(!checkGenerateValidation(rummikub)){
                throw new JAXBException("");
            }
            
            // Copy properties
            board = copyGenratedBoardToBoard(rummikub.getBoard());
            int sum = getSumOfPointsInBoard(board);
            boolean isValidBoard = (board.validateBoard() || sum == 0) && (sum >= 30 || sum == 0);
            
            if(isValidBoard){
                playerArray = copyGeneratedPlayersToPlayers(rummikub.getPlayers(), sum);
                currPlayer = getCurrPlayerFromGeneratedPlayers(rummikub.getPlayers(), rummikub.getCurrentPlayer(), sum);
                currPlayer = playerArray.get(playerArray.indexOf(currPlayer));
                gameName = rummikub.getName();
            }
            else{
                return false;
            }
            
            return true;
        }
        catch (JAXBException exception){
            return false;
        }
    }
    public static boolean loadSettingsFromXml(File source) throws SAXException, IOException{
        //get the Schema from the XSD file
        URL csdURL = JaxBXmlParser.class.getResource(RESOURCES + "rummikub.xsd");
        SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        Schema schema = schemaFactory.newSchema(csdURL);

        // Get the file chosen from here
    
        try {
            JAXBContext context = JAXBContext.newInstance(Rummikub.class);
            Unmarshaller unmarshaller = context.createUnmarshaller();

            //attach the Schema to the unmarshaller so it will use it to run validations
            //on the content of the XML
            unmarshaller.setSchema(schema);

            Rummikub rummikub = (Rummikub) unmarshaller.unmarshal(source); //unmarshal(xmlInputStream);
            
            if(!checkGenerateValidation(rummikub)){
                throw new JAXBException("");
            }
            
            // Copy properties
            board = copyGenratedBoardToBoard(rummikub.getBoard());
            int sum = getSumOfPointsInBoard(board);
            boolean isValidBoard = (board.validateBoard() || sum == 0) && (sum >= 30 || sum == 0);
            
            if(isValidBoard){
                playerArray = copyGeneratedPlayersToPlayers(rummikub.getPlayers(), sum);
                currPlayer = getCurrPlayerFromGeneratedPlayers(rummikub.getPlayers(), rummikub.getCurrentPlayer(), sum);
                currPlayer = playerArray.get(playerArray.indexOf(currPlayer));
                gameName = rummikub.getName();
            }
            else{
                return false;
            }
            
            return true;
        }
        catch (JAXBException exception){
            return false;
        }
    }
  
    public static boolean saveSettingsToXml(ArrayList<Player> playerArray, 
                                          rummikub.gameLogic.model.gameobjects.Board board, 
                                          String gameName, 
                                          String currPlayerName) throws SAXException, JAXBException, IOException{
        // Not first save in game
        //if(lastPathSaved != null){
            return saveSettinngToXmlInSpecificFile(lastPathSaved, playerArray, board, gameName, currPlayerName);  
        //}
        // First save in game - use save as
//        else{
//            return saveAsSettingsToXml(playerArray, board, gameName, currPlayerName);
//        }
    }

    public static boolean saveAsSettingsToXml(ArrayList<Player> playerArray, 
                                          rummikub.gameLogic.model.gameobjects.Board board, 
                                          String gameName, 
                                          String currPlayerName) throws JAXBException, FileNotFoundException, IOException, SAXException{
        String strPathGameFileName = InputOutputParser.getPathToSaveXmlFile();
        
        if(lastPathSaved == null){
           lastPathSaved = strPathGameFileName;
        }
        
        return saveSettinngToXmlInSpecificFile(strPathGameFileName, playerArray, board, gameName, currPlayerName);
  }
        public static boolean saveAsSettingsToXml(String strPathGameFileName,ArrayList<Player> playerArray, 
                                          rummikub.gameLogic.model.gameobjects.Board board, 
                                          String gameName, 
                                          String currPlayerName) throws JAXBException, FileNotFoundException, IOException, SAXException{

        if(lastPathSaved == null){
           lastPathSaved = strPathGameFileName;
        }
        
        return saveSettinngToXmlInSpecificFile(strPathGameFileName, playerArray, board, gameName, currPlayerName);
  }
}