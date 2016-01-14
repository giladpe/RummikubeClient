/*
 * represents a player in the game
 */
package rummikub.gameLogic.model.player;
import rummikub.gameLogic.model.gameobjects.Tile;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.Objects;

public abstract class Player {
    // Constants
    static final String HAND = "Hand:  "; 
    public static final String WHITESPACE_LINE = new String(new char[HAND.length()]).replace('\0', ' '); 
    public static final int FIRST_SCOORE = 0;
    public static final boolean MOVE_PLAYED = true;
    public static final int EMPTY_HAND = 0;
    
    // Data Members
    private int score;
    private String name;
    private ArrayList<Tile> listPlayerTiles;
    private boolean PlayedFirstMove;
    private boolean PlayedNormalMove;
    protected boolean isHuman;
    
    // Constructor
    public Player(String name) {
        this();
        this.name = name;
    }
    
    public Player() {
        this.score = 0;
        this.name = null;
        this.listPlayerTiles = new ArrayList<>();
        this.PlayedFirstMove = false;
        this.PlayedNormalMove = false;
    }
    
    // Copy constructor
    public Player(Player player) {
        this.score = player.getScore();
        this.name = player.getName();
        this.listPlayerTiles = new ArrayList<>(player.getListPlayerTiles());
        this.PlayedFirstMove = player.isFirstMoveDone();
        this.PlayedNormalMove = player.isNormalMoveDone(); 
    }
    
    // Getter && Setter
    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }
    
    public boolean isFirstMoveDone() {
        return PlayedFirstMove;
    }
    
    public void setFirstMoveDone(boolean FirstMoveDone) {
        this.PlayedFirstMove = FirstMoveDone;
    }
    
    public boolean isNormalMoveDone() {
        return PlayedNormalMove;
    }
    
    public void setNormalMoveDone(boolean NormalMoveDone) {
        this.PlayedNormalMove = NormalMoveDone;
    }
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    public ArrayList<Tile> getListPlayerTiles() {
        return listPlayerTiles;
    }

    public void setListPlayerTiles(ArrayList<Tile> listPlayerTiles) {
        this.listPlayerTiles = listPlayerTiles;
        Collections.sort(this.listPlayerTiles);
    }
 
    // Public Methods
    public void AddTileToPlayersTiles(Tile someTile) {
        this.listPlayerTiles.add(someTile);
        Collections.sort(this.listPlayerTiles);
    }

    public boolean getIsHuman() {
        return isHuman;
    }

    public void setIsHuman(boolean isHuman) {
        this.isHuman = isHuman;
    }   

    public void initPlayer(ArrayList<Tile> newHand) {
        this.score = 0;
        this.PlayedFirstMove = false;
        this.PlayedNormalMove = false;
        this.listPlayerTiles.clear();
        this.listPlayerTiles = newHand;
        Collections.sort(this.listPlayerTiles);
    }
    
    public abstract Player clonePlayer();
    
    public String PlayerTilesString(){
        StringBuilder tempStr = new StringBuilder();
        
        tempStr.append(HAND);
        for (Iterator<Tile> iterator = this.listPlayerTiles.iterator(); iterator.hasNext();) {
            Tile CurrentTile = iterator.next();
            
           if (iterator.hasNext()) {
               if (CurrentTile.getEnumTileNumber().getTileNumberValue()<10) {
                    tempStr.append(String.format("%s ,",CurrentTile.toString()));
               } else {
                    tempStr.append(String.format("%s,",CurrentTile.toString()));
               }
            }
            else{
                tempStr.append(String.format("%s",CurrentTile.toString()));
            }   
        }
        tempStr.append(System.getProperty("line.separator"));
        
        return tempStr.toString();
    }
    
    // Override Methods
    @Override
    public String toString() {
        return this.name;
    }
    
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 47 * hash + Objects.hashCode(this.name);
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
        final Player other = (Player) obj;
        if (!Objects.equals(this.name, other.name)) {
            return false;
        }
        return true;
    }
}