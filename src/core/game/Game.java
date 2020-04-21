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
import org.json.JSONArray;
import org.json.JSONObject;
import players.Agent;
import players.HumanAgent;
import utils.ElapsedCpuTimer;
import utils.GUI;
import utils.Vector2d;
import utils.WindowInput;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Random;

import static core.Constants.*;

public class Game {

    private boolean FORCE_FULL_OBSERVABILITY = true;

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
     * @param gameMode Game Mode for this game.
     */
    public void init(ArrayList<Agent> players, ArrayList<Tribe> tribes, String filename, long seed, Types.GAME_MODE gameMode) {

        //Initiate the bare bones of the main game classes
        this.seed = seed;
        this.rnd = new Random(seed);
        this.gs = new GameState(rnd, gameMode);

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
     */
    public void run(GUI frame, WindowInput wi)
    {
        if (frame == null || wi == null)
            VISUALS = false;
        boolean firstEnd = true;

        while(!gameOver()) {
            // Loop while window is still open, even if the game ended.
            // If not playing with visuals, loop while the game's not ended.
            tick(frame);

            // Check end of game
            if (firstEnd && gameOver()) {
                firstEnd = false;

                if (!VISUALS) {
                    // The game has ended, end the loop if we're running without visuals.
                    break;
                }
            }
        }

        terminate();
        terminate();
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

            // Save Game
            saveGame();

            //it may be that this player won the game, no more playing.
            if(gameOver())
            {
                return;
            }
        }

        //All turns passed, time to increase the tick.
        gs.incTick();
    }

