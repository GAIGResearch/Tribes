package core.game;

import core.TechnologyTree;
import core.TribesConfig;
import core.Types;
import core.actions.Action;
import core.actions.cityactions.factory.CityActionBuilder;
import core.actions.tribeactions.EndTurn;
import core.actions.tribeactions.factory.TribeActionBuilder;
import core.actions.unitactions.Recover;
import core.actions.unitactions.Attack;
import core.actions.unitactions.factory.RecoverFactory;
import core.actions.unitactions.factory.UnitActionBuilder;
import core.actors.*;
import core.actors.units.Unit;
import core.levelgen.LevelGenerator;
import utils.IO;
import utils.Vector2d;
import core.FMLearner.NN;
import core.Types;
import java.util.stream.*;


import java.util.*;

public class GameState {

    //Game mode
    private Types.GAME_MODE gameMode;

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

    //Indicates if the game is over.
    private boolean gameIsOver;

    //TribesConfig object
    private TribesConfig tc = new TribesConfig();

    /**
     * This variable indicates if the computed actions in this class are updated.
     * It will take the value of the tribeId for which the actions are computed, and -1 if they are
     * not computed or next() is called (as that makes the computed actions obsolete).
     */
    private int computedActionTribeIdFlag;

    // Indicates if a city is leveling up, which reduces action list to only 2 options
    private boolean levelingUp;

    //Ranking of the game
    private TreeSet<TribeResult> ranking;

    public boolean isCopy;


    //Constructor.
    public GameState(Random rnd, Types.GAME_MODE gameMode, boolean isCopy) {
        this.rnd = rnd;
        this.gameMode = gameMode;
        computedActionTribeIdFlag = -1;
        this.cityActions = new HashMap<>();
        this.unitActions = new HashMap<>();
        this.tribeActions = new ArrayList<>();
        this.ranking = new TreeSet<>();
        this.turnMustEnd = false;
        this.gameIsOver = false;
        this.isCopy = isCopy;
    }

    //This Constructor is used when loading from a savegame.
    public GameState(Random rnd, Types.GAME_MODE gameMode, Tribe[] tribes, Board board, int tick){
        this(rnd, gameMode, false);
        this.tick = tick;
        this.board = board;
        board.setTribes(tribes);

        if (board.getActiveTribeID() == tribes.length-1){
            this.tick++;
            board.setActiveTribeID(0);
        }else{
            board.setActiveTribeID(board.getActiveTribeID() + 1);
        }

        canEndTurn = new boolean[tribes.length];

        computePlayerActions(tribes[board.getActiveTribeID()]);
        this.isCopy = false;
    }

    /**
     * Initializes the GameState using a level generator.
     */
    void init(long levelgen_seed, Types.TRIBE[] tribes) {

        LevelGenerator levelGen = new LevelGenerator(levelgen_seed);
        levelGen.init(tc.DEFAULT_MAP_SIZE[tribes.length-1], 3, 4, 0.5, tribes);
        levelGen.generate();
        String[] lines = levelGen.gelLevelLines();
        initGameState(lines);
    }

    /**
     * Initializes the GameState from a file with the board information.
     */
    void init(String filename) {
        String[] lines = new IO().readFile(filename);
        initGameState(lines);
    }

