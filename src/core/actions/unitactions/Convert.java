package core.actions.unitactions;

import core.game.GameState;
import core.units.Unit;

public class Convert extends UnitAction
{
    private Unit target;

    public Convert(Unit attacker, Unit target)
    {
        super.unit = attacker;
        this.target = target;
    }

    public Unit getTarget() {
        return target;
    }

    @Override
    public boolean isFeasible(GameState gs) {
        return false;
    }

    @Override
    public void execute(GameState gs) {

    }
}