    public void saveGame(){
        try{
            File rootFileLoc = new File("save/" + this.seed);
            File turnFile = new File(rootFileLoc, gs.getTick() + "_" + gs.getBoard().getActiveTribeID());

            // Only create root file for first time
            if(gs.getTick() == 0 && gs.getActiveTribeID() == 0){
                //Create dictionary
                rootFileLoc.mkdirs();
            }


            turnFile.mkdir();

            // JSON
            JSONObject game = new JSONObject();

            // Board INFO (2D array) - Terrain, Resource, UnitID, CityID, NetWorks
            JSONObject board = new JSONObject();
            JSONArray terrain2D = new JSONArray();
            JSONArray terrain;

            JSONArray resource2D = new JSONArray();
            JSONArray resource;

            JSONArray unit2D = new JSONArray();
            JSONArray units;

            JSONArray city2D = new JSONArray();
            JSONArray cities;

            JSONArray network2D = new JSONArray();
            JSONArray networks;

            // Unit INFO: id:{type, x, y, kills, isVeteran, cityId, tribeId, HP}
            JSONObject unit = new JSONObject();

            // City INFO: id:{x, y, tribeId, population_need, bound, level, isCapital, population,
            //                production, hasWalls, pointsWorth, building(array)}
            JSONObject city = new JSONObject();

            // Building INFO: {x, y, type, level(optional), turnsToScore(optional), bonus}
            JSONObject building = new JSONObject();

            for (int i=0; i<getBoard().getSize(); i++){

                // Initial JSON Object for each row
                terrain = new JSONArray();
                resource = new JSONArray();
                units = new JSONArray();
                cities = new JSONArray();
                networks = new JSONArray();

                for(int j=0; j<getBoard().getSize(); j++){
                    // Save Terrain INFO
                    terrain.put(gs.getBoard().getTerrainAt(i, j).getKey());
                    // Save Resource INFO
                    resource.put(gs.getBoard().getResourceAt(i, j) != null? gs.getBoard().getResourceAt(i, j).getKey():-1);

                    // Save unit INFO
                    int unitINFO = gs.getBoard().getUnitIDAt(i, j);
                    units.put(unitINFO);
                    if (unitINFO != 0){
                        Unit u = (Unit)gs.getActor(unitINFO);
                        JSONObject uInfo = new JSONObject();
                        uInfo.put("type", u.getType().getKey());
                        uInfo.put("x", i);
                        uInfo.put("y", j);
                        uInfo.put("kill", u.getKills());
                        uInfo.put("isVeteran", u.isVeteran());
                        uInfo.put("cityID", u.getCityId());
                        uInfo.put("tribeId", u.getTribeId());
                        uInfo.put("currentHP", u.getCurrentHP());
                        unit.put(String.valueOf(unitINFO), uInfo);
                    }
                    // Save city INFO
                    int cityINFO = gs.getBoard().getCityIdAt(i, j);
                    cities.put(cityINFO);
                    if (cityINFO != -1){
                        City c = (City)gs.getActor(cityINFO);
                        // City INFO: id:{x, y, tribeId, population_need, bound, level, isCapital, population,
                        //                production, hasWalls, pointsWorth, building(array)}
                        JSONObject cInfo = new JSONObject();
                        cInfo.put("x", i);
                        cInfo.put("y", j);
                        cInfo.put("tribeID", c.getTribeId());
                        cInfo.put("population_need", c.getPopulation_need());
                        cInfo.put("bound", c.getBound());
                        cInfo.put("level", c.getLevel());
                        cInfo.put("isCapital", c.isCapital());
                        cInfo.put("population", c.getPopulation());
                        cInfo.put("production", c.getProduction());
                        cInfo.put("hasWalls", c.hasWalls());
                        cInfo.put("pointsWorth", c.getPointsWorth());
                        // Save Buildings INFO
                        JSONArray buildingList = new JSONArray();
                        LinkedList<Building> buildings = c.getBuildings();
                        if (buildings != null) {
                            for (Building b : buildings) {
                                JSONObject bInfo = new JSONObject();
                                bInfo.put("x", b.position.x);
                                bInfo.put("y", b.position.y);
                                bInfo.put("type", b.type.getKey());
                                bInfo.put("bonus", b.getBonus());
                                if (b.type == Types.BUILDING.TEMPLE || b.type == Types.BUILDING.WATER_TEMPLE || b.type == Types.BUILDING.FOREST_TEMPLE) {
                                    Temple t = (Temple) b;
                                    bInfo.put("level", t.getLevel());
                                    bInfo.put("turnsToScore", t.getTurnsToScore());
                                }
                                buildingList.put(bInfo);
                            }
                        }
                        cInfo.put("buildings", buildingList);
                        city.put(String.valueOf(unitINFO), cInfo);
                    }

                    // Save network INFO
                    networks.put(gs.getBoard().getNetworkTilesAt(i, j));

                }
                // Update row value
                terrain2D.put(terrain);
                resource2D.put(resource);
                unit2D.put(units);
                city2D.put(cities);
                network2D.put(networks);
            }

            board.put("terrain", terrain2D);
            board.put("resource", resource2D);
            board.put("unitID", unit2D);
            board.put("cityID", city2D);
            board.put("network", network2D);

            game.put("board", board);
            game.put("unit", unit);
            game.put("city", city);

            // Save Tribes Information () - id: {citiesID, capitalID, type, techTree, stars, winner, score, obsGrid,
            //                                   connectedCities, monuments:{type: status}, tribesMet, extraUnits,
            //                                   nKills, nPacifistCount}
            JSONObject tribesINFO = new JSONObject();
            Tribe[] tribes = gs.getTribes();
            for(Tribe t: tribes){
                JSONObject tribeInfo = new JSONObject();
                tribeInfo.put("citiesID", t.getCitiesID());
                tribeInfo.put("capitalID", t.getCapitalID());
                tribeInfo.put("type", t.getType().getKey());
                JSONObject techINFO = new JSONObject();
                techINFO.put("researched", t.getTechTree().getResearched());
                techINFO.put("everythingResearched", t.getTechTree().isEverythingResearched());
                tribeInfo.put("technology", techINFO);
                tribeInfo.put("star", t.getStars());
                tribeInfo.put("winner", t.getWinner().getKey());
                tribeInfo.put("score", t.getScore());
                tribeInfo.put("obsGrid", t.getObsGrid());
                tribeInfo.put("connectedCities", t.getConnectedCities());
                HashMap<Types.BUILDING, Types.BUILDING.MONUMENT_STATUS> m = t.getMonuments();
                JSONObject monumentInfo = new JSONObject();
                for (Types.BUILDING key : m.keySet()) {
                    monumentInfo.put(String.valueOf(key.getKey()), m.get(key).getKey());
                }
                tribeInfo.put("monuments", monumentInfo);
                JSONArray tribesMetInfo = new JSONArray();
                ArrayList<Integer> tribesMet= t.getTribesMet();
                for (Integer tribeId : tribesMet){
                    tribesMetInfo.put(tribeId);
                }
                tribeInfo.put("tribesMet", tribesMetInfo);
                tribeInfo.put("extraUnits", t.getExtraUnits());
                tribeInfo.put("nKills", t.getnKills());
                tribeInfo.put("nPacifistCount", t.getnPacifistCount());
                tribesINFO.put(String.valueOf(t.getActorId()), tribeInfo);
            }

            game.put("tribes", tribesINFO);
            game.put("seed", seed);
            game.put("tick", gs.getTick());
            game.put("activeTribeID", gs.getActiveTribeID());


//            JSONObject read = new JSONObject(game.toString());
//            System.out.println(read.get("board"));
//            System.out.println(read.get("board"));
//
//            JSONObject board_read = read.getJSONObject("board");
//            System.out.println(board_read.get("resource"));

            FileWriter fw_game = new FileWriter(turnFile.getPath() + "/game.json");
            fw_game.write(game.toString());
            fw_game.close();

        } catch (IOException e){
            e.printStackTrace();
        }
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
        int curActionCounter = 0;

        while(continueTurn)
        {
            //get one action from the player
            Action action = ag.act(gameStateObservations[playerID], ect);

//            System.out.println(gs.getTick() + " " + curActionCounter + " " + action + "; stars: " + gs.getBoard().getTribe(playerID).getStars());
            curActionCounter++;

            //note down the remaining time to use it for the next iteration
            long remaining = ect.remainingTimeMillis();

            //play the action in the game and update the available actions list
            gs.next(action);
            gs.computePlayerActions(tribe);

            updateAssignedGameStates();

            // Update GUI after every action
            // Paint game state
            if (VISUALS && frame != null) {
                if(FORCE_FULL_OBSERVABILITY)
                    frame.update(getGameState(-1));
                else
                    frame.update(gameStateObservations[gs.getActiveTribeID()]);        //Partial Obs
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
            acumProd = Math.max(0, acumProd); //Never have a negative amount of stars.
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
     * This method call all agents' end-of-game method for post-processing.
     * Agents receive their final game state and reward
     */
    @SuppressWarnings("UnusedReturnValue")
    private void terminate() {

        Tribe[] tribes = gs.getTribes();
        for (int i = 0; i < numPlayers; i++) {
            Agent ag = players[i];
            ag.result(gs.copy(), tribes[i].getScore());
        }
    }

    /**
     * Returns the winning status of all players.
     * @return the winning status of all players.
     */
    public Types.RESULT[] getWinnerStatus()
    {
        //Build the results array
        Tribe[] tribes = gs.getTribes();
        Types.RESULT[] results = new Types.RESULT[numPlayers];
        for (int i = 0; i < numPlayers; i++) {
            Tribe tribe = tribes[i];
            results[i] = tribe.getWinner();
        }
        return results;
    }

    /**
     * Returns the current scores of all players.
     * @return the current scores of all players.
     */
    public int[] getScores()
    {
        //Build the results array
        Tribe[] tribes = gs.getTribes();
        int[] scores = new int[numPlayers];
        for (int i = 0; i < numPlayers; i++) {
            scores[i] = tribes[i].getScore();
        }
        return scores;
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
     * Method to identify the end of the game. If the game is over, the winner is decided.
     * The winner of a game is determined by TribesConfig.GAME_MODE and TribesConfig.MAX_TURNS
     * @return true if the game has ended, false otherwise.
     */
    boolean gameOver() {
        return gs.gameOver();
    }

}
