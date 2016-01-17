/*
 * this class responsible to control the game screen
 */
package rummikub.view;

import java.awt.Point;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;
import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.DataFormat;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;
import rummikub.client.ws.*;
import rummikub.gameLogic.model.gameobjects.Tile;
import rummikub.gameLogic.model.player.Player;
import rummikubFX.Rummikub;
import rummikub.gameLogic.controller.rummikub.SingleMove;
import rummikub.gameLogic.model.gameobjects.Board;
import rummikub.gameLogic.model.gameobjects.Serie;
import rummikub.gameLogic.model.logic.SeriesGenerator;
import rummikub.gameLogic.model.player.ComputerSingleMoveGenerator;
import rummikub.gameLogic.view.ioui.Utils;
import rummikub.view.viewObjects.AnimatedGameBoardPane;
import rummikub.view.viewObjects.AnimatedSeriePane;
import rummikub.view.viewObjects.AnimatedTilePane;

/**
 * FXML Controller class
 *
 * @author Arthur
 */
public class PlayScreenController implements Initializable, ResetableScreen, ControlledScreen, ServerConnection {

    //Constatns
    private static final String PLAY = " Play";
    private static final String WAIT = " Wait";
    private static final boolean DAEMON_THREAD = true;
    private static final long TIMER_DELAY = 800;
    private static final String STYLE_WHITE = "-fx-text-fill: white";
    private static final String STYLE_GREEN = "-fx-text-fill: green";
    private static final boolean CAN_SAVE_THE_GAME = false;
    private static final int MAX_NUM_OF_PLAYERS = 4;
    private static final boolean VISABLE = true;
    private static final boolean LEGAL_MOVE = true;
    private static final boolean ENABLE_BUTTON = true;
    private static final boolean DISABLE_DRAG_AND_DROP = true;

    //Private members
    private RummikubWebServiceService service;
    private RummikubWebService rummikubWebService;
    private int playerID;
    private int currEvent;
    private String gameName;
    private List<PlayerDetails> playersDetails;
    private int currEventId;
    private PlayerDetails myDetails;
    private Board logicBoard;

    private static final String PLAYER_RESIGNED = " decided to quite";
    private static final String PLAYER_DONE = " done is Turn";
    private static final String GAME_OVER = "Game Is Over";
    private Timer timer;
    private final ArrayList<Label> playersLabelsList = new ArrayList<>();
    private ScreensController myController;
    //private GameLogic rummikubLogic = new GameLogic();
    private SeriesGenerator serieGenerator;
    private ComputerSingleMoveGenerator newMoveGenerator;
    private SimpleBooleanProperty isLegalMove;
    //private Timeline swapTurnTimeLineDelay;
    private AnimatedGameBoardPane centerPane;
    //private PlayersMove currentPlayerMove;
    private final ArrayList<HBox> playersBarList = new ArrayList<>(MAX_NUM_OF_PLAYERS);
    private final ArrayList<Label> labelOfNumOfTileInHandList = new ArrayList<>(MAX_NUM_OF_PLAYERS);
    private final ArrayList<Button> buttonsList = new ArrayList<>();
    private boolean isUserMadeFirstMoveInGame;
    private String nameOfCurrPlayerTurn;

    //FXML Private filds
    @FXML
    private Label turnMsgLabel;
    @FXML
    private Label errorMsg;
    @FXML
    private BorderPane board;
    @FXML
    private Button menu;
    @FXML
    private FlowPane handTile;
    @FXML
    private Button endTrun;
    @FXML
    private Label heapTile;
    @FXML
    private Label firstMoveMsg;
    @FXML
    private Label player1;
    @FXML
    private Label player2;
    @FXML
    private Label player3;
    @FXML
    private Label player4;
    @FXML
    private HBox barPlayer1;
    @FXML
    private Label numTileP1;
    @FXML
    private HBox barPlayer3;
    @FXML
    private Label numTileP3;
    @FXML
    private HBox barPlayer2;
    @FXML
    private Label numTileP2;
    @FXML
    private HBox barPlayer4;
    @FXML
    private Label numTileP4;

    //Private FXML methods
    @FXML
    private void handleMenuButtonAction(ActionEvent event) {
        this.myController.setScreen(Rummikub.SUBMENU_SCREEN_ID, ScreensController.NOT_RESETABLE);
    }

    @FXML
    private void handleEndTrunAction(ActionEvent event) {
        Thread thread = new Thread(() -> {
            try {
                this.rummikubWebService.finishTurn(playerID);
            } catch (InvalidParameters_Exception ex) {
                ///to do 
                Platform.runLater(() -> showGameMsg(errorMsg, ex.getMessage()));
            } //catch(Exception ex) {
            //onServerLostException();
            //}
        });
        thread.setDaemon(DAEMON_THREAD);
        thread.start();
    }

