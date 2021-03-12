package core.actions.unitactions;

import core.Types;
import core.actions.Action;
import core.actors.Tribe;
import core.game.GameState;
import core.actors.units.Unit;
import utils.Vector2d;

public class Examine extends UnitAction
{
    private Types.EXAMINE_BONUS bonus;

    public Examine(int unitId)
    {
        super(Types.ACTION.EXAMINE);
        super.unitId = unitId;
    }


    @Override
    public boolean isFeasible(final GameState gs) {
        Unit unit = (Unit) gs.getActor(this.unitId);
        Vector2d unitPos = unit.getPosition();
        Tribe t = gs.getTribe(unit.getTribeId());
        if(t.getCitiesID().size() == 0)
            return false;

        return unit.isFresh() && gs.getBoard().getResourceAt(unitPos.x, unitPos.y) == Types.RESOURCE.RUINS;
    }

    public Types.EXAMINE_BONUS getBonus() {
        return bonus;
    }

    @Override
    public Action copy() {
        Examine copy = new Examine(this.unitId);
        copy.bonus = bonus;
        return copy;
    }

    public String toString() {
        return "EXAMINE by unit " + this.unitId + " of ruins.";
    }

    public boolean equals(Object o) {
        if(!(o instanceof Examine))
            return false;
        Examine other = (Examine) o;

        return super.equals(other) && bonus == other.getBonus();
    }
}
