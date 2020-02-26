package core.actions.unitactions;

import core.actions.Action;
import core.game.Board;
import core.game.GameState;
import core.actors.units.Unit;

import java.util.LinkedList;

public class Convert extends UnitAction
{
    private Unit target;

    public Convert(Unit attacker, Unit target)
    {
        super.unit = attacker;
    }

    public void setTarget(Unit target) {this.target = target;}
    public Unit getTarget() {
        return target;
    }

    @Override
    public LinkedList<Action> computeActionVariants(final GameState gs) {
        LinkedList<Action> converts = new LinkedList<>();
        Board b = gs.getBoard();
        boolean[][] obsGrid = b.getTribe(this.unit.getTribeId()).getObsGrid();
        for(int x = this.unit.getCurrentPosition().x- this.unit.RANGE; x <= x+ this.unit.RANGE; x++) {
            for (int y = this.unit.getCurrentPosition().y - this.unit.RANGE; y <= y + this.unit.RANGE; y++) {
                Convert c = new Convert(this.unit, b.getUnitAt(x,y));
                if(!obsGrid[x][y]){
                    continue;
                }
                if(c.isFeasible(gs)){
                    converts.add(c);
                }
            }
            }
        return converts;

    }




    @Override
    public boolean isFeasible(final GameState gs) {
        //Check if target in range
        if(target!=null|| target.getTribeId() == this.unit.getTribeId())
            return true;
        return false;
    }

    @Override
    public boolean execute(GameState gs) {
        //Check if action is feasible before execution
        if(isFeasible(gs)) {
            target.setTribeID(this.unit.getTribeId());
            return true;
        }
        return false;
    }
}
