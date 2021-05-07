package utils.mapelites;

import utils.file.IO;

import java.util.ArrayList;
import java.util.TreeMap;

public class EliteRecord
{
    double[] weights;
    TreeMap<String, Double> features;

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


    private String featuresString(String separator) {

        StringBuilder mapFeaturesStr = new StringBuilder();
        for(String featString : features.keySet())
            mapFeaturesStr.append(featString).append(":").append(features.get(featString)).append(separator);
        return mapFeaturesStr.toString();
    }

    public void printInfo(String statsResultsFileName, String coordStr) {
        ArrayList<String> lines = new ArrayList<>();
        lines.add("-----------------------");
        lines.add(coordStr);

        StringBuilder w = new StringBuilder();
        for (int i = 0; i < (weights.length - 1); i++)
            w.append(weights[i]).append(",");
        w.append(weights[weights.length - 1]);

        lines.add("Weights: " + w);
        lines.add("Feature values: "+ featuresString(", "));

        new IO().writeFile(statsResultsFileName, lines, true);
    }


    public void readFromFile(String filename)
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
        TreeMap<String, Double> featuresMap = new TreeMap<>();
        for(int i = 1; i <= 2; i++)
        {
            int start = lines[i].indexOf(":");
            String f = lines[i].substring(start+1, lines[i].length()-1).trim();
            String[] mapFeatures = f.split(",");
            for (String mapFeature : mapFeatures) {
                String chunk = mapFeature.trim();
                String featName = chunk.split(":")[0];
                double featVal = Double.parseDouble(chunk.split(":")[1]);
                featuresMap.put(featName, featVal);
            }
        }

        this.weights = doubleWeights;
        this.features = featuresMap;
    }
}