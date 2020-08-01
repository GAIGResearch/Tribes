package core.actions.unitactions.command;

import core.TribesConfig;
import core.actions.Action;
import core.actions.ActionCommand;
import core.actions.unitactions.MakeVeteran;
import core.actors.units.Unit;
import core.game.GameState;

public class MakeVeteranCommand implements ActionCommand {

    @Override
    public boolean execute(Action a, GameState gs) {
        MakeVeteran action = (MakeVeteran)a;
        int unitId = action.getUnitId();
        Unit unit = (Unit) gs.getActor(unitId);
        if(action.isFeasible(gs))
        {
            unit.setVeteran(true);
            unit.setMaxHP(unit.getMaxHP() + TribesConfig.VETERAN_PLUS_HP);
            unit.setCurrentHP(unit.getMaxHP());
            return true;
        }
        return false;
    }
}
