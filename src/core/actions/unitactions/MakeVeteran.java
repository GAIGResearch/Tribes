package core.actions.unitactions;

import core.actions.Action;
import core.game.GameState;
import core.actors.units.Unit;

import java.util.LinkedList;

public class MakeVeteran extends UnitAction
{
    public MakeVeteran(Unit target)
    {
        super.unit = target;
    }

    @Override
    public LinkedList<Action> computeActionVariants(final GameState gs) {
        LinkedList<Action> actions = new LinkedList<>();
        MakeVeteran newAction = new MakeVeteran(unit);
        if(isFeasible(gs)){ actions.add(newAction); }
        return actions;
    }

    @Override
    public boolean isFeasible(final GameState gs) {
        return unit.getKills() >= 3 && !unit.isVeteran();
    }

    @Override
    public boolean execute(GameState gs) {
        if(isFeasible(gs))
        {
            unit.setVeteran(true);
            unit.setMaxHP(unit.getMaxHP() + 5);
            unit.setCurrentHP((float)unit.getMaxHP());
            return true;
        }
        return false;
    }
}
