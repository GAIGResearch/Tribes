package core.FMLearner;
import org.neuroph.core.NeuralNetwork;
import org.neuroph.core.data.DataSetRow;
import org.neuroph.nnet.MultiLayerPerceptron;
import org.neuroph.nnet.learning.BackPropagation;
import org.neuroph.core.data.DataSet;

import java.util.ArrayList;

public  class NN {

    public static ArrayList<Float> trainingData = new ArrayList<>(); //holds attack result
    public static ArrayList<Float> trainingData2 = new ArrayList<>(); //holds target
    public static ArrayList<Float> trainingData3 = new ArrayList<>(); //holds unit attacking
    public static ArrayList<Float> trainingData4 = new ArrayList<>(); // holds if in border or not

    public static ArrayList<Float>expectedValues = new ArrayList<>();
    public static int trainDataCounter = 0;
    public static int testDataCounter = 0;
    public static double[] output;

    static NeuralNetwork nn = createNN();

   static double dataHigh = 0;
    static double dataLow = 0;
    static double normalizedHigh = 1;
    static double normalizedLow = 0;
    public static double finalAccuracy = 0;


    private static NeuralNetwork createNN(){
        MultiLayerPerceptron neuralNet = new MultiLayerPerceptron( 4, 4, 1);
        //Todo implement value correction system
        neuralNet.setLearningRule(new BackPropagation());
        BackPropagation learningRule = neuralNet.getLearningRule();
        learningRule.addListener((event)->{
            BackPropagation bp = (BackPropagation) event.getSource();
            if(bp.getCurrentIteration() == 100) {
               // System.out.println(bp.getCurrentIteration() + ". iteration | Total network error: " + bp.getTotalNetworkError());
                finalAccuracy = bp.getTotalNetworkError();
            }
        });

        // set learning rate and max error
        learningRule.setLearningRate(0.01);
        learningRule.setMaxIterations(100);
        //learningRule.setMaxError(0.3);


        return neuralNet;
    }

    private static double normalize(double x, double dataH) {
        return (x-dataLow)/ (dataH - dataLow);
    }
    public static double denormalise(double x){
        return x*(dataHigh-dataLow) + dataLow;
    }

    private static DataSet createDataset(){
        int inputSize = 4;
        int outputSize = 1;
        DataSet ds = new DataSet(inputSize, outputSize);
        for (int i = 0; i< trainingData.size(); i++){
                if (trainingData.get(i) > dataHigh)
                    dataHigh = trainingData.get(i);

        }
        int length = trainingData.size();
        if(trainingData.size() > expectedValues.size())
            length = expectedValues.size();
        for (int i = 0; i< length; i++){
                DataSetRow dataSetRow = new DataSetRow(new double[]{normalize(trainingData.get(i), dataHigh), normalize(trainingData2.get(i), 11), normalize(trainingData3.get(i), 11), trainingData4.get(i)}, new double[]{normalize(expectedValues.get(i), dataHigh)});
                ds.add(dataSetRow);

        }
        return ds;
    }

    public static void train(){

        DataSet ds = createDataset();
        nn.learn(ds);


    }


    public static void test(double damage, double defenderType, double attackerType, double isInBorder){

        damage = normalize(damage,dataHigh);
        defenderType = normalize(defenderType, 11);
        attackerType = normalize(attackerType, 11);

        nn.setInput(damage, defenderType, attackerType, isInBorder);
        nn.calculate();
        output = nn.getOutput();
        double out = denormalise(output[0]);

         //   System.out.println(out);


        //return output;
    }




}