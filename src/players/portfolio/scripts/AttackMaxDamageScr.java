package players.portfolio.scripts;

import core.actions.Action;
import core.actions.unitactions.Attack;
import core.actions.unitactions.command.AttackCommand;
import core.actors.Actor;
import core.actors.units.Unit;
import core.game.GameState;

public class AttackMaxDamageScr extends Script {

    //This script returns the attack action that would cause more damage. Bonus if
    //  that damage also kills the enemy unit.

    @Override
    public Action process(GameState gs, Actor ac) {

        int minDiff = Integer.MAX_VALUE;
        Action finalAction = null;

        for(Action act : actions)
        {
            GameState gsCopy = gs.copy();
            Attack action = (Attack)act;
            Unit target = (Unit) gs.getActor(action.getTargetId());

            int currHP = target.getCurrentHP();
            new AttackCommand().execute(action, gsCopy);
            int nextHP = target.getCurrentHP();

            int diff = nextHP - currHP;
            if(nextHP <= 0)
                diff -= 100;

            if(diff < minDiff)
            {
                minDiff = diff;
                finalAction = action;
            }
        }

        return finalAction;

    }

}
