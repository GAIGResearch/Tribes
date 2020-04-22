package players;

import core.actions.Action;
import core.game.GameState;
import utils.ElapsedCpuTimer;

public abstract class Agent {

    protected int playerID;
    protected long seed;

    /**
     * Default constructor, to be called in subclasses (initializes player ID and random seed for this agent.
     * @param seed - random seed for this player.
     */
    protected Agent(long seed) {
        reset(seed);
    }

    /**
     * Function requests an action from the agent, given current game state observation.
     * @param gs - current game state.
     * @param ect - a timer that indicates when the turn time is due to finish.
     * @return - action to play in this game state.
     */
    public abstract Action act(GameState gs, ElapsedCpuTimer ect);

    /**
     * Function called at the end of the game. May be used by agents for final analysis.
     * @param reward - final reward for this agent.
     */
    public void result(GameState gs, double reward) {}

    /**
     * Getter for player ID field.
     * @return - this player's ID.
     */
    public final int getPlayerID() {
        return playerID;
    }

    /**
     * Setter for the player ID field
     * @param playerID the player ID of this agent
     */
    public void setPlayerID (int playerID) { this.playerID = playerID; }

    /**
     * Getter for seed field.
     * @return - this player's random seed.
     */
    public final long getSeed() {
        return seed;
    }

    public abstract Agent copy();

    public void reset(long seed) {
        this.seed = seed;
    }



}