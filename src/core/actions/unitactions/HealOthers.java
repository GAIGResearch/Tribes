package core.actions.unitactions;

import core.actions.Action;
import core.game.GameState;
import core.actors.units.Unit;

import java.util.LinkedList;

public class HealOthers extends UnitAction
{
    private LinkedList<Unit> targets;

    public HealOthers(Unit healer)
    {
        super.unit = healer;
    }

    @Override
    public LinkedList<Action> computeActionVariants(final GameState gs) {
        LinkedList<Action> actions = new LinkedList<>();
        HealOthers action = new HealOthers(unit);

        if(isFeasible(gs)){ actions.add(action); }
        return actions;
    }

    @Override
    public boolean isFeasible(final GameState gs) {
        //TODO: check if this HealOthers is feasible.
        //add some kind of action point system for every unit?
        //action point can be used to move a unit, attack, use an ability
        return false;
    }

    @Override
    public boolean execute(GameState gs) {
        //TODO: execute HealOthers
        //implement adjacency, action points

        return false;
    }
}
