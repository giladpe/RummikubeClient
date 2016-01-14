/*
 * this class represents the board of the game where players place thier tiles
 */
package rummikub.gameLogic.model.gameobjects;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;


public class Board {
    //Constants
    public static final String BOARD = "Board: ";
    public static final String WHITESPACE_LINE = new String(new char[BOARD.length()]).replace('\0', ' '); 
    private final static String EMPTY_BOARD = "THE BOARD IS EMPTY";
    
    // Data Members
    private final ArrayList<Serie> listOfSerie;

    // Getters
    public ArrayList<Serie> getListOfSerie() {
        return listOfSerie;
    }
    
    public void setSpecificTile(Tile tileToAdd, int nLineNum, int nIndex) {
        this.listOfSerie.get(nLineNum).addSpecificTileToSerie(tileToAdd, nIndex);
    }
    
    public void addSeries(Serie serieToAdd) {
        this.listOfSerie.add(serieToAdd);
    }
    
    public Serie getSeries(int index) {
        Serie serie;
        if(this.listOfSerie.isEmpty()) {
            serie = new Serie();
        }
        else {
            if (this.listOfSerie.size() > index ) {
                serie = this.listOfSerie.get(index);
            }
            else {
                serie = new Serie();
            }
        }
        
        return serie;
    }
    
    // Constuctor  
    public Board(ArrayList<Serie> seriesList) {
       this.listOfSerie = new ArrayList<>();
        for (Serie serie : seriesList) {
            this.listOfSerie.add(new Serie(serie));
        }
    }
    
    // Empty constructor
    public Board() {
        this.listOfSerie = new ArrayList<>();
    }
    
    // method that return the size of the board
    public int boardSize() {
        return this.listOfSerie.size();
    }

    // Private methods
    
    //removes any empty series in the board before validation
    private void removeEmptySeries() {
        
        for (Iterator<Serie> iterator = listOfSerie.iterator(); iterator.hasNext();) {
            Serie serie = iterator.next();
            
            if (serie.isEmptySeries()) {
                iterator.remove();
            }
        }
   }
      
    private int getScoreOfBoard() {
        int sum=0;
        
        sum = listOfSerie.stream().map((series) -> series.getScoreOfSerie()).reduce(sum, Integer::sum); //map((series) -> series.getSizeOfSerie()).reduce(sum, Integer::sum);
        
        return  sum;       
    }
    
    // Public Methods
    
    //validates the boar
    //return true if the board is valid, otherwise returns false
    public boolean validateBoard() {
        boolean validResualt = false;
        
        removeEmptySeries();

        for (Serie series : listOfSerie) {

            validResualt =  series.isValidSerie(); 

            if (!validResualt) {
                break;
            }
        }

        return validResualt;
    }

    public int getNumOfTilesInBoard() {
        int sum=0;
        
        sum = listOfSerie.stream().map((series) -> series.getSizeOfSerie()).reduce(sum, Integer::sum);

        return  sum;
    }
    
    //get the index of the longest series in the board
    public int getSizeOfLongestSerie() {
        Serie maxSerie;
        int resualt=0;
        
        if (!this.listOfSerie.isEmpty()) {
            maxSerie = Collections.max(listOfSerie, (Serie Ser1, Serie Ser2) -> Ser1.getSizeOfSerie() - Ser2.getSizeOfSerie());        
            resualt = maxSerie.getSizeOfSerie();
        }
        
        return  resualt;
    }

    public void initBoard() {
        this.listOfSerie.clear();
    }
    
    @Override
    public String toString(){
        StringBuilder board = new StringBuilder();
        int lineNum = 0;
        
        if (this.listOfSerie.isEmpty()) {
            board.append(EMPTY_BOARD).append(System.getProperty("line.separator"));
        } 
        else 
        {
            for (Serie series : this.listOfSerie) {
                board.append(String.format("%s%d. ", Tile.Color.RESET_COLOR, lineNum)).append(series.toString());                     
                lineNum++;
            }            
        }

        board.append(System.getProperty("line.separator"));
        
        return board.toString();
    }
  
    public Tile getSpecificTile(int nLineNum, int nIndex) {               
       return this.listOfSerie.get(nLineNum).getSpecificTile(nIndex);
    }
    
    public Tile removeSpecificTile(int nLineNum, int nIndex) {  
        Tile tileToRemove = this.listOfSerie.get(nLineNum).removeSpecificTile(nIndex);
        
        if (this.listOfSerie.get(nLineNum).isEmptySeries()) {
            this.listOfSerie.remove(nLineNum);
        }

        return tileToRemove;
    }
    
    // cheacks if the param newLine is new line in board or existing one
    // @param newLine - nominated as new line in the board
    // @return true if param newLine is a new line in board, otherwise false
    public boolean isNewLineForTheBoard(int newLine) {
        return  this.listOfSerie.size() - newLine == 0;
    }
    
    // returns the maximun row size
    // row - the wanted row
    // inserNewTile - if we want to add new tile to serie we need inc the num of col by 1
    // return integer value  of the the maximun row size 
    public int getMaxColSizeForSpecificRow(int row, boolean inserNewTile) {
        int index = 0;
        
        if(!this.listOfSerie.isEmpty() && this.listOfSerie.size() > row) {
            
            if (!this.listOfSerie.get(row).isEmptySeries() && !inserNewTile){
                index = this.listOfSerie.get(row).getSizeOfSerie()-1;
            }
            else if(!this.listOfSerie.get(row).isEmptySeries() && inserNewTile) {
                index = this.listOfSerie.get(row).getSizeOfSerie();
            }
        }
        
        return index;
    }

    // looks for the first valid location of a tile in the board and places it there
    // currTile - tile to find location for it
    // return Point with the cordinates of place where the param currTile was places or
    //null if no valid location found
    public Point findPlaceForNewTile(Tile currTile) {
        Point location = null;
        Serie serie;
        Integer index;
        boolean found = false;
        
        for (int i = 0; i < this.listOfSerie.size() && !found; i++) {
            serie = this.listOfSerie.get(i);
            index = serie.canAddTileToBegginingOrEndOfSerie(currTile);
            found = index != null;
            if(found) {
                location = new Point(i, index);
            }
        }
                
        return location;
    }

    public ArrayList<Tile> getArrayListOfBoardTiles() {
        ArrayList<Tile> allBoardTiles = new ArrayList<>();
        
        this.listOfSerie.stream().forEach((serie) -> { allBoardTiles.addAll(serie.getSerieOfTiles()); });
        
        return allBoardTiles;
    }
    
    public boolean isEmpty() {
        return this.listOfSerie.isEmpty();
    }
    
    public void clear() {
        this.listOfSerie.clear();
    }
}
