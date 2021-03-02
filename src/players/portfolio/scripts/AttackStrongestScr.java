package players.portfolio.scripts;

import core.actions.Action;
import core.actions.unitactions.Attack;
import core.actors.Actor;
import core.actors.units.Unit;
import core.game.GameState;

public class AttackStrongestScr extends Script {

    //This script returns the attack action that targets the weakest enemy unit. We
    //  understand the weakest unit as the one with the lowest ATTACK value.

    @Override
    public Action process(GameState gs, Actor ac) {

        int minDefValue = Integer.MAX_VALUE;
        Action finalAction = null;

        for(Action act : actions)
        {
            Attack action = (Attack)act;
            Unit target = (Unit) gs.getActor(action.getTargetId());

            if(target.ATK < minDefValue)
            {
                minDefValue = target.ATK;
                finalAction = action;
            }
        }

        return finalAction;

    }

}
