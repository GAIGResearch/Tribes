package core.actions.unitactions;

import core.actors.units.Unit;

public class HealOthers extends UnitAction
{
    public HealOthers(Unit target)
    {
        super.unit = target;
    }

}
