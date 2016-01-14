/*
 * represents a computer player in the game
 */
package rummikub.gameLogic.model.player;

public class ComputerPlayer extends Player{
    // Static counter
    static private int noOfInstances = 0; // counting number Of instances
  
    // Constructor
    public ComputerPlayer() {
        super();
        noOfInstances++; 
        this.setName("Comp-" + String.valueOf(noOfInstances));
        this.setIsHuman(false);
    }
    
    // Copy constructor
    public ComputerPlayer(Player player) {
        super(player);
        this.setIsHuman(false);
    }
    
    @Override
    protected void finalize() throws Throwable {
        noOfInstances--;
        super.finalize();
    }

    @Override
    public Player clonePlayer() {
        return new ComputerPlayer(this);
    }
}
