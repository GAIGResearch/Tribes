package players.portfolio.scripts;

import core.actions.Action;
import core.actors.Actor;
import core.game.GameState;

public class SpawnStrongestScr extends BaseScript {

    //This script returns the spawn action that spawns the strongest available unit. We
    //  understand the strongest unit as the one with the highest ATTACK value.

    @Override
    public Action process(GameState gs, Actor ac) {
        return new Func().getActionByActorAttr(gs, actions, ac, Feature.ATTACK, true);
    }

}
