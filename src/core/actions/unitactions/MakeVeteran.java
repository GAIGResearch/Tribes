package core.actions.unitactions;

import core.TribesConfig;
import core.actions.Action;
import core.game.GameState;
import core.actors.units.Unit;

import java.util.LinkedList;

public class MakeVeteran extends UnitAction
{
    public MakeVeteran(int unitId)
    {
        super.unitId = unitId;
    }


    @Override
    public boolean isFeasible(final GameState gs) {
        Unit unit = (Unit) gs.getActor(this.unitId);
        return unit.getKills() >= TribesConfig.VETERAN_KILLS && !unit.isVeteran();
    }

    @Override
    public boolean execute(GameState gs) {
        Unit unit = (Unit) gs.getActor(this.unitId);
        if(isFeasible(gs))
        {
            unit.setVeteran(true);
            unit.setMaxHP(unit.getMaxHP() + TribesConfig.VETERAN_PLUS_HP);
            unit.setCurrentHP(unit.getMaxHP());
            return true;
        }
        return false;
    }

    @Override
    public Action copy() {
        return new MakeVeteran(this.unitId);
    }
}
