package core.actions.unitactions.factory;

import core.Types;
import core.actions.Action;
import core.actions.ActionFactory;
import core.actions.unitactions.HealOthers;
import core.actors.Actor;
import core.actors.units.Unit;
import core.game.Board;
import core.game.GameState;
import utils.Vector2d;

import java.util.LinkedList;

public class HealOthersFactory implements ActionFactory {

    @Override
    public LinkedList<Action> computeActionVariants(final Actor actor, final GameState gs) {
        LinkedList<Action> actions = new LinkedList<>();
        Unit unit = (Unit) actor;

        //Only if the unit can 'attack'
        if(unit.canAttack())
        {
            HealOthers action = new HealOthers(unit.getActorId());
            if(action.isFeasible(gs)){
                actions.add(action);
            }
        }

        return actions;
    }

}
