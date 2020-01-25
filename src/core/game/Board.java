package core.game;

import core.Types;

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
    }

    // Return deep copy of board
    public Board copyBoard(){
        //todo
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
                break;
            case RIDER:
                return Types.UNIT.RIDER;
                break;
            case KNIGHT:
                return Types.UNIT.KNIGHT;
                break;
            case ARCHER:
                return Types.UNIT.ARCHER;
                break;
            case WARRIOR:
                return Types.UNIT.WARRIOR;
                break;
            case CATAPULT:
                return Types.UNIT.CATAPULT;
                break;
            case DEFENDER:
                return Types.UNIT.DEFENDER;
                break;
            case SWORDMAN:
                return Types.UNIT.SWORDMAN;
                break;
            case MIND_BEARER:
                return Types.UNIT.MIND_BEARER;
                break;
        }
        return Types.UNIT.NONE;
    }
}
