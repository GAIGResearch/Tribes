package core.actions.unitactions.command;

import core.Types;
import core.actions.Action;
import core.actions.ActionCommand;
import core.actions.unitactions.Convert;
import core.actions.unitactions.Disband;
import core.actors.City;
import core.actors.Tribe;
import core.actors.units.Unit;
import core.game.Board;
import core.game.GameState;

public class DisbandCommand implements ActionCommand {

    @Override
    public boolean execute(Action a, GameState gs) {
        Disband action = (Disband)a;
        int unitId = action.getUnitId();

        Unit unit = (Unit) gs.getActor(unitId);
        Board b = gs.getBoard();
        Tribe t = gs.getTribe(unit.getTribeId());
        City c = (City) b.getActor(unit.getCityId());

        if(action.isFeasible(gs))
        {
            int starsGained = (int) (unit.COST / 2.0); //half, rounded down
            t.addStars(starsGained);
            b.removeUnitFromBoard(unit);
            b.removeUnitFromCity(unit, c, t);
            t.subtractScore(unit.getType().getPoints());
            return true;
        }

        return false;
    }
}
