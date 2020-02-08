package core.actions.cityactions;

import core.Types;
import core.actions.Action;
import core.game.GameState;
import core.actors.City;

import java.util.LinkedList;

public class ResourceGathering extends CityAction
{
    private Types.RESOURCE resource;

    public ResourceGathering(City c)
    {
        super.city = c;
    }

    public void setResource(Types.RESOURCE resource) {this.resource = resource;}
    public Types.RESOURCE getResource() {
        return resource;
    }

    @Override
    public LinkedList<Action> computeActionVariants(final GameState gs) {
        //TODO: compute resource gathering
        return null;
    }

    @Override
    public boolean isFeasible(final GameState gs)
    {
        //TODO: isFeasible ResourceGathering
        return false;
    }

    @Override
    public boolean execute(GameState gs) {
        //TODO: execute resource gathering
        return false;
    }
}
