/*
 * class used in old gilad's project of ex. 1, not used
 */
package rummikub.gameLogic.model.player;

import java.util.ArrayList;
import java.util.Collections;
import rummikub.gameLogic.model.gameobjects.Tile;
//import rummikubpro.Engin.TilesLogic.Tile;

public class Solution {

    private final ArrayList<Tile> pcHand;
    private final ArrayList<Tile> jokersHand;
    private int jokerIndex = 0;
    
    public Solution(ArrayList<Tile> hand) {
        pcHand = new ArrayList<>();
        
        hand.stream().forEach((tile) -> { pcHand.add(tile); });

        jokersHand = new ArrayList<>();

        for (int i = (pcHand.size() - 1); i >= 0; i--) {
            if (isJoker(pcHand.get(i))) {
                jokersHand.add(pcHand.get(i));
                pcHand.remove(i);
            }
        }
    }

    public ArrayList<Tile> getSol(boolean firstMove) {
        ArrayList<ArrayList<Tile>> seriesList = findSeries();
        ArrayList<Tile> resSerie = new ArrayList<>();

        if (seriesList.size() > 0) {
            
            if (firstMove) {
                resSerie = seriesList.get(maxValSerie(seriesList));
            } 
            else {
                resSerie = seriesList.get(maxSizeSerie(seriesList));
            }
        }
        
        return resSerie;
    }

    public ArrayList<ArrayList<Tile>> findSeries() {
        ArrayList<ArrayList<Tile>> seriesList = new ArrayList<>();

        if (pcHand.size() > 0) {
            seriesList = findSet();
            ArrayList<ArrayList<Tile>> tmp = findStraight();
            seriesList.addAll(tmp);
        }
        
        return seriesList;
    }

    public int maxValSerie(ArrayList<ArrayList<Tile>> serieList) {
        int currVal, maxSerieVal = 0;
        int maxIndex = 0;

        for (int i = 0; i < serieList.size(); i++) {
            currVal = getSerieVal(serieList.get(i));
            
            if (currVal > maxSerieVal) {
                maxIndex = i;
                maxSerieVal = currVal;
            }
        }
        
        return maxIndex;
    }

    public int maxSizeSerie(ArrayList<ArrayList<Tile>> serieList) {

        int currSize, maxSerieSize = 0;
        int maxIndex = 0;

        for (int i = 0; i < serieList.size(); i++) {

            currSize = serieList.get(i).size();
            if (currSize > maxSerieSize) {

                maxIndex = i;
                maxSerieSize = currSize;
            }
        }
        return maxIndex;
    }

    public int getSerieVal(ArrayList<Tile> serie) {
        int val = 0;
        
        val = serie.stream().map((tile) -> tile.getEnumTileNumber().getTileNumberValue()).reduce(val, Integer::sum);
        
        return val;
    }

    public ArrayList<ArrayList<Tile>> findSet() {
        int i, prevColorCode;
        ArrayList<ArrayList<Tile>> resSet = new ArrayList<>();
        ArrayList<Tile> resSerie = new ArrayList<>();

        Collections.sort(pcHand);

        for (i = (pcHand.size() - 1); i >= 0; i--) {

            if (resSerie.size() >= 3) {
                resSet.add(resSerie);
            }
            
            resSerie = new ArrayList<>();
            resSerie.add(pcHand.get(i));

            for (int j = i - 1; j >= 0; j--) {
                prevColorCode = getLastColor(resSerie);

                if (isValidSetTileAfterTile(i, j, prevColorCode)) {
                    resSerie.add(pcHand.get(j));
                }
            }
            tryAddJokerToSet(resSerie);
        }
        
        return resSet;
    }

    private int getLastColor(ArrayList<Tile> serie) {
        return serie.get(serie.size() - 1).getTileColor().getColorVal();
    }

    private boolean isValidSetTileAfterTile(int iIndex, int jIndex, int prevColorCode) {
        int iValue = getTileValue(iIndex);
        int currColorCode = getTileColorCode(jIndex);
        int jValue = getTileValue(jIndex);

        return iValue == jValue && prevColorCode != currColorCode;
    }

    private void tryAddJokerToSet(ArrayList<Tile> serie) {
        int jokerSize = jokersHand.size();

        if (jokerSize > 0) {
            while (jokerSize > 0 && serie.size() < 4) {
                jokerSize--;
                serie.add(jokersHand.get(jokerSize));
            }
        }
    }

    public ArrayList<ArrayList<Tile>> findStraight() { //computer function to find straight 
        int i = 0, jokerSize = jokersHand.size(), prevColor, prevVal;
        ArrayList<ArrayList<Tile>> resStraight = new ArrayList<>();
        ArrayList<Tile> serie = new ArrayList<>();

        Collections.sort(pcHand);
        serie.add(pcHand.get(i));
        prevColor = getTileColorCode(i);
        prevVal = getTileValue(i);

        for (i = 1; i < pcHand.size(); i++) {
            if ((prevVal == getTileValue(i) - 1) && (prevColor == getTileColorCode(i))) {
                serie.add(pcHand.get(i));
                prevVal++;

            } else if (jokerSize - jokerIndex > 0) {
                serie.add(jokersHand.get(jokerIndex));
                prevVal++;
                jokerIndex++;
                i--; 
            } 
            else {
                if (serie.size() >= 3) {
                    resStraight.add(serie);
                    serie = new ArrayList<>();
                    i--;
                } 
                else {
                    serie = new ArrayList<>();
                    prevVal = getTileValue(i);
                    prevColor = getTileColorCode(i);
                    serie.add(pcHand.get(i));
                    jokerIndex = 0;
                }
            }
        }
        
        if (serie.size() >= 3) {
            resStraight.add(serie);
        }
        
        return resStraight;
    }

    private boolean isJoker(Tile tile) {
        return tile.getEnumTileNumber().getTileNumberValue() == 0;
    }

    private int getTileValue(int index) {
        return pcHand.get(index).getEnumTileNumber().getTileNumberValue();
    }

    private int getTileColorCode(int index) {
        return pcHand.get(index).getTileColor().getColorVal();
    }
}