    public void changeTribesConfig(double distance, long seed){
        if(distance ==0d)
            return; //do nothing to change tribesconfig

        Random random = new Random();
        random.setSeed(seed);
        //Set warrior values
        if(random.nextDouble() <= distance)
            tc.WARRIOR_ATTACK = random.nextInt(10);
//        if(random.nextDouble() <= distance)
//            tc.WARRIOR_DEFENCE = random.nextInt(10);
//        if(random.nextDouble() <= distance)
//            tc.WARRIOR_MOVEMENT = random.nextInt(10);
//        if(random.nextDouble() <= distance)
//            tc.WARRIOR_MAX_HP = random.nextInt(10);;
//        if(random.nextDouble() <= distance)
//            tc.WARRIOR_RANGE = random.nextInt(10);
//        if(random.nextDouble() <= distance)
//            tc.WARRIOR_COST = random.nextInt(10);
//        if(random.nextDouble() <= distance)
//            tc.WARRIOR_POINTS = random.nextInt(10);

//        //Set archer values
        if(random.nextDouble() <= distance)
            tc.ARCHER_ATTACK = random.nextInt(10);
//        if(random.nextDouble() <= distance)
//            tc.ARCHER_DEFENCE = random.nextInt(10);
//        if(random.nextDouble() <= distance)
//            tc.ARCHER_MOVEMENT = random.nextInt(10);
//        if(random.nextDouble() <= distance)
//            tc.ARCHER_MAX_HP = random.nextInt(10);
//        if(random.nextDouble() <= distance)
//            tc.ARCHER_RANGE = random.nextInt(10);
//        if(random.nextDouble() <= distance)
//            tc.ARCHER_COST = random.nextInt(10);
//        if(random.nextDouble() <= distance)
//            tc.ARCHER_POINTS = random.nextInt(10);

//        //Set catapult values
        if(random.nextDouble() <= distance)
            tc.CATAPULT_ATTACK = random.nextInt(10);
//        if(random.nextDouble() <= distance)
//            tc.CATAPULT_DEFENCE = random.nextInt(10);
//        if(random.nextDouble() <= distance)
//            tc.CATAPULT_MOVEMENT = random.nextInt(10);
//        if(random.nextDouble() <= distance)
//            tc.CATAPULT_MAX_HP = random.nextInt(10);
//        if(random.nextDouble() <= distance)
//            tc.CATAPULT_RANGE = random.nextInt(10);
//        if(random.nextDouble() <= distance)
//            tc.CATAPULT_COST = random.nextInt(10);
//        if(random.nextDouble() <= distance)
//            tc.CATAPULT_POINTS = random.nextInt(10);

//        //Set Swordman values
        if(random.nextDouble() <= distance)
            tc.SWORDMAN_ATTACK = random.nextInt(10);
//        if(random.nextDouble() <= distance)
//            tc.SWORDMAN_DEFENCE = random.nextInt(10);
//        if(random.nextDouble() <= distance)
//            tc.SWORDMAN_MOVEMENT = random.nextInt(10);
//        if(random.nextDouble() <= distance)
//            tc.SWORDMAN_MAX_HP = random.nextInt(10);;
//        if(random.nextDouble() <= distance)
//            tc.SWORDMAN_RANGE = random.nextInt(10);
//        if(random.nextDouble() <= distance)
//            tc.SWORDMAN_COST = random.nextInt(10);
//        if(random.nextDouble() <= distance)
//            tc.SWORDMAN_POINTS = random.nextInt(10);

//        //Set mindbender values
        if(random.nextDouble() <= distance)
            tc.MINDBENDER_ATTACK = random.nextInt(10);
//        if(random.nextDouble() <= distance)
//            tc.MINDBENDER_DEFENCE = random.nextInt(10);
//        if(random.nextDouble() <= distance)
//            tc.MINDBENDER_MOVEMENT = random.nextInt(10);
//        if(random.nextDouble() <= distance)
//            tc.MINDBENDER_MAX_HP = random.nextInt(10);;
//        if(random.nextDouble() <= distance)
//            tc.MINDBENDER_MAX_HP = random.nextInt(10);
//        if(random.nextDouble() <= distance)
//            tc.MINDBENDER_COST = random.nextInt(10);
//        if(random.nextDouble() <= distance)
//            tc.MINDBENDER_POINTS = random.nextInt(10);

//        //Set defender values
        if(random.nextDouble() <= distance)
            tc.DEFENDER_ATTACK = random.nextInt(10);
//        if(random.nextDouble() <= distance)
//            tc.DEFENDER_DEFENCE = random.nextInt(10);
//        if(random.nextDouble() <= distance)
//            tc.DEFENDER_MOVEMENT = random.nextInt(10);
//        if(random.nextDouble() <= distance)
//            tc.DEFENDER_MAX_HP = random.nextInt(10);;
//        if(random.nextDouble() <= distance)
//            tc.DEFENDER_RANGE = random.nextInt(10);
//        if(random.nextDouble() <= distance)
//            tc.DEFENDER_COST = random.nextInt(10);
//        if(random.nextDouble() <= distance)
//            tc.DEFENDER_POINTS = random.nextInt(10);

//        //Set Knight values
        if(random.nextDouble() <= distance)
            tc.KNIGHT_ATTACK = random.nextInt(10);
//        if(random.nextDouble() <= distance)
//            tc.KNIGHT_DEFENCE = random.nextInt(10);
//        if(random.nextDouble() <= distance)
//            tc.KNIGHT_MOVEMENT = random.nextInt(10);
//        if(random.nextDouble() <= distance)
//            tc.KNIGHT_MAX_HP = random.nextInt(10);;
//        if(random.nextDouble() <= distance)
//            tc.KNIGHT_RANGE = random.nextInt(10);
//        if(random.nextDouble() <= distance)
//            tc.KNIGHT_COST = random.nextInt(10);
//        if(random.nextDouble() <= distance)
//            tc.KNIGHT_POINTS = random.nextInt(10);

//        //Set Rider values
        if(random.nextDouble() <= distance)
            tc.RIDER_ATTACK = random.nextInt(10);
//        if(random.nextDouble() <= distance)
//            tc.RIDER_DEFENCE = random.nextInt(10);
//        if(random.nextDouble() <= distance)
//            tc.RIDER_MOVEMENT = random.nextInt(10);
//        if(random.nextDouble() <= distance)
//            tc.RIDER_MAX_HP = random.nextInt(10);;
//        if(random.nextDouble() <= distance)
//            tc.RIDER_RANGE = random.nextInt(10);
//        if(random.nextDouble() <= distance)
//            tc.RIDER_COST = random.nextInt(10);
//        if(random.nextDouble() <= distance)
//            tc.RIDER_POINTS = random.nextInt(10);
//
//        //Set Boat values
        if(random.nextDouble() <= distance)
            tc.BOAT_ATTACK = random.nextInt(10);
//        if(random.nextDouble() <= distance)
//            tc.BOAT_DEFENCE = random.nextInt(10);
//        if(random.nextDouble() <= distance)
//            tc.BOAT_MOVEMENT = random.nextInt(10);
//        if(random.nextDouble() <= distance)
//            tc.BOAT_RANGE = random.nextInt(10);
//        if(random.nextDouble() <= distance)
//            tc.BOAT_COST = random.nextInt(10);
//        if(random.nextDouble() <= distance)
//            tc.BOAT_POINTS = random.nextInt(10);

//        //Set Ship values
        if(random.nextDouble() <= distance)
            tc.SHIP_ATTACK = random.nextInt(10);
//        if(random.nextDouble() <= distance)
//            tc.SHIP_DEFENCE = random.nextInt(10);
//        if(random.nextDouble() <= distance)
//            tc.SHIP_MOVEMENT = random.nextInt(10);
//        if(random.nextDouble() <= distance)
//            tc.SHIP_RANGE = random.nextInt(10);
//        if(random.nextDouble() <= distance)
//            tc.SHIP_COST = random.nextInt(10);
//        if(random.nextDouble() <= distance)
//            tc.SHIP_POINTS = random.nextInt(10);

//        //Set Battleship values
        if(random.nextDouble() <= distance)
            tc.BATTLESHIP_ATTACK = random.nextInt(10);
//        if(random.nextDouble() <= distance)
//            tc.BATTLESHIP_DEFENCE = random.nextInt(10);
//        if(random.nextDouble() <= distance)
//            tc.BATTLESHIP_MOVEMENT = random.nextInt(10);
//        if(random.nextDouble() <= distance)
//            tc.BATTLESHIP_RANGE = random.nextInt(10);
//        if(random.nextDouble() <= distance)
//            tc.BATTLESHIP_COST = random.nextInt(10);
//        if(random.nextDouble() <= distance)
//            tc.BATTLESHIP_POINTS = random.nextInt(10);

//        //Set Superunit values
        if(random.nextDouble() <= distance)
            tc.SUPERUNIT_ATTACK = random.nextInt(10);
//        if(random.nextDouble() <= distance)
//            tc.SUPERUNIT_DEFENCE = random.nextInt(10);
//        if(random.nextDouble() <= distance)
//            tc.SUPERUNIT_MOVEMENT = random.nextInt(10);
//        if(random.nextDouble() <= distance)
//            tc.SUPERUNIT_MAX_HP = random.nextInt(10);;
//        if(random.nextDouble() <= distance)
//            tc.SUPERUNIT_RANGE = random.nextInt(10);
//        if(random.nextDouble() <= distance)
//            tc.SUPERUNIT_COST = random.nextInt(10);
//        if(random.nextDouble() <= distance)
//            tc.SUPERUNIT_POINTS = random.nextInt(10);

//        //Explorer
//        if(random.nextDouble() <= distance)
//            tc.NUM_STEPS = random.nextInt(getBoard().getSize());

//        //General Unit constants
//        if(random.nextDouble() <= distance)
//            tc.ATTACK_MODIFIER =  0 + (10 - 0) * random.nextDouble();
//        if(random.nextDouble() <= distance)
//            tc.DEFENCE = 0 + (10 - 0) * random.nextDouble();
//        if(random.nextDouble() <= distance)
//            tc.DEFENCE_IN_WALLS =  0 + (10 - 0) * random.nextDouble();
//        if(random.nextDouble() <= distance)
//            tc.VETERAN_KILLS = random.nextInt(50);
//        if(random.nextDouble() <= distance)
//            tc.VETERAN_PLUS_HP = random.nextInt(10);
//        if(random.nextDouble() <= distance)
//            tc.RECOVER_PLUS_HP = random.nextInt();
//        if(random.nextDouble() <= distance)
//            tc.RECOVER_IN_BORDERS_PLUS_HP = random.nextInt(10);

//        /* BUILDINGS */

//        // Farm
//        if(random.nextDouble() <= distance)
//            tc.FARM_COST = random.nextInt(10);
//        if(random.nextDouble() <= distance)
//            tc.FARM_BONUS = random.nextInt();
//       // Types.RESOURCE FARM_RES_CONSTRAINT = Types.RESOURCE.CROPS;
//
////        //WindMill
//        if(random.nextDouble() <= distance)
//            tc.WIND_MILL_COST = random.nextInt(10);
//        if(random.nextDouble() <= distance)
//            tc.WIND_MILL_BONUS = random.nextInt(10);
//
////        // LumberHut
////
//        if(random.nextDouble() <= distance)
//            tc.LUMBER_HUT_COST = random.nextInt(10);
//        if(random.nextDouble() <= distance)
//            tc.LUMBER_HUT_BONUS = random.nextInt(10);
//
////        // SawMill
//        if(random.nextDouble() <= distance)
//            tc.SAW_MILL_COST = random.nextInt(10);
//        if(random.nextDouble() <= distance)
//            tc.SAW_MILL_BONUS = random.nextInt(10);
//
////        //Mine
//        if(random.nextDouble() <= distance)
//            tc.MINE_COST = random.nextInt(10);
//        if(random.nextDouble() <= distance)
//            tc.MINE_BONUS = random.nextInt(10);
//     //   Types.RESOURCE MINE_RES_CONSTRAINT = Types.RESOURCE.ORE;
//
////        // Forge
//        if(random.nextDouble() <= distance)
//            tc.FORGE_COST = random.nextInt(10);
//        if(random.nextDouble() <= distance)
//            tc.FORGE_BONUS = random.nextInt(10);
//
////        // Port
//        if(random.nextDouble() <= distance)
//            tc.PORT_COST = random.nextInt(10);
//        if(random.nextDouble() <= distance)
//            tc.PORT_BONUS = random.nextInt(50);
//        if(random.nextDouble() <= distance)
//            tc.PORT_TRADE_DISTANCE = random.nextInt(10); //Count includes destination port.
//
////        // Customs House
//        if(random.nextDouble() <= distance)
//            tc.CUSTOMS_COST = random.nextInt(10);
//        if(random.nextDouble() <= distance)
//            tc.CUSTOMS_BONUS = random.nextInt(10);
//
////        //Monuments
//        if(random.nextDouble() <= distance)
//            tc.MONUMENT_BONUS = random.nextInt(50);
//        if(random.nextDouble() <= distance)
//            tc.MONUMENT_POINTS = random.nextInt(250);
//        if(random.nextDouble() <= distance)
//            tc.EMPERORS_TOMB_STARS = random.nextInt(10);
//        if(random.nextDouble() <= distance)
//            tc.GATE_OF_POWER_KILLS = random.nextInt(10);
//        if(random.nextDouble() <= distance)
//            tc.GRAND_BAZAR_CITIES = random.nextInt(10);
//        if(random.nextDouble() <= distance)
//            tc.ALTAR_OF_PEACE_TURNS = random.nextInt(10);
//        if(random.nextDouble() <= distance)
//            tc.PARK_OF_FORTUNE_LEVEL = random.nextInt(10);
////
////
////        // Temple
//        if(random.nextDouble() <= distance)
//            tc.TEMPLE_COST = random.nextInt(10);
//        if(random.nextDouble() <= distance)
//            tc.TEMPLE_FOREST_COST = random.nextInt(10);
//        if(random.nextDouble() <= distance)
//            tc.TEMPLE_BONUS = random.nextInt(50);
//        if(random.nextDouble() <= distance)
//            tc.TEMPLE_TURNS_TO_SCORE = random.nextInt(50);
//         tc.TEMPLE_POINTS = new int[]{random.nextInt(100), random.nextInt(50), random.nextInt(50), random.nextInt(50), random.nextInt(150)};
////
////        //Resources
//        if(random.nextDouble() <= distance)
//            tc.ANIMAL_COST = random.nextInt(10);
//        if(random.nextDouble() <= distance)
//            tc.FISH_COST = random.nextInt(10);
//        if(random.nextDouble() <= distance)
//            tc.WHALES_COST = random.nextInt(10);
//        if(random.nextDouble() <= distance)
//            tc.FRUIT_COST = random.nextInt(10);
//        if(random.nextDouble() <= distance)
//            tc.ANIMAL_POP = random.nextInt(10);
//        if(random.nextDouble() <= distance)
//            tc.FISH_POP = random.nextInt(10);
//        if(random.nextDouble() <= distance)
//            tc.WHALES_STARS = random.nextInt(10);
//        if(random.nextDouble() <= distance)
//            tc.FRUIT_POP = random.nextInt(10);
////
////        // ROAD
//        if(random.nextDouble() <= distance)
//            tc.ROAD_COST = random.nextInt(10);
////
////        // City
//        if(random.nextDouble() <= distance)
//            tc.CITY_LEVEL_UP_WORKSHOP_PROD = random.nextInt(10);
//        if(random.nextDouble() <= distance)
//            tc.CITY_LEVEL_UP_RESOURCES = random.nextInt(10);
//        if(random.nextDouble() <= distance)
//            tc.CITY_LEVEL_UP_POP_GROWTH = random.nextInt(10);
//        if(random.nextDouble() <= distance)
//            tc.CITY_LEVEL_UP_PARK = random.nextInt(10);
//        if(random.nextDouble() <= distance)
//            tc.CITY_BORDER_POINTS = random.nextInt(10);
//        if(random.nextDouble() <= distance)
//            tc.PROD_CAPITAL_BONUS = random.nextInt(10);
//        if(random.nextDouble() <= distance)
//            tc.EXPLORER_CLEAR_RANGE = random.nextInt(10);
//        if(random.nextDouble() <= distance)
//            tc.FIRST_CITY_CLEAR_RANGE = random.nextInt(10);
//        if(random.nextDouble() <= distance)
//            tc.NEW_CITY_CLEAR_RANGE = random.nextInt(10);
//        if(random.nextDouble() <= distance)
//            tc.CITY_EXPANSION_TILES = random.nextInt(10);
//        if(random.nextDouble() <= distance)
//            tc.POINTS_PER_POPULATION = random.nextInt(10);
//
////
////        // Research
//        if(random.nextDouble() <= distance)
//            tc.TECH_BASE_COST = random.nextInt(10);
//        //public Types.TECHNOLOGY TECH_DISCOUNT = Types.TECHNOLOGY.PHILOSOPHY;
//        if(random.nextDouble() <= distance)
//            tc.TECH_DISCOUNT_VALUE = random.nextDouble();
////
////        /* TRIBES */
//        if(random.nextDouble() <= distance)
//            tc.INITIAL_STARS = random.nextInt(10);//1000;
////
////        /* ACTIONS */
//        if(random.nextDouble() <= distance)
//            tc.CLEAR_FOREST_STAR = random.nextInt(10);
//        if(random.nextDouble() <= distance)
//            tc.GROW_FOREST_COST = random.nextInt(10);
//        if(random.nextDouble() <= distance)
//            tc.BURN_FOREST_COST = random.nextInt(10);
//        if(random.nextDouble() <= distance)
//            tc.CLEAR_VIEW_POINTS = random.nextInt(100);
    }

