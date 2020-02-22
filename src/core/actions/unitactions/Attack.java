package core.actions.unitactions;

import core.actions.Action;
import core.game.Board;
import core.game.GameState;
import core.actors.units.Unit;

import java.util.LinkedList;

public class Attack extends UnitAction
{
    private Unit target;

    public Attack (Unit attacker)
    {
        super.unit = attacker;
    }

    public void setTarget(Unit target) {this.target = target;}
    public Unit getTarget() {
        return target;
    }

    @Override
    public LinkedList<Action> computeActionVariants(final GameState gs) {

        LinkedList<Action> attacks = new LinkedList<>();

        if(isFeasible(gs)){
            Attack a = new Attack(this.unit);
            a.target = this.target;
            attacks.add(a);

        }
        return attacks;
    }

    @Override
    public boolean isFeasible(final GameState gs)
    {
        if(target.getCurrentPosition().x < unit.getCurrentPosition().x  + this.unit.RANGE || target.getCurrentPosition().y < unit.getCurrentPosition().y  + this.unit.RANGE)
            return true;
        return false;
    }

    @Override
    public boolean execute(GameState gs) {
        if(isFeasible(gs)) {
            if (target.getCurrentHP() <= unit.ATK) {
                unit.addKill();
                target = null;
            } else {
                target.setCurrentHP(target.getCurrentHP() - unit.ATK);
            }
            return true;
        }
        return false;
    }
}
