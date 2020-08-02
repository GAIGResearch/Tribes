package players;

import core.Types;
import core.actions.Action;
import core.actions.cityactions.*;
import core.actions.tribeactions.BuildRoad;
import core.actions.tribeactions.EndTurn;
import core.actions.tribeactions.ResearchTech;
import core.actions.unitactions.*;
import core.actors.Building;
import core.actors.City;
import core.actors.Tribe;
import core.actors.units.Archer;
import core.actors.units.Catapult;
import core.actors.units.Unit;
import core.game.Board;
import core.game.GameState;
import utils.ElapsedCpuTimer;
import utils.Vector2d;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Random;
import static core.Types.ACTION.*;

/**
 * This is the Simple agent for the game.
 */
public class SimpleAgent extends Agent {

    private Random m_rnd;

    /**
     * Default constructor, to be called in subclasses (initializes player ID and random seed for this agent.
     *
     * @param seed - random seed for this player.
     */
    public SimpleAgent(long seed) {
        super(seed);
        m_rnd = new Random(seed);
    }


    @Override
    public Agent copy() {
        SimpleAgent player = new SimpleAgent(seed);
        return player;
    }

    @Override
    public Action act(GameState gs, ElapsedCpuTimer ect) {
        //Gather all available actions:
        ArrayList<Action> allActions = gs.getAllAvailableActions();
        int nActions = allActions.size();
        //Initially pick a random action so that at least that can be returned
        //Action bestAction = allActions.get(m_rnd.nextInt(nActions));
        int bestActionScore = -1; //evalAction(gs,bestAction);

        HashMap<Integer, ArrayList<Action>> desiredActions = new HashMap<>();

        for (Action a : allActions ) {
            int actionScore = evalAction(gs, a);

            ArrayList<Action> listActions;
            if(!desiredActions.containsKey(actionScore))
            {
                listActions = new ArrayList<>();
                desiredActions.put(actionScore, listActions);
            }else
            {
                listActions = desiredActions.get(actionScore);
            }
            listActions.add(a);

            if (actionScore > bestActionScore) {
//                bestAction = a;
                bestActionScore = actionScore;
            }
        }

        Action chosenAction = null;
        boolean actionFound = false;
        int val = bestActionScore;
        while(!actionFound && val >= -1)
        {
            if(desiredActions.containsKey(val))
            {
                actionFound = true;
                int n = desiredActions.get(val).size();
                chosenAction = desiredActions.get(val).get(m_rnd.nextInt(n));
            }else
            {
                val--;
            }
        }

//        System.out.println(bestActionScore + " " + chosenAction);

        return chosenAction;
    }

    /**
     * Each action is evaluated individually and given a score representing a priority from 0-5
     * 0 is the lowest priority and 5 is the highest priority
     */
    int evalAction(GameState gs, Action a) {

        int score = 0;
        Tribe thisTribe = gs.getActiveTribe();

        //UNIT ACTIONS:
        if (a.getActionType() ==  MOVE) {
            score = evalMove(a, gs, thisTribe);
        }else if (a.getActionType() ==  ATTACK) {
            score = evalAttack(a, gs);
        }else if (a.getActionType() ==  UPGRADE_SHIP || a.getActionType() == UPGRADE_BOAT) {
            score = evalUpgrade(a, gs, thisTribe);
        }else if (a.getActionType() ==  RECOVER) {
            score = evalRecover(a, gs, thisTribe);
        }else if (a.getActionType() ==  CAPTURE || a.getActionType() ==  EXAMINE) {
            score = 5; //Capturing provides only benefits
        }else if (a.getActionType() ==  HEAL_OTHERS) {
            score = evalHeal(a, gs);
        }else if (a.getActionType() ==  CONVERT) {
            score = evalConvert(a, gs);
        }else if (a.getActionType() ==  MAKE_VETERAN) {
            score = 5; //Making a veteran is placed as highest priority
        }else if (a.getActionType() ==  DISBAND) {
            score = -2;
        }

        //CITY ACTIONS
        if (a.getActionType() == DESTROY) {
            score = -2;
        }else if (a.getActionType() ==  BURN_FOREST) {
            score = evalBurn(a, gs, thisTribe);
        }else if (a.getActionType() ==  CLEAR_FOREST) {
            score = evalClear(thisTribe);
        }else if (a.getActionType() ==  GROW_FOREST) {
            score = evalGrow(a, gs);
        }else if (a.getActionType() ==  BUILD) {
            score = evalBuild(a, gs, thisTribe);
        }else if (a.getActionType() ==  SPAWN) {
            score = evalSpawn(a, gs, thisTribe);
        }else if (a.getActionType() ==  RESOURCE_GATHERING) {
            score = evalResourceGathering(a, gs);
        }else if (a.getActionType() ==  LEVEL_UP) {
            score = evalLevelUp(a);
        }

        //TRIBE ACTIONS
        if (a.getActionType() ==  BUILD_ROAD) {
            score = evalRoad(a,gs,thisTribe);
        }else if (a.getActionType() ==  RESEARCH_TECH) {
            score = evalResearch(a, gs, thisTribe);
        }else if (a.getActionType() ==  END_TURN)
        {
            score = -1;
        }

        return score;

    }

