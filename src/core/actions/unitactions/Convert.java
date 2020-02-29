package core.actions.unitactions;

import core.actions.Action;
import core.game.Board;
import core.game.GameState;
import core.actors.units.Unit;
import utils.Vector2d;

import java.util.LinkedList;

public class Convert extends UnitAction
{
    private Unit target;

    public Convert(Unit attacker)
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
        // Loop through unit range, check if tile observable and action feasible, if so add action
        Vector2d position = this.unit.getPosition();

        for(int i = position.x- this.unit.RANGE; i <= position.x+ this.unit.RANGE; i++) {
            for (int j = position.y - this.unit.RANGE; j <= position.y + this.unit.RANGE; j++) {

                //Not converting itself
                if(i != position.x || j != position.y) {
                    Convert c = new Convert(this.unit);
                    c.setTarget(b.getUnitAt(i,j));
                    if(!obsGrid[i][j]){
                        continue;
                    }
                    if(c.isFeasible(gs)){
                        converts.add(c);
                    }
                }

            }
        }
        return converts;
    }


    @Override
    public boolean isFeasible(final GameState gs) {
        //Check if target in range
        if(target == null || target.getTribeId() == this.unit.getTribeId())
            return false;
        return true;
    }

    @Override
    public boolean execute(GameState gs) {
        //Check if action is feasible before execution
        if(isFeasible(gs)) {
            target.setTribeId(this.unit.getTribeId());
            return true;
        }
        return false;
    }
}
