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

}
