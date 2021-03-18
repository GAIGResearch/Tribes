package players.portfolio.scripts;

import core.actions.Action;
import core.actors.Actor;
import core.game.GameState;
import players.portfolio.scripts.utils.BuildingFunc;
import utils.Pair;

import java.util.Random;

import static core.Types.BUILDING.*;

public class BuildCustomHouseScr extends BaseScript {

    //Selects the action that builds the Custom House in the best possible place.

    private Random rnd;

    public BuildCustomHouseScr(Random rnd)
    {
        this.rnd = rnd;
    }

    @Override
    public Pair<Action, Double> process(GameState gs, Actor ac)
    {
        return new BuildingFunc().buildSupportBuilding(CUSTOMS_HOUSE, gs, actions, rnd);
    }

}
