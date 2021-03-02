package players.portfolio.scripts;

import core.actions.Action;
import core.actors.Actor;
import core.actors.City;
import core.actors.units.Unit;
import core.game.GameState;
import players.portfolio.scripts.Script;

import java.util.ArrayList;
import java.util.Random;

public class RandomScr extends Script {

    private Random rnd;

    public RandomScr(Random rnd)
    {
        this.rnd = rnd;
    }

    @Override
    public Action process(GameState gs, Actor ac) {
        ArrayList<Action> allActions;

        if(ac instanceof Unit)
        {
            allActions = gs.getUnitActions(ac.getActorId());
        }else if(ac instanceof City)
        {
            allActions = gs.getCityActions(ac.getActorId());
        }else
        {
            allActions = gs.getTribeActions();
        }

        if(allActions == null || allActions.size() == 0) return null;

        int nActions = allActions.size();
        return allActions.get(rnd.nextInt(nActions));
    }

}
