package core.actions.unitactions.factory;

import core.actions.Action;
import core.actions.ActionFactory;
import core.actions.unitactions.MakeVeteran;
import core.actors.Actor;
import core.actors.units.Unit;
import core.game.GameState;

import java.util.LinkedList;

public class MakeVeteranFactory implements ActionFactory {

    @Override
    public LinkedList<Action> computeActionVariants(final Actor actor, final GameState gs) {
        Unit unit = (Unit) actor;
        LinkedList<Action> actions = new LinkedList<>();

        MakeVeteran action = new MakeVeteran(unit.getActorId());
        if(action.isFeasible(gs))
        {
            actions.add(action);
        }

        return actions;
    }

}
