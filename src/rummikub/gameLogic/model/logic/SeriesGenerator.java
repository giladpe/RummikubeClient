/*
 * this class generates series to put in the board
 */
package rummikub.gameLogic.model.logic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import javafx.util.Pair;
import rummikub.gameLogic.model.gameobjects.Serie;
import rummikub.gameLogic.model.gameobjects.Serie.SerieType;
import rummikub.gameLogic.model.gameobjects.Tile;


public class SeriesGenerator {

    private static final boolean ROUND_MOVE_GENERATED = true; 
    private final ArrayList<Pair<Serie,Integer>> listBoard;
    private ArrayList<Tile> tilesInHand;
    private final ArrayList<Tile> jokersInHand;
    private boolean isRoundMoveAlreadyGenerated;
    
    private void initMembers(ArrayList<Tile> playerHand) {
        Tile currTile;
        
        resetMoveGenerator();

        this.tilesInHand = new ArrayList<>(playerHand); 
        
        for (Iterator<Tile> iterator = tilesInHand.iterator(); iterator.hasNext();) {
            currTile = iterator.next();
            
            if (currTile.isJocker()) {
                this.jokersInHand.add(currTile);
                iterator.remove();
            }
        }
        
        this.isRoundMoveAlreadyGenerated = ROUND_MOVE_GENERATED;
    }

    public SeriesGenerator() {

        this.jokersInHand = new ArrayList<>();
        this.listBoard = new ArrayList<>();
        resetMoveGenerator();
    }
       
    private void resetMoveGenerator() {
        this.listBoard.clear();
        this.jokersInHand.clear();
        this.tilesInHand = null;
        this.isRoundMoveAlreadyGenerated = !ROUND_MOVE_GENERATED;
    }
    
    public void turnFinishedResetMoveGenerator() {
        resetMoveGenerator();
    }

    public boolean getIsRoundMoveAlreadyGenerated() {
        return this.isRoundMoveAlreadyGenerated;
    }

    public Serie generateSerieMove(ArrayList<Tile> playerHand, boolean isFirstMoveDone) {
        Serie generatedSerie;
        initMembers(playerHand);
        
        if(isFirstMoveDone)
        {
            generatedSerie = implementSingleMovesWithMostTileDisposal();
        }
        else {
            generatedSerie = implementSingleMovesToGetAtleast30Points();
        }

        return generatedSerie;
    }

    private Serie implementSingleMovesWithMostTileDisposal() {
        ArrayList<Serie> seriesList;
        Serie generatedSerie = null;

        if(!this.tilesInHand.isEmpty()){
            seriesList = createAllpossibleSeries();
            
            if (!seriesList.isEmpty()) {
                seriesList.stream().forEach((serie) -> {this.listBoard.add(new Pair<>(serie,serie.getSizeOfSerie()));});
                generatedSerie = dealWithSelectedSerie();
            }
        }
        
        return generatedSerie;
    }
    
    private Serie implementSingleMovesToGetAtleast30Points() {
        ArrayList<Serie> seriesList;
        Serie generatedSerie = null;


        if(!this.tilesInHand.isEmpty()){
            seriesList = createAllpossibleSeries();
            
            if (!seriesList.isEmpty()) {
                seriesList.stream().forEach((serie) -> {this.listBoard.add(new Pair<>(serie,serie.getScoreOfSerie()));});
                generatedSerie = dealWithSelectedSerie();
            }
        }
        
        return generatedSerie;

    }
    
    private ArrayList<Serie> createAllpossibleSeries(){
    
        ArrayList<Serie> seriesList = new ArrayList<>();

        createAllPossibleSameTypeSeries(seriesList);
        createAllPossibleIncreasingSeries(seriesList);

        return seriesList;
    }
    
    private Serie dealWithSelectedSerie() {
        Serie chosenSerie;

        chosenSerie = Collections.max(listBoard, (Pair pair1, Pair pair2) -> (int)pair1.getValue() - (int)pair2.getValue()).getKey();
        
        if (!chosenSerie.isEmptySeries()) {
            this.tilesInHand.removeAll(chosenSerie.toArrayListOfTiles());
            this.jokersInHand.removeAll(chosenSerie.toArrayListOfTiles());
        }
        this.listBoard.clear();
        
        return chosenSerie;
    } 
    
    private void createAllPossibleSameTypeSeries(ArrayList<Serie> seriesList) {
        ArrayList<Tile> cheackForSameTypeSeriesInHand = this.tilesInHand;
        Iterator<Tile> forEachTile = cheackForSameTypeSeriesInHand.iterator();
        Iterator<Tile> cheackAllTiles;
        Tile nextTile, prevTile;
        Serie newSerie;
        
        while(forEachTile.hasNext()) {
            prevTile = forEachTile.next();
            newSerie = new Serie(prevTile,SerieType.SAME_TYPE_SERIES);
            cheackAllTiles = cheackForSameTypeSeriesInHand.iterator();
                
            while(cheackAllTiles.hasNext()) {
                nextTile = cheackAllTiles.next();
                
                if (prevTile.isSameNumberTile(nextTile) && !newSerie.contains(nextTile) ) {
                    newSerie.addSpecificTileToSerie(nextTile);
                }
            }
            
            for (Tile jocker : this.jokersInHand) {
                if(newSerie.canAddTileToSameNumberSerie()) {
                    newSerie.addSpecificTileToSerie(jocker);
                }
            }
            
            if(newSerie.isLegalSameNumberSerieSize(newSerie.getSizeOfSerie())  && !seriesList.contains(newSerie)) {
                seriesList.add(newSerie);
            }
        }
    }

    private void createAllPossibleIncreasingSeries(ArrayList<Serie> seriesList) {
        Iterator<Tile> iterator = this.tilesInHand.iterator();
        Tile nextTile, prevTile;
        Serie newSerie = new Serie(SerieType.INCREASING_SERIES);
        
        if(iterator.hasNext()) {
            prevTile = iterator.next();
            newSerie.addSpecificTileToSerie(prevTile);
            while(iterator.hasNext()) {
                nextTile = iterator.next();
                if(!nextTile.isEqualTiles(prevTile)) {
                    if (nextTile.isIncreasingAndSameColorTiles(prevTile)) {
                        newSerie.addSpecificTileToSerie(nextTile);
                    }
                    else if (nextTile.isIncreasingAndSameColorTilesWithJocker(prevTile, this.jokersInHand.size())) {
                        for (int i = 0; i < nextTile.differanceBetweenTiles(prevTile)-1; i++) {
                            newSerie.addSpecificTileToSerie(this.jokersInHand.get(i));
                        }
                        newSerie.addSpecificTileToSerie(nextTile);
                    }
                    else {
                        if(newSerie.isLegalIncreasingSerieSize(newSerie.getSizeOfSerie())) {
                            seriesList.add(newSerie);
                        }
                        newSerie = new Serie(SerieType.INCREASING_SERIES);
                        newSerie.addSpecificTileToSerie(nextTile);
                    }
                }
                prevTile = nextTile;
            }
        }
        if(!iterator.hasNext() && newSerie.isLegalIncreasingSerieSize(newSerie.getSizeOfSerie())) {
            seriesList.add(newSerie);
        }
    }
}

