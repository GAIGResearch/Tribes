package core.game;

import core.Types;
import core.actions.Action;

public class ForwardModel {

    // Board of the game, with all objects distributed in a 2D array of size 'this.size x this.size'
    private Types.TERRAIN[][] board; //Change this for terrain types, rather than integers

    // Size of the board.
    private int size;

    // Game tick counter as in GameState, for logging purposes (only valid for true model of the game)
    private int tick;

    /**
     * Forward model constructor
     * @param size Size of board
     */
    ForwardModel(int size) {
        this.size = size;
    }

    void next(Action playerAction, int gsTick){

    }

    ForwardModel copy()
    {
        return null;
    }


}
