package players.portfolio.scripts;

import core.actions.Action;
import core.actors.Actor;
import core.game.GameState;
import players.portfolio.scripts.utils.BuildingFunc;

import java.util.Random;

import static core.Types.BUILDING.FORGE;

public class BuildForgeScr extends BaseScript {

    //Selects the action that builds a Forge in the best possible place.

    private Random rnd;

    public BuildForgeScr(Random rnd)
    {
        this.rnd = rnd;
    }

    @Override
    public Action process(GameState gs, Actor ac)
    {
        return new BuildingFunc().buildSupportBuilding(FORGE, gs, actions, rnd);
    }

}
