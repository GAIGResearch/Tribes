package players.heuristics;

import core.actors.City;
import core.game.GameState;
import utils.StatSummary;

import java.util.ArrayList;

/**
 * Compares some important game metrics between an old gamestate and a new gamestate. Returns a score called 'entropy'.
 * An entropy of 0 means the gamestates are identical in terms of the metrics being compared, positive entropy means
 * that some metrics in the new gamestate have increased.
 */
public class TribesDiffHeuristic implements StateHeuristic {

    private int playerID;
    ArrayList<Integer> allIds;

    public TribesDiffHeuristic(int playerID, ArrayList<Integer> allIds)
    {
        this.allIds = allIds;
        this.playerID = playerID;
    }

    @Override
    public double evaluateState(GameState gsOld, GameState gsNew) {
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
        double difference = 0;
        int connections = gsNew.getTribe(playerID).getConnectedCities().size();
        boolean [][] visibility = gsNew.getVisibilityMap();
        int visCountNew = 0;
        for (boolean[] booleans : visibility)
            for (boolean aBoolean : booleans) visCountNew += aBoolean ? 1 : 0;

        visibility = gsOld.getVisibilityMap();
        int visCountOld = 0;
        for (boolean[] booleans : visibility)
            for (boolean aBoolean : booleans) visCountOld += aBoolean ? 1 : 0;


        difference += (connections - gsOld.getTribe(playerID).getConnectedCities().size());
        difference += (visCountNew - visCountOld);
        return difference;
    }

    private double score(GameState gsOld, GameState gsNew, int playerId)
    {
        //Metrics of the game that we want to maximise.
        int production = gsNew.getTribeProduction(playerId);
        int technologies = gsNew.getTribeTechTree(playerId).getNumResearched();
        int score = gsNew.getScore(playerId);
        int cities = gsNew.getCities(playerId).size();
        int sumCityLevelsNew = 0;
        for(City c : gsNew.getCities(playerId))
            sumCityLevelsNew += c.getLevel();
        int units = gsNew.getUnits(playerId).size();
        int kills = gsNew.getNKills(playerId);

        //compare with old metrics and calculate entropy.
        double difference = 0;

        int sumCityLevelsOld = 0;
        for(City c : gsOld.getCities(playerId))
            sumCityLevelsOld += c.getLevel();

        difference += 5 * (production - gsOld.getTribeProduction(playerId));
        difference += 4 * (technologies - gsOld.getTribeTechTree(playerId).getNumResearched());
        difference += 0.1 * (score - gsOld.getScore(playerId));
        difference += 4 * (cities - gsOld.getCities(playerId).size());
        difference += 2 * (units - gsOld.getUnits(playerId).size());
        difference += 3 * (kills - gsOld.getNKills(playerId));
        difference += 2 * (sumCityLevelsNew - sumCityLevelsOld);

        return difference;
    }
}
