package core.actions.unitactions;

import core.game.GameState;
import core.actors.units.Unit;

public class Convert extends UnitAction
{
    private int targetId;

    public Convert(int unitId)
    {
        super.unitId = unitId;
    }

    public void setTargetId(int targetId) {this.targetId = targetId;}
    public int getTargetId() {
        return targetId;
    }


    @Override
    public boolean isFeasible(final GameState gs) {
        Unit target = (Unit) gs.getActor(this.targetId);
        Unit unit = (Unit) gs.getActor(this.unitId);

        // Check if target is not null
        if(target == null)
            return false;

        return unitInRange(unit, target, gs.getBoard());
    }

    @Override
    public boolean execute(GameState gs) {
        //Check if action is feasible before execution
        if(isFeasible(gs)) {
            Unit target = (Unit) gs.getActor(this.targetId);
            Unit unit = (Unit) gs.getActor(this.unitId);

            target.setTribeId(unit.getTribeId());

            //add tribe to converted units
            gs.getActiveTribe().addExtraUnit(target);
            return true;
        }
        return false;
    }
}
