package core.game;

import core.TribesConfig;
import core.actions.Action;
import core.actions.cityactions.factory.CityActionBuilder;
import core.actions.tribeactions.EndTurn;
import core.actions.tribeactions.factory.TribeActionBuilder;
import core.actions.unitactions.factory.UnitActionBuilder;
import core.actors.Actor;
import core.actors.City;
import core.actors.Tribe;
import core.actors.units.Unit;
import utils.IO;
import utils.Vector2d;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class GameState {

    // Random generator for the game state.
    private Random rnd;

    // Current tick of the game.
    private int tick = 0;

    // Board of the game
    private Board board;

    //Indicates if this tribe can end its turn.
    private boolean[] canEndTurn;

    //Actions per city, unit and tribe. These are computed when computePlayerActions() is called
    private HashMap<Integer, ArrayList<Action>> cityActions;
    private HashMap<Integer, ArrayList<Action>> unitActions;
    private ArrayList<Action> tribeActions;

    //Flags the state to indicate that the turn must end
    private boolean turnMustEnd;

    /**
     * This variable indicates if the computed actions in this class are updated.
     * It will take the value of the tribeId for which the actions are computed, and -1 if they are
     * not computed or next() is called (as that makes the computed actions obsolete).
     */
    private int computedActionTribeIdFlag;

    private boolean levelingUp;  // Indicates if a city is leveling up, which reduces action list to only 2 options

    //Constructor.
    public GameState(Random rnd) {
        this.rnd = rnd;
        computedActionTribeIdFlag = -1;
        this.cityActions = new HashMap<>();
        this.unitActions = new HashMap<>();
        this.tribeActions = new ArrayList<>();
        this.turnMustEnd = false;
    }

    /**
     * Initializes the GameState.
     * The level is only generated when this initialization method is called.
     */
    void init(String filename, Tribe[] tribes) {

        String[] lines = new IO().readFile(filename);
        LevelLoader ll = new LevelLoader();
        board = ll.buildLevel(tribes, lines, rnd);

        for(Tribe tribe : tribes)
        {
            int startingCityId = tribe.getCitiesID().get(0);
            City c = (City) board.getActor(startingCityId);
            Vector2d cityPos = c.getPosition();
            tribe.clearView(cityPos.x, cityPos.y, TribesConfig.FIRST_CITY_CLEAR_RANGE, rnd, board);
        }

        canEndTurn = new boolean[tribes.length];

    }

    /**
     * Gets a game actor from its id.
     * @param actorId the id of the actor to retrieve
     * @return the actor, null if the id doesn't correspond to an actor (note that it may have
     * been deleted if the actor was removed from the game).
     */
    public Actor getActor(int actorId)
    {
        return board.getActor(actorId);
    }

    /**
     * Removes an actor from the list of actor
     * @param actorId id of the actor to remove
     * @return true if the actor was removed (false may indicate that it didn't exist).
     */
    public boolean removeActor(int actorId)
    {
        return board.removeActor(actorId);
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
    void incTick()
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
        board.setActiveTribeID(tribe.getTribeId());

        if(computedActionTribeIdFlag != -1 && computedActionTribeIdFlag == tribe.getTribeId())
        {
            //Actions already computed and next() hasn't been called. No need to recompute again.
            return;
        }

        computedActionTribeIdFlag = tribe.getTribeId();
        this.cityActions = new HashMap<>();
        this.unitActions = new HashMap<>();
        this.tribeActions = new ArrayList<>();

        ArrayList<Integer> cities = tribe.getCitiesID();
        ArrayList<Integer> allUnits = new ArrayList<>();
        CityActionBuilder cab = new CityActionBuilder();

        int numCities = cities.size();
        int i = 0;
        levelingUp = false;

        while (!levelingUp && i < numCities)
        {
            int cityId = cities.get(i);
            City c = (City) board.getActor(cityId);
            ArrayList<Action> actions = cab.getActions(this, c);
            levelingUp = cab.cityLevelsUp();

            if(actions.size() > 0)
            {
                if(levelingUp)
                {
                    //We may have already processed other cities. Actions for those should be eliminated.
                    cityActions.clear();
                }
                cityActions.put(cityId, actions);
            }

            if(!levelingUp)
            {
                ArrayList<Integer> unitIds = c.getUnitsID();
                allUnits.addAll(unitIds);
                i++;
            }
        }

        int activeTribeID = board.getActiveTribeID();
        if(levelingUp)
        {
            //A city is levelling up. We're done with this city.
            canEndTurn[activeTribeID] = false;
            return;
        }else{
            canEndTurn[activeTribeID] = true;
        }

        //Add the extra units that don't belong to any city.
        allUnits.addAll(tribe.getExtraUnits());

        //Units!
        UnitActionBuilder uab = new UnitActionBuilder();
        for(Integer unitId : allUnits)
        {
            Unit u = (Unit) board.getActor(unitId);
            ArrayList<Action> actions = uab.getActions(this, u);
            if(actions.size() > 0)
                unitActions.put(unitId, actions);
        }

        //This tribe
        TribeActionBuilder tab = new TribeActionBuilder();
        ArrayList<Action> actions = tab.getActions(this, tribe);
        tribeActions.addAll(actions);
    }

    /**
     * Checks if there are actions that the given tribe can take.
     * @param tribe to check if can execute actions.
     * @return true if actions exist. False if no actions available
     * (that includes if this is not this tribe's turn)
     */
    public boolean existAvailableActions(Tribe tribe)
    {
        int tribeId = tribe.getTribeId();
        if(board.getActiveTribeID() != tribeId) //Not sure if this is needed, actually.
            return false;

        //Just one action for a city or a unit makes this question false.
        int nActions = 0;
        for(int cityId : cityActions.keySet())
        {
            nActions += cityActions.get(cityId).size();
            if(nActions>0) return true;
        }
        for(int cityId : unitActions.keySet()) {
            nActions += unitActions.get(cityId).size();
            if(nActions>0) return true;
        }

        //No city or unit actions - if there's only one (EndTurn) tribe action, there are no actions available.
        return tribeActions.size() != 1 || !(tribeActions.get(0) instanceof EndTurn);
    }

    /**
     * Advances the game state applying a single action received.
     * @param action to be executed in the current game state.
     */
    public void next(Action action)
    {
        //At least it'll have these two things:
        if(action != null)
        {
            action.execute(this);

            //Post-action execution matters:

            //new actions may have become available, update the 'dirty' flag
            computedActionTribeIdFlag = -1;
        }
    }

    /**
     * Pushes a unit following the game rules. If the unit can't be pushed, destroys it.
     * @param toPush unit to push
     * @param startX initial x position
     * @param startY initial y position.
     */
    public void pushUnit(Unit toPush, int startX, int startY)
    {
        Tribe tribe = getTribe(toPush.getTribeId());
        boolean pushed = board.pushUnit(tribe, toPush, startX, startY, rnd);
        if(!pushed)
        {
            killUnit(toPush);
        }
    }

    /**
     * Kills a unit from the game, removing it from the board, its original city and substracting game score.
     * @param toKill unit to Kill
     */
    public void killUnit(Unit toKill)
    {
        board.removeUnitFromBoard(toKill);
        City c = (City) getActor(toKill.getCityId());
        Tribe tribe = getTribe(toKill.getTribeId());
        board.removeUnitFromCity(toKill, c, tribe);
        Tribe t = getTribe(toKill.getTribeId());
        t.subtractScore(toKill.getType().getPoints());
    }


    /**
     * Public accessor to the copy() functionality of this state.
     * @return a copy of the current game state.
     */
    public GameState copy() {
        return copy(-1);  // No reduction happening if no index specified
    }

    /**
     * Creates a deep copy of this game state, given player index. Sets up the game state so that it contains
     * only information available to the given player. If -1, state contains all information.
     * @param playerIdx player index that indicates who is this copy for.
     * @return a copy of this game state.
     */
    public GameState copy(int playerIdx)
    {
        GameState copy = new GameState(new Random()); //copies of the game state can't have the same random generator.
        copy.board = board.copy(playerIdx!=-1, playerIdx);
        copy.tick = this.tick;
        copy.turnMustEnd = turnMustEnd;

        int numTribes = getTribes().length;
        copy.canEndTurn = new boolean[numTribes];
        System.arraycopy(canEndTurn, 0, copy.canEndTurn, 0, numTribes);
        copy.levelingUp = levelingUp;

        copy.tribeActions = new ArrayList<>();
        for(Action ta : tribeActions)
        {
            copy.tribeActions.add(ta.copy());
        }

        copy.unitActions = new HashMap<>();
        for(int unitId : unitActions.keySet())
        {
            ArrayList<Action> actions = unitActions.get(unitId);
            ArrayList<Action> actionsC = new ArrayList<>();
            for(Action act : actions)
            {
                actionsC.add(act.copy());
            }
            copy.unitActions.put(unitId, actionsC);
        }

        copy.cityActions = new HashMap<>();
        for(int cityId : cityActions.keySet())
        {
            ArrayList<Action> actions = cityActions.get(cityId);
            ArrayList<Action> actionsC = new ArrayList<>();
            for(Action act : actions)
            {
                actionsC.add(act.copy());
            }
            copy.cityActions.put(cityId, actionsC);
        }

        return copy;
    }

    public boolean canEndTurn(int tribeId)
    {
        return canEndTurn[tribeId];
    }

    /**
     * Sets the flag for turning ending to 'endTurn'
     * @param endTurn true if the turn must end
     */
    public void endTurn(boolean endTurn)
    {
        turnMustEnd = endTurn;
    }

    public boolean isTurnEnding()
    {
        return turnMustEnd;
    }

    /**
     * Gets the tribes playing this game.
     * @return the tribes
     */
    public Tribe[] getTribes()
    {
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


    /**
     * Gets the tribe tribeId playing this game.
     * @param tribeID ID of the tribe to pick
     * @return the tribe with the ID requested
     */
    public Tribe getTribe(int tribeID)
    {
        return board.getTribes()[tribeID];
    }

    public Tribe getActiveTribe() {
        int activeTribeID = board.getActiveTribeID();
        if (activeTribeID != -1) {
            return board.getTribe(activeTribeID);
        } else return null;
    }

    public int getActiveTribeID() {
        return board.getActiveTribeID();
    }

    public Random getRandomGenerator() {
        return rnd;
    }


    public HashMap<Integer, ArrayList<Action>> getCityActions() {
        return cityActions;
    }

    public HashMap<Integer, ArrayList<Action>> getUnitActions() {
        return unitActions;
    }

    public ArrayList<Action> getTribeActions() {
        return tribeActions;
    }

    public ArrayList<Action> getCityActions(City c) {
        return cityActions.get(c.getActorId());
    }

    public ArrayList<Action> getUnitActions(Unit u) {
        return unitActions.get(u.getActorId());
    }


    public ArrayList<Action> getCityActions(int cityId) {
        return cityActions.get(cityId);
    }

    public ArrayList<Action> getUnitActions(int unitId) {
        return unitActions.get(unitId);
    }


    public boolean isLevelingUp() {
        return levelingUp;
    }
}
