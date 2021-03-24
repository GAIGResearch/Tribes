/**
 * Author: Cristina Guerrero
 * Date: 5th February 2021
 */

package utils.mapelites;


import core.game.TribeResult;
import utils.Pair;
import utils.file.IO;
import utils.stats.GameplayStats;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.TreeMap;

/**
 * Class containing the information stored in each cell of the MAP-Elites
 * 
 * Returns the feature id and performance of the elite using its stats
 */
public class Elite {
    private double[] genome;
    private ArrayList<GameplayStats> allStats;
    private TreeMap<Feature, Double> featureValues;



    public Elite(final double weightList[], ArrayList<GameplayStats> allStats) {
        this.genome = new double[weightList.length];
        System.arraycopy(weightList, 0, genome, 0, genome.length);
        this.allStats = allStats;
        featureValues = new TreeMap<>();
    }

    public int getFeatureIdx(Feature feature) {
        Pair<Integer, Double> p = feature.getBucketIdx(allStats);
        featureValues.put(feature, p.getSecond());
        return p.getFirst();
    }

    public boolean isBetterThan(Elite other)
    {
        double thisWin = Feature.WINS.getFeatureValue(allStats);
        double otherWin = Feature.WINS.getFeatureValue(other.allStats);
        if(thisWin > otherWin) return true;
        else if(otherWin > thisWin) return false;

        double thisScore = Feature.SCORE.getFeatureValue(allStats);
        double otherScore = Feature.SCORE.getFeatureValue(other.allStats);
        //by default (if equal), we say True.
        return thisScore >= otherScore;
    }

    public String getPerformance() {
        return "Win: " + Feature.WINS.getFeatureValue(allStats) + ", Score: " + Feature.SCORE.getFeatureValue(allStats);
    }

    public void copyWeightsListValues(double[] weightsList) {
        System.arraycopy(genome, 0, weightsList, 0, genome.length);
    }

    public void calculateAllStats() {
        //TODO: Prepare data for output.
        //gameStats.calculateStats();
    }


    public String printWeights() {
        return "[" + weightsString(", ") + "]";
    }

