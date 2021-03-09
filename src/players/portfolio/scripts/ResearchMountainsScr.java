package players.portfolio.scripts;

import core.Types;
import core.actions.Action;
import core.actors.Actor;
import core.game.GameState;
import players.portfolio.scripts.utils.MilitaryFunc;

import java.util.ArrayList;
import java.util.Random;

import static core.Types.TECHNOLOGY.*;

public class ResearchMountainsScr extends BaseScript {


    //Selects the action that researchers a tech in the Mountain branch.

    private Random rnd;

    public ResearchMountainsScr(Random rnd)
    {
        this.rnd = rnd;
    }

    @Override
    public Action process(GameState gs, Actor ac)
    {
        if(actions.size() == 1)
            return actions.get(0);

        ArrayList<Types.TECHNOLOGY> techs = new ArrayList<>();
        techs.add(CLIMBING);
        techs.add(MINING);
        techs.add(MEDITATION);
        techs.add(SMITHERY);
        techs.add(PHILOSOPHY);

        return new MilitaryFunc().getPreferredResearchTech(gs, actions, techs, rnd);
    }

}
