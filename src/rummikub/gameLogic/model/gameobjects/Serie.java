/*
 * basic object in the game that represents a serie of tiles in the board
 */
package rummikub.gameLogic.model.gameobjects;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Objects;

public class Serie {

    public enum SerieType {
        INCREASING_SERIES(0),
        SAME_TYPE_SERIES(1),
        ILLEGAL_SERIES(2);

        final int Val;

        private SerieType(int val) {
            this.Val = val;
        }
    }

    // Constants
    private static final int MIN_LEGAL_SERIES_SIZE = 3;
    private static final int MAX_LEGAL_SAME_TYPE_SERIES_SIZE = 4;
    private static final int MAX_SIZE_OF_INCREASING_SERIES = 13;
    private static final int INDEX_MOST_LEFT_IN_SERIES = 0;
    private static final int LEFT_SERIE_BOUND = 0;
    private static final int RIGHT_SERIE_BOUND = 14;

    // Data Members
    private final ArrayList<Tile> serieOfTiles;
    private SerieType typeOfTheSerie;

    // Constructor
    public Serie(Serie serie) {
        this.serieOfTiles = new ArrayList<>();
        for (Tile tile : serie.getSerieOfTiles()) {
            this.serieOfTiles.add(new Tile(tile));
        }
    }

    public Serie(ArrayList<Tile> listOfTilesList) {
        this.serieOfTiles = listOfTilesList;
    }

    public Serie() {
        this.serieOfTiles = new ArrayList<>();
    }

    public Serie(SerieType typeOfTheSerie) {
        this();
        this.typeOfTheSerie = typeOfTheSerie;
    }

    public Serie(Tile newTile, SerieType typeOfTheSerie) {
        this(typeOfTheSerie);
        this.serieOfTiles.add(newTile);
    }

    // Getter && Setter    
    public ArrayList<Tile> getSerieOfTiles() {
        return serieOfTiles;
    }

    public Tile getSpecificTile(int Index) {
        return this.serieOfTiles.get(Index);
    }

    public int getSizeOfSerie() {
        return this.serieOfTiles.size();
    }
    
    public SerieType getTypeOfTheSerie() {
        return this.typeOfTheSerie;
    }

    // Private methods
    // validates an increasing serie
    private boolean isLegalIncreasingSerie(int IndexOfFirstTileNum, int firstTileWithNumber) {
        int seriesSize = this.serieOfTiles.size(), currTileNumVal, currNum = firstTileWithNumber - IndexOfFirstTileNum;
        boolean isValidResualt = isLegalIncreasingSerieSize(seriesSize);
        Tile.Color seriesColor = this.serieOfTiles.get(IndexOfFirstTileNum).getTileColor();
        Tile currTile;
        ArrayList<Integer> tilesValues = new ArrayList<>();

        if (isValidResualt) {

            for (int i = 0; i < this.serieOfTiles.size(); i++) {
                tilesValues.add(currNum + i);
            }

            for (int i = 0; i < this.serieOfTiles.size() && isValidResualt; i++) {
                currTile = this.serieOfTiles.get(i);
                currTileNumVal = tilesValues.get(i);

                if (currTile.isJocker()) {
                    isValidResualt = isValidResualt && currTileNumVal > LEFT_SERIE_BOUND && currTileNumVal < RIGHT_SERIE_BOUND;

                } else {
                    isValidResualt = isValidResualt && currTile.isSameNumberTile(currTileNumVal)
                            && currTile.getTileColor() == seriesColor;
                }
            }
        }

        if (isValidResualt) {
            this.typeOfTheSerie = SerieType.INCREASING_SERIES;
        }

        return isValidResualt;
    }

    //validates a serie with same type tiles
    private boolean isLegalSerieOfSameNumberTiles(int ind, int firstTileWithNumber) {
        int seriesSize = this.serieOfTiles.size();
        boolean validResualt = isLegalSameNumberSerieSize(seriesSize);
        ArrayList<Tile.Color> currentColorsInSerie = new ArrayList<>();
        Tile currTile;

        if (validResualt) {
            for (int i = ind; i < seriesSize && validResualt; i++) {
                currTile = this.serieOfTiles.get(i);

                if (!currTile.isJocker()) {
                    validResualt = validResualt && currTile.isSameNumberTile(firstTileWithNumber)
                            && !currentColorsInSerie.contains(currTile.getTileColor());
                    if (validResualt) {
                        currentColorsInSerie.add(currTile.getTileColor());
                    }
                }
            }
        }

        if (validResualt) {
            this.typeOfTheSerie = SerieType.SAME_TYPE_SERIES;
        }

        return validResualt;
    }

