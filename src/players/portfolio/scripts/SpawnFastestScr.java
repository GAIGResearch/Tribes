package players.portfolio.scripts;

import core.actions.Action;
import core.actors.Actor;
import core.game.GameState;
import players.portfolio.scripts.utils.MilitaryFunc;

public class SpawnFastestScr extends BaseScript {

    //This script returns the spawn action that spawns the fastest available unit. We
    //  understand the fastest unit as the one with the highest MOVEMENT value.

    @Override
    public Action process(GameState gs, Actor ac) {
        return new MilitaryFunc().getActionByActorAttr(gs, actions, ac, Feature.MOVEMENT, true);
    }

}
