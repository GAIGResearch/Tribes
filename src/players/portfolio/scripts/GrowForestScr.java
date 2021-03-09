package players.portfolio.scripts;

import core.Types;
import core.actions.Action;
import core.actions.cityactions.GrowForest;
import core.actors.Actor;
import core.game.GameState;
import players.portfolio.scripts.utils.BuildingFunc;
import utils.Vector2d;

import java.util.ArrayList;
import java.util.Random;

import static core.Types.BUILDING.LUMBER_HUT;

public class GrowForestScr extends BaseScript {

    //This script returns the Grow Forest action that grows a forest if it's a good location for it.
    private Random rnd;

    public GrowForestScr(Random rnd)
    {
        this.rnd = rnd;
    }

    @Override
    public Action process(GameState gs, Actor ac)
    {
        ArrayList<Action> candidate_actions = new ArrayList<>();

        for(Action act : actions)
        {
            GrowForest action = (GrowForest)act;
            BuildingFunc f = new BuildingFunc();
            Vector2d targetPos = action.getTargetPos();

            //Grow if good place for a lumber hut
            if( f.goodForBaseBuilding(gs, targetPos, new Types.BUILDING[]{LUMBER_HUT}, action.getCityId()) ){
                candidate_actions.add(act);
            }
        }

        int nActions = candidate_actions.size();
        if( nActions > 0)
            return candidate_actions.get(rnd.nextInt(nActions));
        return null;
    }



}
