package core.actors;

import core.TribesConfig;
import core.Types;
import core.game.Board;
import core.game.GameState;
import org.json.JSONArray;
import org.json.JSONObject;
import utils.Vector2d;
import java.util.ArrayList;
import java.util.LinkedList;

public class City extends Actor{

    //level of this city
    private int level;

    //current population of this city
    private int population = 0;

    //population needed to level up
    private int population_need;

    //Indicates if this city is a capital city
    private boolean isCapital;

    //total production of this city
    private int production = 0;

    //indicates if this city has walls
    private boolean hasWalls;

    //Extension of the city
    private int bound;

    //Points this city is worth, which will be lost if the city is captured by an enemy.
    private int pointsWorth;

    // List of IDs of units controlled by this city.
    private ArrayList<Integer> unitsID = new ArrayList<>();

    //List of buildings that belong to this city.
    private LinkedList<Building> buildings = new LinkedList<>();

    private TribesConfig tc = new TribesConfig();
    /**
     * Constructor of a city
     * @param x x position of the city in the grid
     * @param y y position of the city in the grid
     * @param tribeId Tribe id that this city belongs to.
     */
    public City(int x, int y, int tribeId) {
        this.position = new Vector2d(x,y);
        this.tribeId = tribeId;
        population_need = 2; //level 1 requires population_need = 2
        bound = 1; //cities start with 1 tile around it for territory
        level = 1; //and starting level is 1
        isCapital = false;
        hasWalls = false;
    }

    /**
     * Creates a city from a JSON object
     * @param obj object to create the city from
     * @param cityID id of this city.
     */
    public City(JSONObject obj, int cityID){
        level = obj.getInt("level");
        population = obj.getInt("population");
        population_need = obj.getInt("population_need");
        isCapital = obj.getBoolean("isCapital");
        production = obj.getInt("production");
        hasWalls = obj.getBoolean("hasWalls");
        bound = obj.getInt("bound");
        this.position = new Vector2d(obj.getInt("x"),obj.getInt("y"));
        this.tribeId = obj.getInt("tribeID");
        pointsWorth = obj.getInt("pointsWorth");
        JSONArray jUnits = obj.getJSONArray("units");
        for (int i=0; i<jUnits.length(); i++){
            unitsID.add(jUnits.getInt(i));
        }

        JSONArray jBuildings = obj.getJSONArray("buildings");
        for (int i=0; i<jBuildings.length(); i++){
            JSONObject buildingINFO = jBuildings.getJSONObject(i);
            Types.BUILDING type = Types.BUILDING.getTypeByKey(buildingINFO.getInt("type"));
            if (type != null && type.isTemple()){
                buildings.add(new Temple(buildingINFO, cityID));
            }else{
                buildings.add(new Building(buildingINFO, cityID));
            }
        }
    }

    /**
     * Adds population to this city.
     * @param tribe tribe this city belongs to
     * @param value amount of population to add to this city.
     */
    public void addPopulation(Tribe tribe, int value){

        //-level is a maximum negative value.
        if(population + value < -level)
            value = - level - population;

        population += value;
        tribe.addScore(value * tc.POINTS_PER_POPULATION);
        addPointsWorth(value * tc.POINTS_PER_POPULATION);
    }

    /**
     * Adds production to this city. The value can be negative, but final total production
     * will never be negative.
     * @param prod production value to add to this city.
     */
    public void addProduction(int prod) {
        production += prod;
        if(production < 0) production = 0;
    }

    /**
     * Adds a building to this city.  It updates
     * the productive effects of the building in the city.
     * @param gameState game state in which this building is added.
     * @param building to add
     */
    public void addBuilding(GameState gameState, Building building)
    {
        updateBuildingEffects(gameState, building, false, false);
        buildings.add(building);
    }

    /**
     * Removes a building from this city. It updates
     * the productive effects of the building in the city.
     * @param gameState game state from which the city is removed
     * @param building to remove
     */
    public void removeBuilding(GameState gameState, Building building)
    {
        updateBuildingEffects(gameState, building, true, false);
        buildings.remove(building);
    }

