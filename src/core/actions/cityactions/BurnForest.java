package core.actions.cityactions;

import core.actions.Action;
import core.game.GameState;
import core.actors.City;

import java.util.LinkedList;

public class BurnForest extends CityAction
{
    public BurnForest(City c) {
        super.city = c;
    }

    @Override
    public LinkedList<Action> computeActionVariants(final GameState gs) {
        //TODO compute burn forest
        return null;
    }

    @Override
    public boolean isFeasible(final GameState gs) {
        //TODO: is feasible burn forest
        return false;
    }

    @Override
    public boolean execute(GameState gs) {
        //TODO: executes the forest burn
        return false;
    }
}