    //get the score of same type serie 
    private int sumSameTypeSeriePoints() {
        int sum = 0;
        boolean foundNumTile = false;

        for (Iterator<Tile> iterator = this.serieOfTiles.iterator(); iterator.hasNext() && !foundNumTile;) {
            Tile CurrentTile = iterator.next();

            switch (CurrentTile.getEnumTileNumber()) {
                case JOKER:
                    break;

                default:
                    sum = this.serieOfTiles.size() * CurrentTile.getEnumTileNumber().getTileNumberValue();
                    foundNumTile = true;
            }
        }

        return sum;
    }

    //get the score of increasing serie
    private int sumIncreasingSeriePoints() {
        int sum = 0;
        int IndexOfFirstTileNum = findIndexFirstNumberTile();
        int firstTileWithNumber = this.serieOfTiles.get(IndexOfFirstTileNum).getEnumTileNumber().getTileNumberValue();
        int currNum = firstTileWithNumber - IndexOfFirstTileNum;
        ArrayList<Integer> tilesValues = new ArrayList<>();

        for (int i = 0; i < this.serieOfTiles.size(); i++) {
            tilesValues.add(currNum + i);
        }

        for (Integer tileValue : tilesValues) {
            sum += tileValue;
        }

        return sum;
    }

    //cheack if the tiles plced in increasing order
    private boolean cheackIncreasingOrderNextTile(Tile tileToAdd) {
        boolean isValid = true;
        Tile currTile = this.serieOfTiles.get(INDEX_MOST_LEFT_IN_SERIES);

        if (!(currTile.isJocker() || tileToAdd.isJocker())) {
            isValid = currTile.isAcendingTiles(tileToAdd);
        }
        return isValid;
    }

    //cheacks increasing order with previous tile
    private boolean cheackIncreasingOrderPreviousTile(Tile tileToAdd) {
        boolean isValid = true;

        Tile prevTile = this.serieOfTiles.get(this.serieOfTiles.size() - 1);
        if (!prevTile.isJocker() && !tileToAdd.isJocker()) {
            isValid = tileToAdd.isAcendingTiles(prevTile);
        }

        return isValid;
    }

    // cheack increasing order between two tiles
    private boolean cheackIncreasingOrderBetweenTile(Tile tileToAdd, int index) {
        boolean isValid = true;
        Tile prevTile = this.serieOfTiles.get(index - 1);
        Tile nextTile = this.serieOfTiles.get(index);
        if (!tileToAdd.isJocker()) {
            if (!prevTile.isJocker()) {
                isValid = tileToAdd.isAcendingTiles(prevTile);
            }

            if (isValid && !nextTile.isJocker()) {
                isValid = nextTile.isAcendingTiles(tileToAdd);
            }
        }
        return isValid;
    }

    //skips jockers and give the first tile with number 
    private int findIndexFirstNumberTile() {
        int ind = 0;
        boolean found = false;

        for (int i = 0; this.serieOfTiles.size() >= MIN_LEGAL_SERIES_SIZE && !found && i < 3; i++) {
            found = !this.serieOfTiles.get(i).isJocker();
            if (!found) {
                ind++;
            }
        }

        return ind;
    }

    private Integer canAddTileToIncreasingSerie(Tile currTile) {
        Integer index = null;

        if (this.serieOfTiles.size() < MAX_SIZE_OF_INCREASING_SERIES) {
            index = canAddTileToEndOfIncSerie(currTile);

            if (index == null) {
                index = canAddTileToBegginingOfIncSerie(currTile);
            }
        }

        return index;
    }

    private Integer canAddTileToEndOfIncSerie(Tile tileToAdd) {
        Integer index = null;
        Tile lastTile = this.serieOfTiles.get(this.serieOfTiles.size() - 1);
        boolean canAddTile = checkSpecialCasesEndOfSerie();

        if (canAddTile) {
            if (tileToAdd.isJocker() || lastTile.isIncreasingAndSameColorTiles(tileToAdd)) {
                index = this.serieOfTiles.size();
                this.serieOfTiles.add(tileToAdd);
            }
        }

        return index;
    }

