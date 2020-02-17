package core.actors;

import core.Types;
import core.actors.buildings.Building;

import java.util.LinkedList;

public class City extends Actor{

    private int tribeId;
    private int x;
    private int y;
    private int level;
    private int population = 0;
    private int population_need;
    private boolean isValley;
    private boolean isPrism;
    private int extraStar = 0;
    private int points = 0;
    private int longTermPoints = 0;

    private LinkedList<Integer> unitsID = new LinkedList<>();
    private LinkedList<Building> buildings = new LinkedList<>();
    int bound;

    // The constructor to initial the valley
    public City(int x, int y, int tribeId) {
        this.x = x;
        this.y = y;
        isValley = true;
        population_need = 0;
        bound = 1; //cities start with 1 tile around it for territory
        level = 1; //and starting level is 1
        isPrism = false;
        this.tribeId = tribeId;
    }

    public City(int x, int y, int level, int population, int population_need, boolean isValley, boolean isPrism, int extraStar, LinkedList<Building> buildings, int tribeId, LinkedList<Integer> unitsID) {
        this.x = x;
        this.y = y;
        this.level = level;
        this.population = population;
        this.population_need = population_need;
        this.isValley = isValley;
        this.isPrism = isPrism;
        this.extraStar = extraStar;
        this.buildings = buildings;
        this.tribeId = tribeId;
        this.unitsID = unitsID;
    }

    public void occupy(){
        if (isValley){
            isValley=false;
            levelUp();
        }
    }

    // Increase population
    public void addPopulation(int number){
        population += number;
        if (population > population_need){
            levelUp();
        }
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
        }else{
            addPoints(building.getPoints());
        }

        if (building.getTYPE().equals(Types.BUILDING.CUSTOM_HOUSE)){
            addExtraStar(building.getPRODUCTION());
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
                        addExtraStar(2);
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

    // Level up
    public void levelUp(){
        level++;
        population = population - population_need;
        population_need = level + 1;
        /*
        TODO: level up Bonus
        switch (level){
            case 1:
                break;
            case 2:
                // TODO: WorkShop -> Extra Star +1 or get explorer unit
                break;
            case 3:
                // TODO: isCityWall to Ture or getStar(num = 5)
                break;
            case 4:
                // TODO: getPopulation(num = 3) or increase border
                break;
            default:
               // TODO: getPark(250 points) or get super unit
        }
        */
        addPoints(getLevelUpPoints());
    }

    public void addExtraStar(int Star) {
        extraStar += Star;
    }

    public boolean addUnits(int id){
        if (unitsID.size() < level){
            unitsID.add(id);
            return true;
        }
        return false;
    }

    public void removeUnits(int id){
        for(int i=0; i<unitsID.size(); i++){
            if (unitsID.get(i) == id){
                unitsID.remove(i);
                return;
            }
        }
        System.out.println("Error!! Unit ID "+ id +" does not belong to this tribe");
    }

    public int getLevelUpPoints(){
        if (level == 1){
            return 100;
        }
        return 50 - level * 5;
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

    public int getStar(){
        return level + extraStar;
    }

    public int getPopulation() {
        return population;
    }

    public boolean isValley() {
        return isValley;
    }

    public boolean isPrism() {
        return isPrism;
    }

    public int getPopulation_need() {
        return population_need;
    }

    public Types.TERRAIN type(){
        if (isValley){
            return Types.TERRAIN.VILLAGE;
        }
        return Types.TERRAIN.CITY;
    }

    public LinkedList<Building> getBuildings() {
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

    public int getTribeId() {
        return tribeId;
    }

    public LinkedList<Integer> getUnitsID() {
        LinkedList<Integer> copyUnits = new LinkedList<>();
        for (Integer integer : unitsID) {
            copyUnits.add(integer);
        }
        return copyUnits;
    }

    public City copy(){
        return new City(x, y, level, population, population_need, isValley, isPrism, extraStar, getBuildings(), tribeId, getUnitsID());
    }

    public boolean getIsValley(){
        return this.isValley;
    }

    public void setIsValley(boolean isVal){
         this.isValley = isValley;
    }

    public int getBound(){
        return this.bound;
    }
    public void setBound(int b){
        this.bound = b;
    }
}
