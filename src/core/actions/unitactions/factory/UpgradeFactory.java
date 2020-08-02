package core.actions.unitactions.factory;

import core.actions.Action;
import core.actions.ActionFactory;
import core.actions.unitactions.Upgrade;
import core.actors.Actor;
import core.actors.units.Unit;
import core.game.GameState;
import core.Types;

import java.util.LinkedList;

public class UpgradeFactory implements ActionFactory {

    @Override
    public LinkedList<Action> computeActionVariants(final Actor actor, final GameState gs) {
        Unit unit = (Unit) actor;
        LinkedList<Action> upgradeActions = new LinkedList<>();

        Types.ACTION actionType = null;
        if(unit.getType() == Types.UNIT.BOAT) actionType = Types.ACTION.UPGRADE_BOAT;
        if(unit.getType() == Types.UNIT.SHIP) actionType = Types.ACTION.UPGRADE_SHIP;

        Upgrade action = new Upgrade(actionType, unit.getActorId());

        if(action.isFeasible(gs)){
            upgradeActions.add(action);
        }
        return upgradeActions;
    }

}
