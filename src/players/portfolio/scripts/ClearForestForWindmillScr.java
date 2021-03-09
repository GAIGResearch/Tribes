package players.portfolio.scripts;

import core.actions.Action;
import core.actors.Actor;
import core.game.GameState;
import players.portfolio.scripts.utils.BuildingFunc;

import java.util.Random;

import static core.Types.BUILDING.*;

public class ClearForestForWindmillScr extends BaseScript {

    //This script returns the Clear Forest action that makes room for a custom house.

    private Random rnd;

    public ClearForestForWindmillScr(Random rnd)
    {
        this.rnd = rnd;
    }

    @Override
    public Action process(GameState gs, Actor ac)
    {
        return new BuildingFunc().buildSupportBuilding(WINDMILL, gs, actions, rnd);
    }



}
