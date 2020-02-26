package core.actions.unitactions;

import core.actions.Action;
import core.actors.Tribe;
import core.game.Board;
import core.game.GameState;
import core.actors.City;
import core.actors.units.Unit;

import java.util.ArrayList;
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
        Board b = gs.getBoard();
        LinkedList<Action> captures = new LinkedList<>();
        Tribe t = b.getTribe(this.unit.getTribeId());
        ArrayList<Integer> cityIds = t.getCitiesID();
        //TODO: Get City, check if city and unit pos is same
        if(isFeasible(gs)){


        }

        return captures;
    }

    @Override
    public boolean isFeasible(final GameState gs)
    {
        //todo: check if capturing this city is feasible.
        Board b = gs.getBoard();
        if(b.getUnitAt(targetCity.getX(),targetCity.getY()) != null)
            return false;
        else if(targetCity.getX() != unit.getCurrentPosition().x || targetCity.getY() !=unit.getCurrentPosition().y)
            return false;

        return true;
    }

    @Override
    public boolean execute(GameState gs) {
        //todo: execute the capture action
        if(isFeasible(gs)) {
            targetCity.setTribeId(this.unit.getTribeId());
            return true;
        }
        return false;
    }
}
