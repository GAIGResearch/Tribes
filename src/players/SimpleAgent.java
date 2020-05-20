package players;

import core.Types;
import core.actions.Action;
import core.actions.cityactions.*;
import core.actions.tribeactions.BuildRoad;
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
import java.util.LinkedList;

/**
 * This is the Simple agent for the game.
 */
public class SimpleAgent extends Agent {

    private ArrayList<Vector2d> recentlyVisitedPositions;

    /**
     * Default constructor, to be called in subclasses (initializes player ID and random seed for this agent.
     *
     * @param seed - random seed for this player.
     */
    public SimpleAgent(long seed) {
        super(seed);
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
        Action bestAction = allActions.get(gs.getRandomGenerator().nextInt(nActions));
        int bestActionScore = evalAction(gs,bestAction);
        TribesSimpleHeuristic heuristic = new TribesSimpleHeuristic(this.playerID);

        for (Action a : allActions
        ) {
            int actionScore = evalAction(gs, a);

            if (actionScore > bestActionScore) {
                bestAction = a;
                bestActionScore = actionScore;
            }

        }



        return bestAction;
    }

    /**
     * Each action is evaluated individually and given a score representing a priority from 0-5
     * 0 is the lowest priority and 5 is the highest priority
     */
    int evalAction(GameState gs, Action a) {

        int score = 0;
        Tribe thisTribe = gs.getActiveTribe();

        if (a instanceof Attack) {
            score = evalAttack(a, gs, thisTribe);
        }

        if (a instanceof Move) {
            score = evalMove(a, gs, thisTribe);
        }
        if (a instanceof Capture) {
            score = 5; //Capturing provides only benefits
        }
        if (a instanceof Convert) {
            score = evalConvert(a, gs, thisTribe);
        }
        if (a instanceof ResourceGathering) {
            if (thisTribe.getStars() > 5 || thisTribe.getMaxProduction(gs) < 10) {
                score = 5;
            } else {
                score = 3;
            }
        }
        if (a instanceof Examine) {
            score = 5;
        }
        if (a instanceof HealOthers) {
            score = evalHeal(a, gs, thisTribe);
        }

        if (a instanceof MakeVeteran) {
            score = 5; //Making a veteran is placed as highest priority
        }

        if (a instanceof Recover) {
            score = evalRecover(a, gs, thisTribe);
        }
        if (a instanceof Upgrade) {
            score = evalUpgrade(a, gs, thisTribe);
        }

        if (a instanceof Build) {
            score = evalBuild(a, gs, thisTribe);
        }

        if (a instanceof BurnForest) {
            score = evalBurn(a, gs, thisTribe);
        }

        if (a instanceof ClearForest) {
            score = evalClear(a, gs, thisTribe);
        }
        if (a instanceof Destroy) {
            score = evalDestroy(a, gs, thisTribe);
        }
        if (a instanceof GrowForest) {
            score = evalGrow(a, gs, thisTribe);

        }
        if (a instanceof LevelUp) {
            Types.CITY_LEVEL_UP bonus = ((LevelUp) a).getBonus();
            switch (bonus) {
                case SUPERUNIT:
                case PARK:
                case EXPLORER:
                case WORKSHOP:
                case CITY_WALL:
                case RESOURCES:
                case POP_GROWTH:
                case BORDER_GROWTH:
                    score = 5;
            }

        }

        if (a instanceof Spawn) {
            score = evalSpawn(a, gs, thisTribe);

        }

        if (a instanceof BuildRoad) {
            score = evalRoad(a,gs,thisTribe);

        }

        if (a instanceof ResearchTech) {
            score = evalResearch(a, gs, thisTribe);

        }

        if(a instanceof MakeVeteran){
            score = 5;
        }

        return score;


    }


    //Evaluate an action for building a road
    private int evalRoad(Action a, GameState gs, Tribe thisTribe){
        Vector2d pos = ((BuildRoad) a).getPosition();
        ArrayList<Integer> citiesID = thisTribe.getCitiesID();
        for(int x =pos.x-1; x<pos.x+1; x++){
            for (int y =pos.y-1; y<pos.y+1; y++){
                try {
                    City c = gs.getBoard().getCityInBorders(x,y);
                    //Check if there is a city behind us
                    if (c!=null){

                        if(c.getTribeId() == thisTribe.getTribeId()){
                            if(x == c.getPosition().x && y == c.getPosition().y){
                                if(thisTribe.getMaxProduction(gs) > 5 && thisTribe.getStars() > 4){
                                    return 3;
                                }
                            }else{
                                if(gs.getBoard().isRoad(x,y)){
                                    return 3;
                                }
                            }
                        }

                    }else if(gs.getBoard().isRoad(x,y)){
                        if(thisTribe.getMaxProduction(gs) > 5 && thisTribe.getStars() > 4){
                            return 3;
                        }
                        return 1;
                    }
                }catch (IndexOutOfBoundsException e){
                    continue;
                }
            }
        }

        //if(pos.x)

        return 0;
    }

