package players.portfolio.scripts;

import core.actions.Action;
import core.actors.Actor;
import core.game.GameState;

public class ConvertHighestHPScr extends BaseScript {

    //This script returns the convert action that targets the enemy unit with highest current HP.


    @Override
    public Action process(GameState gs, Actor ac) {
        return new Func().getActionByActorAttr(gs, actions, ac, Feature.HP, true);
    }

}
