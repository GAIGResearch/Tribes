package core.units;

import java.util.LinkedList;
import java.util.List;

public class City {

    private int x;
    private int y;
    private int level;
    private int population = 0;
    private int population_need;
    private boolean isValley;
    private boolean isPrism;
    private boolean hasWorkShop = false;
    // TODO: Add the owner(tribe)
    private LinkedList<Building> buildings = new LinkedList();

    // The constructor to initial the valley
    public City(int x, int y) {
        this.x = x;
        this.y = y;
        isValley = true;
        population_need = 0;
        level = 0;
        isPrism = false;
    }

    public City(int x, int y, int level, int population, int population_need, boolean isValley, boolean isPrism, boolean hasWorkShop, LinkedList<Building> buildings) {
        this.x = x;
        this.y = y;
        this.level = level;
        this.population = population;
        this.population_need = population_need;
        this.isValley = isValley;
        this.isPrism = isPrism;
        this.hasWorkShop = hasWorkShop;
        this.buildings = buildings;
    }

    /*
    TODO: ADD the constructor to initial the city (x, y, owner). isValley = false, isPrism = True,
    population_need = level+1, level = 1
     */

    // TODO: Parameter should get the owner and occupy need to assign the owner
    // Occupy the valley/city
    public void occupy(){
        if (isValley){
            levelUp();
        }
        // TODO: Assign the owner
    }

    // Increase population (return points)
    public int addPopulation(int number){
        population += number;
        if (population > population_need){
            return levelUp();
        }
        return 0;
    }

    public void addBuildings(Building building){
        buildings.add(building);
    }

    // Level up
    public int levelUp(){
        level++;
        population = population - population_need;
        population_need = level + 1;
        /*
        TODO: level up Bonus
        switch (level){
            case 1:
                break;
            case 2:
                // TODO: isWorkship to True or get explorer unit
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
        return getPoints();
    }

    public int getPoints(){
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
        if (hasWorkShop){
            return level + 1;
        }
        return level;
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

    public LinkedList<Building> getBuildings() {
        return (LinkedList<Building>)buildings.clone();
    }

    public City copy(){
        return new City(x, y, level, population, population_need, isValley, isPrism, hasWorkShop, (LinkedList<Building>)buildings.clone());
    }
}
