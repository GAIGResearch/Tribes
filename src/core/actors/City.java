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


    public void addBuilding(Building building){
        if (building.type == WINDMILL || building.type == SAWMILL ||
                building.type == FORGE || building.type == CUSTOM_HOUSE){
            setProduction(building);
        }else if (building.type == FARM || building.type == LUMBER_HUT ||
                building.type == MINE || building.type == PORT){
            changeProduction(building);
        }else if (building.type == TEMPLE || building.type == WATER_TEMPLE){
            addPointsPerTurn(building.getPoints());
        }

        if (building.type != CUSTOM_HOUSE){
            addPopulation(building.getBonus());
        }

        buildings.add(building);
    }

    private void addPointsPerTurn(int points){
        this.pointsPerTurn = points;
    }

    public void setProduction(Building building){
        Vector2d pos = building.position;
        int production = 0;
        for(Building existBuilding: buildings){
            Vector2d existingPos = existBuilding.position;
            if ( (existingPos.x >= pos.x-1 && existingPos.x <= pos.x+1) && (existingPos.y >= pos.y-1 && existingPos.y <= pos.y+1)){
                if (checkMatchedBuilding(existBuilding, building)){
                    production++;
                }
            }
        }
        //TODO: building.setProduction(production);
    }

    public void changeProduction(Building building){
        Vector2d pos = building.position;
        for(Building existBuilding: buildings){
            Vector2d existingPos = existBuilding.position;
            if ( (existingPos.x >= pos.x-1 && existingPos.x <= pos.x+1) && (existingPos.y >= pos.y-1 && existingPos.y <= pos.y+1)){
                if (checkMatchedBuilding(building, existBuilding)){
                    if (existBuilding.type == FORGE){
                        addPopulation(2);
                    }else if(existBuilding.type == CUSTOM_HOUSE){
                        addProduction(2);
                        //TODO: existBuilding.setProduction(existBuilding.getBonus() + 2);
                    }else{
                        addPopulation(1);
                    }

                }
            }
        }
    }

    public boolean checkMatchedBuilding(Building original, Building functional){
        return (original.type == FARM && functional.type == Types.BUILDING.WINDMILL) ||
                (original.type == LUMBER_HUT && functional.type == SAWMILL) ||
                (original.type == MINE && functional.type == FORGE) ||
                (original.type == PORT && functional.type == CUSTOM_HOUSE);
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

    public boolean addUnitAble(){
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

    public int getProduction(){
        // If population less than 0, return start between [0 ~ level+production]
        if(population > 0) {
            return level + production;
        }
        return Math.max(0, (level + production - population));
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

    public Building removeBuilding(int x, int y){
        for(Building building :buildings){
            if (building.position.x == x && building.position.y == y){
                buildings.remove(building);
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
