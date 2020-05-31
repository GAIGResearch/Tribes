package core.actions.unitactions;

import core.TribesConfig;
import core.Types;
import core.actions.Action;
import core.game.Board;
import core.game.GameState;
import core.actors.units.Unit;
import utils.Vector2d;

import java.util.ArrayList;

public class HealOthers extends UnitAction
{
    public HealOthers(int unitId)
    {
        super.unitId = unitId;
    }


    @Override
    public boolean isFeasible(final GameState gs) {
        Board board = gs.getBoard();
        Unit unit = (Unit) gs.getActor(this.unitId);

        //This needs to be a mind bender that can "attack"
        if(unit.getType() != Types.UNIT.MIND_BENDER || !unit.canAttack())
            return false;

        //Feasible if this unit can heal this turn and if there is at least one friendly unit adjacent.
        for(Vector2d tile : unit.getPosition().neighborhood(unit.RANGE, 0, board.getSize())){
            Unit u = board.getUnitAt(tile.x, tile.y);
            if (canBeHealed(unit, u))
                return true;
        }

        return false;
    }

    @Override
    public boolean execute(GameState gs) {

        if (isFeasible(gs)) {
            Unit unit = (Unit) gs.getActor(this.unitId);
            ArrayList<Unit> targets = getTargets(gs);
            TribesConfig tc = new TribesConfig();

            for (Unit target: targets) {
                target.setCurrentHP(Math.min(target.getCurrentHP() + tc.MINDBENDER_HEAL, target.getMaxHP()));
            }

            unit.transitionToStatus(Types.TURN_STATUS.ATTACKED);
            return true;
        }
        return false;
    }

    @Override
    public Action copy() {
        return new HealOthers(this.unitId);
    }

    private boolean canBeHealed(Unit healer, Unit target)
    {
        if(target != null && target.getTribeId() == healer.getTribeId()){
            return (target.getCurrentHP() < target.getMaxHP()) && (target.getTribeId() == healer.getTribeId());
        }
        return false;
    }

    public ArrayList<Unit> getTargets(GameState gs) {
        ArrayList<Unit> targets = new ArrayList<>();
        Unit unit = (Unit) gs.getActor(this.unitId);
        for (Vector2d tile : unit.getPosition().neighborhood(unit.RANGE, 0, gs.getBoard().getSize())){
            Unit u = gs.getBoard().getUnitAt(tile.x, tile.y);
            if (canBeHealed(unit, u))
                targets.add(u);
        }
        return targets;
    }

    public String toString() {
        return "HEAL by unit " + this.unitId;
    }
}
