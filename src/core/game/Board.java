package core.game;

import core.Types;
import core.actions.cityactions.Build;
import core.actors.City;
import core.actors.buildings.*;
import core.actors.units.*;
import core.units.*;

import java.util.ArrayList;

public class Board {

    // Array for the type of terrain that each tile of board will have
    private Types.TERRAIN[][] terrains;
    
    // Array for resource each tile of the board will have
    private Types.RESOURCE[][] resources;
    
    // Array for units each tile of the board will have
    // TODO: We need to know which tribe these units belong to.
    private Unit[][] units;

    // Array for buildings each tile of the board will have
    private Building[][] buildings;

    // Array for cities
   // private City[][] cities;

    // Array for tribes
    private Tribe [] tribes;

    // Array for id of each tile
    private int[][] id;



    //variable to declare size of board
    private int size;

    // Constructor for board
    public Board (int size){
        terrains = new Types.TERRAIN[size][size];
        resources = new Types.RESOURCE[size][size];
        units = new Unit[size][size];
        buildings = new Building[size][size];
        this.size = size;
        this.tribes = new Tribe[4];
        this.id = new int[size][size];

        //Initialise tile IDs
        for (int x = 0; x<size; x++){
            for(int y =0; y<size; y++){
                id[x][y] = -1;
            }
        }

        this.setBorders();

    }

    // Extra constructor for deep copy of board
    public Board (int size, Tribe[] tribes){
        terrains = new Types.TERRAIN[size][size];
        resources = new Types.RESOURCE[size][size];
        units = new Unit[size][size];
        buildings = new Building[size][size];
        this.size = size;
        this.tribes = tribes;
        this.id = new int[size][size];

        //Initialise tile IDs
        for (int x = 0; x<size; x++){
            for(int y =0; y<size; y++){
                id[x][y] = -1;
            }
        }

    }

    // Return deep copy of board
    public Board copyBoard(){
        Board copyBoard = new Board(this.size);
        Tribe[] copyTribes = new Tribe[4];
        // Copy tribes and cities
        for (int i = 0; i< tribes.length; i++){
            Tribe copyT = new Tribe();
            copyT.setTribeID(tribes[i].getTribeID());
            copyT.setScore(tribes[i].getScore());
            ArrayList<City> cities = tribes[i].getCities();
            ArrayList<City> copyCities = new ArrayList<>();
            for (City c:cities) {
                // TODO: Will copy over tribe city later but need a city copy method in city class
                // City copyCity =  c.copy();
                // copycities.add(copyCity)
            }
            copyT.setCities(cities);
        }

        copyBoard.setTribes(copyTribes);

        // Copy board objects
        for (int x = 0; x<this.size; x++){
            for(int y = 0; y<this.size; y++){
                Types.TERRAIN t = checkTerrain(x,y);
                Unit u = checkUnit(x,y);
                Types.RESOURCE r = checkResource(x,y);
                Building b = checkBuilding(x,y);
                copyBoard.setTerrainAt(x,y,terrains[x][y]);
                copyBoard.setUnitAt(x,y,u);
                copyBoard.setResourceAt(x,y,resources[x][y]);
                copyBoard.setBuildingAt(x,y,buildings[x][y]);
                copyBoard.setBorders();
            }
        }

        return copyBoard;
    }



    // Get size of board
    public int getSize() {
        return size;
    }

    // Get terrains array
    public Types.TERRAIN[][] getTerrains(){
        return this.terrains;
    }

    // Get units array
    public Unit[][] getUnits(){
        return this.units;
    }

    // Get Resources array
    public Types.RESOURCE[][] getResources(){
        return this.resources;
    }

    // Get Terrain at pos x,y
    public Types.TERRAIN getTerrainAt(int x, int y){
        return terrains[x][y];
    }

    // Get Unit at pos x,y
    public Unit getUnitAt(int x, int y){
        return units[x][y];
    }

    // Set Resource at pos x,y
    public void setResourceAt(int x, int y, Types.RESOURCE r){
        resources[x][y] =  r;
    }
    // Set Terrain at pos x,y
    public void setTerrainAt(int x, int y, Types.TERRAIN t){
        terrains[x][y] =  t;
    }

