package core.actions.unitactions;

import core.actions.Action;
import core.game.GameState;
import core.actors.units.Unit;

import java.util.LinkedList;

public class Upgrade extends UnitAction
{
    public Upgrade(Unit target)
    {
        super.unit = target;
    }

    @Override
    public LinkedList<Action> computeActionVariants(final GameState gs) {
        //TODO: Compute all the available Upgrade actions
        return null;
    }

    @Override
    public boolean isFeasible(final GameState gs) {
        //TODO: check if this Upgrade action is feasible.
        return false;
    }

    @Override
    public boolean execute(GameState gs) {
        //TODO: executes this Upgrade action
        return false;
    }
}
