package core.actions.unitactions;

import core.actions.Action;
import core.actors.units.Unit;

public abstract class UnitAction extends Action
{
    /**
     * Unit that PERFORMS this action
     */
    protected Unit unit;

    public void setUnit(Unit unit) {this.unit = unit;}

    public Unit getUnit()
    {
        return unit;
    }


}
