package core.actions.unitactions;

import core.actors.units.Unit;

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
}
