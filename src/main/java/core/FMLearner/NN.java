package core.FMLearner;
import org.neuroph.core.NeuralNetwork;
import org.neuroph.core.data.DataSetRow;
import org.neuroph.nnet.MultiLayerPerceptron;
import org.neuroph.nnet.learning.BackPropagation;
import org.neuroph.core.data.DataSet;
import org.neuroph.util.data.norm.MaxMinNormalizer;
import org.neuroph.util.data.norm.Normalizer;

import java.util.ArrayList;
import java.util.Arrays;

public  class NN {

    public static ArrayList<Float> trainingData = new ArrayList<>();
    public static ArrayList<Float>expectedValues = new ArrayList<>();
    public static int trainDataCounter = 0;
    public static int testDataCounter = 0;

    static NeuralNetwork nn = createNN();

   static double dataHigh = 0;
    static double dataLow = 0;
    static double normalizedHigh = 1;
    static double normalizedLow = 0;


//    public void addData(float[][] trainingData, float[][] expectedValues){
//        this.trainingData = trainingData;
//        this.expectedValues = expectedValues;
//
//    }

    private static NeuralNetwork createNN(){
        MultiLayerPerceptron neuralNet = new MultiLayerPerceptron( 1, 1, 1);
        //Todo implement value correction system
        neuralNet.setLearningRule(new BackPropagation());
        BackPropagation learningRule = neuralNet.getLearningRule();
        learningRule.addListener((event)->{
            BackPropagation bp = (BackPropagation) event.getSource();
            System.out.println(bp.getCurrentIteration() + ". iteration | Total network error: " + bp.getTotalNetworkError());
        });

        // set learning rate and max error
        learningRule.setLearningRate(0.1);
        learningRule.setMaxIterations(100);
        //learningRule.setMaxError(0.3);


        return neuralNet;
    }

    private static double normalize(double x) {
        return (x-dataLow)/ (dataHigh - dataLow);
    }
    private static double denormalise(double x){
        return x*(dataHigh-dataLow) + dataLow;
    }

    private static DataSet createDataset(){
        int inputSize = 1;
        int outputSize = 1;
        DataSet ds = new DataSet(inputSize, outputSize);
        for (int i = 0; i< trainingData.size(); i++){
            if(trainingData.get(i) > dataHigh)
                dataHigh = trainingData.get(i);
        }
        for (int i = 0; i< trainingData.size(); i++){
            DataSetRow dataSetRow = new DataSetRow( new double[] {normalize(trainingData.get(i))}, new double[]{normalize(expectedValues.get(i))});
            ds.addRow(dataSetRow);
        }
        return ds;
    }

    public static void train(){

        DataSet ds = createDataset();
        nn.learn(ds);


    }


    public static void test(double warriorDamage){
        warriorDamage = normalize(warriorDamage);
        nn.setInput(warriorDamage);
        nn.calculate();
        double[] output = nn.getOutput();
//        for (int i = 0; i<output.length; i++){
//            System.out.println(denormalise(output[i]));
//
//        }
        //return output;
    }




}