    // Set Terrain at pos x,y
    public void setUnitAt(int x, int y, Unit u){
        units[x][y] =  u;
    }

    // Set Building at pos x,y
    public void setBuildingAt(int x, int y, Building b){
        buildings[x][y] = b;
    }

//    public void setCityAt(int x, int y, City city){
//        cities[x][y] = city;
//    }

    // Get Resource at pos x,y
    public Types.RESOURCE getResourceAt(int x, int y){
        return resources[x][y];
    }

    // Get Resource at pos x,y
    public Building getBuildingAt(int x, int y){
        return buildings[x][y];
    }

    //public City getCityAt(int x, int y){
  //      return cities[x][y];
  //  }

    // Moves a unit on the board if unit exists on tile
    void moveUnit(Types.DIRECTIONS direction, int x, int y){
        Unit[][] newUnits = new Unit[size][size];

        if(units[x][y] == null){
            System.out.println("Invalid move, there is no unit on this tile");
            return;
        }

        // Loop through old units array and update new units array with new position
        for(int i =0; i<units.length; i++){
            for(int j =0; x<units.length; x++){
                if((i !=x && j!=y) && (i != x+direction.x() && j != y+direction.y())){
                    newUnits[i][j] = checkUnit(i,j);
                }else if( i == x && j == y){
                    newUnits[i][j] = null;
                }else if(i == x+direction.x() && j == y+direction.y()){
                    Unit oldUnit = checkUnit(x,y);
                    newUnits[i][j] = oldUnit;
                }
            }
        }

        // Update units array
        units = newUnits;

    }

    // Helper method to check which unit at which tile for deep copying unit array
    Unit checkUnit(int x, int y){
        Unit u = units[x][y];
        String unitName = u.getClass().getName();
        switch (unitName){
            case "Rider":
                return new Rider(u.getCurrentPosition(), u.getKills(), u.isVeteran(), u.getOwnerID());
            case "Knight":
                return new Knight(u.getCurrentPosition(), u.getKills(), u.isVeteran(), u.getOwnerID());
            case "Archer":
                return new Archer(u.getCurrentPosition(), u.getKills(), u.isVeteran(), u.getOwnerID());
            case "Warrior":
                return new Warrior(u.getCurrentPosition(), u.getKills(), u.isVeteran(), u.getOwnerID());
            case "Catapult":
                return new Catapult(u.getCurrentPosition(), u.getKills(), u.isVeteran(), u.getOwnerID());
            case "Defender":
                return new Defender(u.getCurrentPosition(), u.getKills(), u.isVeteran(), u.getOwnerID());
            case "Swordman":
                return new Swordman(u.getCurrentPosition(), u.getKills(), u.isVeteran(), u.getOwnerID());
            //case 'MIND_BEARER':
            //    return new Warrior(u.getCurrentPosition(), u.getKills(), u.isVeteran(), u.getOwnerID());
        }
        return u;
    }

    // Helper method to check which terrain at which tile for deep copying unit array
    Types.TERRAIN checkTerrain(int x, int y){
        switch (terrains[x][y]){
            case PLAIN :
                return Types.TERRAIN.PLAIN;
            case VILLAGE:
                return Types.TERRAIN.VILLAGE;
            case SHALLOW_WATER:
                return Types.TERRAIN.SHALLOW_WATER;
            case  DEEP_WATER:
                return Types.TERRAIN.DEEP_WATER;
            case CITY:
                return Types.TERRAIN.CITY;
            case MOUNTAIN:
                return Types.TERRAIN.MOUNTAIN;
            case FOREST:
                return Types.TERRAIN.FOREST;
        }
        return Types.TERRAIN.PLAIN;
    }

    // Helper method to check which terrain at which tile for deep copying unit array
    Types.RESOURCE checkResource(int x, int y){
        Types.RESOURCE r = null;
        switch (resources[x][y]){
            case FISH:
                return Types.RESOURCE.FISH;
            case FRUIT:
                return Types.RESOURCE.FRUIT;
            case ANIMAL:
                return Types.RESOURCE.ANIMAL;
            case WHALES:
                return Types.RESOURCE.WHALES;
            case ORE:
                return Types.RESOURCE.ORE;
            case CROPS:
                return Types.RESOURCE.CROPS;
            case RUINS:
                return Types.RESOURCE.RUINS;
        }
        return r;
    }