    //checks special cases when adding tile to end of serie
    private boolean checkSpecialCasesEndOfSerie() {
        Tile lastTile = this.serieOfTiles.get(this.serieOfTiles.size() - 1);
        Tile oneBeforelastTile = this.serieOfTiles.get(this.serieOfTiles.size() - 2);
        Tile twoBeforeoneBeforelastTile = this.serieOfTiles.get(this.serieOfTiles.size() - 3);
        boolean validCase;

        validCase = lastTile.getEnumTileNumber() != Tile.TileNumber.THIRTEEN;
        validCase = validCase && lastTile.getEnumTileNumber() != Tile.TileNumber.JOKER
                && oneBeforelastTile.getEnumTileNumber() != Tile.TileNumber.TWELVE;
        validCase = validCase && lastTile.getEnumTileNumber() != Tile.TileNumber.JOKER
                && oneBeforelastTile.getEnumTileNumber() != Tile.TileNumber.JOKER
                && twoBeforeoneBeforelastTile.getEnumTileNumber() != Tile.TileNumber.ELEVEN;

        return validCase;
    }

    private Integer canAddTileToBegginingOfIncSerie(Tile tileToAdd) {
        Integer index = null;
        boolean canAddTile = checkSpecialCasesBegginingOfSerie();
        final int indexZero = 0;
        Tile firstTile = this.serieOfTiles.get(indexZero);

        if (canAddTile) {
            if (tileToAdd.isJocker() || firstTile.isIncreasingAndSameColorTiles(tileToAdd)) {
                index = indexZero;
                this.serieOfTiles.add(indexZero, tileToAdd);
            }
        }

        return index;
    }

    //checks special cases when adding tile to beggining of serie
    private boolean checkSpecialCasesBegginingOfSerie() {
        final int indexZero = 0;
        boolean validCase;
        Tile firstTile = this.serieOfTiles.get(indexZero);
        Tile secoundTile = this.serieOfTiles.get(indexZero + 1);
        Tile thirdTile = this.serieOfTiles.get(indexZero + 2);

        validCase = firstTile.getEnumTileNumber() != Tile.TileNumber.ONE;
        validCase = validCase && firstTile.getEnumTileNumber() != Tile.TileNumber.JOKER
                && secoundTile.getEnumTileNumber() != Tile.TileNumber.ONE;
        validCase = validCase && firstTile.getEnumTileNumber() != Tile.TileNumber.JOKER
                && secoundTile.getEnumTileNumber() != Tile.TileNumber.JOKER
                && thirdTile.getEnumTileNumber() != Tile.TileNumber.THREE;
        return validCase;
    }

    private Integer canAddTileSameTypeSerie(Tile currTile) {
        Integer index = null;
        final int beginingOfSeries = 0;

        if (currTile.isSameNumberTile(this.serieOfTiles.get(beginingOfSeries))
                && this.serieOfTiles.size() < MAX_LEGAL_SAME_TYPE_SERIES_SIZE) {

            this.serieOfTiles.add(currTile);
            index = this.serieOfTiles.indexOf(currTile);
        }

        return index;
    }

    // Public Methods
    // checks if possible to place currTile in beggining or end of series
    // param currTile - tile to place in serie
    //return Integer with the index of place where the param currTile was places or
    // null if no valid location found
    public Integer canAddTileToBegginingOrEndOfSerie(Tile currTile) {
        Integer index = null;

        switch (this.typeOfTheSerie) {

            case INCREASING_SERIES:
                index = canAddTileToIncreasingSerie(currTile);
                break;

            case SAME_TYPE_SERIES:
                index = canAddTileSameTypeSerie(currTile);
                break;

            case ILLEGAL_SERIES:
            default:
                break;
        }
        return index;
    }

    //makes the series to array of tiles
    public Collection<?> toArrayListOfTiles() {
        return this.serieOfTiles;
    }

    public boolean isLegalIncreasingSerieSize(int seriesSize) {
        return seriesSize >= MIN_LEGAL_SERIES_SIZE && seriesSize <= MAX_SIZE_OF_INCREASING_SERIES;
    }

