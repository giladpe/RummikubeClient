/*
 * represents a human player in the game
 */
package rummikub.gameLogic.model.player;

public class HumanPlayer extends Player{
    // Constructor
    public HumanPlayer(String name) {
        super(name);
        this.setIsHuman(true);
    }
    
    public HumanPlayer(Player player) {
        super(player);
        this.setIsHuman(true);
    }
    
    // Override
    @Override
    public Player clonePlayer() {
        return new HumanPlayer(this);
    }
}
