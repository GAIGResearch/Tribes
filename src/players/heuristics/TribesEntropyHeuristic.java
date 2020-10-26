package players.heuristics;

import core.game.GameState;
import utils.stats.StatSummary;

import java.util.ArrayList;

/**
 * Compares some important game metrics between an old gamestate and a new gamestate. Returns a score called 'entropy'.
 * An entropy of 0 means the gamestates are identical in terms of the metrics being compared, positive entropy means
 * that some metrics in the new gamestate have increased.
 */
public class TribesEntropyHeuristic implements StateHeuristic {

    private int playerID;
    ArrayList<Integer> allIds;

    public TribesEntropyHeuristic(int playerID, ArrayList<Integer> allIds)
    {
        this.allIds = allIds;
        this.playerID = playerID;
    }

    @Override
    public double evaluateState(GameState gsOld, GameState gsNew)
    {
        double[] scores = new double[allIds.size()];
        double myScore = 0.0;
        StatSummary othersScore = new StatSummary();
        for(int i = 0; i < scores.length; ++i)
        {
            scores[i] = score(gsOld, gsNew, i);
            if(i == playerID)
                myScore = scores[i];
            else
                othersScore.add(scores[i]);
        }

        double scoreDiff = myScore - othersScore.mean();
        double scoreOwn = scoreOwn(gsOld, gsNew);
        return scoreDiff + scoreOwn;
    }

    private double scoreOwn(GameState gsOld, GameState gsNew)
    {
        int connections = gsNew.getTribe(playerID).getConnectedCities().size();
        boolean [][] visibility = gsNew.getVisibilityMap();
        int visCountNew = 0;
        for (boolean[] booleans : visibility)
            for (boolean aBoolean : booleans) visCountNew += aBoolean ? 1 : 0;

        visibility = gsOld.getVisibilityMap();
        int visCountOld = 0;
        for (boolean[] booleans : visibility)
            for (boolean aBoolean : booleans) visCountOld += aBoolean ? 1 : 0;

        double entropy = 0;
        entropy += connections > gsOld.getTribe(playerID).getConnectedCities().size() ? 1 : 0;
        entropy += visCountNew > visCountOld ? 1 : 0;
        return entropy;
    }


    private double score(GameState gsOld, GameState gsNew, int playerId) {
        //Metrics of the game that we want to maximise.
        int production = gsNew.getTribeProduction(playerId);
        int technologies = gsNew.getTribeTechTree(playerId).getNumResearched();
        int score = gsNew.getScore(playerId);
        int cities = gsNew.getCities(playerId).size();
        int units = gsNew.getUnits(playerId).size();
        int kills = gsNew.getNKills(playerId);

        //compare with old metrics and calculate entropy.
        double entropy = 0;

        entropy += production > gsOld.getTribeProduction(playerId) ? 1 : 0;
        entropy += technologies > gsOld.getTribeTechTree(playerId).getNumResearched() ? 1 : 0;
        entropy += score > gsOld.getScore(playerId) ? 1 : 0;
        entropy += cities > gsOld.getCities(playerId).size() ? 1 : 0;
        entropy += units > gsOld.getUnits(playerId).size() ? 1 : 0;
        entropy += kills > gsOld.getNKills(playerId) ? 1 : 0;

        return entropy;
    }
}