    /**
     * Updates the effect of change of buildings in a city
     * @param gameState current game state
     * @param building building to add/remove
     * @param negative indicates if the effects are positive or negative
     * @param onlyMatching flag to indicate if the effects come from the matching buildings (those
     *      *                     associated in production) or the referenced building only.
     */
    void updateBuildingEffects(GameState gameState, Building building, boolean negative, boolean onlyMatching)
    {
        int multiplier = negative ? -1 : 1;
        Tribe tribe = gameState.getTribe(this.tribeId);
        switch (building.type) {
            case FARM:
            case LUMBER_HUT:
            case MINE:
            case WINDMILL:
            case SAWMILL:
            case FORGE:
                applyBonus(gameState, building, true, onlyMatching, multiplier);
                break;
            case PORT:
                if(!onlyMatching) addPopulation(tribe, building.type.getBonus(building.type.getKey(), gameState.getTribesConfig()) * multiplier);
                applyBonus(gameState, building, false, onlyMatching, multiplier);
                break;
            case CUSTOMS_HOUSE:
                applyBonus(gameState, building, false, onlyMatching, multiplier);
                break;
            case TEMPLE:
            case WATER_TEMPLE:
            case MOUNTAIN_TEMPLE:
            case FOREST_TEMPLE:
                if(!onlyMatching)
                {
                    addPopulation(tribe, building.type.getBonus(building.type.getKey(),gameState.getTribesConfig()) * multiplier);
                }
                int scoreDiff = negative ? ((Temple)building).getPoints() : tc.TEMPLE_POINTS[0];
                tribe.addScore(scoreDiff);
                break;
            case ALTAR_OF_PEACE:
            case EMPERORS_TOMB:
            case EYE_OF_GOD:
            case GATE_OF_POWER:
            case PARK_OF_FORTUNE:
            case TOWER_OF_WISDOM:
            case GRAND_BAZAR:
                if(!onlyMatching) addPopulation(tribe,building.type.getBonus(building.type.getKey(), gameState.getTribesConfig()) * multiplier);
                tribe.addScore(tc.MONUMENT_POINTS * multiplier);
                break;
        }
    }

    /**
     * Applies the bonus of a building and its associated buildings to this city.
     * @param gameState current game state
     * @param building building that is providing the bonus.
     * @param isPopulation indicates if the bonus affects the population or the production of the city.
     * @param onlyMatching flag to indicate if the effects come from the matching buildings (those
     *                     associated in production) or the referenced building only.
     * @param multiplier bonus applied is multiplied by this amount.
     */
    private void applyBonus(GameState gameState, Building building, boolean isPopulation, boolean onlyMatching, int multiplier){

        int bonusToAdd;
        boolean isBase = building.type.isBase();
        City cityToAddTo = this;
        Board board = gameState.getBoard();
        Tribe tribe = gameState.getTribe(this.tribeId);

        //Population added by the base building.
        if(isBase && isPopulation && !onlyMatching) addPopulation(tribe, multiplier * building.getBonus(gameState.getTribesConfig()));

        //Check all buildings next to the new building position.
        for(Vector2d adjPosition : building.position.neighborhood(1, 0, board.getSize()))
        {
            //For each position, if there's a building and of the production matching point
            Types.BUILDING b = board.getBuildingAt(adjPosition.x, adjPosition.y);
            if(b != null && building.type.getMatchingBuilding() == b)
            {
                //Retrieve this building, which could be form this city or from another one from the tribe.
                Building existingBuilding;
                int cityId = board.getCityIdAt(adjPosition.x, adjPosition.y);
                if(cityId == actorId)
                {
                    //the matching building belongs to this city
                    existingBuilding = this.getBuilding(adjPosition.x, adjPosition.y);
                }else if(tribe.controlsCity(cityId)) {
                    //the matching building belongs to a city from a different tribe
                    City city = (City) gameState.getActor(cityId);
                    existingBuilding = city.getBuilding(adjPosition.x, adjPosition.y);
                    cityToAddTo = city;

                }else return; //This may happen if the building belongs to a city from another tribe.

                if(existingBuilding != null) {
                    bonusToAdd = isBase ? existingBuilding.getBonus(gameState.getTribesConfig()) : building.getBonus(gameState.getTribesConfig());

                    if (isPopulation)
                        cityToAddTo.addPopulation(tribe, bonusToAdd * multiplier);
                    else
                        cityToAddTo.addProduction(bonusToAdd * multiplier);
                }

            }
        }
    }

    /**
     * Gets the production associated to this city. If the population is negative, the production is the population
     * value. If not, it's the level + prodiction + TribesConfig.PROD_CAPITAL_BONUS (if capital)
     * @return the production of this city
     */
    public int getProduction(){
        if(population >= 0) {
            int capitalBonus = isCapital ? tc.PROD_CAPITAL_BONUS : 0;
            return level + production + capitalBonus;
        }
        return population;
    }

