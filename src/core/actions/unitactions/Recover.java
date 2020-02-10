package core.actions.unitactions;

import core.actions.Action;
import core.game.GameState;
import core.actors.units.Unit;

import java.util.LinkedList;

public class Recover extends UnitAction
{
    public Recover(Unit target)
    {
        super.unit = target;
    }

    @Override
    public LinkedList<Action> computeActionVariants(final GameState gs) {
        //TODO: Compute all the Recover actions.
        return null;
    }

    @Override
    public boolean isFeasible(final GameState gs) {
        //TODO: is feasible this Recover action
        return false;
    }

    @Override
    public boolean execute(GameState gs) {
        //TODO: Execute this Recover action
        return false;
    }
}
