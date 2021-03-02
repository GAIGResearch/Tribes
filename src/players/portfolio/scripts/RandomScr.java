package players.portfolio.scripts;

import core.actions.Action;
import core.actors.Actor;
import core.actors.City;
import core.actors.units.Unit;
import core.game.GameState;
import players.portfolio.scripts.Script;

import java.util.ArrayList;
import java.util.Random;

public class RandomScr extends Script {

    private Random rnd;

    public RandomScr(Random rnd)
    {
        this.rnd = rnd;
    }

    @Override
    public Action process(GameState gs, Actor ac) {
        int nActions = actions.size();
        return actions.get(rnd.nextInt(nActions));
    }

}
