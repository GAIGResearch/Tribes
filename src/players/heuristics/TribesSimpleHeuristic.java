package players.heuristics;

import core.TechnologyTree;
import core.actors.City;
import core.actors.units.Unit;
import core.game.GameState;
import core.*;
import utils.stats.StatSummary;

import java.util.ArrayList;

public class TribesSimpleHeuristic implements StateHeuristic{

    private int playerID;
    ArrayList<Integer> allIds;
    private int WIN_BOOST = 1000000000;
    private int LOSE_BOOST = 1000000000;

    public TribesSimpleHeuristic(int playerID, ArrayList<Integer> allIds)
    {
        this.allIds = allIds;
        this.playerID = playerID;
    }

    @Override
    public double evaluateState(GameState gameState)
    {
        double[] scores = new double[allIds.size()];
        double myScore = 0.0;
        StatSummary othersScore = new StatSummary();
        for(int i = 0; i < scores.length; ++i)
        {
            scores[i] = score(gameState, i);
            if(i == playerID)
                myScore = scores[i];
            else
                othersScore.add(scores[i]);
        }
        double scoreDiff = myScore - othersScore.mean();

        double visCount = viscPerc(gameState);

        return scoreDiff + visCount*10;
    }

    private double viscPerc(GameState gameState)
    {
        boolean [][] visibility = gameState.getVisibilityMap();
        int visCount = 0;
        for (boolean[] booleans : visibility)
            for (boolean aBoolean : booleans) visCount += aBoolean ? 1 : 0;

        return (double)visCount / (visibility.length*visibility[0].length);
    }

    private double score(GameState gameState, int playerId)
    {
        int numAvailableActions = gameState.getAllAvailableActions().size();
        int availableProduction = gameState.getTribeProduction(playerId);
        TechnologyTree tt = gameState.getTribeTechTree(playerId);
        int score = gameState.getScore(playerId);
        ArrayList<City> cities = gameState.getCities(playerId);
        ArrayList<Unit> units = gameState.getUnits(playerId);

        int boost = 0;
        if(gameState.isGameOver())
            if(gameState.getTribeWinStatus(playerId) == Types.RESULT.WIN)
                boost = WIN_BOOST;
            else if (gameState.getTribeWinStatus(playerId) == Types.RESULT.LOSS)
                boost = LOSE_BOOST;


        return boost + numAvailableActions + availableProduction*100 + tt.getNumResearched()*10 + score + cities.size()*500 + units.size()*10;
    }
}
