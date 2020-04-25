package players.heuristics;

import core.TechnologyTree;
import core.actors.City;
import core.actors.units.Unit;
import core.game.GameState;
import core.*;

import java.util.ArrayList;

public class TribesSimpleHeuristic implements StateHeuristic{

    private int playerID;
    private int WIN_BOOST = 100000;
    private int LOSE_BOOST = 100000;

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
            for (boolean aBoolean : booleans) visCount += aBoolean ? 1 : 0;

        int boost = 0;
        if(gameState.isGameOver())
            if(gameState.getTribeWinStatus() == Types.RESULT.WIN)
                boost = WIN_BOOST;
            else if (gameState.getTribeWinStatus() == Types.RESULT.LOSS) boost = LOSE_BOOST;

        return boost + numAvailableActions + availableProduction + tt.getNumResearched() + score + tribesMet.size() + cities.size() + units.size() + visCount;
    }
}
