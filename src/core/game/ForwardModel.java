package core.game;

import core.Types;
import core.actions.Action;
import core.units.Tribe;

public class ForwardModel {

    // Board of the game, with all objects distributed in a 2D array of size 'this.size x this.size'
    private Types.TERRAIN[][] board; //Change this for terrain types, rather than integers

    //Tribes playing this game
    private Tribe[] tribes;

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


    /**
     * Advances the game state applying a single action received.
     * @param playerAction to be executed in the current game state.
     */
    public void next(Action playerAction){
        //TODO: MAIN function of this class.
        // Takes the action passed as parameter and runs it in the game.

    }

    /**
     * Creates an exact, deep copy of this model
     * @param playerID player that views the model, used for
     *                 reducing the state for partial observability
     * @return a copy of the model.
     */
    public ForwardModel copy(int playerID)
    {
        //TODO: Make an exact copy of this model, reducing if for PO if playerID != -1
        return null;
    }


    public Tribe[] getTribes() {
        return tribes;
    }
}
