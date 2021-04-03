/**
 * Author: Cristina Guerrero
 * Date: 5th February 2021
 */

package utils.mapelites;

import core.Types;
import utils.Pair;
import utils.stats.GameplayStats;
import utils.stats.LinearRegression;
import utils.stats.StatSummary;

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
    NUM_SPAWN_SWORDMAN("NUM_SPAWN_SWORDMAN", 0, 100, 10),
    NUM_SPAWN_DEFENDER("NUM_SPAWN_DEFENDER", 0, 10, 1),
    SPEED_WIN("SPEED_WIN", 0, 50, 5),
    RESEARCH_PROGRESS("RESEARCH_PROGRESS", 0, 20, 2), //= 10*slope; 0: no progress; 50: very fast progress
    OWNED_TILES_PROGRESS("OWNED_TILES_PROGRESS", 0, 20, 2),
    ;

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
        StatSummary ss;

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
            case NUM_SPAWN_DEFENDER:
                for (GameplayStats gps : gameStats) featureValue += gps.getFinalActionCount("Spawn DEFENDER");
                return featureValue / gameStats.size();
            case SPEED_WIN:
                ss = new StatSummary();
                for (GameplayStats gps : gameStats)
                {
                    if(gps.getTribeResult().getResult() == Types.RESULT.WIN)
                        ss.add(gps.getNumTurns());
                }
                if(ss.n() > 0)
                    return ss.mean();
                return 0.0;

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
                return 5.0 + (avgRange - avgMelee);

            case RESEARCH_PROGRESS:
                ss = new StatSummary();
                for(GameplayStats gps : gameStats)
                {
                    if(gps.getTribeResult().getResult() == Types.RESULT.WIN)
                        ss.add(getRegression("Num techs", gps).slope());
                }
                if(ss.n() > 0)
                    featureValue = ss.mean() * 10.0;
                return featureValue;

            case OWNED_TILES_PROGRESS:
                ss = new StatSummary();
                for(GameplayStats gps : gameStats)
                {
                    if(gps.getTribeResult().getResult() == Types.RESULT.WIN)
                        ss.add(getRegression("Tiles owned", gps).slope());
                }

                if(ss.n() > 0)
                    featureValue = ss.mean() * 10.0;
                return featureValue;
        }

        return -1;
    }

    private LinearRegression getRegression(String key, GameplayStats gps)
    {
        int[] tilesPerTurn = gps.getStatsArray(key);
        LinearRegression lr = new LinearRegression(tilesPerTurn);
        return lr;
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
