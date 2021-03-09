package players.portfolio.scripts;

import core.Types;
import core.actions.Action;
import core.actions.cityactions.LevelUp;
import core.actions.cityactions.ResourceGathering;
import core.actors.Actor;
import core.game.GameState;
import players.portfolio.scripts.utils.BuildingFunc;
import utils.Vector2d;

import java.util.ArrayList;
import java.util.Random;

import static core.Types.BUILDING.*;
import static core.Types.BUILDING.MOUNTAIN_TEMPLE;
import static core.Types.RESOURCE.*;

public class ResourceGatheringScr extends BaseScript {

    //Selects the action that levels up following a growth strategy: workshop, resources or pop_growth.
    // If park or super unit, picks at random.

    private Random rnd;

    public ResourceGatheringScr(Random rnd)
    {
        this.rnd = rnd;
    }

    @Override
    public Action process(GameState gs, Actor ac)
    {
        ArrayList<Action> candidate_actions = new ArrayList<>();
        boolean isSupport = false;

        for(Action act : actions)
        {
            ResourceGathering action = (ResourceGathering) act;
            if(action.getResource() == WHALES)
                return act;

            BuildingFunc bf = new BuildingFunc();
            Vector2d targetPos = action.getTargetPos();
            if(bf.goodForSupportingBuilding(gs, targetPos, new Types.BUILDING[]{SAWMILL, CUSTOMS_HOUSE, WINDMILL, FORGE}, action.getCityId()))
            {
                if(!isSupport)
                {
                    //first we found which would be a good space for a support building. New array.
                    candidate_actions.clear();
                }
                isSupport = true;
                candidate_actions.add(act);
            }else if(!isSupport)
            {
                candidate_actions.add(act);
            }
        }

        int nActions = candidate_actions.size();
        if( nActions > 0)
            return candidate_actions.get(rnd.nextInt(nActions));
        return null;
    }

}