    private synchronized void getRummikubeWsEvents() {

        List<Event> eventList;
        try {
            eventList = this.rummikubWebService.getEvents(playerID, currEventId); //todo
            if (!eventList.isEmpty()) {
                for (Event event : eventList) {
                    handleRummikubWsEvent(event);
                }
                currEventId = getLastEventID(eventList);
            }
        } catch (InvalidParameters_Exception ex) {
            Platform.runLater(() -> (showGameMsg(errorMsg, ex.getMessage())));
        } //catch(Exception ex) {
        //onServerLostException();
        //}

        timer.cancel();
        this.timer = new Timer(DAEMON_THREAD);// allready open new thared 
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                getRummikubeWsEvents();
            }
        }, TIMER_DELAY);
    }

    //Private methods
    private void setHandEvents() {
        this.handTile.setOnDragOver(this::onDragOverOfHandTilePane);
        this.handTile.setOnDragDropped(this::onDragDroppedOfHandTilePane);
        this.handTile.setOnDragDone(this::onDragDoneOfHandTilePane);
    }

    private void onDragDoneOfHandTilePane(DragEvent event) {
        if (event.getTransferMode() == TransferMode.MOVE) {
        }
        event.consume();
    }

    private void onDragDroppedOfHandTilePane(DragEvent event) {
        Dragboard db = event.getDragboard();
        AnimatedTilePane currTile = (AnimatedTilePane) db.getContent(DataFormat.RTF);
        boolean success = event.getTransferMode() == TransferMode.MOVE && currTile.isTileParentIsSerie();

        if (success) {
            int ySource = currTile.getIndexOfTileInSerie(currTile);
            int xSource = currTile.getSerieIndexFromTile(currTile);
            Point pSource = new Point(xSource, ySource);
            SingleMove singleMove = new SingleMove(pSource, SingleMove.MoveType.BOARD_TO_HAND);
            currTile.setSingleMove(singleMove);
            success = currTile.getIsLegalMove();
        }

        event.setDropCompleted(success);
        event.consume();
    }

    private void onDragOverOfHandTilePane(DragEvent event) {
        AnimatedTilePane currTile = (AnimatedTilePane) event.getDragboard().getContent(DataFormat.RTF);

        if (currTile.getClass() == AnimatedTilePane.class/* && currTile.getIsTileMovedFromHandToBoard()*/) {
            event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
        }

        event.consume();
    }

    private void initPlayerAndButtonComponents() {

        //init the list Of players bar
        this.playersBarList.add(this.barPlayer1);
        this.playersBarList.add(this.barPlayer2);
        this.playersBarList.add(this.barPlayer3);
        this.playersBarList.add(this.barPlayer4);
        this.playersLabelsList.add(this.player1);
        this.playersLabelsList.add(this.player2);
        this.playersLabelsList.add(this.player3);
        this.playersLabelsList.add(this.player4);
        this.labelOfNumOfTileInHandList.add(this.numTileP1);
        this.labelOfNumOfTileInHandList.add(this.numTileP2);
        this.labelOfNumOfTileInHandList.add(this.numTileP3);
        this.labelOfNumOfTileInHandList.add(this.numTileP4);
        this.buttonsList.add(this.endTrun);
        this.buttonsList.add(this.menu);

    }

    private void initScreenComponentetWithoutBoardWs(rummikub.client.ws.Event event) throws GameDoesNotExists_Exception, InvalidParameters_Exception {
        playersDetails = rummikubWebService.getPlayersDetails(gameName);

        logicBoard = new Board();
        Platform.runLater(() -> {
            setPlayersBarWs();
            initPlayerLabelWs();

        });
//        initAboveHeapLabel();
//        initHeapButton();
    }

    private void setLabelWs(PlayerDetails player, int index) {
        Label currentPlayer = this.playersLabelsList.get(index);
        playersBarList.get(index).setVisible(true);
        currentPlayer.setVisible(true);
        currentPlayer.setText(" " + player.getName() + "  ");
        currentPlayer.setAlignment(Pos.CENTER);
        currentPlayer.setTextAlignment(TextAlignment.JUSTIFY);
        if (isHumanPlayer(player)) {
            currentPlayer.setGraphic(ImageUtils.getImageView(ImageUtils.HUMAN_PLAYER_LOGO));
        } else {
            currentPlayer.setGraphic(ImageUtils.getImageView(ImageUtils.COMPUTER_PLAYER_LOGO));
        }
    }

    private void setLabel(Player player, int index) {
        Label currentPlayer = this.playersLabelsList.get(index);

        playersBarList.get(index).setVisible(true);
        currentPlayer.setVisible(true);
        currentPlayer.setText(" " + player.getName() + "  ");
        currentPlayer.setAlignment(Pos.CENTER);
        currentPlayer.setTextAlignment(TextAlignment.JUSTIFY);

        if (player.getIsHuman()) {
            currentPlayer.setGraphic(ImageUtils.getImageView(ImageUtils.HUMAN_PLAYER_LOGO));
        } else {
            currentPlayer.setGraphic(ImageUtils.getImageView(ImageUtils.COMPUTER_PLAYER_LOGO));
        }
    }

    private void showPlayerHandWs() {
        try {
            myDetails = this.rummikubWebService.getPlayerDetails(playerID);
            createPlayerHandWs(myDetails.getTiles());
        } catch (GameDoesNotExists_Exception | InvalidParameters_Exception ex) {
            showGameMsg(errorMsg, ex.getMessage());
        } //catch(Exception ex) {
        //onServerLostException();
        //}

    }

    private void onMakeSingleMove(SingleMove singleMove) {
        Point source = singleMove.getpSource();
        Point target = singleMove.getpTarget();

        Thread thread = new Thread(() -> {
            boolean isLegal = true;
            try {

                switch (singleMove.getMoveType()) {
                    case BOARD_TO_BOARD:
                        this.rummikubWebService.moveTile(playerID, source.x, source.y, target.x, target.y);
                        break;
                    case BOARD_TO_HAND:
                        this.rummikubWebService.takeBackTile(playerID, source.x, source.y);
                        break;
                    case HAND_TO_BOARD:
                    default:
                        rummikub.client.ws.Tile tile = myDetails.getTiles().get(singleMove.getnSource());
                        if (isNewSequance(target.x)) {
                            ArrayList<rummikub.client.ws.Tile> tileList = new ArrayList<>();
                            tileList.add(tile);
                            this.rummikubWebService.createSequence(playerID, tileList);
                        } else {
                            this.rummikubWebService.addTile(playerID, tile, target.x, target.y);
                        }
                        break;

                }
            } catch (InvalidParameters_Exception ex) {
                Platform.runLater(() -> (showGameMsg(this.errorMsg, ex.getMessage())));
                isLegal = false;
            } //catch(Exception ex) {
            // onServerLostException();
            //}
            this.isLegalMove.set(isLegal);
        });
        thread.setDaemon(DAEMON_THREAD);
        thread.start();

//        if (!this.isUserMadeFirstMoveInGame) {
//            this.isUserMadeFirstMoveInGame = !CAN_SAVE_THE_GAME;
//        }
//        try {
//            isLegal = dealWithSingleMoveResualt(singleMove);
//        } catch (Exception ex) {
//            isLegal = false;
//            initCurrentPlayerMove();
//            showGameBoardAndPlayerHand();
//        }
    }

    private void onSuccesfulyCompletedMove(boolean newVal) {
        if (newVal) {
            initPlayerLabelWs();
            //updateCurrPlayerBarWs(nameOfCurrPlayerTurn);
            try {
                this.myDetails = rummikubWebService.getPlayerDetails(playerID);
            } catch (GameDoesNotExists_Exception | InvalidParameters_Exception ex) {
                showGameMsg(errorMsg, ex.getMessage());
            } //catch(Exception ex) {
            //  onServerLostException();
            //}
            this.isLegalMove.set(!LEGAL_MOVE);
            //showCurrentGameBoardAndCurrentPlayerHand();
        }
    }

    private void setBoard(ArrayList<Serie> serieList) {
        centerPane.resetScreen();
        ArrayList<FlowPane> flowPaneSeriesList = new ArrayList<>();

        for (Serie serie : serieList) {
            flowPaneSeriesList.add(createFlowPaneSerie(serie));
        }
        centerPane.getChildren().addAll(flowPaneSeriesList);
    }

    private FlowPane createFlowPaneSerie(Serie serie) {
        FlowPane serieFlowPane = new AnimatedSeriePane();

        serieFlowPane.getChildren().addListener((ListChangeListener.Change<? extends Node> c) -> {
            onChangeOfSerieContent(serieFlowPane);
        });

        for (Tile tile : serie.getSerieOfTiles()) {
            AnimatedTilePane viewTile = new AnimatedTilePane(tile);
            initTileListeners(viewTile);
            serieFlowPane.getChildren().add(viewTile);
        }

        serieFlowPane.setMinWidth(serieFlowPane.getChildren().size() * 30);

        return serieFlowPane;
    }

    private void initPlayerLabelWs() {
        Thread thread = new Thread(() -> {
            try {
                playersDetails = this.rummikubWebService.getPlayersDetails(gameName);
                Platform.runLater(() -> {
                    int index = 0, currPlayerIndex = 0;
                    if (!this.nameOfCurrPlayerTurn.isEmpty()) {
                        currPlayerIndex = findPlayerByName(nameOfCurrPlayerTurn);
                    }

                    for (HBox barPlayer : playersBarList) {
                        String style = index == currPlayerIndex ? STYLE_GREEN : STYLE_WHITE;
                        for (Node child : barPlayer.getChildren()) {
                            child.setStyle(style);
                        }
                        index++;
                    }
                    index=0;
                    for (PlayerDetails playerDetails : playersDetails) {
                        this.labelOfNumOfTileInHandList.get(index).setText(String.valueOf(playerDetails.getNumberOfTiles()));
                        index++;
                    }
                });
            } catch (GameDoesNotExists_Exception ex) {
                Platform.runLater(() -> (showGameMsg(errorMsg, ex.getMessage())));
            }  //catch(Exception ex) {
            //                onServerLostException();
            //            }
        });
        thread.setDaemon(DAEMON_THREAD);
        thread.start();
        //need to move to turn message updateCurrPlayerBar();
    }