    //Evaluate all spawn actions
    private int evalSpawn(Action a, GameState gs, Tribe thisTribe) {

        boolean enemyInCity = false;

        Types.UNIT u = ((Spawn) a).getUnitType();
        int cityID = ((Spawn) a).getCityId();
        boolean[][] obsGrid = thisTribe.getObsGrid();
        //Check if enemy in city
        outer:
        for(int i = 0; i<obsGrid.length; i++){
            for(int j =0; j<obsGrid.length; j++){
                if(obsGrid[i][j]){
                    if(gs.getBoard().getCityIdAt(i,j) == cityID){
                        Unit unit = gs.getBoard().getUnitAt(i,j);
                        if(unit!=null) {
                            if (gs.getBoard().getUnitAt(i, j).getTribeId() != thisTribe.getTribeId()) {
                                enemyInCity = true;
                                break outer;
                            }
                        }
                    }
                }

            }
        }


        int score = 0;
        switch (u) {
            case RIDER:
            case ARCHER:
                score = 2;
            case SWORDMAN:
                score = 4;
            case BATTLESHIP:
            case KNIGHT:
            case CATAPULT:
                score = 4;
            case MIND_BENDER:
            case BOAT:
            case WARRIOR:
                score = 1;
            case DEFENDER:
            case SHIP:
                score = 3;
        }
        //Higher priority if enemies in city.
        if(enemyInCity)
            score +=1;

        return score;

    }

    private int evalResearch(Action a, GameState gs, Tribe thisTribe) {
        Types.TECHNOLOGY t = ((ResearchTech) a).getTech();
        //If we don't have enough stars over a certain threshold we need to focus on that before researching tech
        if (thisTribe.getStars() < 8) {
            return 0;
        }
        switch (t) {
            //These allows the agent to gather more resources so these techs will be given a higher priority.
            case ORGANIZATION:
            case CLIMBING:
            case MINING:
            case FISHING:
            case HUNTING:
            case WHALING:
                return 5;
            case FARMING: //These allows the agent to gather more resources or be more well armed in future turns but provides less immediate benefit
            case AQUATISM:
            case RIDING:
            case FORESTRY:
            case ARCHERY:
            case SAILING:
            case SHIELDS:
            case CHIVALRY:
                return 4;
            case NAVIGATION: //These provide even less immediate benefit so they will be scored a little lower.
            case ROADS:
            case MEDITATION:
            case MATHEMATICS:
            case FREE_SPIRIT:
            case TRADE:
            case CONSTRUCTION:
            case SPIRITUALISM:
            case SMITHERY:
            case PHILOSOPHY:
                return 3;


        }
        return 4;
    }

    //Evaluate a grow forest action
    private int evalGrow(Action a, GameState gs, Tribe thisTribe) {
        int cityID = ((GrowForest) a).getCityId();
        LinkedList<Vector2d> cityTiles = gs.getBoard().getCityTiles(cityID);
        int noOfForests = 0;
        int noOfLumberHuts = 0;
        for (Vector2d t: cityTiles
        ) {

            Types.TERRAIN type = gs.getBoard().getTerrainAt(t.x,t.y);
            Types.BUILDING b = gs.getBoard().getBuildingAt(t.x,t.y);
            if(type == Types.TERRAIN.FOREST)
                noOfForests++;

            if(b == Types.BUILDING.LUMBER_HUT)
                noOfLumberHuts++;
        }

        if(noOfForests < 2 && noOfLumberHuts <2){ //We want more forests to build more lumber huts providing we have less than what we want
            return 2;
        }
        return 0;
    }


    //Evaluate a clear action
    private int evalClear(Action a, GameState gs, Tribe thisTribe) {
        //This is the least desirable action for a forest as it only gives a default tile
        //So this action is scored lowest
        return 0;
    }

