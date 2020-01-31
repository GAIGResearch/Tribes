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

    public final boolean is_buildable(Board board){
        return board.getTerrainAt(x, y).equals(getTERRAIN_CONSTRAINT()) && board.getResourceAt(x, y).equals(getRESOURCE_CONSTRAINT());
    }
}
