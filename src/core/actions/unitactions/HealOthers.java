package core.actions.unitactions;

import core.TribesConfig;
import core.Types;
import core.actions.Action;
import core.game.Board;
import core.game.GameState;
import core.actors.units.Unit;
import utils.Vector2d;

import java.util.LinkedList;

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

        if(unit.getType() != Types.UNIT.MIND_BENDER) return false;

        //Feasible if this unit can heal this turn and if there is at least one friendly unit adjacent.
        for(Vector2d tile : unit.getPosition().neighborhood(unit.RANGE, board.getSize())){
            Unit u = board.getUnitAt(tile.x, tile.y);
            if(canBeHealed(u, board.getUnitAt(tile.x, tile.y)))
                return true;
        }

        return false;
    }

    @Override
    public boolean execute(GameState gs) {

        if(isFeasible(gs)){
            Unit unit = (Unit) gs.getActor(this.unitId);
            Board board = gs.getBoard();

            for(Vector2d tile : unit.getPosition().neighborhood(unit.RANGE, board.getSize())){
                Unit target  = board.getUnitAt(tile.x, tile.y);
                if(canBeHealed(unit, target))
                {
                    target.setCurrentHP(Math.min(target.getCurrentHP() + TribesConfig.MINDBENDER_HEAL, target.getMaxHP()));
                }
            }

            unit.setStatus(Types.TURN_STATUS.ATTACKED);
            return true;
        }
        return false;
    }

    private boolean canBeHealed(Unit healer, Unit target)
    {
        if(target != null && target.getActorId() != healer.getActorId()){
            return (target.getCurrentHP() < target.getMaxHP()) && (target.getTribeId() == healer.getTribeId());
        }
        return false;
    }
}
