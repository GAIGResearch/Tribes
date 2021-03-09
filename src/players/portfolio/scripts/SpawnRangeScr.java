package players.portfolio.scripts;

import core.actions.Action;
import core.actors.Actor;
import core.game.GameState;
import players.portfolio.scripts.utils.MilitaryFunc;

public class SpawnRangeScr extends BaseScript {

    //This script returns the spawn action that spawns the available unit with the highest range.

    @Override
    public Action process(GameState gs, Actor ac) {
        return new MilitaryFunc().getActionByActorAttr(gs, actions, ac, Feature.RANGE, true);
    }

}
