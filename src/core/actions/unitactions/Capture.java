package core.actions.unitactions;

import core.actions.Action;
import core.game.Board;
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

        LinkedList<Action> captures = new LinkedList<>();
        if(isFeasible(gs)){
            Capture c = new Capture(this.unit);
            c.setTargetCity(this.targetCity);
            captures.add(c);
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
        else if(targetCity.getX() > unit.getCurrentPosition().x  + this.unit.RANGE ||targetCity.getY() > unit.getCurrentPosition().y  + this.unit.RANGE)
            return false;

        return true;
    }

    @Override
    public boolean execute(GameState gs) {
        //todo: execute the capture action
        if(isFeasible(gs)) {
            targetCity.setActorID(this.unit.getActorID());
            targetCity.setTribeId(unit.getTribeID());
            return true;
        }
        return false;
    }
}
