package core.actions.unitactions;

import core.actors.units.Unit;

public class Move extends UnitAction
{
    private int destX;
    private int destY;

    public Move (Unit u, int x, int y)
    {
        super.unit = u;
        destX = x;
        destY = y;
    }

    public int getDestX() {
        return destX;
    }

    public int getDestY() {
        return destY;
    }
}
