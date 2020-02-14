package core.actions.unitactions;

import core.actions.Action;
import core.game.GameState;
import core.actors.units.Unit;

import java.util.LinkedList;

public class Recover extends UnitAction
{
    public Recover(Unit target)
    {
        super.unit = target;
    }


    @Override
    public LinkedList<Action> computeActionVariants(final GameState gs) {

        LinkedList<Action> actions = new LinkedList<>();
        Recover newAction = new Recover(unit);
        float currentHP = unit.getCurrentHP();
        if (currentHP < unit.MAX_HP && currentHP > 0){
            actions.add(newAction);
        }
        return actions;
    }

    @Override
    public boolean isFeasible(final GameState gs) {
        float currentHP = unit.getCurrentHP();
        return currentHP < unit.MAX_HP && currentHP > 0;
    }

    @Override
    public boolean execute(GameState gs) {
        float currentHP = unit.getCurrentHP();
        if (currentHP < unit.MAX_HP && currentHP > 0) {
            // TODO: need to have a way to know if the unit is in the territory or not
            unit.setCurrentHP(Math.min(currentHP + 2, unit.MAX_HP));
            return true;
        }
        return false;
    }
}
