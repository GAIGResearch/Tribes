package core.actions.unitactions;

import core.game.GameState;
import core.actors.units.Unit;

public class Disband extends UnitAction
{
    public Disband(Unit target)
    {
        super.unit = target;
    }

    @Override
    public boolean isFeasible(GameState gs) {
        return false;
    }

    @Override
    public void execute(GameState gs) {

    }
}
