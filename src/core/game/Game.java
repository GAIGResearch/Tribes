package core.game;

import core.Types;
import core.actions.Action;
import core.units.Tribe;
import players.Agent;
import utils.ElapsedCpuTimer;
import utils.GUI;
import utils.WindowInput;

import java.util.ArrayList;

import static core.Constants.*;

public class Game {

    // State of the game (objects, ticks, etc).
    private GameState gs;

    // GameState objects for players to make decisions
    private GameState[] gameStateObservations;

    // Seed for the game state.
    private long seed;

    // Size of the board.
    private int size;

    // List of players of the game
    private ArrayList<Agent> players;

    public Game()
    {}

    /**
     * Constructor of the game
     * @param seed Seed for the game (used only for board generation)
     * @param size Size of the board.
     */
    public Game(long seed, int size) {
        this.seed = seed;
        this.size = size;
        this.gs = new GameState(seed, size);
        updateAssignedGameStates();
    }

    /**
     * Runs a game once. Receives frame and window input. If any is null, forces a run with no visuals.
     * @param frame window to draw the game
     * @param wi input for the window.
     * @return the results of the game, per player.
     */
    public Types.RESULT[] run(GUI frame, WindowInput wi)
    {
        if (frame == null || wi == null)
            VISUALS = false;

        boolean firstEnd = true;
        Types.RESULT[] results = null;

        while(!isEnded() || VISUALS && wi != null && !wi.windowClosed && !isEnded()) {
            // Loop while window is still open, even if the game ended.
            // If not playing with visuals, loop while the game's not ended.
            tick();

            // Check end of game
            if (firstEnd && isEnded()) {
                firstEnd = false;
                results = terminate();

                if (!VISUALS) {
                    // The game has ended, end the loop if we're running without visuals.
                    break;
                }
            }

            // Paint game state
            if (VISUALS && frame != null) {
                frame.paint();
                try {
                    Thread.sleep(FRAME_DELAY);
                } catch (Exception e) {
                    System.out.println("EXCEPTION " + e);
                }
            }
        }

        // The loop may have been broken out of before the game ended. Handle end-of-game:
        if (firstEnd) {
            results = terminate();
        }

        return results;
    }

    /**
     * Ticks the game forward. Asks agents for actions and applies returned actions to obtain the next game state.
     */
    void tick () {
        if (VERBOSE) {
            System.out.println("tick: " + gs.getTick());
        }

        Tribe[] tribes = gs.getTribes();
        for (int i = 0; i < players.size(); i++) {
            Tribe tribe = tribes[i];
            Agent ag = players.get(i);

            //play the full turn for this player
            processTurn(ag, tribe);

            //it may be that this player won the game, no more playing.
            if(isEnded())
            {
                return;
            }
        }

        //All turns passed, time to increase the tick.
        gs.incTick();
    }

    /**
     * Process a turn for a given player. It queries the player for an action until no more
     * actions are available or the player returns a EndTurnAction action.
     * @param ag agent to process the turn for.
     * @param tribe tribe that corresponds to this player.
     */
    void processTurn(Agent ag, Tribe tribe)
    {
        //compute the actions available for this player.
        gs.computePlayerActions(tribe);

        //start the timer to the max duration
        ElapsedCpuTimer ect = new ElapsedCpuTimer();
        ect.setMaxTimeMillis(TURN_TIME_MILLIS);

        while(gs.existAvailableActions(tribe) && !ect.exceededMaxTime())
        {
            //get one action from the player
            //TODO: gs.copy() is wrong. We have to use gameStateObservations[i]
            Action action = ag.act(gs.copy(), ect);

            //note down the remaining time to use it for the next iteration
            long remaining = ect.remainingTimeMillis();

            //play the action in the game and update the avaliable actions list
            gs.next(action);
            gs.computePlayerActions(tribe);

            updateAssignedGameStates();

            //the timer needs to be updated to the remaining time, not counting action computation.
            ect.setMaxTimeMillis(remaining);
        }
    }


    /**
     * This method terminates the game, assigning the winner/result state to all players.
     * @return an array of result states for all players.
     */
    @SuppressWarnings("UnusedReturnValue")
    private Types.RESULT[] terminate() {

        //Build the results array
        Tribe[] tribes = gs.getTribes();
        Types.RESULT[] results = new Types.RESULT[players.size()];
        for (int i = 0; i < players.size(); i++) {
            Tribe tribe = (Tribe) tribes[i];
            results[i] = tribe.getWinner();
        }

        // Call all agents' end-of-game method for post-processing. Agents receive their final reward.
        for (int i = 0; i < players.size(); i++) {
            Agent ag = players.get(i);
            ag.result(tribes[i].getScore());
        }

        return results;
    }


    /**
     * Updates the state observations for all players.
     */
    private void updateAssignedGameStates() {
        if (gameStateObservations == null) {
            gameStateObservations = new GameState[players.size()];
        }
        for (int i = 0; i < players.size(); i++) {
            gameStateObservations[i] = getGameState(i);
        }
    }

    /**
     * Returns the game state as seen for the player with the index playerIdx. This game state
     * includes only the observations that are visible if partial observability is enabled.
     * @param playerIdx index of the player for which the game state is generated.
     * @return the game state.
     */
    private GameState getGameState(int playerIdx) {
        return gs.copy(playerIdx);
    }

    /**
     * Method to identify the end of the game.
     * @return true if the game has ended, false otherwise.
     */
    boolean isEnded() {

        //TODO: Analyze the game state to find out if the game is over.

        return false;
    }

}
