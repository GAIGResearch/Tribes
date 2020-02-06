package core.actions.unitactions;

import core.game.GameState;
import core.units.Unit;

public class HealOthers extends UnitAction
{
    public HealOthers(Unit target)
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
