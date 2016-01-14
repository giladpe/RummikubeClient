/*
 * this class responsible to control the game screen
 */
package rummikub.view;

import java.awt.Point;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.animation.Animation;
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
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;
import rummikub.gameLogic.model.gameobjects.Tile;
import rummikub.gameLogic.model.logic.GameLogic;
import rummikub.gameLogic.model.logic.Settings;
import rummikub.gameLogic.model.player.Player;
import rummikubFX.Rummikub;
import rummikub.gameLogic.controller.rummikub.SingleMove;
import rummikub.gameLogic.model.gameobjects.Board;
import rummikub.gameLogic.model.gameobjects.Serie;
import rummikub.gameLogic.model.logic.PlayersMove;
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
public class PlayScreenController implements Initializable, ResetableScreen, ControlledScreen {

    //Constatns
    private static final String styleWhite = "-fx-text-fill: white";
    private static final String styleBlue = "-fx-text-fill: green";
    private static final boolean CAN_SAVE_THE_GAME = false;
    private static final int MAX_NUM_OF_PLAYERS = 4;
    private static final boolean VISABLE = true;
    private static final boolean LEGAL_MOVE = true;
    private static final boolean ENABLE_BUTTON = true;
    private static final boolean DISABLE_DRAG_AND_DROP = true;
    private static final long SLEEP_TIME_IN_MILLISECOUNDS = 1000;

    //FXML Private filds
    @FXML private Label errorMsg;
    @FXML private BorderPane board;
    @FXML private Button menu;
    @FXML private FlowPane handTile;
    @FXML private Button endTrun;
    @FXML private Button withdrawCard;
    @FXML private Label heapTile;
    @FXML private Label firstMoveMsg;
    @FXML private Label player1;
    @FXML private Label player2;
    @FXML private Label player3;
    @FXML private Label player4;
    @FXML private HBox barPlayer1;
    @FXML private Label numTileP1;
    @FXML private HBox barPlayer3;
    @FXML private Label numTileP3;
    @FXML private HBox barPlayer2;
    @FXML private Label numTileP2;
    @FXML private HBox barPlayer4;
    @FXML private Label numTileP4;
    
    //Private members
    private final ArrayList<Label> playersLabelsList = new ArrayList<>();
    private ScreensController myController;
    private GameLogic rummikubLogic = new GameLogic();
    private SeriesGenerator serieGenerator;
    private ComputerSingleMoveGenerator newMoveGenerator;
    private SimpleBooleanProperty isLegalMove ; 
    private Timeline swapTurnTimeLineDelay;
    private AnimatedGameBoardPane centerPane;
    private PlayersMove currentPlayerMove;
    private final ArrayList<HBox> playersBarList = new ArrayList<>(MAX_NUM_OF_PLAYERS);
    private final ArrayList<Label> labelOfNumOfTileInHandList = new ArrayList<>(MAX_NUM_OF_PLAYERS);
    private final ArrayList<Button> buttonsList = new ArrayList<>();
    private boolean isUserMadeFirstMoveInGame;

    //Private FXML methods
    @FXML
    private void handleMenuButtonAction(ActionEvent event) {
        this.myController.setScreen(Rummikub.SUBMENU_SCREEN_ID, ScreensController.NOT_RESETABLE);
    }

    @FXML
    private void handleEndTrunAction(ActionEvent event) {
        onEndTurnAcions(event);
    }

    @FXML
    private void handleWithdrawCardAction(ActionEvent event) {
        onWithdrawCardAndSkipTurnAction(event);
    }

    //Private methods
    private void setHandEvents() {
        this.handTile.setOnDragOver(this::onDragOverOfHandTilePane);
        this.handTile.setOnDragDropped(this::onDragDroppedOfHandTilePane);
        this.handTile.setOnDragDone(this::onDragDoneOfHandTilePane);
    }
    
    private void onDragDoneOfHandTilePane(DragEvent event) {
        if (event.getTransferMode() == TransferMode.MOVE) { }
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
        this.buttonsList.add(this.withdrawCard);

    }

