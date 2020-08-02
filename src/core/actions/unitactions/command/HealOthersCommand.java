package core.actions.unitactions.command;

import core.TribesConfig;
import core.Types;
import core.actions.Action;
import core.actions.ActionCommand;
import core.actions.unitactions.HealOthers;
import core.actors.units.Unit;
import core.game.GameState;
import java.util.ArrayList;

public class HealOthersCommand implements ActionCommand {

    @Override
    public boolean execute(Action a, GameState gs) {
        HealOthers action = (HealOthers)a;
        int unitId = action.getUnitId();

        if (action.isFeasible(gs)) {
            Unit unit = (Unit) gs.getActor(unitId);
            ArrayList<Unit> targets = action.getTargets(gs);

            for (Unit target: targets) {
                target.setCurrentHP(Math.min(target.getCurrentHP() + TribesConfig.MINDBENDER_HEAL, target.getMaxHP()));
            }

            unit.transitionToStatus(Types.TURN_STATUS.ATTACKED);
            return true;
        }
        return false;
    }
}
