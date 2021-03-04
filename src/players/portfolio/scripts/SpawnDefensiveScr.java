package players.portfolio.scripts;

import core.actions.Action;
import core.actors.Actor;
import core.game.GameState;

public class SpawnDefensiveScr extends BaseScript {

    //This script returns the spawn action that spawns the most defensive available unit. We
    //  understand the most defensive unit as the one with the highest DEFENCE value.

    @Override
    public Action process(GameState gs, Actor ac) {
        return new Func().getActionByActorAttr(gs, actions, ac, Feature.DEFENCE, true);
    }

}
