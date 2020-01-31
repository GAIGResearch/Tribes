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

    // List of players of the game
    private Agent[] players;

    //Number of players of the game.
    private int numPlayers;

    public Game()
    {}

    /**
     * Constructor of the game
     * @param seed Seed for the game (used only for board generation)
     */
    public Game(long seed) {
        this.seed = seed;
        this.gs = new GameState(seed);
    }


    /**
     * Initializes the game. This method does the following:
     *   Sets the players of the game, the number of players and their IDs
     *   Initializes the array to hold the player game states.
     *   Assigns the tribes that will play the game.
     *   Creates the board according to the above information and resets the game so it's ready to start.
     * @param players Players of the game.
     * @param tribes Tribes to play the game with. Players and tribes related by position in array lists.
     * @param filename Name of the file with the level information.
     */
    public void init(ArrayList<Agent> players, ArrayList<Tribe> tribes, String filename) {

        if(players.size() != tribes.size())
        {
            System.out.printf("ERROR: Number of tribes must equal the number of players.");
        }

        numPlayers = players.size();
        this.players = new Agent[numPlayers];
        for(int i = 0; i < numPlayers; ++i)
        {
            this.players[i] = players.get(i);
            this.players[i].setPlayerID(i);
        }
        this.gameStateObservations = new GameState[numPlayers];
        this.gs.assignTribes(tribes);

        resetGame(filename);
    }


    /**
     * Resets the game, providing a seed.
     * @param repeatLevel true if the same level should be played.
     * @param filename Name of the file with the level information.
     */
    public void reset(boolean repeatLevel, String filename)
    {
        this.seed = repeatLevel ? seed : System.currentTimeMillis();
        resetGame(filename);
    }

    /**
     * Resets the game, providing a seed.
     * @param seed new seed for the game.
     * @param filename Name of the file with the level information.
     */
    public void reset(int seed, String filename)
    {
        this.seed = seed;
        resetGame(filename);
    }

    /**
     * Resets the game, creating the original game state (and level) and assigning the initial
     * game state views that each player will have.
     * @param filename Name of the file with the level information.
     */
    private void resetGame(String filename)
    {
        this.gs.init(filename);
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
                frame.paint(getBoard());
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
        for (int i = 0; i < numPlayers; i++) {
            Tribe tribe = tribes[i];

            //play the full turn for this player
            processTurn(i, tribe);

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
     * @param playerID ID of the player whose turn is being processed.
     * @param tribe tribe that corresponds to this player.
     */
    void processTurn(int playerID, Tribe tribe)
    {
        //Take the player for this turn
        Agent ag = players[playerID];

        //compute the actions available for this player.
        gs.computePlayerActions(tribe);

        //start the timer to the max duration
        ElapsedCpuTimer ect = new ElapsedCpuTimer();
        ect.setMaxTimeMillis(TURN_TIME_MILLIS);

        while(gs.existAvailableActions(tribe) && !ect.exceededMaxTime())
        {
            //get one action from the player
            Action action = ag.act(gameStateObservations[playerID], ect);

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
        Types.RESULT[] results = new Types.RESULT[numPlayers];
        for (int i = 0; i < numPlayers; i++) {
            Tribe tribe = (Tribe) tribes[i];
            results[i] = tribe.getWinner();
        }

        // Call all agents' end-of-game method for post-processing. Agents receive their final reward.
        for (int i = 0; i < numPlayers; i++) {
            Agent ag = players[i];
            ag.result(tribes[i].getScore());
        }

        return results;
    }


    /**
     * Updates the state observations for all players with copies of the
     * current game state, adapted for PO.
     */
    private void updateAssignedGameStates() {

        for (int i = 0; i < numPlayers; i++) {
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
     * Returns the game board.
     * @return the game board.
     */
    public Board getBoard()
    {
        return gs.getBoard();
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