//    private void initAboveHeapLabel() {
//        this.heapTile.setStyle(styleWhite);
//        this.heapTile.setText("Tile Left:" + rummikubLogic.getHeap().getTileList().size());
//    }
//
//    private void initHeapButton() {
//        if (rummikubLogic.getHeap().isEmptyHeap()) {
//            this.withdrawCard.setFont(new Font(14));
//            this.withdrawCard.setText("Empy Deck");
//            this.withdrawCard.setDisable(true);
//        }
//    }
    public static void disappearAnimation(Node node) {
        FadeTransition animation = new FadeTransition();
        animation.setNode(node);
        animation.setDuration(Duration.seconds(3));
        animation.setFromValue(1.0);
        animation.setToValue(0.0);
        animation.play();
    }

    public static void showGameMsg(Label label, String msg) {
        label.setText(msg);
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(0.5), (ActionEvent event) -> {
            disappearAnimation(label);
        }));
        timeline.setCycleCount(1);
        timeline.play();
    }

//    private boolean dealWithSingleMoveResualt(SingleMove singleMove) {
//        SingleMove.SingleMoveResult singleMoveResualt;
//        singleMoveResualt = this.currentPlayerMove.implementSingleMove(singleMove);
//        boolean isLegalMoveDone = false;
//        switch (singleMoveResualt) {
//            case TILE_NOT_BELONG_HAND: {
//                showGameMsg(this.errorMsg, Utils.Constants.ErrorMessages.ILEGAL_TILE_IS_NOT_BELONG_TO_HAND);
//                break;
//            }
//            case NOT_IN_THE_RIGHT_ORDER: {
//                showGameMsg(this.errorMsg, Utils.Constants.ErrorMessages.ILEGAL_TILE_INSERTED_NOT_IN_RIGHT_ORDER);
//                break;
//            }
//            case CAN_NOT_TOUCH_BOARD_IN_FIRST_MOVE: {
//                showGameMsg(this.errorMsg, Utils.Constants.ErrorMessages.ILEGAL_CANT_TUCH_BOARD_IN_FIRST_MOVE);
//                break;
//            }
//            case LEGAL_MOVE:
//            default:
//                showGameMsg(this.errorMsg, Utils.Constants.QuestionsAndMessagesToUser.SUCCSESSFUL_MOVE);
//                isLegalMoveDone = true;
//                break;
//        }
//        return isLegalMoveDone;
//    }
    // Deals with the computer player and allows him to makes his inputs