    public boolean isLegalSameNumberSerieSize(int seriesSize) {
        return seriesSize >= MIN_LEGAL_SERIES_SIZE && seriesSize <= MAX_LEGAL_SAME_TYPE_SERIES_SIZE;
    }

    public boolean canAddTileToInceasingSerie() {
        return this.serieOfTiles.size() < MAX_SIZE_OF_INCREASING_SERIES;
    }

    public boolean canAddTileToSameNumberSerie() {
        return this.serieOfTiles.size() < MAX_LEGAL_SAME_TYPE_SERIES_SIZE;
    }

    public boolean isLegalPlaceOfTile(Tile tileToAdd, int index) {
        boolean isValid = true;

        if (!this.serieOfTiles.isEmpty()) {
            if (index == INDEX_MOST_LEFT_IN_SERIES) {
                isValid = cheackIncreasingOrderNextTile(tileToAdd);
            } else if (index == this.serieOfTiles.size()) {
                isValid = cheackIncreasingOrderPreviousTile(tileToAdd);
            } else {
                isValid = cheackIncreasingOrderBetweenTile(tileToAdd, index);
            }
        }

        return isValid;
    }

    public void addSpecificTileToSerie(Tile tileToAdd, int index) {
        this.serieOfTiles.add(index, tileToAdd);
    }

    public void addSpecificTileToSerie(Tile tileToAdd) {
        this.serieOfTiles.add(tileToAdd);
    }

    public Tile removeSpecificTile(int Index) {
        return this.serieOfTiles.remove(Index);
    }

    public void clear() {
        this.serieOfTiles.clear();
    }

    public boolean isEmptySeries() {
        return this.serieOfTiles.isEmpty();
    }

    public boolean isValidSerie() {
        boolean resualt;
        int ind = findIndexFirstNumberTile();
        int firstTileWithNumber = this.serieOfTiles.get(ind).getEnumTileNumber().getTileNumberValue();

        resualt = isLegalIncreasingSerie(ind, firstTileWithNumber);

        if (!resualt) {
            resualt = isLegalSerieOfSameNumberTiles(ind, firstTileWithNumber);
        }

        if (!resualt) {
            this.typeOfTheSerie = SerieType.ILLEGAL_SERIES;
        }

        return resualt;
    }

    public int getScoreOfSerie() {
        int sum = 0;

        switch (this.typeOfTheSerie) {
            case INCREASING_SERIES:
                sum = sumIncreasingSeriePoints();
                break;

            case SAME_TYPE_SERIES:
                sum = sumSameTypeSeriePoints();
                break;

            default:
                break;
        }

        return sum;
    }

    public boolean contains(Tile nextTile) {
        return this.serieOfTiles.contains(nextTile);
    }

    @Override
    public String toString() {
        StringBuilder tempStr = new StringBuilder();

        for (Iterator<Tile> iterator = this.serieOfTiles.iterator(); iterator.hasNext();) {
            Tile CurrentTile = iterator.next();

            if (iterator.hasNext()) {
                if (CurrentTile.getEnumTileNumber().getTileNumberValue() < 10) {
                    tempStr.append(String.format("%s |", CurrentTile.toString()));
                } else {
                    tempStr.append(String.format("%s|", CurrentTile.toString()));
                }
            } else {
                tempStr.append(String.format("%s", CurrentTile.toString()));
            }
        }

        tempStr.append(System.getProperty("line.separator"));

        return tempStr.toString();
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 41 * hash + Objects.hashCode(this.serieOfTiles);
        hash = 41 * hash + Objects.hashCode(this.typeOfTheSerie);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }

        final Serie other = new Serie(((Serie) obj).serieOfTiles);
        final Serie thisSerie = new Serie(this.serieOfTiles);

        if (thisSerie.typeOfTheSerie != other.typeOfTheSerie) {
            return false;
        }

        if (thisSerie.serieOfTiles.size() != other.serieOfTiles.size()) {
            return false;
        }

        Collections.sort(thisSerie.serieOfTiles);
        Collections.sort(other.serieOfTiles);

        for (int i = 0; i < thisSerie.serieOfTiles.size(); i++) {
            if (thisSerie.serieOfTiles.get(i).getEnumTileNumber() != other.serieOfTiles.get(i).getEnumTileNumber()) {
                return false;
            }

        }

        return true;
    }
}
