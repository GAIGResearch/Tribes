package core.actions.unitactions;

import core.Types;
import core.actions.Action;
import core.game.Board;
import core.game.GameState;
import core.actors.units.Unit;
import utils.Vector2d;

import java.util.LinkedList;

public class HealOthers extends UnitAction
{
    private LinkedList<Unit> targets;

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
                if(board.getUnitAt(tile.x, tile.y).getTribeId() == unit.getTribeId() && !tile.equals(unit.getPosition())){
                    action.targets.add(board.getUnitAt(tile.x, tile.y));
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
                if(board.getUnitAt(tile.x, tile.y).getTribeId() == unit.getTribeId()){
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean execute(GameState gs) {
        if(isFeasible(gs)){
            for(Unit target : targets){
                if(target.getCurrentHP() + 4 >= target.getMaxHP()){
                    target.setCurrentHP(target.getMaxHP());
                }else {
                    target.setCurrentHP(target.getCurrentHP() + 4);
                }
            }
            unit.setStatus(Types.TURN_STATUS.ATTACKED);
        }
        return false;
    }
}
