package players.portfolio.scripts;

import core.actions.Action;
import core.actors.Actor;
import core.game.GameState;
import players.portfolio.scripts.utils.MilitaryFunc;

public class ConvertStrongestScr extends BaseScript {

    //This script returns the convert action that targets the strongest enemy unit. We
    //  understand the strongest unit as the one with the highest ATTACK value.

    @Override
    public Action process(GameState gs, Actor ac) {
        return new MilitaryFunc().getActionByActorAttr(gs, actions, ac, Feature.ATTACK, true);
    }

}
