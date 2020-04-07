package core.game;

import core.TribesConfig;
import core.Types;
import core.actions.Action;
import core.actions.unitactions.Recover;
import core.actions.unitactions.factory.RecoverFactory;
import core.actors.Building;
import core.actors.City;
import core.actors.Temple;
import core.actors.Tribe;
import core.actors.units.Unit;
import players.Agent;
import players.HumanAgent;
import utils.ElapsedCpuTimer;
import utils.GUI;
import utils.Vector2d;
import utils.WindowInput;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Random;

import static core.Constants.*;

public class Game {

    private boolean FORCE_FULL_OBSERVABILITY = false;

    // State of the game (objects, ticks, etc).
    private GameState gs;

    // GameState objects for players to make decisions
    private GameState[] gameStateObservations;

    // Seed for the game state.
    private long seed;

    //Random number generator for the game.
    private Random rnd;

    // List of players of the game
    private Agent[] players;

    //Number of players of the game.
    private int numPlayers;

    /**
     * Constructor of the game
     */
    public Game()
    {}

    /**
     * Initializes the game. This method does the following:
     *   Sets the players of the game, the number of players and their IDs
     *   Initializes the array to hold the player game states.
     *   Assigns the tribes that will play the game.
     *   Creates the board according to the above information and resets the game so it's ready to start.
     *   Turn order: by default, turns run following the order in the tribes array.
     * @param players Players of the game.
     * @param tribes Tribes to play the game with. Players and tribes related by position in array lists.
     * @param filename Name of the file with the level information.
     * @param seed Seed for the game (used only for board generation)
     */
    public void init(ArrayList<Agent> players, ArrayList<Tribe> tribes, String filename, long seed) {

        //Initiate the bare bones of the main game classes
        this.seed = seed;
        this.rnd = new Random(seed);
        this.gs = new GameState(rnd);

        if(players.size() != tribes.size())
        {
            System.out.println("ERROR: Number of tribes must equal the number of players.");
        }

        Tribe[] tribesArray = new Tribe[tribes.size()];
        for (int i = 0; i < tribesArray.length; ++i)
        {
            tribesArray[i] = tribes.get(i);
        }

        //Create the players and agents to control them
        numPlayers = players.size();
        this.players = new Agent[numPlayers];
        for(int i = 0; i < numPlayers; ++i)
        {
            this.players[i] = players.get(i);
            this.players[i].setPlayerID(i);
        }
        this.gameStateObservations = new GameState[numPlayers];

        //Assign the tribes to the players
//        this.gs.assignTribes(tribes);

        this.gs.init(filename, tribesArray);

        updateAssignedGameStates();
    }


//    /**
//     * Resets the game, providing a seed.
//     * @param repeatLevel true if the same level should be played.
//     * @param filename Name of the file with the level information.
//     */
//    public void reset(boolean repeatLevel, String filename)
//    {
//        this.seed = repeatLevel ? seed : System.currentTimeMillis();
//        resetGame(filename, numPlayers);
//    }
//
//    /**
//     * Resets the game, providing a seed.
//     * @param seed new seed for the game.
//     * @param filename Name of the file with the level information.
//     */
//    public void reset(int seed, String filename)
//    {
//        this.seed = seed;
//        resetGame(filename, numPlayers);
//    }

//    /**
//     * Resets the game, creating the original game state (and level) and assigning the initial
//     * game state views that each player will have.
//     * @param filename Name of the file with the level information.
//     */
//    private void resetGame(String filename)
//    {
//        this.gs.init(filename);
//        updateAssignedGameStates();
//    }




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

            // Check end of game
            if (firstEnd && isEnded()) {
                firstEnd = false;
                results = terminate();

                if (!VISUALS) {
                    // The game has ended, end the loop if we're running without visuals.
                    break;
                }
            }

//            System.out.println(gs.getTick());

