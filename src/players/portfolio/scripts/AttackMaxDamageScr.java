package players.portfolio.scripts;

import core.actions.Action;
import core.actors.Actor;
import core.game.GameState;
import players.portfolio.scripts.utils.MilitaryFunc;
import utils.Pair;

public class AttackMaxDamageScr extends BaseScript {

    //This script returns the attack action that would cause more damage. Bonus if
    //  that damage also kills the enemy unit.

    @Override
    public Pair<Action, Double> process(GameState gs, Actor ac) {
        return new MilitaryFunc().getActionByActorAttr(gs, actions, ac, Feature.DAMAGE, true);
    }

}
