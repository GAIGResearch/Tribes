package core.actors.buildings;

import core.Types;
import core.game.Board;
import utils.Vector2d;

public abstract class Building {

    protected Vector2d position;
    protected int cost;
    protected  Types.BUILDING type;
    protected int production = 0;
    protected int points = 0;

    public Building(int x, int y, int cost, Types.BUILDING type) {
        this.position = new Vector2d(x, y);
        this.cost = cost;
        this.type = type;
    }

    public Building(int x, int y, int cost, Types.BUILDING type, int production) {
        this.position = new Vector2d(x, y);
        this.cost = cost;
        this.type = type;
        this.production = production;
    }

    public Building(int x, int y, int cost, Types.BUILDING type, int production, int points) {
        this.position = new Vector2d(x, y);
        this.cost = cost;
        this.type = type;
        this.production = production;
        this.points = points;
    }

    public final Vector2d getPosition() {return position;}


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
            condition = board.getTerrainAt(position.x, position.y).equals(getTERRAIN_CONSTRAINT());
        }
        if (getTERRAIN_CONSTRAINT() != null && condition){
            condition = board.getResourceAt(position.x, position.y).equals(getRESOURCE_CONSTRAINT());
        }
        return condition;
    }

    public void setProduction(int production){
        this.production = production;
    }

}
