package players.portfolio.scripts;

import core.actions.Action;
import core.actors.Actor;
import core.game.GameState;
import players.portfolio.scripts.utils.MilitaryFunc;
import utils.Pair;

public class AttackStrongestScr extends BaseScript {

    //This script returns the attack action that targets the strongest enemy unit. We
    //  understand the weakest unit as the one with the highest ATTACK value.

    @Override
    public Pair<Action, Double> process(GameState gs, Actor ac) {
        return new MilitaryFunc().getActionByActorAttr(gs, actions, ac, Feature.ATTACK, true);
    }

}
