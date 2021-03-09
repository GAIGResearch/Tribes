package players.portfolio.scripts;

import core.Types;
import core.actions.Action;
import core.actors.Actor;
import core.game.GameState;
import players.portfolio.scripts.utils.BuildingFunc;

import java.util.Random;

import static core.Types.BUILDING.*;

public class BuildTempleScr extends BaseScript {

    //Selects the action that builds a temple in the best possible place.

    private Random rnd;

    public BuildTempleScr(Random rnd)
    {
        this.rnd = rnd;
    }

    @Override
    public Action process(GameState gs, Actor ac)
    {
        //Looks for the first temple that can be built in an idle space. Returns null if no idle spaces.
        Types.BUILDING[] targets = new Types.BUILDING[]{TEMPLE, FOREST_TEMPLE, WATER_TEMPLE, MOUNTAIN_TEMPLE};
        for(Types.BUILDING target : targets){
            Action buildTemple = new BuildingFunc().buildInIdle(target, gs, actions, rnd);
            if(buildTemple != null)
                return buildTemple;
        }
        return null;
    }

}
