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
        if(isFeasible(gs)){
            Convert a = new Convert(this.unit, this.target);
            converts.add(a);
        }
        return converts;
    }

    @Override
    public boolean isFeasible(final GameState gs) {
        if(target.getCurrentPosition().x < unit.getCurrentPosition().x  + this.unit.RANGE && target.getCurrentPosition().y < unit.getCurrentPosition().y  + this.unit.RANGE)
            return true;
        return false;
    }

    @Override
    public boolean execute(GameState gs) {
        if(isFeasible(gs)) {
            target.setActorID(unit.getActorID());
        }
        return true;
    }
}
