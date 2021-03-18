package players.portfolio.scripts;

import core.Types;
import core.actions.Action;
import core.actions.tribeactions.ResearchTech;
import core.actors.Actor;
import core.game.GameState;
import players.portfolio.scripts.utils.MilitaryFunc;
import utils.Pair;

import java.util.ArrayList;
import java.util.Random;

import static core.Types.TECHNOLOGY.*;

public class ResearchRangeScr extends BaseScript {

    //Selects the action that researchers a tech in the Range branch.

    private Random rnd;

    public ResearchRangeScr(Random rnd)
    {
        this.rnd = rnd;
    }

    @Override
    public Pair<Action, Double> process(GameState gs, Actor ac)
    {
        if(actions.size() == 1)
            return new Pair<>(actions.get(0), DEFAULT_VALUE);

        ArrayList<Types.TECHNOLOGY> techs = new ArrayList<>();
        techs.add(HUNTING);
        techs.add(ARCHERY);
        techs.add(FORESTRY);
        techs.add(SPIRITUALISM);
        techs.add(MATHEMATICS);

        return new MilitaryFunc().getPreferredResearchTech(gs, actions, techs, rnd);
    }

}
