package core.actors.buildings;

import core.Types;
import core.game.Board;

public abstract class Building {


    private int x;
    private int y;


    public Building(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public final int getX() {
        return x;
    }

    public final int getY() {
        return y;
    }

    public abstract Building copy();

    public abstract Types.BUILDING getTYPE();

    public abstract int getCOST();

    public abstract int getPRODUCTION();

    public Types.TERRAIN getTERRAIN_CONSTRAINT() {
        return null;
    }

    public Types.RESOURCE getRESOURCE_CONSTRAINT(){return null;}

    public boolean is_buildable(Board board){
        boolean condition = true;
        if (getTERRAIN_CONSTRAINT() != null){
            condition = board.getTerrainAt(x, y).equals(getTERRAIN_CONSTRAINT());
        }
        if (getRESOURCE_CONSTRAINT() != null && condition){
            condition = board.getResourceAt(x, y).equals(getRESOURCE_CONSTRAINT());
        }
        return condition;
    }

    public void setProduction(int production){}

    public int getPoints(){return -1;}
}
