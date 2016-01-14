/*
 * this class holds the information inouted by the user or computer player and transfered to PlayerMove class
 * that parses the information and deals with it
 */

package rummikub.gameLogic.controller.rummikub;

import java.awt.Point;


public class SingleMove {

    public void setnSource(int nSource) {
        this.nSource = nSource;
    }

    public void setpTarget(Point pTarget) {
        this.pTarget = pTarget;
    }

    public void setpSource(Point pSource) {
        this.pSource = pSource;
    }
    
    public enum MoveType {
        HAND_TO_BOARD(0),
        BOARD_TO_HAND(1),
        BOARD_TO_BOARD(2);
        
        private final int moveType;
        
        MoveType(int moveType){
            this.moveType = moveType;
        }
        
        public int getMoveTypeValue(){
            return this.moveType;
        }
    }
    
    public enum SingleMoveResult {
        LEGAL_MOVE(0),
        TILE_NOT_BELONG_HAND(1),
        NOT_IN_THE_RIGHT_ORDER(2),
        CAN_NOT_TOUCH_BOARD_IN_FIRST_MOVE(3);
        
        
        final int Val;
        
        private SingleMoveResult(int val) {
            this.Val = val;
        }
    }
    
    // Data Members
    private int nSource;
    private Point pTarget;
    private Point pSource;
    private final MoveType moveType;
    
    // Constructor
    public SingleMove(Point pTarget, Point pSource, MoveType moveType) {
        this.pTarget = pTarget;
        this.pSource = pSource;
        this.moveType = moveType;
    }
    
    public SingleMove(Point pTarget, int nSource, MoveType moveType) {
        this.pTarget = pTarget;
        this.nSource = nSource;
        this.moveType = moveType;
    }
    
    public SingleMove(Point pSource, MoveType moveType) {
        this.pSource = pSource;
        this.moveType = moveType;
    }
    
    // Getters
    public Point getpTarget() {
        return pTarget;
    }

    public Point getpSource() {
        return pSource;
    }

    public int getnSource() {
        return nSource;
    }
    
    public MoveType getMoveType() {
        return moveType;
    }
}
