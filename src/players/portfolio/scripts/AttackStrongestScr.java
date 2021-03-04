package players.portfolio.scripts;

import core.actions.Action;
import core.actions.unitactions.Attack;
import core.actors.Actor;
import core.actors.units.Unit;
import core.game.GameState;

public class AttackStrongestScr extends BaseScript {

    //This script returns the attack action that targets the strongest enemy unit. We
    //  understand the weakest unit as the one with the highest ATTACK value.

    @Override
    public Action process(GameState gs, Actor ac) {
        return new Func().getActionByActorAttr(gs, actions, ac, Feature.ATTACK, true);
    }

}