    /**
     * Initializes a game state from a series of Strings that determine the initial level disposition
     * @param lines all components for the board in its initial state.
     */
    private void initGameState(String[] lines) {

        LevelLoader ll = new LevelLoader();
        board = ll.buildLevel(lines, rnd, this);

        Tribe[] tribes = board.getTribes();
        for(Tribe tribe : tribes)
        {
            int startingCityId = tribe.getCitiesID().get(0);
            City c = (City) board.getActor(startingCityId);
            Vector2d cityPos = c.getPosition();
            tribe.clearView(cityPos.x, cityPos.y, tc.FIRST_CITY_CLEAR_RANGE, rnd, board);
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
    void computePlayerActions(Tribe tribe)
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

        if(gameIsOver)
            return; // no actions available if the game is over


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
    boolean existAvailableActions(Tribe tribe)
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
    void next(Action action)
    {
        if(action != null)
        {
            if(action instanceof Attack){

                    //add test data
                    try {
                       // Unit attacker = (Unit) this.getActor(((Attack) action).getUnitId());
                        Unit defender = (Unit) this.getActor(((Attack) action).getTargetId());
                        if(defender.getType() == Types.UNIT.WARRIOR) {
                            NN.expectedValues.add((float) ((Attack) action).getAttackResults(this).getFirst());
                            NN.testDataCounter ++;
                        }

                        //  System.out.println(NN.expectedValues);

                    }catch (NullPointerException e){

                    }




            }

            boolean executed = action.execute(this);

            if(!executed) {
              //  System.out.println("Tick: " + this.tick + "; action [" + action + "] couldn't execute?");
                action.execute(this);
            }

            //Post-action execution matters:

            //new actions may have become available, update the 'dirty' flag
            computedActionTribeIdFlag = -1;
        }
    }

    /**
     * Advances the game state applying a single action received.
     * It may also compute the actions available for the next step.
     * It handles turn change if 'action' is an EndTurn action that can be executed.
     * @param action to be executed in the current game state.
     * @param computeActions true if actions available after action has been executed should be computed.
     */
    public void advance(Action action, boolean computeActions)
    {
        if(action != null)
        {
            boolean executed = action.execute(this);

            if(!executed) {
            //    System.out.println("FM: Action [" + action + "] couldn't execute?");
                action.execute(this);
            }

            if(executed) {


                //it's an end turn
                if(action instanceof EndTurn)
                {
                    //manage the end of this turn.
                    this.endTurn(getActiveTribe());

                    //the game may be over
                    gameOver();

                    //Advance player
                    if(!gameIsOver)
                    {
                        int curActiveTribeId = board.getActiveTribeID();
                        boolean playerFound = false;
                        while(!playerFound)
                        {
                            curActiveTribeId = (curActiveTribeId + 1) % canEndTurn.length;
                            if(board.getTribe(curActiveTribeId).getWinner() != Types.RESULT.LOSS)
                                playerFound = true;

                            if(curActiveTribeId == board.getActiveTribeID()) {
                                System.out.println("ForwardModel ERROR: this shouldn't happen (all players but " +
                                        board.getActiveTribeID() + " lost, but it's not game over?)");
                                gameOver();
                            }
                        }

                        board.setActiveTribeID(curActiveTribeId);

                        //Start the turn for the next tribe
                        this.initTurn(getActiveTribe());
                    }

                }

                if(action instanceof Attack){

                        // NN training and testing
                        if (this.isCopy) {
                            //add training data
                            try {
                                //boolean check = checkIfAllEqual(NN.testDataCounter);
                                //boolean check2 = checkIfAllEqual(NN.trainDataCounter);

                                if(NN.trainDataCounter == NN.testDataCounter){
                                    Unit attacker = (Unit) this.getActor(((Attack) action).getUnitId());
                                    try {
                                        NN.test((float) ((Attack) action).getAttackResults(this).getFirst());

                                    }catch (Exception e){

                                    }
                                  try {
                                //      System.out.println(NN.denormalise(NN.output[0]));
                                      if(isCopy) {
                                          if (attacker.getType() == Types.UNIT.WARRIOR)
                                              tc.WARRIOR_ATTACK = (int) NN.denormalise(NN.output[0]) / (attacker.getCurrentHP() / attacker.getMaxHP());

                                          if (attacker.getType() == Types.UNIT.RIDER)
                                              tc.RIDER_ATTACK = (int) NN.denormalise(NN.output[0]) / (attacker.getCurrentHP() / attacker.getMaxHP());

                                          if (attacker.getType() == Types.UNIT.DEFENDER)
                                              tc.DEFENDER_ATTACK = (int) NN.denormalise(NN.output[0]) / (attacker.getCurrentHP() / attacker.getMaxHP());

                                          if (attacker.getType() == Types.UNIT.SWORDMAN)
                                              tc.SWORDMAN_ATTACK = (int) NN.denormalise(NN.output[0]) / (attacker.getCurrentHP() / attacker.getMaxHP());

                                          if (attacker.getType() == Types.UNIT.ARCHER)
                                              tc.ARCHER_ATTACK = (int) NN.denormalise(NN.output[0]) / (attacker.getCurrentHP() / attacker.getMaxHP());

                                          if (attacker.getType() == Types.UNIT.CATAPULT)
                                              tc.CATAPULT_ATTACK = (int) NN.denormalise(NN.output[0]) / (attacker.getCurrentHP() / attacker.getMaxHP());

                                          if (attacker.getType() == Types.UNIT.KNIGHT)
                                              tc.KNIGHT_ATTACK = (int) NN.denormalise(NN.output[0]) / (attacker.getCurrentHP() / attacker.getMaxHP());

                                          if (attacker.getType() == Types.UNIT.MIND_BENDER)
                                              tc.MINDBENDER_ATTACK = (int) NN.denormalise(NN.output[0]) / (attacker.getCurrentHP() / attacker.getMaxHP());

                                          if (attacker.getType() == Types.UNIT.BOAT)
                                              tc.BOAT_ATTACK = (int) NN.denormalise(NN.output[0]) / (attacker.getCurrentHP() / attacker.getMaxHP());

                                          if (attacker.getType() == Types.UNIT.SHIP)
                                              tc.SHIP_ATTACK = (int) NN.denormalise(NN.output[0]) / (attacker.getCurrentHP() / attacker.getMaxHP());

                                          if (attacker.getType() == Types.UNIT.BATTLESHIP)
                                              tc.BATTLESHIP_ATTACK = (int) NN.denormalise(NN.output[0]) / (attacker.getCurrentHP() / attacker.getMaxHP());

                                          if (attacker.getType() == Types.UNIT.SUPERUNIT)
                                              tc.SUPERUNIT_ATTACK = (int) NN.denormalise(NN.output[0]) / (attacker.getCurrentHP() / attacker.getMaxHP());
                                      }
                                  }catch (Exception e){

                                  }
                                }
                                if(NN.trainDataCounter >= NN.testDataCounter) {
                                 //   Unit attacker = (Unit) this.getActor(((Attack) action).getUnitId());
                                    Unit defender = (Unit) this.getActor(((Attack) action).getTargetId());
                                    //add data for all unit types
                                    if(defender.getType() == Types.UNIT.WARRIOR) {
                                        NN.trainingData.add((float) ((Attack) action).getAttackResults(this).getFirst());
                                        NN.trainDataCounter ++;
                                    }


                                }
                            }catch (NullPointerException e){

                            }
                        }

                }

                computedActionTribeIdFlag = -1;
                if (computeActions)
                    this.computePlayerActions(getActiveTribe());
            }
        }
    }

    private boolean checkIfAllEqual(int[] array){
        boolean allEqual = true;
        for (int i =0; i<array.length -1; i++){
            if (array[i] != array[i+1]){
                return false;
            }
        }
        return allEqual;
    }

    /**
     * Ends this turn. Executes a Recover action on all the units that are not fresh
     * @param tribe tribe whose turn is ending.
     */
    void endTurn(Tribe tribe)
    {
        //For all units that didn't execute any action, a Recover action is executed.
        ArrayList<Integer> allTribeUnits = new ArrayList<>();
        ArrayList<Integer> tribeCities = tribe.getCitiesID();

        //1. Get all units
        for(int cityId : tribeCities)
        {
            City city = (City) getActor(cityId);
            allTribeUnits.addAll(city.getUnitsID());
        }

        //Heal the ones that were in a FRESH state.
        allTribeUnits.addAll(tribe.getExtraUnits());    //Add the extra units that don't belong to a city.
        for(int unitId : allTribeUnits)
        {
            Unit unit = (Unit) getActor(unitId);
            if(unit.getStatus() == Types.TURN_STATUS.FRESH)
            {
                LinkedList<Action> recoverActions = new RecoverFactory().computeActionVariants(unit, this);
                if(recoverActions.size() > 0)
                {
                    Recover recoverAction = (Recover)recoverActions.get(0);
                    recoverAction.execute(this);
                }
            }
        }
    }

    /**
     * Inits the turn for this player
     * @param tribe whose turn is starting
     */
    void initTurn(Tribe tribe)
    {
        //Get all cities of this tribe
        ArrayList<Integer> tribeCities = tribe.getCitiesID();
        ArrayList<Integer> allTribeUnits = new ArrayList<>();
        this.setEndTurn(false);

        //1. Compute stars per turn.
        int acumProd = 0;
        for (int cityId : tribeCities) {
            City city = (City) getActor(cityId);

            //Cities with an enemy unit in the city's tile don't generate production.
            boolean produces = true;
            Vector2d cityPos = city.getPosition();
            int unitIDAt = board.getUnitIDAt(cityPos.x, cityPos.y);
            if (unitIDAt > 0) {
                Unit u = (Unit) getActor(unitIDAt);
                produces = (u.getTribeId() == tribe.getTribeId());
            }

            if (produces)
                acumProd += city.getProduction();

            allTribeUnits.addAll(city.getUnitsID());

            //All temples grow;
            for(Building b : city.getBuildings())
            {
                if(b.type.isTemple()) {
                    int templePoints = ((Temple) b).newTurn();
                    tribe.addScore(templePoints);
                    city.addPointsWorth(templePoints);
                }
            }
        }

        if(tick == 0)
        {
            tribe.setScore(tribe.getType().getInitialScore());
            tribe.setStars(tc.INITIAL_STARS);
        }else{
            acumProd = Math.max(0, acumProd); //Never have a negative amount of stars.
            tribe.addStars(acumProd);
        }

        //2. Units: all become available. This needs to be done here as some units may have become
        // pushed during other player's turn.
        allTribeUnits.addAll(tribe.getExtraUnits());    //Add the extra units that don't belong to a city.
        for(int unitId : allTribeUnits)
        {
            Unit unit = (Unit) getActor(unitId);
            if(unit.getStatus() == Types.TURN_STATUS.PUSHED)
                //Pushed units in the previous turn start as if they moved already.
                unit.setStatus(Types.TURN_STATUS.MOVED);
            else
                unit.setStatus(Types.TURN_STATUS.FRESH);
        }

        //3. Update tribe pacifist counter
        tribe.addPacifistCount();
    }


    /**
     * Pushes a unit following the game rules. If the unit can't be pushed, destroys it.
     * @param toPush unit to push
     * @param startX initial x position
     * @param startY initial y position.
     */
    public void pushUnit(Unit toPush, int startX, int startY, GameState gs)
    {
        Tribe tribe = getTribe(toPush.getTribeId());
        boolean pushed = board.pushUnit(tribe, toPush, startX, startY, rnd,gs);
        if(!pushed)
        {
            killUnit(toPush);
        }
    }

    /**
     * Kills a unit from the game, removing it from the board, its original city and subtracting game score.
     * @param toKill unit to Kill
     */
    public void killUnit(Unit toKill)
    {
        board.removeUnitFromBoard(toKill);
        City c = (City) getActor(toKill.getCityId());
        Tribe tribe = getTribe(toKill.getTribeId());
        board.removeUnitFromCity(toKill, c, tribe);
        Tribe t = getTribe(toKill.getTribeId());
        t.subtractScore(toKill.getType().getPoints(toKill.getType().getKey(),this.getTribesConfig()));
    }


    /**
     * Public accessor to the copy() functionality of this state.
     * @return a copy of the current game state.
     */
    public GameState copy() {
        return copy(-1, 0d);  // No reduction happening if no index specified
    }

    /**
     * Creates a deep copy of this game state, given player index. Sets up the game state so that it contains
     * only information available to the given player. If -1, state contains all information.
     * @param playerIdx player index that indicates who is this copy for.
     * @return a copy of this game state.
     */
    public GameState copy(int playerIdx, double distance)
    {
//        GameState copy = new GameState(this.rnd, this.gameMode); //use this for a 100% repetition of the game based on random seed and game seed.
        GameState copy = new GameState(new Random(), this.gameMode, true); //copies of the game state can't have the same random generator.
        copy.board = board.copy(playerIdx!=-1, playerIdx);
        copy.tick = this.tick;
        copy.turnMustEnd = turnMustEnd;
        copy.gameIsOver = gameIsOver;
        copy.tc = tc.copy();
        //long seed = 1860142121111L; //possibly different seeds
        long seed = 1631159151187L;
        copy.changeTribesConfig(distance,seed);
        copy.isCopy = true;

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

        copy.ranking = new TreeSet<>();
        for(TribeResult tr : ranking) copy.ranking.add(tr.copy());

        return copy;
    }


    /**
     * Method to identify the end of the game. If the game is over, the winner is decided.
     * The winner of a game is determined by TribesConfig.GAME_MODE and gameMode.getMaxTurns()
     * @return true if the game has ended, false otherwise.
     */
    boolean gameOver() {
        int maxTurns = gameMode.getMaxTurns();
        boolean isEnded = false;
        int[] capitals = board.getCapitalIDs();

        if(gameMode == Types.GAME_MODE.CAPITALS) {
            //Game over if one tribe controls all capitals
            for (int i = 0; i < canEndTurn.length; ++i) {
                Tribe t = board.getTribe(i);

                //Already lost?
                if (t.getWinner() == Types.RESULT.LOSS)
                    continue;

                boolean winner = true;
                for (int cap : capitals) {
                    if (!t.getCitiesID().contains(cap)) {
                        winner = false;
                        break;
                    }
                }

                if (winner) {
                    //we have a winner: tribe t.
                    isEnded = true;
                    board.getTribe(i).setWinner(Types.RESULT.WIN);
                    break; //no need to go further, all the others have lost the game.
                }

            }
        }

        //Compute the current ranking
        computeGameRanking();

        if(gameMode == Types.GAME_MODE.SCORE) {
            int numNonLoss = 0;
            for (TribeResult tr : ranking) {
                int tribeId = tr.getId();
                Types.RESULT res = getTribe(tribeId).getWinner();
                if (res != Types.RESULT.LOSS)
                    numNonLoss++;
            }
            isEnded = numNonLoss <= 1;
        }

        //We need to set all the winning conditions for the tribes if the game is over.
        if(isEnded || tick > maxTurns)
        {
            boolean first = true;
            for(TribeResult tr : ranking)
            {
                int tribeId = tr.getId();
                Types.RESULT res = first? Types.RESULT.WIN : Types.RESULT.LOSS;
                board.getTribe(tribeId).setWinner (res);
                tr.setResult(res);
                first = false;
            }
            isEnded = true;
        }

        gameIsOver = isEnded;
        return isEnded;
    }

    /**
     * Computes the current game ranking based on the current state of the tribes.
     * Updates the field 'ranking' from GameState
     */
    public void computeGameRanking()
    {
        ranking = new TreeSet<>();
        for(int i = 0; i < canEndTurn.length; ++i)
        {
            Tribe t = board.getTribe(i);
            TribeResult tribeResult = new TribeResult(i, t.getWinner(), t.getScore(), t.getTechTree().getNumResearched(), t.getNumCities(), t.getMaxProduction(this));
            ranking.add(tribeResult);
        }
    }

    /**
     * Returns the current ranking of the game. Ranking are computed at the end of each turn.
     * @return the current ranking of the game.
     */
    public TreeSet<TribeResult> getCurrentRanking() {return ranking;}


    /**
     * Indicates if a given tribe can end its turn. Tribes can't end their turn if a city upgrade is pending.
     * @param tribeId id of the tribe to check
     * @return true if turn can be ended.
     */
    public boolean canEndTurn(int tribeId)
    {
        return canEndTurn[tribeId];
    }

    /**
     * Sets the flag for turning ending to 'endTurn'
     * @param endTurn true if the turn must end
     */
    public void setEndTurn(boolean endTurn)
    {
        turnMustEnd = endTurn;
    }

    /**
     * Indicates if the turn is ending to move to the next player.
     * @return if the turn is ending.
     */
    boolean isTurnEnding()
    {
        return turnMustEnd;
    }

    /**
     * Indicates if at present there's a city leveling up
     * @return if there's a city leveling up
     */
    public boolean isLevelingUp() {
        return levelingUp;
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

    /**
     * Returns the tribe which turn it is now (the active tribe)
     * @return Current tribe to move.
     */
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

    boolean isNative() {
        return board.isNative();
    }

    /* AVAILABLE ACTIONS */

    public boolean isGameOver() {
        return gameIsOver;
    }

    void setGameIsOver(boolean gameIsOver) {
        this.gameIsOver = gameIsOver;
    }

    /**
     * Gathers and returns all the available actions for the active tribe in a single ArrayList
     * @return all available actions
     */
    public ArrayList<Action> getAllAvailableActions()
    {
        ArrayList<Action> allActions = new ArrayList<>(this.getTribeActions());
        for (Integer cityId : this.getCityActions().keySet())
        {
            allActions.addAll(this.getCityActions(cityId));
        }
        for (Integer unitId : this.getUnitActions().keySet())
        {
            allActions.addAll(this.getUnitActions(unitId));
        }
        return allActions;
    }

    public ArrayList<Action> getAllAvailableActions(int playerID)
    {
        if(playerID == getActiveTribeID())
            return getAllAvailableActions();
        else
        {
            //TODO: We have to compute them, and now computePlayerActions changes the current active tribe. Fix!
            System.out.println("Warning: requesting actions for non active tribe is not implemented");
            return getAllAvailableActions();
        }
    }

    public HashMap<Integer, ArrayList<Action>> getCityActions() {     return cityActions;  }
    public ArrayList<Action> getCityActions(City c) {  return cityActions.get(c.getActorId());  }
    public ArrayList<Action> getCityActions(int cityId) {  return cityActions.get(cityId);  }
    public ArrayList<Action> getAllCityActions()
    {
        ArrayList<Action> allActions = new ArrayList<>();
        for (Integer cityId : this.getCityActions().keySet())
            allActions.addAll(this.getCityActions(cityId));

        return allActions;
    }

    public HashMap<Integer, ArrayList<Action>> getUnitActions() {  return unitActions;  }
    public ArrayList<Action> getUnitActions(int unitId) {  return unitActions.get(unitId);  }
    public ArrayList<Action> getUnitActions(Unit u) {  return unitActions.get(u.getActorId());  }
    public ArrayList<Action> getAllUnitActions()
    {
        ArrayList<Action> allActions = new ArrayList<>();
        for (Integer unitId : this.getUnitActions().keySet())
            allActions.addAll(this.getUnitActions(unitId));

        return allActions;
    }

    public ArrayList<Action> getTribeActions() {  return tribeActions;  }

    /* Potentially helpful methods for agents */

    public int getTribeProduction(int playerId)
    {
        if(playerId == getActiveTribeID())
            return this.getActiveTribe().getMaxProduction(this);

        return this.getTribe(playerId).getMaxProduction(this);
    }


    public TechnologyTree getTribeTechTree(int playerId)
    {
        if(playerId == getActiveTribeID())
            return getActiveTribe().getTechTree();
        return this.getTribe(playerId).getTechTree();
    }

    public int getNKills(int playerId)
    {
        return getTribe(playerId).getnKills();
    }

    public int getScore(int playerID)
    {
        return getTribe(playerID).getScore();
    }

    public boolean[][] getVisibilityMap() {
        return getActiveTribe().getObsGrid();
    }

    public ArrayList<Integer> getTribesMet() {
        return getActiveTribe().getTribesMet();
    }

    public ArrayList<City> getCities(int playerId)
    {
        ArrayList<Integer> cities = getTribe(playerId).getCitiesID();
        ArrayList<City> cityActors = new ArrayList<>();
        for(Integer cityId : cities)
        {
            cityActors.add((City)board.getActor(cityId));
        }
        return cityActors;
    }

    public ArrayList<Unit> getUnits(int playerId)
    {
        ArrayList<Integer> cities = getTribe(playerId).getCitiesID();
        ArrayList<Unit> unitActors = new ArrayList<>();
        for(Integer cityId : cities)
        {
            City c = (City)board.getActor(cityId);
            for(Integer unitId : c.getUnitsID())
            {
                Unit unit = (Unit) board.getActor(unitId);
                unitActors.add(unit);
            }
        }

        for(Integer unitId : getTribe(playerId).getExtraUnits())
        {
            Unit unit = (Unit) board.getActor(unitId);
            unitActors.add(unit);
        }

        return unitActors;
    }

    public Types.GAME_MODE getGameMode() {
        return gameMode;
    }

    public Types.RESULT getTribeWinStatus(int playerId) {
        return getTribe(playerId).getWinner();
    }

    public TribesConfig getTribesConfig(){
        return tc;
    }

}
