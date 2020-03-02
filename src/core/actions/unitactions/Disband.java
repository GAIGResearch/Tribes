package core.actions.unitactions;

import core.TechnologyTree;
import core.Types;
import core.actions.Action;
import core.actors.City;
import core.actors.Tribe;
import core.game.Board;
import core.game.GameState;
import core.actors.units.Unit;

import java.util.LinkedList;

public class Disband extends UnitAction
{
    public Disband(Unit target)
    {
        super.unit = target;
    }

    @Override
    public LinkedList<Action> computeActionVariants(final GameState gs) {
        LinkedList<Action> disbands = new LinkedList();

        Disband disbandAction = new Disband(this.unit);
        if(disbandAction.isFeasible(gs))
            disbands.add(disbandAction);

        return disbands;
    }

    @Override
    public boolean isFeasible(final GameState gs)
    {
        TechnologyTree tt = gs.getTribe(unit.getTribeId()).getTechTree();
        if(tt.isResearched(Types.TECHNOLOGY.FREE_SPIRIT))
            return true;
        return false;
    }

    @Override
    public boolean execute(GameState gs) {
        Board b = gs.getBoard();
        Tribe t = gs.getTribe(unit.getTribeId());
        City c = (City) b.getActor(unit.getCityID());
        if(isFeasible(gs))
        {
            int starsGained = (int) (unit.COST / 2.0); //half, rounded down
            t.addStars(starsGained);
            b.removeUnitFromBoard(unit);
            b.removeUnitFromCity(unit, c);

            //TODO: Need unit points to remove them when disbanding a unit
            //c.removePoints(unit.POINTS);
            return true;
        }

        return false;
    }
}
