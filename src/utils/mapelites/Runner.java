package utils.mapelites;

import utils.stats.GameplayStats;

import java.util.ArrayList;

public interface Runner {
    ArrayList<GameplayStats> run(double[] genome);
}
