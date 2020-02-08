package core.actions.cityactions;

import core.actions.Action;
import core.game.GameState;
import core.actors.City;

import java.util.LinkedList;

public class GrowForest extends CityAction
{

    public GrowForest(City c)
    {
        super.city = c;
    }

    @Override
    public LinkedList<Action> computeActionVariants(final GameState gs) {
        //TODO: compute grow forest
        return null;
    }

    @Override
    public boolean isFeasible(final GameState gs) {
        //TODO: isFeasible grow forest
        return false;
    }

    @Override
    public boolean execute(GameState gs) {
        //TODO: execute grow forest
        return false;
    }
}
