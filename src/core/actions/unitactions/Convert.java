package core.actions.unitactions;

import core.Types;
import core.actions.Action;
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
        if(target == null || !unit.canAttack())
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

            //manage status of the units after the action is executed
            unit.transitionToStatus(Types.TURN_STATUS.ATTACKED);
            target.setStatus(Types.TURN_STATUS.FINISHED);
            return true;
        }
        return false;
    }

    @Override
    public Action copy() {
        Convert convert = new Convert(this.unitId);
        convert.setTargetId(this.targetId);
        return convert;
    }
}
