package players.heuristics;

import core.game.GameState;

/**
 * Compares some important game metrics between an old gamestate and a new gamestate. Returns a score called 'entropy'.
 * An entropy of 0 means the gamestates are identical in terms of the metrics being compared, positive entropy means
 * that some metrics in the new gamestate have increased.
 */
public class TribesEntropyHeuristic implements StateHeuristic {

    private int playerID;

    public TribesEntropyHeuristic(int playerID)
    {
        this.playerID = playerID;
    }

    @Override
    public double evaluateState(GameState gsOld, GameState gsNew) {
        //Metrics of the game that we want to maximise.
        int actions = gsNew.getAllAvailableActions().size();
        int production = gsNew.getTribeProduction();
        int technologies = gsNew.getTribeTechTree().getNumResearched();
        int score = gsNew.getScore(playerID);
        int tribesMet = gsNew.getTribesMet().size();
        int cities = gsNew.getCities().size();
        int units = gsNew.getUnits().size();
        int kills = gsNew.getActiveTribe().getnKills();
        int connections = gsNew.getActiveTribe().getConnectedCities().size();

        boolean [][] visibility = gsNew.getVisibilityMap();
        int visCountNew = 0;
        for (boolean[] booleans : visibility)
            for (boolean aBoolean : booleans) visCountNew += aBoolean ? 1 : 0;

        //compare with old metrics and calculate entropy.
        double entropy = 0;

        visibility = gsOld.getVisibilityMap();
        int visCountOld = 0;
        for (boolean[] booleans : visibility)
            for (boolean aBoolean : booleans) visCountOld += aBoolean ? 1 : 0;

        entropy += actions > gsOld.getAllAvailableActions().size() ? 1 : 0;
        entropy += production > gsOld.getTribeProduction() ? 1 : 0;
        entropy += technologies > gsOld.getTribeTechTree().getNumResearched() ? 1 : 0;
        entropy += score > gsOld.getScore(playerID) ? 1 : 0;
        entropy += tribesMet > gsOld.getTribesMet().size() ? 1 : 0;
        entropy += cities > gsOld.getCities().size() ? 1 : 0;
        entropy += units > gsOld.getUnits().size() ? 1 : 0;
        entropy += kills > gsOld.getActiveTribe().getnKills() ? 1 : 0;
        entropy += connections > gsOld.getActiveTribe().getConnectedCities().size() ? 1 : 0;
        entropy += visCountNew > visCountOld ? 1 : 0;

        return entropy;
    }
}
