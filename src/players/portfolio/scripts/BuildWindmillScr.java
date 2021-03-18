package players.portfolio.scripts;

import core.actions.Action;
import core.actors.Actor;
import core.game.GameState;
import players.portfolio.scripts.utils.BuildingFunc;
import utils.Pair;

import java.util.Random;

import static core.Types.BUILDING.WINDMILL;

public class BuildWindmillScr extends BaseScript {

    //Selects the action that builds the Forge in the best possible place.

    private Random rnd;

    public BuildWindmillScr(Random rnd)
    {
        this.rnd = rnd;
    }

    @Override
    public Pair<Action, Double> process(GameState gs, Actor ac)
    {
        return new BuildingFunc().buildSupportBuilding(WINDMILL, gs, actions, rnd);
    }

}
