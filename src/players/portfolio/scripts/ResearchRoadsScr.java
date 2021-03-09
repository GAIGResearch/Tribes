package players.portfolio.scripts;

import core.Types;
import core.actions.Action;
import core.actors.Actor;
import core.game.GameState;
import players.portfolio.scripts.utils.MilitaryFunc;

import java.util.ArrayList;
import java.util.Random;

import static core.Types.TECHNOLOGY.*;

public class ResearchRoadsScr extends BaseScript {


    //Selects the action that researchers a tech in the Roads branch.

    private Random rnd;

    public ResearchRoadsScr(Random rnd)
    {
        this.rnd = rnd;
    }

    @Override
    public Action process(GameState gs, Actor ac)
    {
        if(actions.size() == 1)
            return actions.get(0);

        ArrayList<Types.TECHNOLOGY> techs = new ArrayList<>();
        techs.add(RIDING);
        techs.add(ROADS);
        techs.add(FREE_SPIRIT);
        techs.add(CHIVALRY);
        techs.add(TRADE);

        return new MilitaryFunc().getPreferredResearchTech(gs, actions, techs, rnd);
    }

}
