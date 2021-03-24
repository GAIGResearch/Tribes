/**
 * Author: Cristina Guerrero
 * Date: 5th February 2021
 */

package utils.mapelites;

import core.Types;
import utils.Pair;
import utils.stats.GameplayStats;
import java.util.ArrayList;

// FIRST:
// Weights (29): Attack, Convert, Spawn, Level Up, Research, Move
//  ATTACKS: 0,50,5
//  PERC_RANGE: 0,100,10 (feature = 100 * proportion range/(range+melee))
//  PRODUCTION: 0,50,5

// SECOND:
// Weights (11): Spawn, Research
//  ATTACKS: 0,20,2
//  PERC_RANGE: 0,10,1 (feature = 5 + (range - melee))
//  PRODUCTION: 0,30,3

// THIRD:
// As second, but:
//                double avgRange = NUM_SPAWN_ARCHERS.getFeatureValue(gameStats) + NUM_SPAWN_CATAPULTS.getFeatureValue(gameStats);
//                double avgMelee = NUM_SPAWN_KNIGHTS.getFeatureValue(gameStats) + NUM_SPAWN_SWORDMAN.getFeatureValue(gameStats);
//                PERC_RANGE = 5.0 + (avgRange - avgMelee);
// Also: 5 representative games, x2 = 10 games per Elite.


public enum Feature {
    WINS("WIN", 0, 100, 10),
    SCORE("SCORE", 4000, 10000, 1000),
    ATTACKS("ATTACKS", 0, 20, 2),
//    PERC_RANGE("PERC_RANGE", 0, 100, 10),//0: All melee, 100: all range
    PERC_RANGE("PERC_RANGE", 0, 10, 1),//0: -5 melee, 10: +5 melee
    PRODUCTION("PRODUCTION", 0, 30, 3),
    NUM_SPAWN_ARCHERS("NUM_SPAWN_ARCHERS", 0, 100, 10),
    NUM_SPAWN_CATAPULTS("NUM_SPAWN_CATAPULTS", 0, 100, 10),
    NUM_SPAWN_KNIGHTS("NUM_SPAWN_KNIGHTS", 0, 100, 10),
    NUM_SPAWN_SWORDMAN("NUM_SPAWN_SWORDMAN", 0, 100, 10);

    String statName;
    Integer minValue;
    Integer maxValue;
    Integer bucketSize;

    Feature(String statName, Integer min, Integer max, Integer bucketSize) {
        this.statName = statName;
        this.minValue = min;
        this.maxValue = max;
        this.bucketSize = bucketSize;
    }

    public static Feature getFeatureByName(String name)
    {
        for(Feature f: Feature.values())
        {
            if(f.statName.equalsIgnoreCase(name))
                return f;
        }
        return null;
    }

    public Integer getFeatureMinValue() {
        return minValue;
    }

    public Integer getFeatureMaxValue() {
        return maxValue;
    }

    public Integer getFeatureBucketSize() {
        return bucketSize;
    }

    public Integer featureArraySize() {
        return Buckets.getMapNBuckets(minValue, maxValue, bucketSize);
    }

    public double getFeatureValue(ArrayList<GameplayStats> gameStats) {
        double featureValue = 0.0;

        switch (this)
        {
            case WINS:
                //count wins
                for (GameplayStats gps : gameStats)  featureValue += (gps.getTribeResult().getResult() == Types.RESULT.WIN) ? 1 : 0;
                return 100.0 * (featureValue / gameStats.size());
            case SCORE:
                for (GameplayStats gps : gameStats)  featureValue += gps.getTribeResult().getScore();
                return featureValue / gameStats.size();
            case PRODUCTION:
                for (GameplayStats gps : gameStats) featureValue += gps.getTribeResult().getProduction();
                return featureValue / gameStats.size();
            case ATTACKS:
                for (GameplayStats gps : gameStats) featureValue += gps.getFinalActionCount(Types.ACTION.ATTACK);
                return featureValue / gameStats.size();

            case NUM_SPAWN_ARCHERS:
                for (GameplayStats gps : gameStats) featureValue += gps.getFinalActionCount("Spawn ARCHER");
                return featureValue / gameStats.size();
            case NUM_SPAWN_CATAPULTS:
                for (GameplayStats gps : gameStats) featureValue += gps.getFinalActionCount("Spawn CATAPULT");
                return featureValue / gameStats.size();
            case NUM_SPAWN_KNIGHTS:
                for (GameplayStats gps : gameStats) featureValue += gps.getFinalActionCount("Spawn KNIGHT");
                return featureValue / gameStats.size();
            case NUM_SPAWN_SWORDMAN:
                for (GameplayStats gps : gameStats) featureValue += gps.getFinalActionCount("Spawn SWORDMAN");
                return featureValue / gameStats.size();

            case PERC_RANGE:
//                for (GameplayStats gps : gameStats)
//                {
//                    // Hypothesis: The fact that I give high value to the "Spawn ARCHER" action _alone_ doesn't mean that
//                    // there will be more archers AND the strategy will be successful.
//                    int rangeSpawns = gps.getFinalActionCount("Spawn ARCHER") + gps.getFinalActionCount("Spawn CATAPULT");
//                    int meleeSpawns = gps.getFinalActionCount("Spawn KNIGHT") + gps.getFinalActionCount("Spawn SWORDMAN");
//
//                    featureValue = 5 + (rangeSpawns - meleeSpawns); //0 .. 10
//
////                    int total = rangeSpawns + meleeSpawns;
////                    double prop = 50.0;
////                    if(total>0)
////                        prop = 100.0 * (double)rangeSpawns / total;
////                    featureValue += prop;
//                }
//                return featureValue / gameStats.size();

                double avgRange = NUM_SPAWN_ARCHERS.getFeatureValue(gameStats) + NUM_SPAWN_CATAPULTS.getFeatureValue(gameStats);
                double avgMelee = NUM_SPAWN_KNIGHTS.getFeatureValue(gameStats) + NUM_SPAWN_SWORDMAN.getFeatureValue(gameStats);
                double diff = 5.0 + (avgRange - avgMelee);
                return diff;
        }

        return -1;
    }

    public Pair<Integer, Double> getBucketIdx(ArrayList<GameplayStats> gameStats) {
        double featureStatsValue = getFeatureValue(gameStats);
        int bucketId = Buckets.getMapIdx(featureStatsValue, minValue, maxValue, bucketSize);
        return new Pair<>(bucketId, featureStatsValue);
    }

    public String[] featureArrayInfo() {
        return Buckets.getMapRangesInfo(minValue, maxValue, bucketSize);
    }

    public String getStatName() {return statName;}
}
