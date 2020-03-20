package core.game;

import core.actions.Action;
import core.actions.cityactions.factory.CityActionBuilder;
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
import java.util.LinkedList;
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
    private HashMap<City, ArrayList<Action>> cityActions;
    private HashMap<Unit, ArrayList<Action>> unitActions;
    private ArrayList<Action> tribeActions;

    /**
     * This variable indicates if the computed actions in this class are updated.
     * It will take the value of the tribeId for which the actions are computed, and -1 if they are
     * not computed or next() is called (as that makes the computed actions obsolete).
     */
    private int computedActionTribeIdFlag;


    //Constructor.
    public GameState(Random rnd) {
        this.rnd = rnd;
        computedActionTribeIdFlag = -1;
        this.cityActions = new HashMap<>();
        this.unitActions = new HashMap<>();
        this.tribeActions = new ArrayList<>();
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
            tribe.clearView(cityPos.x, cityPos.y);
        }

        canEndTurn = new boolean[tribes.length];

    }

    /**
     * Adds a new actor to the list of game actors
     * @param actor the actor to add
     * @return the unique identifier of this actor for the rest of the game.
     */
    public int addActor(Actor actor)
    {
        return board.addActor(actor);
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
        boolean done = false;
        int i = 0;

        while (!done && i < numCities)
        {
            City c = (City) board.getActor(cities.get(i));
            ArrayList<Action> actions = cab.getActions(this, c);

            if(actions.size() > 0)
            {
                cityActions.put(c, actions);
            }

            done = cab.cityLevelsUp();
            if(!done)
            {
                //TODO: This misses the converted units that do not belong to any city. FIX!!!
                LinkedList<Integer> unitIds = c.getUnitsID();
                allUnits.addAll(unitIds);
                i++;
            }
        }

        int activeTribeID = board.getActiveTribeID();
        if(done)
        {
            //A city is levelling up. We're done with this city.
            canEndTurn[activeTribeID] = false;
            return;
        }else{
            canEndTurn[activeTribeID] = true;
        }

        //Units!
        UnitActionBuilder uab = new UnitActionBuilder();
        for(Integer unitId : allUnits)
        {
            Unit u = (Unit) board.getActor(unitId);
            ArrayList<Action> actions = uab.getActions(this, u);
            if(actions.size() > 0)
                unitActions.put(u, actions);
        }

        //This tribe
        TribeActionBuilder tab = new TribeActionBuilder();
        ArrayList<Action> actions = tab.getActions(this, tribe);
        tribeActions.addAll(actions);

        int a = 0;
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
        //TODO: MAIN function of this class.
        // Takes the action passed as parameter and runs it in the game.

        //At least it'll have these two things:
        if(action != null)
        {
            action.execute(this);
            computedActionTribeIdFlag = -1;
        }
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
        return board;
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

        int numTribes = getTribes().length;
        copy.canEndTurn = new boolean[numTribes];
        for(int i = 0; i < numTribes; ++i)
            copy.canEndTurn[i] = canEndTurn[i];


        return copy;
    }

    public boolean canEndTurn(int tribeId)
    {
        return canEndTurn[tribeId];
    }

    /**
     * The player has decided to end the turn
     * @param tribeId
     */
    public void endTurn(int tribeId)
    {
        //TODO: need to manage a turn ending here.
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


    public HashMap<City, ArrayList<Action>> getCityActions() {
        return cityActions;
    }

    public HashMap<Unit, ArrayList<Action>> getUnitActions() {
        return unitActions;
    }

    public ArrayList<Action> getTribeActions() {
        return tribeActions;
    }

    public ArrayList<Action> getCityActions(City c) {
        return cityActions.get(c);
    }

    public ArrayList<Action> getUnitActions(Unit u) {
        return unitActions.get(u);
    }

}