    private int evalLevelUp(Action a)
    {
        LevelUp lUp = (LevelUp) a;
        if(lUp.getBonus() == Types.CITY_LEVEL_UP.BORDER_GROWTH || lUp.getBonus() == Types.CITY_LEVEL_UP.WORKSHOP ||
            lUp.getBonus() == Types.CITY_LEVEL_UP.RESOURCES || lUp.getBonus() == Types.CITY_LEVEL_UP.SUPERUNIT)
            return 5;
        return 0;
    }

    private int evalResourceGathering(Action a, GameState gs)
    {
        ResourceGathering rg = (ResourceGathering) a;
        City c = (City) gs.getActor(rg.getCityId());
        int neededToLevelUp = neededToLevelUp(c);
        return Math.min(5, Math.max(0, 5-neededToLevelUp));
    }

    private int neededToLevelUp(City c)
    {
        return c.getLevel() + 1 - c.getProduction();
    }

    //Evaluate an action for building a road
    private int evalRoad(Action a, GameState gs, Tribe thisTribe){
        Vector2d pos = ((BuildRoad) a).getPosition();
        ArrayList<Integer> citiesID = thisTribe.getCitiesID();
        for(Vector2d neigh : pos.neighborhood(1, 0, gs.getBoard().getSize())) {
            int x = neigh.x;
            int y = neigh.y;
            City c = gs.getBoard().getCityInBorders(x,y);
            //Check if there is a city behind us
            if (c!=null && c.getTribeId() == thisTribe.getTribeId()){
                if(x == c.getPosition().x && y == c.getPosition().y){
                    if(thisTribe.getMaxProduction(gs) > 5 && thisTribe.getStars() > 4){
                        return 3;
                    }
                }else{
                    if(gs.getBoard().isRoad(x,y)){
                        return 3;
                    }
                }
            }else if(gs.getBoard().isRoad(x,y)){
                if(thisTribe.getMaxProduction(gs) > 5 && thisTribe.getStars() > 4){
                    return 4;
                }
                return 3;
            }
        }
        return 0;
    }

    //Evaluate all spawn actions
    private int evalSpawn(Action a, GameState gs, Tribe thisTribe) {

        int enemiesInCity = 0;
        Types.UNIT u = ((Spawn) a).getUnitType();
        int cityID = ((Spawn) a).getCityId();
        boolean[][] obsGrid = thisTribe.getObsGrid();

        for(Vector2d pos: gs.getBoard().getCityTiles(cityID)) {
            if (obsGrid[pos.x][pos.y]) {
                Unit unit = gs.getBoard().getUnitAt(pos.x, pos.y);
                if (unit != null && unit.getTribeId() != thisTribe.getTribeId()) {
                    enemiesInCity++;
                }
            }
        }

        int score = 0;
        switch (u) {
            case RIDER:
            case ARCHER:
            case CATAPULT:
                score = enemiesInCity>0 ? 0 : 3;
                break;
            case SWORDMAN:
            case KNIGHT:
                score = enemiesInCity>0 ? 4 : 3;
                break;
            case MIND_BENDER:
                score = enemiesInCity>0 ? 0 : 2;
                break;
            case WARRIOR:
                score = 3;
                break;
            case DEFENDER:
                score = enemiesInCity>0 ? 5 : 3;
        }

        return score;

    }

