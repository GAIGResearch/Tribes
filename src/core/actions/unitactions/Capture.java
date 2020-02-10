package core.actions.unitactions;

import core.actions.Action;
import core.game.GameState;
import core.actors.City;
import core.actors.units.Unit;

import java.util.LinkedList;

public class Capture extends UnitAction
{
    private City targetCity; // This can be a city or a village.

    public Capture(Unit invader)
    {
        super.unit = invader;
    }

    public void setTargetCity(City targetCity) {this.targetCity = targetCity;}
    public City getTargetCity() {
        return targetCity;
    }

    @Override
    public LinkedList<Action> computeActionVariants(final GameState gs) {
        //TODO: compute Capture city actions
        return null;
    }

    @Override
    public boolean isFeasible(final GameState gs)
    {
        //todo: check if capturing this city is feasible.
        return false;
    }

    @Override
    public boolean execute(GameState gs) {
        //todo: execute the capture action
        return false;
    }
}
