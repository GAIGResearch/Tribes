package core.FMLearner;

import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.factory.Nd4j;


public class NeuralNetwork {



    // Layers

    // Training data
    DataSet tDataSet;

    public NeuralNetwork(float[][] trainingData, float[][] expectedVal) {

        INDArray inputs = Nd4j.create(trainingData);
        INDArray desiredOut = Nd4j.create(expectedVal);

        createModel();

    }


    public void createModel(){

    }



}