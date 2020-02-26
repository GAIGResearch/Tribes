package core.actors;

import core.Types;
import core.actors.buildings.Building;

import java.util.LinkedList;

public class City extends Actor{

    private int x;
    private int y;
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
        this.x = x;
        this.y = y;
        population_need = 0;
        bound = 1; //cities start with 1 tile around it for territory
        level = 1; //and starting level is 1
        isCapital = false;
        this.tribeId = tribeId;
    }

    public City(int x, int y, int level, int population, int population_need, boolean isCapital, int production, LinkedList<Building> buildings, int tribeId, LinkedList<Integer> unitsID) {
        this.x = x;
        this.y = y;
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
        if (building.getTYPE().equals(Types.BUILDING.WINDMILL) || building.getTYPE().equals(Types.BUILDING.SAWMILL)
                || building.getTYPE().equals(Types.BUILDING.FORGE) || building.getTYPE().equals(Types.BUILDING.CUSTOM_HOUSE)){
            setProduction(building);
        }else if (building.getTYPE().equals(Types.BUILDING.FARM) || building.getTYPE().equals(Types.BUILDING.LUMBER_HUT)
                || building.getTYPE().equals(Types.BUILDING.MINE) || building.getTYPE().equals(Types.BUILDING.PORT)){
            changeProduction(building);
        }else if (building.getTYPE().equals(Types.BUILDING.TEMPLE) || building.getTYPE().equals(Types.BUILDING.WATER_TEMPLE)){
            addLongTimePoints(building.getPoints());
            addPoints(building.getPoints());
        }else{
            addPoints(building.getPoints());
        }

        if (building.getTYPE().equals(Types.BUILDING.CUSTOM_HOUSE)){
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
        int x = building.getX();
        int y = building.getY();
        int production = 0;
        for(Building existBuilding: buildings){
            int exist_x = existBuilding.getX();
            int exist_y = existBuilding.getY();
            if ( (exist_x >= x-1 && exist_x <= x+1) && (exist_y >= y-1 && exist_y <= y+1)){
                if (checkMatchedBuilding(existBuilding, building)){
                    production++;
                }
            }
        }
        building.setProduction(production);
    }

    public void changeProduction(Building building){
        int x = building.getX();
        int y = building.getY();
        for(Building existBuilding: buildings){
            int exist_x = existBuilding.getX();
            int exist_y = existBuilding.getY();
            if ( (exist_x >= x-1 && exist_x <= x+1) && (exist_y >= y-1 && exist_y <= y+1)){
                if (checkMatchedBuilding(building, existBuilding)){
                    if (existBuilding.getTYPE().equals(Types.BUILDING.FORGE)){
                        addPopulation(2);
                    }else if(existBuilding.getTYPE().equals(Types.BUILDING.CUSTOM_HOUSE)){
                        addProduction(2);
                    }else{
                        addPopulation(1);
                    }

                }
            }
        }
    }

    public boolean checkMatchedBuilding(Building original, Building functional){
        return original.getTYPE().equals(Types.BUILDING.FARM) && functional.getTYPE().equals(Types.BUILDING.WINDMILL) ||
                original.getTYPE().equals(Types.BUILDING.LUMBER_HUT) && functional.getTYPE().equals(Types.BUILDING.SAWMILL) ||
                original.getTYPE().equals(Types.BUILDING.MINE) && functional.getTYPE().equals(Types.BUILDING.FORGE) ||
                original.getTYPE().equals(Types.BUILDING.PORT) && functional.getTYPE().equals(Types.BUILDING.CUSTOM_HOUSE);
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


    public int getX() {
        return x;
    }

    public int getY() {
        return y;
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
        City c = new City(x, y, level, population, population_need, isCapital, production, copyBuildings(), tribeId, copyUnitsID());
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
            if (building.getX() == x && building.getY() == y){
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

}