//    private SingleMove dealWithComputerPlayer() {
//        SingleMove singleMove;
//        Serie serie;
//
//        if (newMoveGenerator.isFinishedGeneratingLastSerie()) {
//            serie = this.serieGenerator.generateSerieMove(currentPlayerMove.getHandAfterMove(), currentPlayerMove.getIsFirstMoveDone());
//            this.newMoveGenerator.setSerieToPlaceOnBoard(serie);
//
//            if (serie != null) {
//                this.newMoveGenerator.setBoardSizeBeforeMove(currentPlayerMove.getBoardAfterMove().boardSize());
//            }
//        }
//
//        singleMove = newMoveGenerator.generateSingleMove(currentPlayerMove.getHandAfterMove(), currentPlayerMove.getBoardAfterMove());
//
//        if (this.newMoveGenerator.isTurnSkipped()) {
//            currentPlayerMove.setIsTurnSkipped(this.newMoveGenerator.isTurnSkipped());
//        }
//
//        return singleMove;
//    }
//    private void ImplementComputerPlayerTurn(SingleMove singleMove) {
//        if (singleMove != null) {
//            try {
//                dealWithSingleMoveResualt(singleMove);
//            } catch (Exception ex) {
//                currentPlayerMove.setIsTurnSkipped(PlayersMove.USER_WANT_SKIP_TRUN);
//            }
//        }
//    }
//    private void updateCurrPlayerBarWs(String currPlayerName) {
//        int index = findPlayerByName(currPlayerName);
//        for (Node child : this.playersBarList.get(index).getChildren()) {
//            child.setStyle(STYLE_GREEN);
//
//        }
//        if (playersDetails.get(index).isPlayedFirstSequence()) {
//            this.firstMoveMsg.setStyle(STYLE_WHITE);
//            this.firstMoveMsg.setVisible(true);
//        } else {
//            this.firstMoveMsg.setVisible(!VISABLE);
//        }
//    }

    private void setFirstTurnMsg() {
        this.firstMoveMsg.setVisible(true);
        this.firstMoveMsg.setVisible(myDetails.isPlayedFirstSequence());

    }

    private void onGameFinished() {
        this.myController.setScreen(Rummikub.RESULT_SCREEN_ID, ScreensController.NOT_RESETABLE);
    }

    private void initTileListeners(AnimatedTilePane viewTile) {
        if (isMyTurn()) {
            viewTile.addSingleMoveListener((ObservableValue<? extends SingleMove> observable, SingleMove oldValue, SingleMove newValue) -> {
                onMakeSingleMove(newValue);
            });

            this.isLegalMove.addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
                viewTile.onSingleMoveDone(newValue);
            });

            viewTile.addIsMoveSuccesfulyCompletedListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
                onSuccesfulyCompletedMove(newValue);
            });
        } else {
            viewTile.dragTilesOption(DISABLE_DRAG_AND_DROP);
        }
    }

    private void onChangeOfSerieContent(FlowPane serieFlowPane) {
        if (serieFlowPane.getChildren().isEmpty()) {
            centerPane.removeEmptySerie(serieFlowPane);
        } else {
            ((AnimatedSeriePane) serieFlowPane).setSize();
        }
    }

