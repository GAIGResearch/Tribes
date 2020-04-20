package players.heuristics;

import core.TechnologyTree;
import core.actors.City;
import core.actors.units.Unit;
import core.game.GameState;
import core.*;

import java.util.ArrayList;

public class TribesSimpleHeuristic implements StateHeuristic{

    private int playerID;

    public TribesSimpleHeuristic(int playerID)
    {
        this.playerID = playerID;
    }

    @Override
    public double evaluateState(GameState gameState) {
        int numAvailableActions = gameState.getAllAvailableActions().size();
        int availableProduction = gameState.getTribeProduction();
        TechnologyTree tt = gameState.getTribeTechTree();
        int score = gameState.getScore(playerID);
        ArrayList<Integer> tribesMet = gameState.getTribesMet();
        ArrayList<City> cities = gameState.getCities();
        ArrayList<Unit> units = gameState.getUnits();

        boolean [][] visibility = gameState.getVisibilityMap();
        int visCount = 0;
        for (boolean[] booleans : visibility)
            for (int j = 0; j < booleans.length; ++j)
                visCount += booleans[j] ? 1 : 0;

        return numAvailableActions + availableProduction + tt.getNumResearched() + score + tribesMet.size() + cities.size() + units.size() + visCount;
    }
}
