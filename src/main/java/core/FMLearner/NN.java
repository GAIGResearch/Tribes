package core.FMLearner;
import org.neuroph.core.NeuralNetwork;
import org.neuroph.core.data.DataSetRow;
import org.neuroph.nnet.MultiLayerPerceptron;
import org.neuroph.nnet.learning.BackPropagation;
import org.neuroph.core.data.DataSet;


public class NN {

    float[][] trainingData;
    float[][] expectedValues;
    NeuralNetwork nn = createNN();
    public NN(float[][] trainingData, float[][] expectedValues){
        this.trainingData = trainingData;
        this.expectedValues = expectedValues;
        train();
        test(6,2);
    }

    private NeuralNetwork createNN(){
        MultiLayerPerceptron neuralNet = new MultiLayerPerceptron( 2, 2, 2);

        neuralNet.setLearningRule(new BackPropagation());
        BackPropagation learningRule = neuralNet.getLearningRule();
        learningRule.addListener((event)->{
            BackPropagation bp = (BackPropagation) event.getSource();
            System.out.println(bp.getCurrentIteration() + ". iteration | Total network error: " + bp.getTotalNetworkError());
        });

        // set learning rate and max error
        learningRule.setLearningRate(0.1);
        learningRule.setMaxIterations(1000);
        //learningRule.setMaxError(0.3);


        return neuralNet;
    }

    private DataSet createDataset(){
        //todo normalise data
        int inputSize = 2;
        int outputSize = 2;
        DataSet ds = new DataSet(inputSize, outputSize);
        for (int i = 0; i< trainingData.length; i++){
            DataSetRow dataSetRow = new DataSetRow( new double[] {trainingData[i][0], trainingData[i][1]}, new double[]{expectedValues[i][0], expectedValues[i][1]});
            ds.addRow(dataSetRow);
        }
        return ds;
    }

    public void train(){

        DataSet ds = createDataset();
        nn.learn(ds);


    }

    public double[] test(double warriorDamage, double warriorCost){
        nn.setInput(warriorDamage,warriorCost);
        nn.calculate();
        double[] output = nn.getOutput();
        for (int i = 0; i<output.length; i++){
            System.out.println(output[i]);

        }
        return output;
    }




}