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
    public double[] genome;
    public ArrayList<GameplayStats> allStats;
    private TreeMap<Feature, Double> featureValues;



    public Elite(final double weightList[], ArrayList<GameplayStats> allStats) {
        this.genome = new double[weightList.length];
        System.arraycopy(weightList, 0, genome, 0, genome.length);
        this.allStats = allStats;
        featureValues = new TreeMap<>();
    }

    public void setFeatureValue(Feature feature) {
        Pair<Integer, Double> p = feature.getBucketIdx(allStats);
        featureValues.put(feature, p.getSecond());
    }

    public int getFeatureIdx(Feature feature) {
        Pair<Integer, Double> p = feature.getBucketIdx(allStats);
        //featureValues.put(feature, p.getSecond());
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
        ArrayList<String> lines = new ArrayList<>();
        lines.add("-----------------------");
        StringBuilder sb = new StringBuilder("(");
        for(Feature f : featureValues.keySet())
        {
            int bucket = f.getBucketIdx(allStats).getFirst();
            sb.append(bucket).append(",");
        }
        sb.append(")");
        lines.add(sb.toString());

        ArrayList<String> linesInfo = getBasicEliteInfo();
        lines.addAll(linesInfo);

        new IO().writeFile(statsResultsFileName, lines, true);
    }


    public void printInfoConsole() {
        ArrayList<String> lines = getBasicEliteInfo();
        for(String str : lines) {
            System.out.println(str);
        }
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
            EliteRecord eliteInFile = new EliteRecord();
            eliteInFile.readFromFile(filename);
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
            new IO().writeFile(saveFilename, getBasicEliteInfo(), false);
        }
    }

    public ArrayList<String> getBasicEliteInfo()
    {
        ArrayList<String> lines = new ArrayList<>();
        lines.add("Weights: " + printWeights());

        String[] allFeatures = featuresString(", ");
        lines.add("Feature values (map): "+ allFeatures[0]);
        lines.add("Feature values (not in map): "+ allFeatures[1]);
        return lines;
    }

}
