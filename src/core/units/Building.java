package core.units;

import core.Types;
import core.game.Board;

public abstract class Building {


    private int x;
    private int y;


    public Building(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public Building copy(){return null;}

    public Types.BUILDING getTYPE() {
        return null;
    }

    public int getCOST() {
        return -1;
    }

    public int getPRODUCTION() {
        return -1;
    }

    public Types.TERRAIN getTERRAIN_CONSTRAINT() {
        return null;
    }

    public Types.RESOURCE getRESOURCE_CONSTRAINT(){return null;}

    public boolean is_buildable(Board board){
        return board.getTerrainAt(x, y).equals(getTERRAIN_CONSTRAINT()) && board.getResourceAt(x, y).equals(getRESOURCE_CONSTRAINT());
    }
}