//    private void showGameBoardAndPlayerHand() {
//        initScreenComponentetWithoutBoard();
//        showGameBoard();
//    }
//    public void showCurrentGameBoardAndCurrentPlayerHand() {
//        showCurrentPlayerBoard();
//        showCurrentPlayerHand();
//    }
//    private void onEndTurnAcions(ActionEvent event) {
////        if (swapTurnTimeLineDelay.getStatus() == Animation.Status.STOPPED) {
//        try {
//            //check the player move
//            rummikubWebService.finishTurn(playerID);
//            disableButtons();
//        } catch (InvalidParameters_Exception ex) {
//            showGameMsg(errorMsg, ex.getMessage());
//        }// catch(Exception ex) {
////                onServerLostException();
////        }
        // Swap players
//            if (rummikubLogic.isGameOver()) {
//                onGameFinished();
//            } else {
        //      swapTurnTimeLineDelay.play();
        //}
        //}
//    }
//    private void onWithdrawCardAndSkipTurnAction(ActionEvent event) {
//        if (swapTurnTimeLineDelay.getStatus() == Animation.Status.STOPPED) {
//
//            this.currentPlayerMove.setIsTurnSkipped(PlayersMove.USER_WANT_SKIP_TRUN);
//            this.rummikubLogic.playSingleTurn(currentPlayerMove);
//
//            if (rummikubLogic.isGameOver() || rummikubLogic.isOnlyOnePlayerLeft()) {
//                onGameFinished();
//            } else {
//                swapTurnTimeLineDelay.play();
//            }
//        }
//    }

    private void handleRummikubWsEvent(rummikub.client.ws.Event event) {
        // <editor-fold defaultstate="collapsed" desc="Game Events Description">
        /*
        • Game Start – game started
        • Game Over – game ended
        • Game Winner – the winner of the game (play name will be in the event)
        • Player Turn – indicates who’s the current player
        • Player Finished Turn – player finished making his moves
        • Player Resigned – player resigned from game
        • Sequence Created – indicates a sequence was created
        • Tile Added – indicates a tile was added from a player to the board
        • Tile Moved – indicates a tile was moved on the board
        • Tile Returned – indicates a tile was taken from the board back to a player
        • Revert – indicates the players’ moves did not sum up to a valid board, thus the board is reverted back to the state before the players’ moves.
         */
        // </editor-fold>

        switch (event.getType()) {
            case GAME_OVER: {
                handleGameOverEven(event);
                break;
            }
            case GAME_START: {
                handleGameStartEvent(event);
                break;
            }
            case GAME_WINNER: {
                handleGameWinnerEvent(event);
                break;
            }
            case PLAYER_FINISHED_TURN: {
                handlePlayerFinishedTurnEvent(event);
                break;
            }
            case PLAYER_RESIGNED: {
                handlePlayerResignedEvent(event);
                break;
            }
            case PLAYER_TURN: {
                handlePlayerTurnEvent(event);
                break;
            }
            case REVERT: {
                handleRevertEvent(event);
                break;
            }
            case SEQUENCE_CREATED: {
                handleSequenceCreatedEvent(event);
                break;
            }
            case TILE_ADDED: {
                handleTileAddedEvent(event);
                break;
            }
            case TILE_MOVED: {
                handleTileMovedEvent(event);
                break;
            }
            case TILE_RETURNED:
                handleTileReturnedEvent(event);
                break;
            default: {
                break;
            }
        }
    }

    //Public methods
    public void resetPlayersBar() {
        for (HBox playerBar : this.playersBarList) {
            playerBar.setVisible(!VISABLE);
        }
    }

