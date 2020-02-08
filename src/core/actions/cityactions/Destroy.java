package core.actions.cityactions;

import core.actions.Action;
import core.game.GameState;
import core.actors.City;

import java.util.LinkedList;

public class Destroy extends CityAction
{

    public Destroy(City c)
    {
        super.city = c;
    }

    @Override
    public LinkedList<Action> computeActionVariants(final GameState gs) {
        //TODO Compute Destroy
        return null;
    }

    @Override
    public boolean isFeasible(final GameState gs)
    {
        //TODO: is Feasible Destroy
        return false;
    }

    @Override
    public boolean execute(GameState gs) {
        //TODO: execute destroy.
        return false;
    }
}
