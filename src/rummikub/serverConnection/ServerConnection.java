/*
 * 
 */
package rummikub.serverConnection;

import java.util.List;
import rummikub.client.ws.*;

public class ServerConnection {

    private final RummikubWebService rummikubWebService;
    private Runnable methodToRun; //maybe not needed at all
    
    public ServerConnection() {
        RummikubWebServiceService service = new RummikubWebServiceService();
        this.rummikubWebService = service.getRummikubWebServicePort();
    }
    
        //**********Public functions used by the Web Service - START **********/

    public List<Event> getEvents(int playerId, int eventId) throws InvalidParameters_Exception {
        return this.rummikubWebService.getEvents(playerId, eventId);
    }

    public String createGameFromXML(String xmlData) throws DuplicateGameName_Exception,
                                                           InvalidParameters_Exception, 
                                                           InvalidXML_Exception {
        
        return this.rummikubWebService.createGameFromXML(xmlData);
    }

    public List<PlayerDetails> getPlayersDetails(String gameName) throws GameDoesNotExists_Exception {
        return this.rummikubWebService.getPlayersDetails(gameName);
    }

    public void createGame(String name, int humanPlayers, int computerizedPlayers) throws DuplicateGameName_Exception,
                                                                                          InvalidParameters_Exception {
        this.rummikubWebService.createGame(name, humanPlayers, computerizedPlayers);
    }

    public GameDetails getGameDetails(String gameName) throws GameDoesNotExists_Exception {
        
        return this.rummikubWebService.getGameDetails(gameName);
    }

    public List<String> getWaitingGames() {
        return this.rummikubWebService.getWaitingGames();
    }

    public int joinGame(String gameName, String playerName) throws GameDoesNotExists_Exception, 
                                                                   InvalidParameters_Exception {
        return this.rummikubWebService.joinGame(gameName, playerName);
    }

    public PlayerDetails getPlayerDetails(int playerId) throws GameDoesNotExists_Exception, 
                                                               InvalidParameters_Exception {
    
        return this.rummikubWebService.getPlayerDetails(playerId);
    }

    public void createSequence(int playerId, List<Tile> tiles) throws InvalidParameters_Exception {

        this.rummikubWebService.createSequence(playerId, tiles);

    }

    public void addTile(int playerId, Tile tile, int sequenceIndex, int sequencePosition) 
                                                            throws InvalidParameters_Exception {
        this.rummikubWebService.addTile(playerId, tile, sequenceIndex, sequencePosition);
    }

    public void takeBackTile(int playerId, int sequenceIndex, int sequencePosition) 
                                                            throws InvalidParameters_Exception {
        this.rummikubWebService.takeBackTile(playerId, sequenceIndex, sequencePosition);
    }

    public void moveTile(int playerId, int sourceSequenceIndex, 
                         int sourceSequencePosition, int targetSequenceIndex, 
                         int targetSequencePosition) throws InvalidParameters_Exception {
        
        this.rummikubWebService.moveTile(playerId, sourceSequenceIndex, sourceSequencePosition, targetSequenceIndex, targetSequencePosition);
    }

    public void finishTurn(int playerId) throws InvalidParameters_Exception {
        this.rummikubWebService.finishTurn(playerId);
    }

    public void resign(int playerId) throws InvalidParameters_Exception {
        this.rummikubWebService.resign(playerId);
    }
    
    //********** Public functions used by the Web Service - END **********/

    
    //maybe not needed at all
    private void runInNewThread(Runnable methodToRun) {
        Thread newThread = new Thread(methodToRun);
        newThread.setDaemon(true);
        newThread.start();
    }
    
    
    
    
    
    
    
    
    
    
    
    //**************** code that need to init the service and then use it - START **************** //
    
    //create a new service
    //private RummikubWebServiceService service = new RummikubWebServiceService();

    //get the port
    //private RummikubWebService rummikubWebService = service.getRummikubWebServicePort();
    
    //****** same code to start it with different server adress - START ********//
        //private String address = "127.0.0.1";
        //private int port = 8080;
        //create a URL
        //private URL location = new URL("http://" + address + ":" + port + "/RummikubApi/RummikubWebServiceService?wsdl");

        //create a new service with the URL
        //RummikubWebServiceService rummikubWebService = new RummikubWebServiceService(location);
    //************************ END ************************//

    //and we can use it to send server things
    //rummikubWebService.addTile (null, null, null, null)
    
    //**************** code that need to init the service and then use it - END **************** //
    
}
