package players.portfolio.scripts;

import core.actions.Action;
import core.actions.unitactions.Attack;
import core.actors.Actor;
import core.actors.units.Unit;
import core.game.GameState;
import utils.Vector2d;

public class AttackClosestScr extends Script {

    //This script returns the attack action that targets the closest enemy unit.

    @Override
    public Action process(GameState gs, Actor ac)
    {
        Vector2d attackerPos = ac.getPosition();
        double minDist = Double.MAX_VALUE;
        Action finalAction = null;

        for(Action act : actions)
        {
            Attack action = (Attack)act;
            Unit target = (Unit) gs.getActor(action.getTargetId());
            Vector2d targetPos = target.getPosition();
            double dist = attackerPos.dist(targetPos);
            if(dist < minDist)
            {
                minDist = dist;
                finalAction = action;
            }
        }

        return finalAction;
    }

}