    private void initScreenComponentetWithoutBoard() {
        setPlayersBar();
        showPlayerHand();
        initCurrPlayerLabel();
        initAboveHeapLabel();
        initHeapButton();
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
        }
        else {
            currentPlayer.setGraphic(ImageUtils.getImageView(ImageUtils.COMPUTER_PLAYER_LOGO));
        }
    }

    private void showPlayerHand() {
        createPlayerHand(this.rummikubLogic.getCurrentPlayer().getListPlayerTiles());
    }

    private void showCurrentPlayerHand() {
        createPlayerHand(this.currentPlayerMove.getHandAfterMove());
    }

    private void createPlayerHand(ArrayList<Tile> handTiles) {
        this.handTile.getChildren().clear();

        for (Tile currTile : handTiles) {
            AnimatedTilePane viewTile = new AnimatedTilePane(currTile);
            initTileListeners(viewTile);
            this.handTile.getChildren().add(viewTile);
        }
    }

    private void onMakeSingleMove(SingleMove singleMove) {
        boolean isLegal;
        
        if (!this.isUserMadeFirstMoveInGame) {
            this.isUserMadeFirstMoveInGame = !CAN_SAVE_THE_GAME;
        }
        try {
            isLegal = dealWithSingleMoveResualt(singleMove);
        }
        catch (Exception ex) {
            isLegal = false;
            initCurrentPlayerMove();
            showGameBoardAndPlayerHand();
        }
        this.isLegalMove.set(isLegal);
    }
    
    private void onSuccesfulyCompletedMove(boolean newVal) {
        if (newVal) {
//            Platform.runLater(() -> {
//                initScreenComponentetWithoutBoard();
//                showCurrentGameBoardAndCurrentPlayerHand();
//            });

        initCurrPlayerLabel();
        this.isLegalMove.set(!LEGAL_MOVE);

        showCurrentGameBoardAndCurrentPlayerHand();  
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

    private void initCurrPlayerLabel() {
        int index = 0;

        for (HBox barPlayer : playersBarList) {
            for (Node child : barPlayer.getChildren()) {
                child.setStyle(styleWhite);
            }
        }

        for (Player player : rummikubLogic.getPlayers()) {
            this.labelOfNumOfTileInHandList.get(index).setText(String.valueOf(player.getListPlayerTiles().size()));
            index++;
        }
        
        updateCurrPlayerBar();
    }

    private void initAboveHeapLabel() {
        this.heapTile.setStyle(styleWhite);
        this.heapTile.setText("Tile Left:" + rummikubLogic.getHeap().getTileList().size());
    }

    private void initHeapButton() {
        if (rummikubLogic.getHeap().isEmptyHeap()) {
            this.withdrawCard.setFont(new Font(14));
            this.withdrawCard.setText("Empy Deck");
            this.withdrawCard.setDisable(true);
        }
    }

    private void setPlayersBar() {
        int i = 0;

        this.playersBarList.stream().forEach((playersLabelBar) -> {
            playersLabelBar.setVisible(!VISABLE);

        });

        for (Player player : this.rummikubLogic.getPlayers()) {
            setLabel(player, this.rummikubLogic.getPlayers().indexOf(player));
            this.playersBarList.get(i).setVisible(true);
            i++;
        }
    }

    public static void disappearAnimation(Node node) {
        FadeTransition animation = new FadeTransition();
        animation.setNode(node);
        animation.setDuration(Duration.seconds(3));
        animation.setFromValue(1.0);
        animation.setToValue(0.0);
        animation.play();
    }
public static void showGameMsg(Label label,String msg){
        label.setText(msg);
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(0.5), (ActionEvent event) -> {
            disappearAnimation(label);
        }));  
        timeline.setCycleCount(1);  
        timeline.play();  
    }

    private boolean dealWithSingleMoveResualt(SingleMove singleMove) {
        SingleMove.SingleMoveResult singleMoveResualt;
        singleMoveResualt = this.currentPlayerMove.implementSingleMove(singleMove);
        boolean isLegalMoveDone = false;
        switch (singleMoveResualt) {
            case TILE_NOT_BELONG_HAND: {
                showGameMsg(this.errorMsg,Utils.Constants.ErrorMessages.ILEGAL_TILE_IS_NOT_BELONG_TO_HAND);
                break;
            }
            case NOT_IN_THE_RIGHT_ORDER: {
                showGameMsg(this.errorMsg,Utils.Constants.ErrorMessages.ILEGAL_TILE_INSERTED_NOT_IN_RIGHT_ORDER);
                break;
            }
            case CAN_NOT_TOUCH_BOARD_IN_FIRST_MOVE: {
                showGameMsg(this.errorMsg,Utils.Constants.ErrorMessages.ILEGAL_CANT_TUCH_BOARD_IN_FIRST_MOVE);
                break;
            }
            case LEGAL_MOVE:
            default:
                showGameMsg(this.errorMsg,Utils.Constants.QuestionsAndMessagesToUser.SUCCSESSFUL_MOVE);
                isLegalMoveDone = true;
                break;
        }
        return isLegalMoveDone;
    }

    // Deals with the computer player and allows him to makes his inputs
    private SingleMove dealWithComputerPlayer () {
        SingleMove singleMove;
        Serie serie;

        if (newMoveGenerator.isFinishedGeneratingLastSerie()) {
            serie = this.serieGenerator.generateSerieMove(currentPlayerMove.getHandAfterMove(), currentPlayerMove.getIsFirstMoveDone());
            this.newMoveGenerator.setSerieToPlaceOnBoard(serie);

            if (serie != null) {
                this.newMoveGenerator.setBoardSizeBeforeMove(currentPlayerMove.getBoardAfterMove().boardSize());
            }
        }

        singleMove = newMoveGenerator.generateSingleMove(currentPlayerMove.getHandAfterMove(), currentPlayerMove.getBoardAfterMove());

        if (this.newMoveGenerator.isTurnSkipped()) {
            currentPlayerMove.setIsTurnSkipped(this.newMoveGenerator.isTurnSkipped());
        }

        return singleMove;
    }

    private void ImplementComputerPlayerTurn(SingleMove singleMove) {
        if (singleMove != null) {
            try {
                dealWithSingleMoveResualt(singleMove);
            } catch (Exception ex) {
                currentPlayerMove.setIsTurnSkipped(PlayersMove.USER_WANT_SKIP_TRUN);
            }
        }
    }

    private void updateCurrPlayerBar() {
        int index = rummikubLogic.getPlayers().indexOf(rummikubLogic.getCurrentPlayer());
        for (Node child : this.playersBarList.get(index).getChildren()) {
            child.setStyle(styleBlue);
            this.labelOfNumOfTileInHandList.get(index).setText(String.valueOf(this.currentPlayerMove.getHandAfterMove().size()));

        }
        
        if(rummikubLogic.getCurrentPlayer().isFirstMoveDone()){
            this.firstMoveMsg.setStyle(styleWhite);
            this.firstMoveMsg.setVisible(true);
        }
        else{
            this.firstMoveMsg.setVisible(!VISABLE);
        }
    }

    private void onGameFinished() {
        ResultScreenController resultScreen = (ResultScreenController) this.myController.getControllerScreen(Rummikub.RESULT_SCREEN_ID);
        resultScreen.updatedGameResultMsg();
        this.myController.setScreen(Rummikub.RESULT_SCREEN_ID, ScreensController.NOT_RESETABLE);
    }

    private void initTileListeners(AnimatedTilePane viewTile) {
        if (this.rummikubLogic.getCurrentPlayer().getIsHuman()) {
            viewTile.addSingleMoveListener((ObservableValue<? extends SingleMove> observable, SingleMove oldValue, SingleMove newValue) -> {
                onMakeSingleMove(newValue);
            });

            this.isLegalMove.addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
                viewTile.onSingleMoveDone(newValue);
            });

            viewTile.addIsMoveSuccesfulyCompletedListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
                onSuccesfulyCompletedMove(newValue);
            });            
        } 
        else {
            viewTile.dragTilesOption(DISABLE_DRAG_AND_DROP);
        }
        
    }

    private void onChangeOfSerieContent(FlowPane serieFlowPane) {
        if (serieFlowPane.getChildren().isEmpty()) {
            centerPane.removeEmptySerie(serieFlowPane);
        } 
        else {
            ((AnimatedSeriePane) serieFlowPane).setSize();
        }
    }
    
    private void showGameBoardAndPlayerHand() {
        initScreenComponentetWithoutBoard();
        showGameBoard();
    }
    
    private synchronized void defineIfTheTurnOfHumanOrComputer() {
        boolean isComputerPlayer = !rummikubLogic.getCurrentPlayer().getIsHuman();
        boolean disableButtons = isComputerPlayer;

        Platform.runLater(() -> {
            buttonsList.stream().forEach((controllButton) -> { controllButton.setDisable(disableButtons); });
        });

        if (isComputerPlayer) {

            while (isComputerPlayer) {
                SingleMove singleMove = dealWithComputerPlayer();

                Platform.runLater(() -> {
                    ImplementComputerPlayerTurn(singleMove);
                });

                try {
                    Thread.sleep(SLEEP_TIME_IN_MILLISECOUNDS);
                    Platform.runLater(() -> {
                        showCurrentGameBoardAndCurrentPlayerHand();
                    });
                } catch (InterruptedException ex) { }

                if (currentPlayerMove.getIsTurnSkipped() || this.newMoveGenerator.isTurnFinnised()) {
                    this.newMoveGenerator.initComputerSingleMoveGenerator();
                    Platform.runLater(() -> {
                        onEndTurnAcions(null);
                    });
                    Thread.currentThread().stop();
                }

                isComputerPlayer = !rummikubLogic.getCurrentPlayer().getIsHuman();
            }   
        } 
        else {
            Platform.runLater(() -> {
                //showGameBoardAndPlayerHand();
                showCurrentGameBoardAndCurrentPlayerHand();
            });
        }            
    }
        
    private void onEndTurnAcions(ActionEvent event) {
        if (swapTurnTimeLineDelay.getStatus() == Animation.Status.STOPPED) {
            //check the player move
            rummikubLogic.playSingleTurn(currentPlayerMove);

            // Swap players
            if (rummikubLogic.isGameOver()) {
                onGameFinished();
            } 
            else {
                swapTurnTimeLineDelay.play(); 
            }
        }
    }
        
    private void onWithdrawCardAndSkipTurnAction(ActionEvent event) {
        if (swapTurnTimeLineDelay.getStatus() == Animation.Status.STOPPED) {

            this.currentPlayerMove.setIsTurnSkipped(PlayersMove.USER_WANT_SKIP_TRUN);
            this.rummikubLogic.playSingleTurn(currentPlayerMove);

            if (rummikubLogic.isGameOver() || rummikubLogic.isOnlyOnePlayerLeft()) { 
                onGameFinished();
            } 
            else {
                swapTurnTimeLineDelay.play();
            }
        }
    }
    //Public methods
    public void resetPlayersBar() {
        for (HBox playerBar : this.playersBarList) {
            playerBar.setVisible(!VISABLE);
        }
    }

    public void createNewGame(Settings gameSetting) {
        this.rummikubLogic.setGameSettings(gameSetting);
        //A: i changed it to new....
        //this.rummikubLogic.setGameOriginalInputedSettings(gameSetting);
        this.rummikubLogic.setGameOriginalInputedSettings( new Settings(gameSetting));
        this.rummikubLogic.initGameFromUserSettings();
        initCurrentPlayerMove();
    }

    public void initAllGameComponents() {
        initScreenComponentetWithoutBoard();
        try {
            new Thread(() -> { defineIfTheTurnOfHumanOrComputer(); }).start();
        }
        catch(Exception ex) {
            this.myController.setScreen(Rummikub.MAINMENU_SCREEN_ID, ScreensController.NOT_RESETABLE);
        }
    }
    
    public void showGameBoard() {
        setBoard(this.rummikubLogic.getGameBoard().getListOfSerie());
    }

    public void showCurrentPlayerBoard() {
        setBoard(this.currentPlayerMove.getBoardAfterMove().getListOfSerie());
    }
    
    public boolean getIsUserMadeFirstMoveInGame() {
        return isUserMadeFirstMoveInGame;
    }
    
    public void initCurrentPlayerMove() {
        //init variables in the statrt of the turn
        Board printableBoard = new Board(new ArrayList<>(rummikubLogic.getGameBoard().getListOfSerie()));
        boolean isFirstMoveDone = rummikubLogic.getCurrentPlayer().isFirstMoveDone();
        Player printablePlayer = rummikubLogic.getCurrentPlayer().clonePlayer();
        this.currentPlayerMove = new PlayersMove(printablePlayer.getListPlayerTiles(), printableBoard, isFirstMoveDone);
        this.isUserMadeFirstMoveInGame = CAN_SAVE_THE_GAME;
    }

    
    public GameLogic getRummikubLogic() {
        return rummikubLogic;
    }

    public void setRummikubLogic(GameLogic rummikubLogic) {
        this.rummikubLogic = rummikubLogic;
    }

    public void swapTurns() {
        rummikubLogic.swapTurns();
        initCurrentPlayerMove();
        initCurrPlayerLabel();
        initAboveHeapLabel(); 
        try{
            new Thread(() -> { defineIfTheTurnOfHumanOrComputer(); }).start();
        }
        catch(Exception ex) {
            this.myController.setScreen(Rummikub.MAINMENU_SCREEN_ID, ScreensController.NOT_RESETABLE);
        }
    }

    public void showCurrentGameBoardAndCurrentPlayerHand() {
        showCurrentPlayerBoard();
        showCurrentPlayerHand();
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        initPlayerAndButtonComponents();
        this.centerPane = new AnimatedGameBoardPane();
        this.serieGenerator = new SeriesGenerator();
        this.newMoveGenerator = new ComputerSingleMoveGenerator();
        this.board.setCenter(centerPane);
        this.swapTurnTimeLineDelay = new Timeline(new KeyFrame(Duration.millis(800), (ActionEvent event1) -> {swapTurns();}));
        this.isLegalMove = new SimpleBooleanProperty(false);
        this.isUserMadeFirstMoveInGame = CAN_SAVE_THE_GAME; 

        setHandEvents();
    }

    @Override
    public void resetScreen() {
        //((AnimatedGameBoardPane) this.board.getCenter()).resetScreen();
        resetPlayersBar();
        setPlayersBar();
        handTile.getChildren().clear();
        withdrawCard.setStyle("-fx-font-size: 24px; -fx-font-weight: bold");
        withdrawCard.setText("+");
        withdrawCard.setDisable(!ENABLE_BUTTON);
        this.errorMsg.setText(Utils.Constants.EMPTY_STRING);
        //show();
    }

    @Override
    public void setScreenParent(ScreensController parentScreen) {
        this.myController = parentScreen;
    }
}


