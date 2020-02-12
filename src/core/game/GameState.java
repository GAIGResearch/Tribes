package core.game;

import core.actions.Action;
import core.actors.Actor;
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
        this.model = new ForwardModel();
    }

    //Another constructor.
    public GameState(long seed) {
        this.seed = seed;
        this.model = new ForwardModel();
    }

    /**
     * Initializes the GameState.
     * The level is only generated when this initialization method is called.
     */
    void init(String filename, Tribe[] tribes) {
        this.model.init(tribes, seed, filename);
    }

    /**
     * Adds a new actor to the list of game actors
     * @param actor the actor to add
     * @return the unique identifier of this actor for the rest of the game.
     */
    public int addActor(Actor actor)
    {
        return model.getBoard().addActor(actor);
    }

    /**
     * Gets a game actor from its id.
     * @param actorId the id of the actor to retrieve
     * @return the actor, null if the id doesn't correspond to an actor (note that it may have
     * been deleted if the actor was removed from the game).
     */
    public Actor getActor(int actorId)
    {
        return model.getBoard().getActor(actorId);
    }

    /**
     * Removes an actor from the list of actor
     * @param actorId id of the actor to remove
     * @return true if the actor was removed (false may indicate that it didn't exist).
     */
    public boolean removeActor(int actorId)
    {
        return model.getBoard().removeActor(actorId);
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