    /**
     * Indicates if this city can level up
     * @return if this city can level up
     */
    public boolean canLevelUp()
    {
        return population >= population_need;
    }

    /**
     * Levels the city up. Updates population and population_need of the city for the new level.
     */
    public void levelUp(){
        level++;
        population = population - population_need;
        population_need = (level+1);
    }

    /**
     * Adds a unit to this city.
     * @param id of the unit to add
     */
    public void addUnit(int id){
        if (canAddUnit()){
            unitsID.add(id);
        }
    }

    /**
     * Checks if a unit can be added to the city (not if the city is full)
     * @return if a unit can be added
     */
    public boolean canAddUnit(){
        return unitsID.size() < (level+1);
    }

    /**
     * Removes a unit from this city.
     * @param id id of the unit to remove.
     */
    public void removeUnit(int id){
        for(int i=0; i<unitsID.size(); i++){
            if (unitsID.get(i) == id){
                unitsID.remove(i);
                return;
            }
        }
        System.out.println("Error!! Unit ID "+ id +" does not belong to this city");
//        Thread.dumpStack();
    }

    /**
     * Removes the unit with index 'index' from this city.
     * @param index index of the unit to remove.
     * @return the element that was removed.
     */
    public Integer removeUnitByIndex(int index){
        return unitsID.remove(index);
    }

    /**
     * Returns a bulding that is located at (x,y). Null if no building there.
     * @param x x position to retrive the building from
     * @param y y position to retrive the building from
     * @return the building at (x,y), null if no building there.
     */
    public Building getBuilding(int x, int y){
        for(Building building :buildings){
            if (building.position.x == x && building.position.y == y){
                return building;
            }
        }
        return null;
    }

    /**
     * Copies all the buildings of this city to a new list
     * @return a list with a copy of each building of this city.
     */
    private LinkedList<Building> copyBuildings() {
        LinkedList<Building> copyList = new LinkedList<>();
        for(Building building : buildings) {
            copyList.add(building.copy());
        }
        return copyList;
    }

    /**
     * Creates a copy if this city and returns it.
     * @param hideInfo indicates if information of this actor should be copied or hidden for
     *                 partial observability.
     * @return a copy of this city.
     */
    public City copy(boolean hideInfo){
        City c = new City(position.x, position.y, tribeId);
        c.level = level;
        c.population = hideInfo ? 0 : population;
        c.population_need = population_need;
        c.isCapital = isCapital;
        c.production = hideInfo ? 0 : production;
        c.hasWalls = hasWalls;
        c.bound = bound;
        c.actorId = actorId;
        c.setBuildings(copyBuildings());
        c.setUnitsID(hideInfo ? new ArrayList<>() : new ArrayList<>(unitsID));
        return c;
    }

    /* Getters and setters */
    public int getLevel() {
        return level;
    }

    public int getPopulation() {
        return population;
    }

    public boolean isCapital() {
        return isCapital;
    }

    public void setCapital(boolean isCapital)
    {
        this.isCapital = isCapital;
    }

    public int getPopulation_need() {
        return population_need;
    }

    private void setUnitsID(ArrayList<Integer> unitsID) {
        this.unitsID = unitsID;
    }

    public void setBuildings(LinkedList<Building> buildings) {
        this.buildings = buildings;
    }

    public void setWalls(boolean walls)
    {
        hasWalls = walls;
    }

    public boolean hasWalls()
    {
        return hasWalls;
    }

    public int getBound(){
        return this.bound;
    }
    public void setBound(int b){
        this.bound = b;
    }

    public ArrayList<Integer> getUnitsID() {
        return unitsID;
    }

    public int getNumUnits()
    {
        return unitsID.size();
    }

    public void setTribeId(int tribeId) {
        this.tribeId = tribeId;
    }

    public LinkedList<Building> getBuildings() {
        return buildings;
    }

    public void addPointsWorth(int points){
        pointsWorth +=points;
    }

    public int getPointsWorth(){
        return pointsWorth;
    }

    public void setPopulation(int popValue) {
        this.population = popValue;
    }

    public void setProduction(int prodValue) {
        this.production = prodValue;
    }

    void clearUnits() {
        unitsID.clear();
    }
}
