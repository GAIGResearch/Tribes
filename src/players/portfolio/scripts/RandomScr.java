package players.portfolio.scripts;

import core.actions.Action;
import core.actors.Actor;
import core.game.GameState;
import utils.Pair;

import java.util.Random;

public class RandomScr extends BaseScript {

    private Random rnd;

    public RandomScr(Random rnd)
    {
        this.rnd = rnd;
    }

    @Override
    public Pair<Action, Double> process(GameState gs, Actor ac) {
        int nActions = actions.size();
        return new Pair<>(actions.get(rnd.nextInt(nActions)), DEFAULT_VALUE);
    }

}
