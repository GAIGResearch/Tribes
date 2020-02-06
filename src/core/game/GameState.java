package core.game;

import core.actions.Action;
import core.actors.Tribe;

import java.util.ArrayList;

public class GameState {

    // Forward model for the game.
    ForwardModel model;

    // Seed for the game state.
    private long seed;

    // Current tick of the game.
    private int tick = 0;

    //Default constructor.
    public GameState()
    {
    }

    //Another constructor.
    public GameState(long seed) {
        this.seed = seed;
        this.model = new ForwardModel();
    }

    /**
     * Initializes the ForwardModel in the GameState. If the model is null, it creates a new one.
     * The level is only generated when this initialization method is called.
     */
    void init(String filename) {
        if (model == null) {
            model = new ForwardModel();
        }
        this.model.init(seed, filename);
    }

    /**
     * Sets the tribes that will play the game. The number of tribes must equal the number of players in Game.
     * @param tribes to play with
     */
    public void assignTribes(ArrayList<Tribe> tribes)
    {
        this.model.assignTribes(tribes);
    }

    /**
     * Returns the current tick of the game. One tick encompasses a turn for all
     * players in the game.
     * @return current tick of the game.
     */
    public int getTick() {
        return tick;
    }

    /**
     * Increases the tick of the game. One tick encompasses a turn for all players in the game.
     */
    public void incTick()
    {
        tick++;
    }

    /**
     * Computes all the actions that a player can take given the current game state.
     * Warning: This method can be expensive. In game loop, its computation sits outside the
     * agent's decision time, but agents can use it on their forward models at real expense.
     * @param tribe Tribe for which actions are being computed.
     */
    public void computePlayerActions(Tribe tribe)
    {
        //TODO: Compute all actions that 'tribe' can execute in this game state.
        // This function should fill a member variable in this class that provides the actions per unit/city.
        // It also needs to update a flag that indicates that actions are computed for this tribe in particular.

    }

    /**
     * Checks if there are actions that the given tribe can take.
     * @param tribe to check if can execute actions.
     * @return true if actions exist. False if no actions available
     * (that includes if this is not this tribe's turn)
     */
    public boolean existAvailableActions(Tribe tribe)
    {
        //TODO: Checks if there are available actions for this tribe.
        return true;
    }

    /**
     * Advances the game state applying a single action received.
     * @param action to be executed in the current game state.
     */
    public void next(Action action)
    {
        model.next(action);
    }

    /**
     * Public accessor to the copy() functionality of this state.
     * @return a copy of the current game state.
     */
    public GameState copy() {
        return copy(-1);  // No reduction happening if no index specified
    }

    /**
     * Returns the game board.
     * @return the game board.
     */
    public Board getBoard()
    {
        return model.getBoard();
    }

    /**
     * Creates a deep copy of this game state, given player index. Sets up the game state so that it contains
     * only information available to the given player. If -1, state contains all information.
     * @param playerIdx player index that indicates who is this copy for.
     * @return a copy of this game state.
     */
    GameState copy(int playerIdx)
    {
        //TODO: Make an exact copy of this game state.
        // It must call model.copy() to copy the model

        // (this code below is incomplete)
        GameState copy = new GameState(seed);
        copy.model = model.copy(playerIdx);

        return copy;
    }

    /**
     * Gets the tribes playing this game.
     * @return the tribes
     */
    public Tribe[] getTribes()
    {
        return model.getTribes();
    }


    /**
     * Gets the tribe tribeID playing this game.
     * @param tribeID ID of the tribe to pick
     * @return the tribe with the ID requested
     */
    public Tribe getTribe(int tribeID)
    {
        return getTribes()[tribeID];
    }

}
