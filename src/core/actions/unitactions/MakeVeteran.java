package core.actions.unitactions;

import core.actions.Action;
import core.game.GameState;
import core.actors.units.Unit;

import java.util.LinkedList;

public class MakeVeteran extends UnitAction
{
    public MakeVeteran(Unit target)
    {
        super.unit = target;
    }

    @Override
    public LinkedList<Action> computeActionVariants(final GameState gs) {
        //TODO: compute MakeVeteran actions
        return null;
    }

    @Override
    public boolean isFeasible(final GameState gs) {
        //TODO: is feasible this MakeVeteran action
        return false;
    }

    @Override
    public boolean execute(GameState gs) {
        //TODO: execute MakeVeteran action
        return false;
    }
}
