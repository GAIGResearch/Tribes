package core.actions.unitactions;

import core.game.GameState;
import core.units.Unit;

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

    @Override
    public boolean isFeasible(GameState gs) {
        return false;
    }

    @Override
    public void execute(GameState gs) {

    }
}
