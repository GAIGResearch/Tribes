package core.actions.unitactions;

import core.TechnologyTree;
import core.Types;
import core.actions.Action;
import core.actors.City;
import core.actors.Tribe;
import core.game.Board;
import core.game.GameState;
import core.actors.units.Unit;

public class Disband extends UnitAction
{
    public Disband(int unitId)
    {
        super.unitId = unitId;
    }

    @Override
    public boolean isFeasible(final GameState gs)
    {
        Unit unit = (Unit) gs.getActor(this.unitId);
        TechnologyTree tt = gs.getTribe(unit.getTribeId()).getTechTree();
        return unit.isFresh() && tt.isResearched(Types.TECHNOLOGY.FREE_SPIRIT);
    }

    @Override
    public boolean execute(GameState gs) {
        Unit unit = (Unit) gs.getActor(this.unitId);
        Board b = gs.getBoard();
        Tribe t = gs.getTribe(unit.getTribeId());
        City c = (City) b.getActor(unit.getCityId());

        if(isFeasible(gs))
        {
            int starsGained = (int) (unit.COST / 2.0); //half, rounded down
            t.addStars(starsGained);
            b.removeUnitFromBoard(unit);
            b.removeUnitFromCity(unit, c);
            t.subtractScore(unit.getType().getPoints());
            return true;
        }

        return false;
    }

    @Override
    public Action copy() {
        return new Disband(this.unitId);
    }

    @Override
    public String toString() {
        return "Disband{unitID=" + unitId + "}";
    }
}
