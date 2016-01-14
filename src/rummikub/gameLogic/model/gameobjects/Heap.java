/*
 * Represents the heap of the game
 */
package rummikub.gameLogic.model.gameobjects;

import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.ThreadLocalRandom;

public class Heap {
    
    // Constants
    public static final int EMPTY_HEAP = 0;     
    public static final int TILES_IN_HAND_WHEN_GAME_STARTS = 14;
    
    // Data Members
    private static Heap instance; 
    private final ArrayList<Tile> tileList; 
    
    // Constructor
    private Heap() {
       // Reset all tiles
       this.tileList = new ArrayList<>();
       resetTiles();
    }
    
    // Singelton
    public static Heap getInstance(){
       if(instance == null){
           instance = new Heap();
       }
       
       return instance;
    }
   
    // Getter && Setter
    public ArrayList<Tile> getTileList() {
        return tileList;
    }
    
    // Public Methods
    
    //creates a new deck of 106 tiles in order.
    //the ejection of tiles used randomly
    public final void resetTiles(){
                
        this.tileList.clear();
        
        // add simple tiles
        for (Tile.Color color : Tile.Color.values()) {
            for (Tile.TileNumber tileNumber : Tile.TileNumber.values()) {
                // Case its joker
                if(tileNumber.equals(Tile.TileNumber.JOKER)){
                    if(!(color.equals(Tile.Color.BLUE) ||
                        color.equals(Tile.Color.YELLOW)))
                        this.tileList.add(new Tile(color, tileNumber));
                }
                // else add each number 2 times
                else{
                    this.tileList.add(new Tile(color, tileNumber));
                    this.tileList.add(new Tile(color, tileNumber));
                }
            }
        }
    }
    
    // return  random tile taken from the heap
    // Coution: need to check that the deck isnt empty before use
    public Tile getRandomTile(){
        // Get random index
        int randomIndex = this.tileList.isEmpty()? 0 : ThreadLocalRandom.current().nextInt(0, this.tileList.size());
        
        Tile chosenTile = this.tileList.get(randomIndex); 
        this.getTileList().remove(randomIndex);
        
        return chosenTile;
    }
    
    // generates  tiles for player's hand
    // return ArrayList of tiles
    public ArrayList<Tile> getNewHandFromHeap() {
        ArrayList<Tile> newHand = new ArrayList<>();
        for (int i = 0; i < TILES_IN_HAND_WHEN_GAME_STARTS; i++) {
            newHand.add(getRandomTile());
        }
        
        return newHand;
    }
    
    public boolean isEmptyHeap() {
        return this.tileList.isEmpty();
    }

    public void addTile(Tile tileToAdd) {
        this.tileList.add(tileToAdd);
    }
    
    public void shuffleHeapTiles() {
        Collections.shuffle(tileList);
    }

    public void remove(Tile usedTile) {
        this.tileList.remove(usedTile);
    }
}