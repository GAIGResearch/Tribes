package core.actions.unitactions;

import core.actions.Action;
import core.game.GameState;
import core.actors.units.Unit;

import java.util.LinkedList;

public class Move extends UnitAction
{
    private int destX;
    private int destY;

    public Move(Unit u)
    {
        super.unit = u;
    }

    public void setDest(int x, int y) {this.destX = x; this.destY = y;}
    public int getDestX() {
        return destX;
    }
    public int getDestY() {
        return destY;
    }

    @Override
    public LinkedList<Action> computeActionVariants(final GameState gs) {
        //TODO: compute all the possible Move actions for super.unit.
        return new LinkedList<>();
    }

    @Override
    public boolean isFeasible(final GameState gs)
    {
        //TODO: isFeasible this Move action
        return true;
    }

    @Override
    public boolean execute(GameState gs) {
        //TODO Execute this Move action
        return true;
    }
}
