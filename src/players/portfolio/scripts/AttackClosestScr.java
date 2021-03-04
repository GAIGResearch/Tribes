package players.portfolio.scripts;

import core.actions.Action;
import core.actions.unitactions.Attack;
import core.actors.Actor;
import core.actors.units.Unit;
import core.game.GameState;
import utils.Vector2d;

public class AttackClosestScr extends BaseScript {

    //This script returns the attack action that targets the closest enemy unit.

    @Override
    public Action process(GameState gs, Actor ac)
    {
        return new Func().getActionByActorAttr(gs, actions, ac, Feature.DISTANCE, false);
    }

}
