package core.game;

import core.actions.Action;
import core.actors.Tribe;
import java.util.ArrayList;


public class ForwardModel {

    // Board of the game
    private Board board;

    // Size of the board.
    private int size;

    // Game tick counter as in GameState, for logging purposes (only valid for true model of the game)
    private int tick;

    /**
     * Forward model constructor
     */
    ForwardModel() {
        board = new Board();
    }

    /**
     * Initializes the data structures of the game: board, bombs, flames, etc.
     * Adds avatars to the game and sets them alive.
     * Generates the initial board of the game.
     */
    void init(long seed, String filename, int numPlayers)
    {
        //TODO: Init the game, including creating the level.
        LevelLoader ll = new LevelLoader();
        ll.buildLevel(board, filename, numPlayers);
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

    /**
     * Sets the tribes that will play the game. The number of tribes must equal the number of players in Game.
     * @param tribes to play with
     */
    public void assignTribes(ArrayList<Tribe> tribes)
    {
        board.assignTribes(tribes);
    }

    /**
     * Returns the tribes playing the game
     * @return the tribes playing the game
     */
    public Tribe[] getTribes() {
        return board.getTribes();
    }

    /**
     * Returns the game board.
     * @return the game board.
     */
    public Board getBoard()
    {
        return board;
    }
}
