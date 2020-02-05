package core.game;

import core.Types;
import core.actions.cityactions.Build;
import core.units.*;

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
    private City[][] cities;

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
        cities = new City[size][size];
        this.size = size;
        this.tribes = new Tribe[4];
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

        for (int x = 0; x<this.size; x++){
            for(int y = 0; y<this.size; y++){
                Types.TERRAIN t = checkTerrain(x,y);
                Unit u = checkUnit(x,y);
                Types.RESOURCE r = checkResource(x,y);
                Building b = checkBuilding(x,y);
                City c = getCityAt(x,y);
                copyBoard.setTerrainAt(x,y,terrains[x][y]);
                copyBoard.setUnitAt(x,y,u);
                copyBoard.setResourceAt(x,y,resources[x][y]);
                copyBoard.setBuildingAt(x,y,buildings[x][y]);
                // TODO: Will copy over city later but need a city copy method in city class
                //if(c !=null)
                 //   copyBoard.setCityAt(x,y,c.copy());
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

    public void setCityAt(int x, int y, City city){
        cities[x][y] = city;
    }

    // Get Resource at pos x,y
    public Types.RESOURCE getResourceAt(int x, int y){
        return resources[x][y];
    }

    // Get Resource at pos x,y
    public Building getBuildingAt(int x, int y){
        return buildings[x][y];
    }

    public City getCityAt(int x, int y){
        return cities[x][y];
    }

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
            case "Swordsman":
                return new Swordsman(u.getCurrentPosition(), u.getKills(), u.isVeteran(), u.getOwnerID());
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
                return new WaterTemple(x,y);
            case FOREST_TEMPLE:
                return new ForestTemple(x,y);
            case MOUNTAIN_TEMPLE:
                return new MountainTemple(x,y);
            case ALTAR_OF_PEACE: //TODO: Need Altar of Peace class
                return null;
            case EMPERORS_TOMB:
                return new EmperorTomb(x,y);
            case EYE_OF_GOD: //TODO: Need Eye of God class
                return null;
            case GATE_OF_POWER:
                return new GateOfPower(x,y);
            case GRAND_BAZAR:
                return new GrandBazaar(x,y);
            case PARK_OF_FORTUNE:
                return new ParkOfFortune(x,y);
            case TOWER_OF_WISDOM:
                return new TowerOfWisdom(x,y);
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

    //TODO: Once city has owners
//    // Method to determine city borders, take city and x and y pos of city as params
//    public void setBorders(City city, int x, int y){
//        if (city.getTribe().equals(getCityAt(x, y).getTribe())) {
//            //   set border and city to that tribe
//        }
//        for (int i =x; i< x+2; i++){
//            for(int j = y; j<x+2; j++) {
//                //todo set border
//
//
//            }
//        }
//    }

    //TODO: Once city has owners
//    // Method to expand city borders, take city x and x and y pos of city as params
//    public void expandBorder(City city, int x, int y){
//        for (int i =x; i< x+2; i++){
//            for(int j = y; j<x+2; j++) {
//                //todo set border
//
//                if (city.getTribe().equals(getCityAt(x, y).getTribe())) {
//                    //   set border and city to that tribe
//                }
//            }
//        }
//    }


    public void occupy(int x, int y){
        City c = getCityAt(x,y);
        if (c.getIsValley()){
            c.setIsValley(false);
            // TODO: Assign the owner
            c.levelUp();
        }
    }


}
