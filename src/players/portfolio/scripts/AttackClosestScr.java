package players.portfolio.scripts;

import core.actions.Action;
import core.actors.Actor;
import core.game.GameState;
import players.portfolio.scripts.utils.MilitaryFunc;

public class AttackClosestScr extends BaseScript {

    //This script returns the attack action that targets the closest enemy unit.

    @Override
    public Action process(GameState gs, Actor ac)
    {
        return new MilitaryFunc().getActionByActorAttr(gs, actions, ac, Feature.DISTANCE, false);
    }

}
