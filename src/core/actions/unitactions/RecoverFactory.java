package core.actions.unitactions;

import core.actions.Action;
import core.actions.ActionFactory;
import core.actors.Actor;
import core.actors.units.Unit;
import core.game.GameState;

import java.util.LinkedList;

public class RecoverFactory implements ActionFactory {

    @Override
    public LinkedList<Action> computeActionVariants(final Actor actor, final GameState gs) {
        Unit unit = (Unit) actor;
        LinkedList<Action> actions = new LinkedList<>();
        Recover newAction = new Recover(unit.getActorId());
        if(newAction.isFeasible(gs)){
            actions.add(newAction);
        }
        return actions;
    }

}
