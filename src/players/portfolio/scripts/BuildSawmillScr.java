package players.portfolio.scripts;

import core.actions.Action;
import core.actors.Actor;
import core.game.GameState;
import players.portfolio.scripts.utils.BuildingFunc;
import utils.Pair;

import java.util.Random;

import static core.Types.BUILDING.SAWMILL;

public class BuildSawmillScr extends BaseScript {

    //Selects the action that builds a Sawmill in the best possible place.

    private Random rnd;

    public BuildSawmillScr(Random rnd)
    {
        this.rnd = rnd;
    }

    @Override
    public Pair<Action, Double> process(GameState gs, Actor ac)
    {
        return new BuildingFunc().buildSupportBuilding(SAWMILL, gs, actions, rnd);
    }

}
