package core.actions.unitactions;

import core.actions.Action;
import core.game.GameState;
import core.actors.units.Unit;

import java.util.LinkedList;

public class HealOthers extends UnitAction
{
    public HealOthers(Unit healer)
    {
        super.unit = healer;
    }

    @Override
    public LinkedList<Action> computeActionVariants(final GameState gs) {
        //TODO: compute all healothers actions
        return new LinkedList<>();
    }

    @Override
    public boolean isFeasible(final GameState gs) {
        //TODO: check if this HealOthers is feasible.
        return false;
    }

    @Override
    public boolean execute(GameState gs) {
        //TODO: execute HealOthers
        return false;
    }
}
