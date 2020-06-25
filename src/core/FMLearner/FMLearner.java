package core.FMLearner;
import org.neuroph.core.Layer;
import org.neuroph.core.NeuralNetwork;
import org.neuroph.core.Neuron;
import org.neuroph.core.data.DataSetRow;
import org.neuroph.nnet.learning.BackPropagation;
import org.neuroph.util.ConnectionFactory;
import org.neuroph.core.data.DataSet;

import java.util.prefs.BackingStoreException;


public class FMLearner {

    Integer[] warriorDamage_train;
    Integer[] warriorCost_train;
    Integer[] warriorDamage_test;
    Integer[] warriorCost_test;
    NeuralNetwork nn = createNN();
    public FMLearner( Integer[] warriorDamage_train, Integer[] warriorCost_train, Integer[] warriorDamage_test, Integer[] warriorCost_test){
        this.warriorCost_train = warriorCost_train;
        this.warriorCost_test = warriorCost_test;
        this.warriorDamage_train = warriorDamage_train;
        this.warriorDamage_test = warriorDamage_test;
    }

    private NeuralNetwork createNN(){
        Layer inputLayer = new Layer();
        inputLayer.addNeuron(new Neuron());
        inputLayer.addNeuron(new Neuron());

        Layer hiddenLayerOne = new Layer();
        hiddenLayerOne.addNeuron(new Neuron());
        hiddenLayerOne.addNeuron(new Neuron());
        hiddenLayerOne.addNeuron(new Neuron());
        hiddenLayerOne.addNeuron(new Neuron());

        Layer hiddenLayerTwo = new Layer();
        hiddenLayerTwo.addNeuron(new Neuron());
        hiddenLayerTwo.addNeuron(new Neuron());
        hiddenLayerTwo.addNeuron(new Neuron());
        hiddenLayerTwo.addNeuron(new Neuron());

        Layer outputLayer = new Layer();
        outputLayer.addNeuron(new Neuron());
        NeuralNetwork ann = new NeuralNetwork();
        ann.addLayer(0, inputLayer);
        ann.addLayer(1, hiddenLayerOne);
        ConnectionFactory.fullConnect(ann.getLayerAt(0), ann.getLayerAt(1));
        ann.addLayer(2, hiddenLayerTwo);
        ConnectionFactory.fullConnect(ann.getLayerAt(1), ann.getLayerAt(2));
        ann.addLayer(3, outputLayer);
        ConnectionFactory.fullConnect(ann.getLayerAt(2), ann.getLayerAt(3));
        ConnectionFactory.fullConnect(ann.getLayerAt(0),
                ann.getLayerAt(ann.getLayersCount()-1), false);
        ann.setInputNeurons(inputLayer.getNeurons());
        ann.setOutputNeurons(outputLayer.getNeurons());
        return ann;
    }

    private DataSet createDataset(){
        int inputSize = 2;
        int outputSize = 2;
        DataSet ds = new DataSet(inputSize, outputSize);
        for (int i = 0; i< warriorDamage_train.length; i++){
            DataSetRow dataSetRow = new DataSetRow( new double[] {warriorDamage_train[i], warriorCost_train[i]}, new double[]{warriorDamage_test[i], warriorCost_test[i]});
            ds.add(dataSetRow);
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
        System.out.println(output);
        return output;
    }




}