    //Evaluate a research action
    private int evalResearch(Action a, GameState gs, Tribe thisTribe) {
        Types.TECHNOLOGY t = ((ResearchTech) a).getTech();
        //If we don't have enough stars over a certain threshold we need to focus on that before researching tech

        switch (t) {
            //These allow the agent to gather more resources so these techs will be given a higher priority.
            case ORGANIZATION:
            case CLIMBING:
            case MINING:
            case FISHING:
            case HUNTING:
            case WHALING:
                return 5;
            case FARMING: //These allow the agent to gather more resources or be more well armed in future turns but provides less immediate benefit
            case AQUATISM:
            case RIDING:
            case FORESTRY:
            case ARCHERY:
            case SAILING:
            case SHIELDS:
            case CHIVALRY:
                return 4;
            default:
                return 3;
        }
    }

    //Evaluate a grow forest action
    private int evalGrow(Action a, GameState gs) {
        int cityID = ((GrowForest) a).getCityId();
        LinkedList<Vector2d> cityTiles = gs.getBoard().getCityTiles(cityID);
        int noOfForests = 0;
        int noOfLumberHuts = 0;
        for (Vector2d t: cityTiles) {
            Types.TERRAIN type = gs.getBoard().getTerrainAt(t.x,t.y);
            Types.BUILDING b = gs.getBoard().getBuildingAt(t.x,t.y);
            if(type == Types.TERRAIN.FOREST)
                noOfForests++;

            if(b == Types.BUILDING.LUMBER_HUT)
                noOfLumberHuts++;
        }

        if(noOfForests < 2 && noOfLumberHuts <2){ //We want more forests to build more lumber huts providing we have less than what we want
            return 4;
        }
        return 2;
    }


    //Evaluate a clear action
    private int evalClear(Tribe thisTribe) {
        //This is the least desirable action for a forest as it only gives a default tile
        //So this action is scored lowest unless we don't have any stars
        return (thisTribe.getStars() == 0) ? 3 : 0;
    }

    //Evaluate the burn forest action
    private int evalBurn(Action a, GameState gs, Tribe thisTribe) {
        int score = 0;
        City c = (City) gs.getActor(((BurnForest)a).getCityId());
        //Check if we have a good amount of stars left
        if (c != null && thisTribe.getStars() > 5) {
            LinkedList<Building> buildings = c.getBuildings();
            score = 1;
            int noOfFarms = 0;
            for (Building b : buildings) {
                if (b.type == Types.BUILDING.FARM) {
                    noOfFarms += 1;
                }
            }
            //Maximize probability of having a good windmill.
            if (noOfFarms < 2) {
                score += noOfFarms;
            }

        }
        return Math.min(5,score);
    }

    //Evaluate each building based on its worth and possible production value
    private int evalBuild(Action a, GameState gs, Tribe thisTribe) {
        //if we don't have a certain amount of stars then it is best to focus on other things
        if (thisTribe.getStars() < 8) {
            return 0;
        }
        Types.BUILDING b = ((Build) a).getBuildingType();
        int score = 0;
        switch (b) {
            case FARM:
            case MINE:
            case FORGE:
            case WINDMILL:
            case CUSTOMS_HOUSE:
                score = 4; //These increase population in a while costing less to build so these cases are weighted higher
//                break;
            case PORT:
            case SAWMILL:
            case LUMBER_HUT:
                score = 3; // These increase population less and cost more so are of less priority
//                break;
            case GRAND_BAZAR:
            case EMPERORS_TOMB:
            case GATE_OF_POWER:
            case EYE_OF_GOD:
            case PARK_OF_FORTUNE:
            case TOWER_OF_WISDOM:
            case ALTAR_OF_PEACE:
                score= 5; //These are for after a task in finished and don't cost anything and increase population so pick these randomly
//                break;
            case TEMPLE:
            case WATER_TEMPLE:
            case MOUNTAIN_TEMPLE:
                score = 1; // These cost a lot and don't offer a lot of population increase so are of less priority
                break;
        }

        City c = (City) gs.getActor(((Build)a).getCityId());
        int neededToLevelUp = neededToLevelUp(c);
        return Math.min(5, Math.max(0, score+5-neededToLevelUp));
    }