//    public void createNewGame(Settings gameSetting) {
//        this.rummikubLogic.setGameSettings(gameSetting);
//        //A: i changed it to new....
//        //this.rummikubLogic.setGameOriginalInputedSettings(gameSetting);
//        this.rummikubLogic.setGameOriginalInputedSettings(new Settings(gameSetting));
//        this.rummikubLogic.initGameFromUserSettings();
//        initCurrentPlayerMove();
//    }
//    public void initAllGameComponents() {
//        initScreenComponentetWithoutBoard();
//        try {
//            new Thread(() -> {
//                defineIfTheTurnOfHumanOrComputer();
//            }).start();
//        } catch (Exception ex) {
//            this.myController.setScreen(Rummikub.MAINMENU_SCREEN_ID, ScreensController.NOT_RESETABLE);
//        }
//    }
    public void showGameBoard() {
        setBoard(logicBoard.getListOfSerie());
    }
//
//    public void showCurrentPlayerBoard() {
//        setBoard();
//    }

    public boolean getIsUserMadeFirstMoveInGame() {
        return isUserMadeFirstMoveInGame;
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        initPlayerAndButtonComponents();
        this.centerPane = new AnimatedGameBoardPane();
        this.serieGenerator = new SeriesGenerator();
        this.newMoveGenerator = new ComputerSingleMoveGenerator();
        this.board.setCenter(centerPane);
//        this.swapTurnTimeLineDelay = new Timeline(new KeyFrame(Duration.millis(800), (ActionEvent event1) -> {
//            swapTurns();
//        }));
        this.isLegalMove = new SimpleBooleanProperty(false);
        this.isUserMadeFirstMoveInGame = CAN_SAVE_THE_GAME;
        setHandEvents();
    }

    @Override
    public void resetScreen() {
        //((AnimatedGameBoardPane) this.board.getCenter()).resetScreen();
        resetPlayersBar();
        handTile.getChildren().clear();
        this.errorMsg.setText(Utils.Constants.EMPTY_STRING);
        //show();
        getRummikubeWsEvents();
    }

    @Override
    public void setScreenParent(ScreensController parentScreen) {
        this.myController = parentScreen;
    }

    @Override
    public void setService(RummikubWebServiceService service) {
        this.service = service;
        this.rummikubWebService = service.getRummikubWebServicePort();
    }

    @Override
    public void setPlayerId(int playerId) {
        this.playerID = playerId;
    }

    @Override
    public int getPlayerId() {
        return this.playerID;
    }

    @Override
    public RummikubWebServiceService getService() {
        return this.service;
    }

    public void disableButtons() {
        initButtons(true);
    }

    public void enableButtons() {
        initButtons(false);
    }

    private void initButtons(boolean disableButtons) {
        buttonsList.stream().forEach((controllButton) -> {
            controllButton.setDisable(disableButtons);
        });
        this.menu.setDisable(false);
    }

    private void handleGameStartEvent(rummikub.client.ws.Event eventGameStart) {
        try {
            initScreenComponentetWithoutBoardWs(eventGameStart);
        } catch (GameDoesNotExists_Exception ex) {
            ///go back to serverSelectSceen
        } catch (InvalidParameters_Exception ex) {
            ///go back to serverSelectSceen
        }

    }

    private void setPlayersBarWs() {
        int i = 0;

        this.playersBarList.stream().forEach((playersLabelBar) -> {
            playersLabelBar.setVisible(!VISABLE);
        });

        for (PlayerDetails playerDetails : playersDetails) {
            setLabelWs(playerDetails, playersDetails.indexOf(playerDetails));
        }
    }

    public void initWsSetting(RummikubWebServiceService service, String gameName, int playerID, PlayerDetails myDetails) {
        setService(service);
        this.timer = new Timer(DAEMON_THREAD);
        this.gameName = gameName;
        this.playerID = playerID;
        this.myDetails = myDetails;
        disableButtons();
        this.currEventId = 0;
        //getRummikubeWsEvents();
    }

    private void handleGameWinnerEvent(Event event) {
        ResultScreenController resultScreenController = (ResultScreenController) this.myController.getControllerScreen(Rummikub.RESULT_SCREEN_ID);
        resultScreenController.updatedGameResultMsg(event.getPlayerName());
    }

    private void handlePlayerFinishedTurnEvent(Event event) {
        Platform.runLater(() -> {

            showPlayerHandWs();
            showGameMsg(errorMsg, event.getPlayerName() + PLAYER_DONE);
        });

    }

    private void handlePlayerResignedEvent(Event event) {
        String playerResignedName = event.getPlayerName();
        Platform.runLater(() -> {
            showGameMsg(errorMsg, playerResignedName + PLAYER_RESIGNED);
            initPlayerLabelWs();
        });
        if (myDetails.getName().equalsIgnoreCase(playerResignedName)) {
            this.myController.setScreen(Rummikub.SERVER_SELECT_SCREEN_ID, this);
        }

    }

    private void handlePlayerTurnEvent(Event event) {
        this.nameOfCurrPlayerTurn = event.getPlayerName();
        String turnMsg = getTurnMsg();

        Platform.runLater(() -> {
            setFirstTurnMsg();
            initPlayerLabelWs();
            //updateCurrPlayerBarWs(nameOfCurrPlayerTurn);
            this.turnMsgLabel.setText(turnMsg);
            this.showPlayerHandWs();
        });
        if (isMyTurn()) {
            Platform.runLater(() -> {
                enableButtons();
                this.turnMsgLabel.setStyle(STYLE_GREEN);
            });
        } else {
            Platform.runLater(() -> {
                disableButtons();
                this.turnMsgLabel.setStyle(STYLE_WHITE);
            });
        }

    }

    private void handleRevertEvent(Event event) {
        this.logicBoard = new Board();

    }

    private void handleTileAddedEvent(Event event) {
        //int sourcePosition = event.getSourceSequencePosition();
        rummikub.client.ws.Tile tileToAdd = event.getTiles().get(0);
        int targetSerie = event.getTargetSequenceIndex();
        int targetPosition = event.getTargetSequencePosition();
        Serie toSerie = this.logicBoard.getSeries(targetSerie);
        toSerie.addSpecificTileToSerie(convertWsTileToLogicTile(tileToAdd), targetPosition); //maybe need to check if to add to end 
        Platform.runLater(() -> {
            showGameBoard();
            showPlayerHandWs();
        });

    }

    private void handleSequenceCreatedEvent(Event event) {
        List<rummikub.client.ws.Tile> serieToAdd = event.getTiles();
        Serie serie = getLogicFromWsTileList(serieToAdd);
        logicBoard.addSeries(serie);
        Platform.runLater(() -> {
            showGameBoard();
            showPlayerHandWs(); //may be no need this 
        });

        //Platform.runLater(() -> {centerPane.getChildren().add(createFlowPaneSerie(serie));});
    }

    private void handleTileMovedEvent(Event event) {
        int sourcePosition = event.getSourceSequencePosition();
        int sourceSerie = event.getSourceSequenceIndex();
        int targetSerie = event.getTargetSequenceIndex();
        int targetPosition = event.getTargetSequencePosition();
        if (this.logicBoard.getListOfSerie().size() == targetSerie) {
            this.logicBoard.addSeries(new Serie());
        }
        Serie fromSerie = this.logicBoard.getSeries(sourceSerie);
        Serie toSerie = this.logicBoard.getSeries(targetSerie);
        Tile tile = fromSerie.getSpecificTile(sourcePosition);
        fromSerie.removeSpecificTile(sourcePosition);

        if (fromSerie.isEmptySeries()) {
            this.logicBoard.removeSeries(fromSerie);
        }

        toSerie.addSpecificTileToSerie(tile, targetPosition); //maybe need to check if to add to end 
        Platform.runLater(() -> (showGameBoard()));

    }

    private void handleTileReturnedEvent(Event event) {
        int sourceIndex = event.getSourceSequenceIndex();
        int sourcePosition = event.getSourceSequencePosition();
        Serie serie = this.logicBoard.getSeries(sourceIndex);
        serie.removeSpecificTile(sourcePosition);

        if (serie.isEmptySeries()) {
            this.logicBoard.removeSeries(serie);
        }

        Platform.runLater(() -> {
            showGameBoard();
            showPlayerHandWs();
        });
    }

    private void handleGameOverEven(Event event) {
        Platform.runLater(() -> {
            disableButtons();
            showGameMsg(errorMsg, GAME_OVER);
        });

    }

    private void createPlayerHandWs(List<rummikub.client.ws.Tile> handWsTiles) {
        this.handTile.getChildren().clear();
        for (rummikub.client.ws.Tile currWsTile : handWsTiles) {
            AnimatedTilePane viewTile = new AnimatedTilePane(convertWsTileToLogicTile(currWsTile));
            initTileListeners(viewTile);
            this.handTile.getChildren().add(viewTile);
        }

    }
    //Tile convertWsTile()

    private Tile.Color convertToLogicColor(Color colorWs) {
        Tile.Color newColor;

        switch (colorWs) {
            case BLACK:
                newColor = Tile.Color.BLACK;
                break;

            case BLUE:
                newColor = Tile.Color.BLUE;
                break;

            case RED:
                newColor = Tile.Color.RED;
                break;

            case YELLOW:
            default:
                newColor = Tile.Color.YELLOW;
                break;
        }

        return newColor;
    }

    private Tile.TileNumber convertToLogicTileNum(int value) {
        return Tile.TileNumber.getTileNumberByValue(value);
    }

    private Tile convertWsTileToLogicTile(rummikub.client.ws.Tile tile) {
        Tile.Color tileColor = convertToLogicColor(tile.getColor());
        Tile.TileNumber tileNum = convertToLogicTileNum(tile.getValue());
        return new Tile(tileColor, tileNum);
    }

    private int findPlayerByName(String currPlayerName) {
        int index = 0;
        boolean found = false;
        for (Iterator<PlayerDetails> it = playersDetails.iterator(); !found && it.hasNext();) {
            PlayerDetails playersDetail = it.next();
            found = playersDetail.getName().equals(currPlayerName);
            if (!found) {
                index++;
            }
        }
        return index;
    }

    private boolean isNewSequance(int sequanceIndex) {
        return (!(sequanceIndex < logicBoard.boardSize()));
    }

    private boolean isHumanPlayer(PlayerDetails player) {
        return player.getType().equals(PlayerType.HUMAN);
    }

