package players.portfolio.scripts;

import core.Types;
import core.actions.Action;
import core.actors.Actor;
import core.game.GameState;

import java.util.ArrayList;
import java.util.Random;

import static core.Types.BUILDING.*;

public class ClearForestForSawmillScr extends BaseScript {

    //This script returns the Clear Forest action that makes room for a custom house.

    private Random rnd;

    public ClearForestForSawmillScr(Random rnd)
    {
        this.rnd = rnd;
    }

    @Override
    public Action process(GameState gs, Actor ac)
    {
        Types.BUILDING[] targets = new Types.BUILDING[]{SAWMILL, CUSTOMS_HOUSE, WINDMILL, FORGE};

        for(Types.BUILDING target : targets)
        {
            ArrayList<Action> candidate_actions = new ArrayList<>();
            new Func().candidatesForBuilding(gs, ac, target, actions, candidate_actions);

            if(candidate_actions.size() > 0)
            {
                int nActions = candidate_actions.size();
                return candidate_actions.get(rnd.nextInt(nActions));
            }
        }

        //if we can't prioritize by any type of building, pick one location at random
        int nActions = actions.size();
        return actions.get(rnd.nextInt(nActions));
    }



}
