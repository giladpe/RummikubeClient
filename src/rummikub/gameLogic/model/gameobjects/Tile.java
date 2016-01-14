/*
 * basic object in the game that represents a tile in the game
 */
package rummikub.gameLogic.model.gameobjects;

import java.util.Objects;

public class Tile implements Comparable<Tile> {

    public enum Color {
        BLACK("#0a0a09", 100),
        RED("#ff0022", 200),
        YELLOW("#ffff00", 300),
        BLUE("#4400ff", 400);
//        BLACK("\u001B[30m", 100),
//        RED("\u001B[31m", 200),
//        YELLOW("\u001B[33m", 300),
//        BLUE("\u001B[34m", 400);
       
        //public static final String RESET_COLOR = "\u001B[0m";
        public static final String RESET_COLOR = "#0a0a09";
        public static final int MAX_TILE_VALUE = 500;
        final private String ansiColor;
        final private int colorVal;

        Color(String ansiColor, int colorVal) {
            this.ansiColor = ansiColor;
            this.colorVal = colorVal;
        }

        // used to giveweight to color when sorting the hand tile
        // return the value of the color
        public int getColorVal() {
            return this.colorVal;
        }

        public String getAnsiColor() {
            return ansiColor;
        }
    }

    public enum TileNumber {
        JOKER(0, "J"),
        ONE(1, "1"),
        TWO(2, "2"),
        THREE(3, "3"),
        FOUR(4, "4"),
        FIVE(5, "5"),
        SIX(6, "6"),
        SEVEN(7, "7"),
        EIGHT(8, "8"),
        NINE(9, "9"),
        TEN(10, "10"),
        ELEVEN(11, "11"),
        TWELVE(12, "12"),
        THIRTEEN(13, "13");

        @Override
        public String toString() {
            return this.myTileString;
        }

        private final int tileNumber;
        private final String myTileString; // used when converting the tiles to strings

        TileNumber(int tileNumber, String myTileString) {
            this.tileNumber = tileNumber;
            this.myTileString = myTileString;
        }

        public int getTileNumberValue() {
            return this.tileNumber;
        }

        public static TileNumber getTileNumberByValue(int value) {
            for (TileNumber curr : TileNumber.values()) {
                if (curr.getTileNumberValue() == value) {
                    return curr;
                }
            }

            return null;
        }
    }

    //Constants
    private final static int HAS_ONE_JOCKER_IN_HAND = 2;
    private final static int HAS_TWO_JOCKER_IN_HAND = 3;

    // Data members
    private final Color tileColor;
    private final TileNumber enumTileNumber; // The Tile-Number; joker will be '0'

    // Constructor
    public Tile(Color tileColor, TileNumber enumTileNumber) {
        this.enumTileNumber = enumTileNumber;
        this.tileColor = tileColor;
    }

    public Tile(Tile newTile) {
        this.enumTileNumber = newTile.getEnumTileNumber();
        this.tileColor = newTile.getTileColor();
    }

    // Getter && Setter
    public Color getTileColor() {
        return tileColor;
    }

    public TileNumber getEnumTileNumber() {
        return enumTileNumber;
    }

    // Public Methods
    public boolean isJocker() {
        return this.enumTileNumber == TileNumber.JOKER;
    }

    public boolean isAcendingTiles(Tile nextTile) {
        return this.enumTileNumber.getTileNumberValue() - nextTile.getEnumTileNumber().getTileNumberValue() >= 0;
    }

    public boolean isIncreasingAndSameColorTiles(Tile nextTile) {
        return this.enumTileNumber.getTileNumberValue() - nextTile.getEnumTileNumber().getTileNumberValue() == 1
                && this.tileColor == nextTile.getTileColor();
    }

    public int differanceBetweenTiles(Tile prevTile) {
        return this.enumTileNumber.getTileNumberValue() - prevTile.getEnumTileNumber().getTileNumberValue();
    }

    public boolean isEqualTiles(Tile prevTile) {
        boolean result = false;
        
        if (prevTile != null) {
            result = this.enumTileNumber.getTileNumberValue() - prevTile.getEnumTileNumber().getTileNumberValue() == 0;
        }
        
        return result;
    }

    public boolean isIncreasingAndSameColorTilesWithJocker(Tile prevTile, int numOfJockers) {
        int prevTileNumValue = prevTile.getEnumTileNumber().getTileNumberValue();
        int diffbetweenTile = this.enumTileNumber.getTileNumberValue() - prevTileNumValue;
        boolean isSameColor = this.tileColor == prevTile.getTileColor();
        boolean isIncreasingTiles = this.enumTileNumber.getTileNumberValue() - prevTileNumValue == 1;
        boolean isIncreasingAndSameColorSerie;

        if (!isIncreasingTiles && numOfJockers >= 1) {
            isIncreasingTiles = HAS_ONE_JOCKER_IN_HAND == diffbetweenTile;
        }

        if (!isIncreasingTiles && numOfJockers == 2) {
            isIncreasingTiles = HAS_TWO_JOCKER_IN_HAND == diffbetweenTile;
        }

        isIncreasingAndSameColorSerie = isSameColor && isIncreasingTiles;

        return isIncreasingAndSameColorSerie;
    }

    public boolean isSameNumberTile(Tile nextTile) {
        return this.enumTileNumber.getTileNumberValue() == nextTile.getEnumTileNumber().getTileNumberValue();
    }

    public boolean isSameNumberTile(int firstTileWithNumber) {
        return this.enumTileNumber.getTileNumberValue() == firstTileWithNumber;
    }

    @Override
    public String toString() {
        String tileStr = String.format("%s%s%s", this.getTileColor().getAnsiColor(),
                this.getEnumTileNumber().toString(),
                Color.RESET_COLOR);

        return tileStr;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 31 * hash + Objects.hashCode(this.tileColor);
        hash = 31 * hash + Objects.hashCode(this.enumTileNumber);
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
        final Tile other = (Tile) obj;
        if (this.tileColor != other.tileColor) {
            return false;
        }
        return this.enumTileNumber == other.enumTileNumber;
    }

    // used to compare tiles.
    // the method used when sorting the players hand
    // param tileToCompare - the tile that the checking tile refers to in comparison*/
    @Override
    public int compareTo(Tile tileToCompare) {
        int myVal, tileToCompareVal;

        myVal = this.enumTileNumber.getTileNumberValue() + this.tileColor.getColorVal();
        tileToCompareVal = tileToCompare.enumTileNumber.getTileNumberValue() + tileToCompare.getTileColor().getColorVal();
        if (this.isJocker()) {
            myVal += Color.MAX_TILE_VALUE;
        }

        if (tileToCompare.isJocker()) {
            tileToCompareVal += Color.MAX_TILE_VALUE;
        }

        return myVal - tileToCompareVal;
    }
}
