package core.actors.buildings;

import core.Types;
import core.game.Board;

public abstract class Building {


    private int x;
    private int y;
    private int cost;
    private  Types.BUILDING type;
    private int production = 0;
    private int points = 0;

    public Building(int x, int y, int cost, Types.BUILDING type) {
        this.x = x;
        this.y = y;
        this.cost = cost;
        this.type = type;
    }

    public Building(int x, int y, int cost, Types.BUILDING type, int production) {
        this.x = x;
        this.y = y;
        this.cost = cost;
        this.type = type;
        this.production = production;
    }

    public Building(int x, int y, int cost, Types.BUILDING type, int production, int points) {
        this.x = x;
        this.y = y;
        this.cost = cost;
        this.type = type;
        this.production = production;
        this.points = points;
    }

    public final int getX() {
        return x;
    }

    public final int getY() {
        return y;
    }

    public abstract Building copy();

    public Types.BUILDING getTYPE(){
        return type;
    }

    public int getCOST(){
        return cost;
    }

    public int getPRODUCTION(){
        return production;
    }

    public int getPoints(){ return points;}

    public Types.TERRAIN getTERRAIN_CONSTRAINT() {
        return null;
    }

    public Types.RESOURCE getRESOURCE_CONSTRAINT(){return null;}

    public boolean is_buildable(Board board){
        boolean condition = true;
        if (getTERRAIN_CONSTRAINT() != null){
            condition = board.getTerrainAt(x, y).equals(getTERRAIN_CONSTRAINT());
        }
        if (getTERRAIN_CONSTRAINT() != null && condition){
            condition = board.getResourceAt(x, y).equals(getRESOURCE_CONSTRAINT());
        }
        return condition;
    }

    public void setProduction(int production){
        this.production = production;
    }

}
