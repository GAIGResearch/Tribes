package players.portfolio.scripts;

import core.Types;
import core.actions.Action;
import core.actions.cityactions.LevelUp;
import core.actions.cityactions.ResourceGathering;
import core.actors.Actor;
import core.game.GameState;
import players.portfolio.scripts.utils.BuildingFunc;
import utils.Pair;
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
    public Pair<Action, Double> process(GameState gs, Actor ac)
    {
        ArrayList<Action> candidate_actions = new ArrayList<>();
        boolean isSupport = false;
        double bestScore = 0;
        double scoreVal = DEFAULT_VALUE;

        for(Action act : actions)
        {
            ResourceGathering action = (ResourceGathering) act;
            if(action.getResource() == WHALES)
                return new Pair<>(act, 1.0);

            BuildingFunc bf = new BuildingFunc();
            Vector2d targetPos = action.getTargetPos();

            double supportScore = bf.valueForSupportingBuilding(gs, targetPos, new Types.BUILDING[]{SAWMILL, CUSTOMS_HOUSE, WINDMILL, FORGE}, action.getCityId());
            if(supportScore > 0)
            {
                if(!isSupport || supportScore > bestScore)
                {
                    //first we found which would be a good space for a support building. New array.
                    candidate_actions.clear();
                    bestScore = supportScore;
                    scoreVal = DEFAULT_VALUE + (1-DEFAULT_VALUE) * supportScore;
                }

                if(bestScore == supportScore)
                    candidate_actions.add(act);

                isSupport = true;

            }else if(!isSupport)
            {
                candidate_actions.add(act);
            }
        }

        int nActions = candidate_actions.size();
        if( nActions > 0)
            return new Pair<>(candidate_actions.get(rnd.nextInt(nActions)), scoreVal);
        return null;
    }

}
