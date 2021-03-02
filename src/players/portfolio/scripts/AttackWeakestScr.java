package players.portfolio.scripts;

import core.actions.Action;
import core.actions.unitactions.Attack;
import core.actors.Actor;
import core.actors.units.Unit;
import core.game.GameState;
import utils.Vector2d;

public class AttackWeakestScr extends Script {

    //This script returns the attack action that targets the weakest enemy unit. We
    //  understand the weakest unit as the one with the lowest DEFENCE value.

    @Override
    public Action process(GameState gs, Actor ac) {

        int minDefValue = Integer.MAX_VALUE;
        Action finalAction = null;

        for(Action act : actions)
        {
            Attack action = (Attack)act;
            Unit target = (Unit) gs.getActor(action.getTargetId());

            if(target.DEF < minDefValue)
            {
                minDefValue = target.DEF;
                finalAction = action;
            }
        }

        return finalAction;

    }

}
