package core.actions.unitactions;

import core.actions.Action;
import core.game.GameState;
import core.actors.units.Unit;

import java.util.LinkedList;

public class Disband extends UnitAction
{
    public Disband(Unit target)
    {
        super.unit = target;
    }

    @Override
    public LinkedList<Action> computeActionVariants(final GameState gs) {
        //TODO: compute all the Disband actions
        return null;
    }

    @Override
    public boolean isFeasible(final GameState gs)
    {
        //TODO: Check if it's feasible to Disband
        return false;
    }

    @Override
    public boolean execute(GameState gs) {
        //TODO: Execute Disband Action
        return false;
    }
}
