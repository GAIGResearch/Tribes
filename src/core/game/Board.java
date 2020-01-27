package core.game;

import core.Types;
import core.units.Unit;

public class Board {

    // Array for the type of terrain that each tile of board will have
    private Types.TERRAIN[][] terrains;
    
    // Array for resource each tile of the board will have
    private Types.RESOURCE[][] resources;
    
    // Array for units each tile of the board will have
    private Types.UNIT[][] units;

    private int size;

    // Constructor for board
    public Board (int size){
        terrains = new Types.TERRAIN[size][size];
        resources = new Types.RESOURCE[size][size];
        units = new Types.UNIT[size][size];
        this.size = size;
    }

    // Return deep copy of board
    public Board copyBoard(){
        Board copyBoard = new Board(this.size);

        for (int x = 0; x<this.size; x++){
            for(int y = 0; y<this.size; y++){
                Types.TERRAIN t = checkTerrain(x,y);
                Types.UNIT u = checkUnit(x,y);
                Types.RESOURCE r = checkResource(x,y);
                copyBoard.setTerrainAt(x,y,t);
                copyBoard.setUnitAt(x,y,u);
                copyBoard.setResourceAt(x,y,r);
            }
        }

        return new Board(this.size);
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

    // Get Resource at pos x,y
    public Types.RESOURCE getResourceAt(int x, int y){
        return resources[x][y];
    }


    // Moves a unit on the board if unit exists on tile
    void moveUnit(Types.DIRECTIONS direction, int x, int y){
        Types.UNIT[][] newUnits = new Types.UNIT[size][size];

        if(units[x][y] != Types.UNIT.NONE){
            System.out.println("Invalid move, there is no unit on this tile");
            return;
        }

        // Loop through old units array and update new units array with new position
        for(int i =0; i<units.length; i++){
            for(int j =0; x<units.length; x++){
                if((i !=x && j!=y) && (i != x+direction.x() && j != y+direction.y())){
                    newUnits[i][j] = checkUnit(i,j);
                }else if( i == x && j == y){
                    newUnits[i][j] = Types.UNIT.NONE;
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
        switch (units[x][y]){
            case NONE:
                return Types.UNIT.NONE;
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
        return Types.UNIT.NONE;
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
            case RUINS:
                return Types.TERRAIN.RUINS;
            case MOUNTAIN:
                return Types.TERRAIN.MOUNTAIN;
        }
        return Types.TERRAIN.PLAIN;
    }

    // Helper method to check which terrain at which tile for deep copying unit array
    Types.RESOURCE checkResource(int x, int y){
        switch (resources[x][y]){
            case FISH:
                return Types.RESOURCE.FISH;
            case FRUIT:
                return Types.RESOURCE.FRUIT;
            case FOREST:
                return Types.RESOURCE.FOREST;
            case  ANIMAL:
                return Types.RESOURCE.ANIMAL;
            case WHALES:
                return Types.RESOURCE.WHALES;
        }
        return Types.RESOURCE.NONE;
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


}
