package core.actions.unitactions;

import core.units.City;
import core.units.Unit;

public class Capture extends UnitAction
{
    private City target; // This can be a city or a village.

    public Capture(Unit invader, City target)
    {
        super.unit = invader;
        this.target = target;
    }

    public City getTarget() {
        return target;
    }
}
