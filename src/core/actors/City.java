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
    private int points = 0;
    private int longTermPoints = 0;
    private boolean hasWalls = false;

    private LinkedList<Integer> unitsID = new LinkedList<>();
    private LinkedList<Building> buildings = new LinkedList<>();
    int bound;

    // The constructor to initial the valley
    public City(int x, int y, int tribeId) {
        this.position = new Vector2d(x,y);
        population_need = 2; //level 1 requires population_need = 2
        bound = 1; //cities start with 1 tile around it for territory
        level = 1; //and starting level is 1
        isCapital = false;
        this.tribeId = tribeId;
    }

    public City(int x, int y, int level, int population, int population_need, boolean isCapital, int production, LinkedList<Building> buildings, int tribeId, LinkedList<Integer> unitsID) {
        this.position = new Vector2d(x,y);
        this.level = level;
        this.population = population;
        this.population_need = population_need;
        this.isCapital = isCapital;
        this.production = production;
        this.buildings = buildings;
        this.tribeId = tribeId;
        this.unitsID = unitsID;
    }

    // Increase population
    public void addPopulation(int number){
        population += number;
    }

    // Decrease population
    public void subtractPopulation(int number){
        population += number;
    }

    public void addBuildings(Building building){
        if (building.getTYPE() == WINDMILL || building.getTYPE() == SAWMILL ||
                building.getTYPE() == FORGE || building.getTYPE() == CUSTOM_HOUSE){
            setProduction(building);
        }else if (building.getTYPE() == FARM || building.getTYPE() == LUMBER_HUT ||
                building.getTYPE() == MINE || building.getTYPE() == PORT){
            changeProduction(building);
        }else if (building.getTYPE() == TEMPLE || building.getTYPE() == WATER_TEMPLE){
            addLongTimePoints(building.getPoints());
            addPoints(building.getPoints());
        }else{
            addPoints(building.getPoints());
        }

        if (building.getTYPE() == CUSTOM_HOUSE){
            addProduction(building.getPRODUCTION());
        }else {
            addPopulation(building.getPRODUCTION());
        }

        buildings.add(building);
    }

    private void addPoints(int points) {
        this.points += points;
    }

    private void addLongTimePoints(int points){
        this.longTermPoints = points;
    }

    public void setProduction(Building building){
        Vector2d pos = building.getPosition();
        int production = 0;
        for(Building existBuilding: buildings){
            Vector2d existingPos = existBuilding.getPosition();
            if ( (existingPos.x >= pos.x-1 && existingPos.x <= pos.x+1) && (existingPos.y >= pos.y-1 && existingPos.y <= pos.y+1)){
                if (checkMatchedBuilding(existBuilding, building)){
                    production++;
                }
            }
        }
        building.setProduction(production);
    }

    public void changeProduction(Building building){
        Vector2d pos = building.getPosition();
        for(Building existBuilding: buildings){
            Vector2d existingPos = existBuilding.getPosition();
            if ( (existingPos.x >= pos.x-1 && existingPos.x <= pos.x+1) && (existingPos.y >= pos.y-1 && existingPos.y <= pos.y+1)){
                if (checkMatchedBuilding(building, existBuilding)){
                    if (existBuilding.getTYPE() == FORGE){
                        addPopulation(2);
                    }else if(existBuilding.getTYPE() == CUSTOM_HOUSE){
                        addProduction(2);
                    }else{
                        addPopulation(1);
                    }

                }
            }
        }
    }

    public boolean checkMatchedBuilding(Building original, Building functional){
        return (original.getTYPE() == FARM && functional.getTYPE() == Types.BUILDING.WINDMILL) ||
                (original.getTYPE() == LUMBER_HUT && functional.getTYPE() == SAWMILL) ||
                (original.getTYPE() == MINE && functional.getTYPE() == FORGE) ||
                (original.getTYPE() == PORT && functional.getTYPE() == CUSTOM_HOUSE);
    }

    public boolean canLevelUp()
    {
        return population >= population_need;
    }

    // Level up
    public void levelUp(){
        level++;
        population = population - population_need;
        population_need = level + 1;
        addPoints(getLevelUpPoints());
    }


    private int getLevelUpPoints(){
        if (level == 1){
            return 100;
        }
        return 50 - level * 5;
    }


    public void addProduction(int prod) {
        production += prod;
    }

    public boolean addUnit(int id){
        if (unitsID.size() < level){
            unitsID.add(id);
            return true;
        }
        return false;
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


    public int getLevel() {
        return level;
    }

    public int getProduction(){
        return level + production;
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


    public LinkedList<Building> copyBuildings() {
        LinkedList<Building> copyList = new LinkedList<>();
        for(Building building : buildings) {
            copyList.add(building.copy());
        }
        return copyList;
    }

    // Get the point for each turn
    public int getPoints() {
        int turnPoint = points + longTermPoints;
        points = 0;
        return turnPoint;
    }

    public LinkedList<Integer> copyUnitsID() {
        LinkedList<Integer> copyUnits = new LinkedList<>();
        for (Integer integer : unitsID) {
            copyUnits.add(integer);
        }
        return copyUnits;
    }

    public City copy(){
        City c = new City(position.x, position.y, level, population, population_need, isCapital, production, copyBuildings(), tribeId, copyUnitsID());
        c.setWalls(hasWalls);
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

    public Building removeBuilding(int x, int y){
        Building removeBuilding = null;
        for(Building building :buildings){
            if (building.getPosition().x == x && building.getPosition().y == y){
                buildings.remove(building);
                removeBuilding = building;

            }
        }
        return removeBuilding;
    }

    public void subtractLongTermPoints(int points){
        if (longTermPoints < points){
            System.out.println("Error in subtract Long Term Points!!! -> Destroy Temple");
        }
        longTermPoints -= points;
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
