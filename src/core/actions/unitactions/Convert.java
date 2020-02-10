package core.actions.unitactions;

import core.actions.Action;
import core.game.GameState;
import core.actors.units.Unit;

import java.util.LinkedList;

public class Convert extends UnitAction
{
    private Unit target;

    public Convert(Unit attacker, Unit target)
    {
        super.unit = attacker;
    }

    public void setTarget(Unit target) {this.target = target;}
    public Unit getTarget() {
        return target;
    }

    @Override
    public LinkedList<Action> computeActionVariants(final GameState gs) {
        //TODO: compute all the Convert actions that are possible
        return null;
    }

    @Override
    public boolean isFeasible(final GameState gs) {
        //TODO: check if this Convert action is possible.
        return false;
    }

    @Override
    public boolean execute(GameState gs) {
        //TODO execute the Convert action
        return false;
    }
}
