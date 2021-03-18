package players.portfolio.scripts;

import core.Types;
import core.actions.Action;
import core.actors.Actor;
import core.game.GameState;
import players.portfolio.scripts.utils.BuildingFunc;
import utils.Pair;

import java.util.ArrayList;
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
    public Pair<Action, Double> process(GameState gs, Actor ac)
    {
        ArrayList<Action> candidate_actions = new ArrayList<>();
        double bestScore = 0;

        //Looks for the first monument that can be built in an idle space. Returns null if no idle spaces.
        Types.BUILDING[] targets = new Types.BUILDING[]{TEMPLE, FOREST_TEMPLE, WATER_TEMPLE, MOUNTAIN_TEMPLE};

        for(Types.BUILDING target : targets){
            Pair<Action, Double> p = new BuildingFunc().buildInIdle(target, gs, actions, rnd);
            if(p != null) {
                Action buildTempleAction = p.getFirst();
                double score = p.getSecond();
                if (score > bestScore) {
                    candidate_actions.clear();
                    bestScore = score;
                }
                if (score == bestScore)
                    candidate_actions.add(buildTempleAction);
            }
        }

        int nActions = candidate_actions.size();
        if( nActions > 0)
            return new Pair<>(candidate_actions.get(rnd.nextInt(nActions)), bestScore);
        return null;
    }

}