    public void printInfo(String statsResultsFileName) {
        System.out.println("Weights: " + printWeights());
        String[] allFeatures = featuresString(", ");
        System.out.println("Feature values (map): "+ allFeatures[0]);
        System.out.println("Feature values (not in map): "+ allFeatures[1]);

        if (statsResultsFileName != null) {
//            String resultsHeuristicFile = statsResultsFileName + "_" + weightsString("_") + ".txt";
            
            BufferedWriter writer;
            try {
                    writer = new BufferedWriter(new FileWriter(new File(statsResultsFileName), true));

                    writer.write("-----------------------\n");
                    writer.write("(");
                    for(Feature f : featureValues.keySet())
                    {
                        int bucket = f.getBucketIdx(allStats).getFirst();
                        writer.write(bucket + ",");
                    }
                    writer.write(")\n");
                    writer.write("Weights: " + printWeights() + "\n");
                    writer.write("Feature values (map): "+ allFeatures[0] + "\n");
                    writer.write("Feature values (not in map): "+ allFeatures[1] + "\n");
                    writer.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
            //printStats(resultsHeuristicFile);
        }
    }


    public void printInfoConsole() {
        System.out.println("Weights: " + printWeights());
        String[] allFeatures = featuresString(", ");
        System.out.println("Feature values (map): "+ allFeatures[0]);
        System.out.println("Feature values (not in map): "+ allFeatures[1]);
    }

    private String weightsString(String separator) {
        String weights = "";
        for (int i = 0; i < (genome.length - 1); i++) {
            weights += genome[i] + separator;
        }
        weights += genome[genome.length - 1];
        return weights;
    }

    private String[] featuresString(String separator) {
        StringBuilder mapFeaturesStr = new StringBuilder();
        StringBuilder otherFeatures = new StringBuilder();
        for (Feature f : Feature.values())
        {
            double val;
            if(featureValues.containsKey(f))
            {
                val = featureValues.get(f);
                mapFeaturesStr.append(f.getStatName()).append(":")
                        .append(val).append(separator);
            }else{
                val = f.getFeatureValue(this.allStats);
                otherFeatures.append(f.getStatName()).append(":")
                        .append(val).append(separator);
            }
        }
        return new String[]{mapFeaturesStr.toString(), otherFeatures.toString()};
    }

    /*
    Weights: [0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0]
    Feature values (map): ATTACKS:10.3, PERC_RANGE:6.5,
    Feature values (not in map): WIN:90.0, SCORE:7952.0, PRODUCTION:23.8, NUM_SPAWN_ARCHERS:0.8, NUM_SPAWN_CATAPULTS:1.5, NUM_SPAWN_KNIGHTS:0.8, NUM_SPAWN_SWORDMAN:0.0,

     */
    public EliteRecord readFromFile(String filename)
    {
        String[] lines = new IO().readFile(filename);

        //weights
        String weightsLine = lines[0].split(":")[1].trim();
        String[] weights = weightsLine.substring(1, weightsLine.length()-1).split(",");
        double[] doubleWeights = new double[weights.length];
        for(int i = 0; i < weights.length; i++)
        {
            doubleWeights[i] = Double.parseDouble(weights[i]);
        }

        //map and no-map features
        TreeMap<String, Double> features = new TreeMap<>();
        for(int i = 1; i <= 2; i++)
        {
            int start = lines[i].indexOf(":");
            String f = lines[i].substring(start+1, lines[i].length()-1).trim();
            String[] mapFeatures = f.split(",");
            for (String mapFeature : mapFeatures) {
                String chunk = mapFeature.trim();
                String featName = chunk.split(":")[0];
                double featVal = Double.parseDouble(chunk.split(":")[1]);
                features.put(featName, featVal);
            }
        }

        return new EliteRecord(doubleWeights, features);
    }


    public void saveToFile(Path path)
    {
        String saveFilename = "";
        boolean save = false;
        StringBuilder sb = new StringBuilder();
        int[] buckets = new int[featureValues.size()];
        int i = 0;
        for(Feature f : featureValues.keySet())
        {
            buckets[i] = f.getBucketIdx(allStats).getFirst();
            sb.append(buckets[i]).append("-");
            i++;
        }


        String header = sb.toString();
        File f = new File(path.toString());
        int num = 0;
        for(File file : f.listFiles())
        {
            String name = file.getName();
            if(name.contains(header))
            {
                num++;
            }
        }

        if(num > 0)
        {
            String filename = path.toString() + "/" + header + num  + ".txt";
            EliteRecord eliteInFile = readFromFile(filename);
            if(eliteInFile.compareTo(this) > 0)
            {
                saveFilename = path.toString()  + "/" + header + (num+1)  + ".txt";
                save = true;
            }
        }else if(num == 0)
        {
            saveFilename = path.toString() + "/" + header + 1 + ".txt";
            save = true;
        }

        if(save) {
            ArrayList<String> lines = new ArrayList<>();
            lines.add("Weights: " + printWeights());

            String[] allFeatures = featuresString(", ");
            lines.add("Feature values (map): "+ allFeatures[0]);
            lines.add("Feature values (not in map): "+ allFeatures[1]);

            new IO().writeFile(saveFilename, lines);
        }
    }

    private class EliteRecord
    {
        double[] weights;
        TreeMap<String, Double> features;

        public EliteRecord(double[] wList, TreeMap<String, Double> features)
        {
            this.weights = wList;
            this.features = features;
        }

        public int compareTo(Elite e)
        {
            double eWins = Feature.WINS.getFeatureValue(e.allStats);
            double eScore = Feature.SCORE.getFeatureValue(e.allStats);

            double thisWins = features.get("WIN");
            double thisScores = features.get("SCORE");

            if( thisWins > eWins )
                return -1;
            else if( thisWins < eWins)
                return 1;
            else
            {
                if( thisScores > eScore )
                    return -1;
                else if( thisScores < eScore)
                    return 1;
            }
            return 0;
        }
    }

}
