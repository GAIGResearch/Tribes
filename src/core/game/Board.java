package core.game;

import core.Types;
import core.units.City;
import core.units.Unit;

public class Board {

    // Array for the type of terrain that each tile of board will have
    private Types.TERRAIN[][] terrains;
    
    // Array for resource each tile of the board will have
    private Types.RESOURCE[][] resources;
    
    // Array for units each tile of the board will have
    // TODO: We need to know which tribe these units belong to.
    private Types.UNIT[][] units;

    // Array for buildings each tile of the board will have
    private Types.BUILDING[][] buildings;

    // Array for cities
    private City[][] cities;



    //variable to declare size of board
    private int size;

    // Constructor for board
    public Board (int size){
        terrains = new Types.TERRAIN[size][size];
        resources = new Types.RESOURCE[size][size];
        units = new Types.UNIT[size][size];
        buildings = new Types.BUILDING[size][size];
        cities = new City[size][size];
        this.size = size;
    }

    // Return deep copy of board
    public Board copyBoard(){
        Board copyBoard = new Board(this.size);

        for (int x = 0; x<this.size; x++){
            for(int y = 0; y<this.size; y++){
//                Types.TERRAIN t = checkTerrain(x,y);
//                Types.UNIT u = checkUnit(x,y);
//                Types.RESOURCE r = checkResource(x,y);
//                Types.BUILDING b = checkBuilding(x,y);
                City c = getCityAt(x,y);
                copyBoard.setTerrainAt(x,y,terrains[x][y]);
                copyBoard.setUnitAt(x,y,units[x][y]);
                copyBoard.setResourceAt(x,y,resources[x][y]);
                copyBoard.setBuildingAt(x,y,buildings[x][y]);
                // todo Will copy over city later but need a city copy method in city class
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
    public Types.UNIT[][] getUnits(){
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
    public Types.UNIT getUnitAt(int x, int y){
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
    public void setUnitAt(int x, int y, Types.UNIT u){
        units[x][y] =  u;
    }

    // Set Building at pos x,y
    public void setBuildingAt(int x, int y, Types.BUILDING b){
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
    public Types.BUILDING getBuildingAt(int x, int y){
        return buildings[x][y];
    }

    public City getCityAt(int x, int y){
        return cities[x][y];
    }

    // Moves a unit on the board if unit exists on tile
    void moveUnit(Types.DIRECTIONS direction, int x, int y){
        Types.UNIT[][] newUnits = new Types.UNIT[size][size];

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
                    Types.UNIT oldUnit = checkUnit(x,y);
                    newUnits[i][j] = oldUnit;
                }
            }
        }

        // Update units array
        units = newUnits;

    }

    // Helper method to check which unit at which tile for deep copying unit array
    Types.UNIT checkUnit(int x, int y){
        Types.UNIT u = null;
        switch (units[x][y]){
            case RIDER:
                return Types.UNIT.RIDER;
            case KNIGHT:
                return Types.UNIT.KNIGHT;
            case ARCHER:
                return Types.UNIT.ARCHER;
            case WARRIOR:
                return Types.UNIT.WARRIOR;
            case CATAPULT:
                return Types.UNIT.CATAPULT;
            case DEFENDER:
                return Types.UNIT.DEFENDER;
            case SWORDMAN:
                return Types.UNIT.SWORDMAN;
            case MIND_BEARER:
                return Types.UNIT.MIND_BEARER;
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

    // Helper method to check which Building at which tile for deep copying unit array
    Types.BUILDING checkBuilding(int x, int y){
        Types.BUILDING b = null;
        switch (buildings[x][y]){
            case TEMPLE:
                return Types.BUILDING.TEMPLE;
            case  PORT:
                return Types.BUILDING.PORT;
            case MINE:
                return Types.BUILDING.MINE;
            case  FORGE:
                return Types.BUILDING.FORGE;
            case FARM:
                return Types.BUILDING.FARM;
            case WINDMILL:
                return Types.BUILDING.WINDMILL;
            case ROAD:
                return Types.BUILDING.ROAD;
            case CUSTOM_HOUSE:
                return Types.BUILDING.CUSTOM_HOUSE;
            case LUMBER_HUT:
                return Types.BUILDING.LUMBER_HUT;
            case SAWMILL:
                return Types.BUILDING.SAWMILL;
            case WATER_TEMPLE:
                return Types.BUILDING.WATER_TEMPLE;
            case FOREST_TEMPLE:
                return Types.BUILDING.FOREST_TEMPLE;
            case MOUNTAIN_TEMPLE:
                return Types.BUILDING.MOUNTAIN_TEMPLE;
            case ALTAR_OF_PEACE:
                return Types.BUILDING.ALTAR_OF_PEACE;
            case EMPERORS_TOMB:
                return Types.BUILDING.EMPERORS_TOMB;
            case EYE_OF_GOD:
                return Types.BUILDING.EYE_OF_GOD;
            case GATE_OF_POWER:
                return Types.BUILDING.GATE_OF_POWER;
            case GRAND_BAZAR:
                return Types.BUILDING.GRAND_BAZAR;
            case PARK_OF_FORTUNE:
                return Types.BUILDING.PARK_OF_FORTUNE;
            case TOWER_OF_WISDOM:
                return Types.BUILDING.TOWER_OF_WISDOM;
        }
        return b;
    }

    // Setter method for units array
    public void setUnits(Types.UNIT[][] u){
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

}