//    public void initCurrentPlayerMove() {
//        init variables in the statrt of the turn
//        Board printableBoard = new Board(new ArrayList<>(rummikubLogic.getGameBoard().getListOfSerie()));
//        boolean isFirstMoveDone = rummikubLogic.getCurrentPlayer().isFirstMoveDone();
//        Player printablePlayer = rummikubLogic.getCurrentPlayer().clonePlayer();
//        this.currentPlayerMove = new PlayersMove(printablePlayer.getListPlayerTiles(), printableBoard, isFirstMoveDone);
//        this.isUserMadeFirstMoveInGame = CAN_SAVE_THE_GAME;
//    }
//    public GameLogic getRummikubLogic() {
//        return rummikubLogic;
//    }
//    public void setRummikubLogic(GameLogic rummikubLogic) {
//        this.rummikubLogic = rummikubLogic;
//    }
//    public void swapTurns() {
//        rummikubLogic.swapTurns();
//        initCurrentPlayerMove();
//        initCurrPlayerLabel();
////        initAboveHeapLabel();
//        try {
//            new Thread(() -> {
//                defineIfTheTurnOfHumanOrComputer();
//            }).start();
//        } catch (Exception ex) {
//            this.myController.setScreen(Rummikub.MAINMENU_SCREEN_ID, ScreensController.NOT_RESETABLE);
//        }
//    }
    //    private synchronized void defineIfTheTurnOfHumanOrComputer() {
