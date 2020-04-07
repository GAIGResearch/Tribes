package players;

import core.actions.Action;
import core.game.GameState;
import utils.ElapsedCpuTimer;

import java.util.ArrayList;
import java.util.Random;

public class RandomAgent extends Agent {

    private Random rnd;

    public RandomAgent(long seed)
    {
        super(seed);
        rnd = new Random(seed);
    }

    @Override
    public Action act(GameState gs, ElapsedCpuTimer ect)
    {
        ArrayList<Action> allActions = new ArrayList<>(gs.getTribeActions());
        for (Integer cityId : gs.getCityActions().keySet())
        {
            allActions.addAll(gs.getCityActions(cityId));
        }
        for (Integer unitId : gs.getUnitActions().keySet())
        {
            allActions.addAll(gs.getUnitActions(unitId));
        }

        int nActions = allActions.size();

        if(nActions == 2)
        {
            int a = 0;
        }

        Action toExecute = allActions.get(rnd.nextInt(nActions));
        System.out.println("[Tribe: " + playerID + "] Tick " +  gs.getTick() + ", num actions: " + nActions + ". Executing " + toExecute);
        return toExecute;
    }

    @Override
    public Agent copy() {
        return null;
    }
}
