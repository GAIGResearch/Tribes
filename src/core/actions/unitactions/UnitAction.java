package core.actions.unitactions;

import core.actions.Action;
import core.actors.units.Unit;

public abstract class UnitAction extends Action
{
    protected Unit unit;

    public Unit getUnit()
    {
        return unit;
    }


}
