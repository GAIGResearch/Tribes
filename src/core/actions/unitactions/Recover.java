package core.actions.unitactions;

import core.game.GameState;
import core.units.Unit;

public class Recover extends UnitAction
{
    public Recover(Unit target)
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