    // Helper method to check which Building at which tile for deep copying building array
    Building checkBuilding(int x, int y){
        Building b = getBuildingAt(x,y);
        switch (b.getTYPE()){
            case TEMPLE:
                return new Temple(x,y);
            case  PORT:
                return new Port(x,y);
            case MINE:
                return new Mine(x,y);
            case  FORGE:
                return new Forge(x,y);
            case FARM:
                return new Farm(x,y);
            case WINDMILL:
                return new Windmill(x,y);
            case ROAD:
                return null;
            case CUSTOM_HOUSE:
                return new CustomHouse(x,y);
            case LUMBER_HUT:
                return new LumberHut(x,y);
            case SAWMILL:
                return new Sawmill(x,y);
            case WATER_TEMPLE:
                return null; // TODO: Need Water Temple class
            case FOREST_TEMPLE:
                return new ForestTemple(x,y);
            case MOUNTAIN_TEMPLE:
                return null; // TODO: Need Mountain Temple class
            case ALTAR_OF_PEACE: //TODO: Need Altar of Peace class
                return null;
            case EMPERORS_TOMB: //TODO: Need Emperors tomb class
                return null;
            case EYE_OF_GOD: //TODO: Need Eye of God class
                return null;
            case GATE_OF_POWER: //TODO: Need Gate of power class
                return null;
            case GRAND_BAZAR: //TODO: Need Grand Bazar class
                return null;
            case PARK_OF_FORTUNE: //TODO: Need Park of fortune class
                return null;
            case TOWER_OF_WISDOM: //TODO: Need tower of wisdom class
                return null;
        }
        return b;
    }

    // Setter method for units array
    public void setUnits(Unit[][] u){
        this.units = u;
    }

    // Setter method for terrains array
    public void setTerrains(Types.TERRAIN[][] t){
        this.terrains = t;
    }

    // Setter method for resources array
    public void setResources(Types.RESOURCE[][] r){
        this.resources = r;
    }

    // Method to determine city borders, take city and x and y pos of city as params
    public void setBorders(){
        ArrayList<City> tribe1Cities = tribes[0].getCities();
        ArrayList<City> tribe2Cities = tribes[1].getCities();
        ArrayList<City> tribe3Cities = tribes[2].getCities();
        ArrayList<City> tribe4Cities = tribes[3].getCities();

        for (City c: tribe1Cities) {
            setBorderHelper(c,c.getBound());
        }

        for (City c: tribe2Cities
             ) {
            setBorderHelper(c,c.getBound());
        }


        for (City c: tribe3Cities
        ) {
            setBorderHelper(c, c.getBound());
        }

        for (City c: tribe4Cities
        ) {
            setBorderHelper(c, c.getBound());
        }


    }

    // Set border helper method to set city bounds
    public void setBorderHelper(City c, int bound){
        int x = c.getX();
        int y = c.getY();
        for (int i =x-bound; i< x+bound; i++){
            for(int j = y-bound; j<x+bound; j++) {
                if(id[i][j] != -1){
                    id[i][j] = tribes[0].getActorID();
                }
            }
        }
    }

    // Method to expand city borders, take city x and x and y pos of city as params
    public void expandBorder(City city, int x, int y){

        city.setBound(city.getBound()+1);
        setBorderHelper(city,city.getBound());

    }


    public void occupy(Tribe t, int x, int y){
        ArrayList<City> cities = t.getCities();
        City c = null;
        for (City city:
             cities) {
            if (city.getX() == x && city.getY() == y){
                c = city;
                break;
            }
        }
        if (c.getIsValley()){
            c.setIsValley(false);
            // TODO: Assign the owner
            c.levelUp();
        }
    }

    // Setter method for tribes array
    public void setTribes(Tribe[] t){
        this.tribes = t;
    }


}
