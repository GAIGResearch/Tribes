package core.actions.unitactions;

import core.units.City;
import core.units.Unit;

public class Examine extends UnitAction
{
    private City target; // This can be a city or a village.

    public Examine(Unit invader, City target)
    {
        super.unit = invader;
        this.target = target;
    }

    public City getTarget() {
        return target;
    }
}