    //Evaluate a destroy action
    private int evalDestroy(Action a, GameState gs, Tribe thisTribe) {
        //Ideally we don't want to destroy anything so this action is scored lowest
        return 0;
    }


    //Evaluate the burn forest action
    private int evalBurn(Action a, GameState gs, Tribe thisTribe) {
        int score = 0;
        //Check if we have a good amount of stars left
        if (thisTribe.getStars() > 5) {
            City c = gs.getBoard().getCityInBorders(((BurnForest) a).getTargetPos().x, ((BurnForest) a).getTargetPos().y);
            if (c != null) {
                LinkedList<Building> buildings = c.getBuildings();
                score = 1;
                int noOfFarms = 0;
                for (Building b : buildings
                ) {
                    if (b.type == Types.BUILDING.FARM) {
                        noOfFarms += 1;
                    }
                }
                //We want a good amount of farms so that the city gets more population and levels up
                if (thisTribe.getMaxProduction(gs) > 5 && noOfFarms < 2) {
                    score = 3;
                }
            }
        }
        return score;
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
                score = 4; //These increase population in a while costing less to build so these cases are weighted higher
            case PORT:
            case SAWMILL:
            case LUMBER_HUT:
                score = 3; // These increase population less and cost more so are of less priority
            case GRAND_BAZAR:
            case CUSTOMS_HOUSE:
            case EMPERORS_TOMB:
            case GATE_OF_POWER:
            case EYE_OF_GOD:
            case PARK_OF_FORTUNE:
            case TOWER_OF_WISDOM:
            case ALTAR_OF_PEACE:
                score= 5; //These are for after a task in finished and don't cost anything and increase population so pick these randomly
            case TEMPLE:
            case WATER_TEMPLE:
            case MOUNTAIN_TEMPLE:
                score = 2; // These cost a lot and don't offer a lot of population incrase so are of less priority
        }

        return score;
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
                    if (enemy != null) {
                        if (enemy.getTribeId() != thisTribe.getTribeId()) {
                            // Check if we are in the range of an enemy
                            inRange = checkInRange(enemy, thisUnit, thisTribe, b);
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
        }
        return 4; // Prioritise healing if not in range of anything
    }


    //Evaluate a healing action
    private int evalHeal(Action a, GameState gs, Tribe thisTribe) {
        Unit thisUnit = (Unit) gs.getActor(((HealOthers) a).getUnitId());
        Board b = gs.getBoard();
        int score = 0;
        int potentialHeals = 0;

        for (Vector2d tile : thisUnit.getPosition().neighborhood(thisUnit.RANGE, 0, b.getSize())) {
            Unit target = b.getUnitAt(tile.x, tile.y);
            if (target.getCurrentHP() < target.getMaxHP()) {
                //Check if unit is in any potential danger
                for (Vector2d t : thisUnit.getPosition().neighborhood(target.RANGE, 0, b.getSize())) {
                    Unit enemy = b.getUnitAt(t.x, t.y);
                    if (enemy.getTribeId() == target.getTribeId())
                        continue;
                    else if (enemy.getCurrentHP() > target.getCurrentHP()) {
                        potentialHeals += 1;
                    }
                }
            }
        }
        if (potentialHeals == 0) {
            return 0;
        } else if (potentialHeals >= 1 && potentialHeals < 5) {
            score = 3;
        } else {
            score = 5;
        }


        return score;
    }

