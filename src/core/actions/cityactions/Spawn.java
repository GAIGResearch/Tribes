package core.actions.cityactions;

import core.Types;
import core.actions.Action;
import core.game.GameState;
import core.actors.City;

import java.util.LinkedList;

public class Spawn extends CityAction
{
    private Types.UNIT unit_type;

    public Spawn(City c)
    {
        super.city = c;
    }

    public void setUnitType(Types.UNIT unit_type) {this.unit_type = unit_type;}
    public Types.UNIT getUnitType() {
        return unit_type;
    }

    @Override
    public LinkedList<Action> computeActionVariants(GameState gs) {
        //TODO: compute spawn actions
        return null;
    }

    @Override
    public boolean isFeasible(GameState gs) {
        //TODO: Is feasible spawn
        return false;
    }

    @Override
    public boolean execute(GameState gs) {
        //TODO: execute a spawn action.
        return false;
    }
}
