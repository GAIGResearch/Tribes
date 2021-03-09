package players.portfolio.scripts;

import core.Types;
import core.actions.Action;
import core.actions.cityactions.BurnForest;
import core.actors.Actor;
import core.game.GameState;
import players.portfolio.scripts.utils.BuildingFunc;
import utils.Vector2d;

import java.util.ArrayList;
import java.util.Random;

import static core.Types.BUILDING.*;

public class BurnForestScr extends BaseScript {

    //This script returns the Burn Forest action that burns a forest if it's in a good location for it.
    private Random rnd;

    public BurnForestScr(Random rnd)
    {
        this.rnd = rnd;
    }

    @Override
    public Action process(GameState gs, Actor ac)
    {
        ArrayList<Action> candidate_actions = new ArrayList<>();

        for(Action act : actions)
        {
            BurnForest action = (BurnForest)act;
            BuildingFunc f = new BuildingFunc();
            Vector2d targetPos = action.getTargetPos();

            //Burn if good place for farm and bad place for lumber hut
            if( f.goodForBaseBuilding(gs, targetPos, new Types.BUILDING[]{FARM}, action.getCityId()) &&
                !f.goodForBaseBuilding(gs, targetPos, new Types.BUILDING[]{LUMBER_HUT}, action.getCityId())){
                candidate_actions.add(act);
            }
        }

        int nActions = candidate_actions.size();
        if( nActions > 0)
            return candidate_actions.get(rnd.nextInt(nActions));
        return null;
    }



}