    //Evaluate an upgrade action on a boat/ship
    private int evalUpgrade(Action a, GameState gs, Tribe thisTribe) {
        Unit u = (Unit) gs.getActor(((Upgrade) a).getUnitId());

        if (u.getType() == Types.UNIT.BATTLESHIP) { // It's a ship to a battleship
            if (thisTribe.getMaxProduction(gs) > 5 && thisTribe.getStars() > 8) { //If the agent has a good amount of stars and good production then its worth an upgrade

                return 3;
            }
        } else { // It's a boat to a ship
            if (thisTribe.getMaxProduction(gs) > 3 && thisTribe.getStars() > 6) {
                return 3;
            }
        }
        return 0;
    }

    //Evaluate a recover action
    private int evalRecover(Action a, GameState gs, Tribe thisTribe) {
        boolean[][] obsGrid = thisTribe.getObsGrid();
        Unit thisUnit = (Unit) gs.getActor(((Recover) a).getUnitId());
        boolean inRange = false;
        Board b = gs.getBoard();
        //Check all tiles to see if we are in range of enemy
        for (int x = 0; x < obsGrid.length; x++) {
            for (int y = 0; y < obsGrid.length; y++) {
                if (obsGrid[x][y]) {
                    Unit enemy = b.getUnitAt(x, y);
                    if (enemy != null && enemy.getTribeId() != thisTribe.getTribeId()) {
                        // Check if we are in the range of an enemy
                        inRange = checkInRange(enemy, thisUnit);
                        if (inRange) {
                            if (enemy.getCurrentHP() < thisUnit.getCurrentHP()) {
                                return 3; // Prioritise healing
                            } else
                                return 1; // Incentive to let unit die as it would be a waste recovering
                        }
                    }
                }
            }
        }
        return 4; // Prioritise healing if not in range of anything
    }


    //Evaluate a healing action
    private int evalHeal(Action a, GameState gs) {
        Unit thisUnit = (Unit) gs.getActor(((HealOthers) a).getUnitId());
        Board b = gs.getBoard();
        int potentialHeals = 0;
        HealOthers ho = (HealOthers) a;

        ArrayList<Unit> targets = ho.getTargets(gs);
        for(Unit target : targets)
        {
            if (target.getCurrentHP() < target.getMaxHP()) {
                //Check if unit is in any potential danger
                for (Vector2d t : thisUnit.getPosition().neighborhood(target.RANGE, 0, b.getSize())) {
                    Unit enemy = b.getUnitAt(t.x, t.y);
                    if(enemy != null && enemy.getTribeId() != target.getTribeId() && enemy.getCurrentHP() > target.getCurrentHP()) {
                        potentialHeals += 1;
                    }
                }
            }
        }

        return Math.min(potentialHeals, 5);
    }

    //Evaluate the convert action, lesser units such as Warriors, riders are prioritised far less than more expensive ones.
    private int evalConvert(Action a, GameState gs) {
        Unit defender = (Unit) gs.getActor(((Convert) a).getTargetId());
        if(defender!=null) {
            switch (defender.getType()) {
                case BATTLESHIP:
                case SUPERUNIT:
                case SWORDMAN:
                case KNIGHT:
                case CATAPULT:
                    return 5;
                case MIND_BENDER:
                case BOAT:
                case SHIP:
                case WARRIOR:
                    return 4;
            }
        }
        return 0;
    }

