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
    private LinkedList<Integer> targets;

    public HealOthers(Unit healer)
    {
        super.unit = healer;
    }

    @Override
    public LinkedList<Action> computeActionVariants(final GameState gs) {
        LinkedList<Action> actions = new LinkedList<>();
        Board board = gs.getBoard();
        HealOthers action = new HealOthers(unit);

        if(isFeasible(gs)){
            for(Vector2d tile : unit.getPosition().neighborhood(1, board.getSize())){
                //Avoid adding self as a target
                Unit u = board.getUnitAt(tile.x, tile.y);  // There might not be a unit there at all
                if(u != null && u.getTribeId() == unit.getTribeId() && !tile.equals(unit.getPosition())){
                    action.targets.add(board.getUnitIDAt(tile.x, tile.y));
                }
            }
            actions.add(action);
        }
        return actions;
    }

    @Override
    public boolean isFeasible(final GameState gs) {
        Board board = gs.getBoard();

        //Feasible if this unit can heal this turn and if there is at least one friendly unit adjacent.
        //Even if that unit has max HP. (Do we want that?)
        if(unit.checkStatus(Types.TURN_STATUS.ATTACKED)){
            for(Vector2d tile : unit.getPosition().neighborhood(1, board.getSize())){
                Unit u = board.getUnitAt(tile.x, tile.y);
                if(u != null && u.getTribeId() == unit.getTribeId()){
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean execute(GameState gs) {
        if(isFeasible(gs)){
            for(Integer targetID : targets){
                Unit target = (Unit) gs.getBoard().getActor(targetID);
                if(target.getCurrentHP() + TribesConfig.MINDBENDER_HEAL >= target.getMaxHP()){
                    target.setCurrentHP(target.getMaxHP());
                }else {
                    target.setCurrentHP(target.getCurrentHP() + TribesConfig.MINDBENDER_HEAL);
                }
            }
            unit.setStatus(Types.TURN_STATUS.ATTACKED);
            return true;
        }
        return false;
    }
}
