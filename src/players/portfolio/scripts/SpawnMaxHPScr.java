package players.portfolio.scripts;

import core.actions.Action;
import core.actors.Actor;
import core.game.GameState;

public class SpawnMaxHPScr extends BaseScript {

    //This script returns the spawn action that spawns the available unit with the highest starting HP.

    @Override
    public Action process(GameState gs, Actor ac) {
        return new Func().getActionByActorAttr(gs, actions, ac, Feature.HP, true);
    }

}
