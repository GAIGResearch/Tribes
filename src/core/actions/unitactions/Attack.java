package core.actions.unitactions;

import core.actors.units.Unit;

public class Attack extends UnitAction
{
    private Unit target;

    public Attack (Unit attacker, Unit target)
    {
        super.unit = attacker;
        this.target = target;
    }

    public Unit getTarget() {
        return target;
    }
}