    //Evaluate the convert action, lesser units such as Warriors, riders are prioritised far less than more expensive ones.
    private int evalConvert(Action a, GameState gs, Tribe thisTribe) {
        Unit defender = (Unit) gs.getActor(((Convert) a).getTargetId());
        switch (defender.getType()) {
            case RIDER:
            case ARCHER:
                return 2;
            case SUPERUNIT:
                return 5; //heavily weight superunit capture
            case SWORDMAN:
            case BATTLESHIP:
            case KNIGHT:
            case CATAPULT:
                return 4;
            case MIND_BENDER:
            case BOAT:
            case WARRIOR:
                return 1;
            case DEFENDER:
            case SHIP:
                return 3;

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
        if(tribesMet.size() >1) {
            for (int tribeID : tribesMet
            ) {
                if (gs.getTribe(tribeID).getNumCities() > maxCities)
                    maxCities = gs.getTribe(tribeID).getNumCities();

            }
        }
        boolean inRange = false;


        // int score = 0;
        boolean[][] obsGrid = thisTribe.getObsGrid();
        for (int x = 0; x < obsGrid.length; x++) {
            for (int y = 0; y < obsGrid.length; y++) {
                if (obsGrid[x][y]) {
                    Unit enemy = b.getUnitAt(x, y);
                    if (enemy != null) {
                        if (enemy.getTribeId() != thisTribe.getTribeId()) {
                            // Check if we are in the range of an enemy
                            inRange = checkInRange(enemy, thisUnit, thisTribe, b);
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
        }

        for(int x = thisUnit.getPosition().x-thisUnit.RANGE; x<thisUnit.getPosition().x+ thisUnit.RANGE; x++){ //Check if any cities/villages in range
            for(int y = thisUnit.getPosition().y-thisUnit.RANGE; y<thisUnit.getPosition().y+ thisUnit.RANGE; y++) {
                try {
                    if(obsGrid[x][y]){
                        City c = b.getCityInBorders(x, y);
                        Types.TERRAIN t = b.getTerrainAt(x, y);
                        if (c != null) {
                            if (c.getTribeId() != thisTribe.getTribeId()) { // High incentive to move towards enemy city to capture
                                if (Vector2d.chebychevDistance(dest, c.getPosition()) < Vector2d.chebychevDistance(thisUnit.getPosition(), c.getPosition())) {

                                    if (thisTribe.getCitiesID().size() > maxCities) // Agent wants the most cities
                                        return 5;
                                    else
                                        return 4;
                                }
                            }
                        }
                        if (t == Types.TERRAIN.VILLAGE) { // High incentive to move to village to capture as it is easier than capturing an actual city
                            if (Vector2d.chebychevDistance(dest, new Vector2d(x, y)) < Vector2d.chebychevDistance(thisUnit.getPosition(), new Vector2d(x, y))) {

                                if (thisTribe.getCitiesID().size() > maxCities) // Agent wants the most cities
                                    return 4;
                                else
                                    return 5;
                            }
                        }
                    }
                }catch (IndexOutOfBoundsException e){
                    continue;
                }
            }
        }
        for (int i = 0; i < thisUnit.RANGE; i++) {
            try {
                if (!obsGrid[dest.x + thisUnit.RANGE][dest.y + thisUnit.RANGE] || !obsGrid[dest.x - thisUnit.RANGE][dest.y - thisUnit.RANGE]) {
                    return 3; //Incentive to explore;
                }
            } catch (ArrayIndexOutOfBoundsException e) {
                continue;
            }

        }
        return 0;
    }

    public int evalAttack(Action a, GameState gs, Tribe thisTribe) {
        int score = 0;
        Unit attacker = (Unit) gs.getActor(((Attack) a).getUnitId());
        Unit defender = (Unit) gs.getActor(((Attack) a).getTargetId());
        Board b = gs.getBoard();
        if (!(attacker instanceof Archer) || !(attacker instanceof Catapult)) {
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
            //Check if we are in defenders range or any other attackers range, make sure all is safe before attacking
            boolean[][] obsGrid = thisTribe.getObsGrid();
            for (int x = 0; x < obsGrid.length; x++) {
                for (int y = 0; y < obsGrid.length; y++) {
                    if (obsGrid[x][y]) {
                        Unit enemy = b.getUnitAt(x, y);
                        if (enemy != null) {

                            boolean inRange = checkInRange(enemy, attacker, thisTribe, b);
                            if (inRange) { // No ranged attacks at all if we are in range of an enemy
                                return 0;
                            }
                            if (enemy.DEF < attacker.ATK && attacker.getCurrentHP() >= enemy.getCurrentHP()) { //Incentive to attack weaker enemy
                                score += 4; // Incentive to stay and attack target
                            }
                        }
                    }
                }
            }
        }

        return score;

    }

    //Check all enemy tiles to see if we are in range of enemy
    private boolean checkInRange(Unit enemy, Unit thisUnit, Tribe thisTribe, Board b) {
        if (enemy.getTribeId() != thisTribe.getTribeId()) {
            // Check if we are in the range of an enemy
            for (Vector2d t : enemy.getPosition().neighborhood(enemy.RANGE, 0, b.getSize())) {
                Unit u = b.getUnitAt(t.x, t.y);
                if(u !=null) {
                    if (u.equals(thisUnit)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}