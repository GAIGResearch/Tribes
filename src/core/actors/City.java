package core.actors;

import core.Types;
import core.actors.buildings.Building;
import utils.Vector2d;

import java.util.LinkedList;
import static core.Types.BUILDING.*;

public class City extends Actor{

    private int level;
    private int population = 0;
    private int population_need;
    private boolean isCapital;
    private int production = 0;
    private int pointsPerTurn = 0;
    private boolean hasWalls = false;
    private int bound;

    private LinkedList<Integer> unitsID = new LinkedList<>();
    private LinkedList<Building> buildings = new LinkedList<>();

    // The constructor to initial the valley
    public City(int x, int y, int tribeId) {
        this.position = new Vector2d(x,y);
        this.tribeId = tribeId;
        population_need = 2; //level 1 requires population_need = 2
        bound = 1; //cities start with 1 tile around it for territory
        level = 1; //and starting level is 1
        isCapital = false;
    }

    // Increase population
    public void addPopulation(int number){
        population += number;
    }
    private void changePointsPerTurn(int points){
        changePointsPerTurn(points, 1);
    }
    private void changePointsPerTurn(int points, int multiplier){
        this.pointsPerTurn = points*multiplier;
    }
    public void addProduction(int prod) {
        production += prod;
        if(production < 0) production = 0;
    }


    public void addBuilding(Building building){

        //TODO: We have to check the other cities of this tribe!

        switch (building.type) {
            case FARM:
            case LUMBER_HUT:
            case MINE:
            case WINDMILL:
            case SAWMILL:
            case FORGE:
                changePopulation(building);
                break;
            case PORT:
                addPopulation(building.type.getBonus());
                changeProduction(building);
                break;
            case CUSTOM_HOUSE:
                changeProduction(building);
                break;
            case TEMPLE:
            case WATER_TEMPLE:
            case MOUNTAIN_TEMPLE:
            case FOREST_TEMPLE:
                changePointsPerTurn(building.getPoints());
                break;
            case ALTAR_OF_PEACE:
            case EMPERORS_TOMB:
            case EYE_OF_GOD:
            case GATE_OF_POWER:
            case PARK_OF_FORTUNE:
            case TOWER_OF_WISDOM:
                addPopulation(building.type.getBonus());
                break;
        }

        buildings.add(building);
    }

    private void changePopulation(Building building) {
        changePopulation(building, 1);
    }

    private void changePopulation(Building building, int multiplier) {
        int popAdd;
        boolean isBase = building.type.isBase();

        //Population added by the base building.
        if(isBase) addPopulation(building.getBonus());

        //Population added due to adjacency to other matching buildings.
        for (Building existingBuilding : buildings) {
            if(building.isMatchingBuilding(existingBuilding) && existingBuilding.adjacent(building))
            {
                // if building is base we add the bonus of the matching building. If it's not a base, the own building.
                popAdd = isBase ? existingBuilding.getBonus() : building.getBonus();
                addPopulation(popAdd * multiplier);
            }
        }
    }

    private void changeProduction(Building building) {
        changeProduction(building, 1);
    }

    private void changeProduction(Building building, int multiplier){

        int prodAdd;
        boolean isBase = building.type.isBase();

        //For all neighbouring buildings to 'building'
        for(Building existingBuilding: buildings){
            if(building.isMatchingBuilding(existingBuilding) && existingBuilding.adjacent(building))
            {
                prodAdd = isBase ? existingBuilding.getBonus() : building.getBonus();
                addProduction(prodAdd * multiplier);
            }
        }
    }


    public void removeBuilding(Building building)
    {
        switch (building.type) {
            case FARM:
            case LUMBER_HUT:
            case MINE:
            case WINDMILL:
            case SAWMILL:
            case FORGE:
                changePopulation(building, -1);
                break;
            case PORT:
                addPopulation(-building.type.getBonus());
                changeProduction(building, -1);
                break;
            case CUSTOM_HOUSE:
                changeProduction(building, -1);
                break;
            case TEMPLE:
            case WATER_TEMPLE:
            case MOUNTAIN_TEMPLE:
            case FOREST_TEMPLE:
                changePointsPerTurn(building.getPoints(), -1);
                break;
            case ALTAR_OF_PEACE:
            case EMPERORS_TOMB:
            case EYE_OF_GOD:
            case GATE_OF_POWER:
            case PARK_OF_FORTUNE:
            case TOWER_OF_WISDOM:
                addPopulation(-building.type.getBonus());
                break;
        }

        buildings.remove(building);
    }


    public int getProduction(){
        // If population less than 0, return start between [0 ~ level+production]
        if(population > 0) {
            return level + production;
        }
        return Math.max(0, (level + production - population));
    }

    public boolean canLevelUp()
    {
        return population >= population_need;
    }

    // Level up
    public void levelUp(){
        level++;
        population = population - population_need;
        population_need = level;
    }

    public boolean addUnit(int id){
        if (unitsID.size() < level){
            unitsID.add(id);
            return true;
        }
        return false;
    }

    public boolean canAddUnit(){
        return unitsID.size() < level;
    }

    public void removeUnit(int id){
        for(int i=0; i<unitsID.size(); i++){
            if (unitsID.get(i) == id){
                unitsID.remove(i);
                return;
            }
        }
        System.out.println("Error!! Unit ID "+ id +" does not belong to this city");
    }

    public Integer removeUnitByIndex(int index){
        return unitsID.remove(index);
    }

    public LinkedList<Integer> moveUnits(){
        LinkedList<Integer> lists = unitsID;
        unitsID = new LinkedList<Integer>();
        return lists;
    }

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


    // Get the point for each turn
    public int getPoints() {
        return pointsPerTurn;
    }


    public void setUnitsID(LinkedList<Integer> unitsID) {
        this.unitsID = unitsID;
    }

    public void setBuildings(LinkedList<Building> buildings) {
        this.buildings = buildings;
    }

    public LinkedList<Building> copyBuildings() {
        LinkedList<Building> copyList = new LinkedList<>();
        for(Building building : buildings) {
            copyList.add(building.copy());
        }
        return copyList;
    }


    public LinkedList<Integer> copyUnitsID() {
        LinkedList<Integer> copyUnits = new LinkedList<>();
        for (Integer id : unitsID) {
            copyUnits.add(id);
        }
        return copyUnits;
    }


    public City copy(){
        City c = new City(position.x, position.y, tribeId);
        c.level = level;
        c.population = population;
        c.population_need = population_need;
        c.isCapital = isCapital;
        c.production = production;
        c.bound = bound;
        c.setWalls(hasWalls);
        c.setBuildings(copyBuildings());
        c.setUnitsID(copyUnitsID());
        return c;
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

    public LinkedList<Integer> getUnitsID() {
        return unitsID;
    }

    public Building getBuilding(int x, int y){
        for(Building building :buildings){
            if (building.position.x == x && building.position.y == y){
                return building;
            }
        }
        return null;
    }

    public void subtractLongTermPoints(int points){
        if (pointsPerTurn < points){
            System.out.println("Error in subtract Long Term Points!!! -> Destroy Temple");
        }
        pointsPerTurn -= points;
    }

    public void subtractProduction(int production){
        if (this.production < production){
            System.out.println("Error in subtract Production!!! -> Destroy Custom House");
        }
        this.production -= production;
    }

    public void setTribeId(int tribeId) {
        this.tribeId = tribeId;
    }
}