//        boolean isComputerPlayer = !rummikubLogic.getCurrentPlayer().getIsHuman();
//        boolean disableButtons = isComputerPlayer;
//
//        Platform.runLater(() -> {
//            //buttonsList.stream().forEach((controllButton) -> { controllButton.setDisable(disableButtons); });
//            initButtons(disableButtons);
//        });
//
//        if (isComputerPlayer) {
//
//            while (isComputerPlayer) {
//                SingleMove singleMove = dealWithComputerPlayer();
//
//                Platform.runLater(() -> {
//                    ImplementComputerPlayerTurn(singleMove);
//                });
//
//                try {
//                    Thread.sleep(SLEEP_TIME_IN_MILLISECOUNDS);
//                    Platform.runLater(() -> {
//                        showCurrentGameBoardAndCurrentPlayerHand();
//                    });
//                } catch (InterruptedException ex) {
//                }
//
//                if (currentPlayerMove.getIsTurnSkipped() || this.newMoveGenerator.isTurnFinnised()) {
//                    this.newMoveGenerator.initComputerSingleMoveGenerator();
//                    Platform.runLater(() -> {
//                        onEndTurnAcions(null);
//                    });
//                    Thread.currentThread().stop();
//                }
//
//                isComputerPlayer = !rummikubLogic.getCurrentPlayer().getIsHuman();
//            }
//        } else {
//            Platform.runLater(() -> {
//                //showGameBoardAndPlayerHand();
//                showCurrentGameBoardAndCurrentPlayerHand();
//            });
//        }
//    }
    private int getLastEventID(List<Event> eventList) {
        return eventList.get(eventList.size() - 1).getId();
    }

    private String getTurnMsg() {
        String msg = myDetails.getName();
        if (isMyTurn()) {
            msg += PLAY;
        } else {
            msg += WAIT;
        }
        return msg;
    }

    private boolean isMyTurn() {
        return this.nameOfCurrPlayerTurn.equalsIgnoreCase(this.myDetails.getName());
    }

    public String getGameName() {
        return gameName;
    }

    public void setGameName(String gameName) {
        this.gameName = gameName;
    }

    private Serie getLogicFromWsTileList(List<rummikub.client.ws.Tile> serieToAdd) {
        Serie serie = new Serie();
        for (rummikub.client.ws.Tile tile : serieToAdd) {
            serie.addSpecificTileToSerie(convertWsTileToLogicTile(tile));
        }
        return serie;
    }

    public void setMyDetails(PlayerDetails myDetails) {
        this.myDetails = myDetails;
    }

//    private void onServerLostException() {
//        Platform.runLater(() -> {
//            showGameMsg(errorMsg, "Lost connection to server");
//        });
//    }
}