            // Loop while window is still open, even if the game ended.
            // If not playing with visuals, loop while the game's not ended.
            tick(frame);

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
    private void tick (GUI frame) {
        if (VERBOSE) {
            //System.out.println("tick: " + gs.getTick());
        }

        Tribe[] tribes = gs.getTribes();
        for (int i = 0; i < numPlayers; i++) {
            Tribe tribe = tribes[i];

            //play the full turn for this player
            processTurn(i, tribe, frame);

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
    private void processTurn(int playerID, Tribe tribe, GUI frame)
    {
        //Init the turn for this tribe (stars, unit reset, etc).
        this.initTurn(tribe);

        //Take the player for this turn
        Agent ag = players[playerID];

        //start the timer to the max duration
        ElapsedCpuTimer ect = new ElapsedCpuTimer();
        ect.setMaxTimeMillis(TURN_TIME_MILLIS);
        boolean continueTurn = true;

        while(continueTurn)
        {
            //get one action from the player
            Action action = ag.act(gameStateObservations[playerID], ect);

            //note down the remaining time to use it for the next iteration
            long remaining = ect.remainingTimeMillis();

            //play the action in the game and update the available actions list
            gs.next(action);
            gs.computePlayerActions(tribe);

            updateAssignedGameStates();

            // Update GUI after every action
            // Paint game state
            if (VISUALS && frame != null) {

                // GUI might take several frames to update with animations,
                // wait for that to be done before doing next update. Using thread to run the update asynchronous,
                // while the next action is being computed
                while (!frame.nextMove()) {
                    try {
                        Thread.sleep(FRAME_DELAY);
                    } catch (Exception e) {
                        System.out.println("EXCEPTION " + e);
                    }
                }

                if(FORCE_FULL_OBSERVABILITY)
                    frame.update(getGameState(-1));
                else
                    frame.update(gameStateObservations[gs.getActiveTribeID()]);        //Partial Obs
                Thread gui = new Thread(frame);
                gui.start();
            }

            //the timer needs to be updated to the remaining time, not counting action computation.
            ect.setMaxTimeMillis(remaining);

            //Continue this turn if there are still available actions. If the agent is human, let him play for now.
            continueTurn = !gs.isTurnEnding();
            if(!(ag instanceof HumanAgent))
                continueTurn &= gs.existAvailableActions(tribe) && !ect.exceededMaxTime();
        }

        //Ends the turn for this tribe (units that didn't move heal).
        this.endTurn(tribe);
    }

    /**
     * Inits the turn for this player
     * @param tribe whose turn is starting
     */
    private void initTurn(Tribe tribe)
    {
        //Get all cities of this tribe
        ArrayList<Integer> tribeCities = tribe.getCitiesID();
        ArrayList<Integer> allTribeUnits = new ArrayList<>();
        gs.endTurn(false);

        //1. Compute stars per turn.
        int acumProd = 0;
        for (int cityId : tribeCities) {
            City city = (City) gs.getActor(cityId);

            //Cities with an enemy unit in the city's tile don't generate production.
            boolean produces = true;
            Vector2d cityPos = city.getPosition();
            int unitIDAt = gs.getBoard().getUnitIDAt(cityPos.x, cityPos.y);
            if (unitIDAt > 0) {
                Unit u = (Unit) gs.getActor(unitIDAt);
                produces = (u.getTribeId() == tribe.getTribeId());
            }

            if (produces)
                acumProd += city.getProduction();

            allTribeUnits.addAll(city.getUnitsID());

            //All temples grow;
            for(Building b : city.getBuildings())
            {
                if(b.type.isTemple()) {
                    int templePoints = ((Temple) b).score();
                    tribe.addScore(templePoints);
                    city.addPointsWorth(templePoints);
                }
            }
        }

        if(gs.getTick() == 0)
        {
            tribe.setScore(tribe.getType().getInitialScore());
            tribe.setStars(TribesConfig.INITIAL_STARS);
        }else{
            tribe.addStars(acumProd);
        }

        //2. Units: all become available. This needs to be done here as some units may have become
        // pushed during other player's turn.
        allTribeUnits.addAll(tribe.getExtraUnits());    //Add the extra units that don't belong to a city.
        for(int unitId : allTribeUnits)
        {
            Unit unit = (Unit) gs.getActor(unitId);
            if(unit.getStatus() == Types.TURN_STATUS.PUSHED)
                //Pushed units in the previous turn start as if they moved already.
                unit.setStatus(Types.TURN_STATUS.MOVED);
            else
                unit.setStatus(Types.TURN_STATUS.FRESH);
        }

        //3. Update tribe pacifist counter
        tribe.addPacifistCount();

        //4. Compute the actions available for this player and copy observations.
        gs.computePlayerActions(tribe);
        updateAssignedGameStates();
    }


    /**
     * Ends this turn. Executes a Recover action on all the units that are not fresh
     * @param tribe tribe whose turn is ending.
     */
    private void endTurn(Tribe tribe)
    {
        //For all units that didn't execute any action, a Recover action is executed.
        ArrayList<Integer> allTribeUnits = new ArrayList<>();
        ArrayList<Integer> tribeCities = tribe.getCitiesID();
        GameState gs = gameStateObservations[tribe.getActorId()];

        //1. Get all units
        for(int cityId : tribeCities)
        {
            City city = (City) gs.getActor(cityId);
            allTribeUnits.addAll(city.getUnitsID());
        }

        //Heal the ones that were in a FRESH state.
        allTribeUnits.addAll(tribe.getExtraUnits());    //Add the extra units that don't belong to a city.
        for(int unitId : allTribeUnits)
        {
            Unit unit = (Unit) gs.getActor(unitId);
            if(unit.getStatus() == Types.TURN_STATUS.FRESH)
            {
                LinkedList<Action> recoverActions = new RecoverFactory().computeActionVariants(unit, gs);
                if(recoverActions.size() > 0)
                {
                    Recover recoverAction = (Recover)recoverActions.get(0);
                    recoverAction.execute(gs);
                }
            }
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

    public Agent[] getPlayers() {
        return players;
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
