package players.portfolio;

import core.actors.City;
import core.actors.units.Unit;
import core.game.GameState;
import players.portfolio.scripts.RandomScr;
import players.portfolio.scripts.Script;

import java.util.ArrayList;
import java.util.Random;

public class RandomPortfolio extends Portfolio
{

    Script[] portfolio;

    public RandomPortfolio()
    {
        initPortfolio();
    }


    @Override
    public void initPortfolio() {
        portfolio = new Script[]{new RandomScr(new Random())};
    }

    public ArrayList<ActionAssignment> produceActionAssignments(GameState state)
    {
        ArrayList<ActionAssignment> list = new ArrayList<>();

        for(Script s : portfolio)
        {
            ArrayList<Unit> units = state.getUnits(state.getActiveTribeID());
            for(Unit u : units)
            {
                if(state.getUnitActions(u) != null && state.getUnitActions(u).size() > 0)
                {
                    ActionAssignment aas = new ActionAssignment(u, s);
                    list.add(aas);
                }
            }

            ArrayList<City> cities = state.getCities(state.getActiveTribeID());
            for(City c : cities)
            {
                if(state.getCityActions(c) != null && state.getCityActions(c).size() > 0) {
                    ActionAssignment aas = new ActionAssignment(c, s);
                    list.add(aas);
                }
            }

            if(state.getTribeActions() != null && state.getTribeActions().size() > 0)
                list.add (new ActionAssignment(state.getActiveTribe(), s));
        }

        return list;
    }
}