    //Evaluate the move action
    public int evalMove(Action a, GameState gs, Tribe thisTribe) {
        Vector2d dest = ((Move) a).getDestination();
        Unit thisUnit = (Unit) gs.getActor(((Move) a).getUnitId());
        Vector2d currentPos = thisUnit.getPosition();
        Board b = gs.getBoard();
        int maxCities = 3; //Arbritary amount of cities we want in case we met no tribes

        ArrayList<Integer> tribesMet = thisTribe.getTribesMet();
        for (int tribeID : tribesMet ) {
            if (gs.getTribe(tribeID).getNumCities() > maxCities)
                maxCities = gs.getTribe(tribeID).getNumCities();
        }

        boolean inRange = false;


        // int score = 0;
        boolean[][] obsGrid = thisTribe.getObsGrid();
        for (int x = 0; x < obsGrid.length; x++) {
            for (int y = 0; y < obsGrid.length; y++) {
                if (obsGrid[x][y]) {
                    Unit enemy = b.getUnitAt(x, y);
                    if (enemy != null && enemy.getTribeId() != thisTribe.getTribeId()) {
                        // Check if we are in the range of an enemy
                        inRange = checkInRange(enemy, thisUnit);
                        if (enemy.DEF < thisUnit.ATK && thisUnit.getCurrentHP() >= enemy.getCurrentHP()) { //Incentive to attack weaker enemy
                            if (Vector2d.chebychevDistance(dest, enemy.getPosition()) < Vector2d.chebychevDistance(currentPos, enemy.getPosition())) {
                                return 3;
                            }
                        } else { //Higher Incentive to move away from enemy if the enemy is stronger, especially if we are in range
                            if (Vector2d.chebychevDistance(dest, enemy.getPosition()) > Vector2d.chebychevDistance(currentPos, enemy.getPosition()) && inRange) {
                                return 4;
                            }
                        }
                    }
                }
            }
        }

        for(Vector2d neigh : thisUnit.getPosition().neighborhood(thisUnit.RANGE, 0, gs.getBoard().getSize()))
        {
            int x = neigh.x;
            int y = neigh.y;
            if(obsGrid[x][y]){
                City c = b.getCityInBorders(x, y);
                Types.TERRAIN t = b.getTerrainAt(x, y);
                if (c != null && c.getTribeId() != thisTribe.getTribeId()) {
                    if (Vector2d.chebychevDistance(dest, c.getPosition()) < Vector2d.chebychevDistance(thisUnit.getPosition(), c.getPosition())) {
                        return 4;
                    }
                }
                if (t == Types.TERRAIN.VILLAGE) { // High incentive to move to village to capture as it is easier than capturing an actual city
                    if (Vector2d.chebychevDistance(dest, new Vector2d(x, y)) < Vector2d.chebychevDistance(thisUnit.getPosition(), new Vector2d(x, y))) {
                        return 5;
                    }
                }
            }
        }

        //Incentive to explore: next to fog
        for(Vector2d neigh : dest.neighborhood(1, 0, gs.getBoard().getSize())) {
            if(obsGrid[neigh.x][neigh.y]){
                return 3;
            }
        }

        return 0;
    }

    //Evaluate an attack action
    public int evalAttack(Action a, GameState gs) {
        int score = 0;
        Unit attacker = (Unit) gs.getActor(((Attack) a).getUnitId());
        Unit defender = (Unit) gs.getActor(((Attack) a).getTargetId());
        Board b = gs.getBoard();
        Types.UNIT attType = attacker.getType();
        if(!attType.isRanged()){
            if (attacker.getCurrentHP() >= defender.getCurrentHP()) {
                if (attacker.ATK > defender.DEF) {
                    return 5;
                } else { // Less priortiy given to
                    return 1;
                }
            } else if (attacker.getCurrentHP() < defender.getCurrentHP()) {
                if (attacker.ATK > defender.DEF) {
                    return 1;
                }//else don't add anything to the score.
            }
        } else {

            boolean inEnemyRange = checkInRange(defender, attacker);
            boolean enemyInRange = checkInRange(attacker, defender);
            if(inEnemyRange && defender.ATK > attacker.DEF)
            {
                //High retaliation danger
                return 0;
            }else if(enemyInRange && !inEnemyRange) {
                score = 5;
            }else if (defender.DEF < attacker.ATK && attacker.getCurrentHP() >= defender.getCurrentHP()) { //Incentive to attack weaker enemy
                score = 4; // Incentive to stay and attack target
            }
        }
        return score;
    }

    //Check all enemy tiles to see if we are in range of enemy
    private boolean checkInRange(Unit attacker, Unit defender) {
        // Check if we are in the range of an enemy
        Vector2d defPos = defender.getPosition();
        Vector2d attPos = attacker.getPosition();
        return Vector2d.chebychevDistance(defPos, attPos) <= attacker.RANGE;
    }
}