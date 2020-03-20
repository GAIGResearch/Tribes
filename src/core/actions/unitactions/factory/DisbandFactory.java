package core.actions.unitactions.factory;

import core.actions.Action;
import core.actions.ActionFactory;
import core.actions.unitactions.Disband;
import core.actors.Actor;
import core.actors.units.Unit;
import core.game.GameState;

import java.util.LinkedList;

public class DisbandFactory implements ActionFactory {

    @Override
    public LinkedList<Action> computeActionVariants(final Actor actor, final GameState gs) {
        LinkedList<Action> disbands = new LinkedList();
        Unit unit = (Unit) actor;

        Disband disbandAction = new Disband(unit.getActorId());
        if(disbandAction.isFeasible(gs))
            disbands.add(disbandAction);

        return disbands;
    }

}
