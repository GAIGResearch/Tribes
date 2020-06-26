package core.FMLearner;

import java.util.ArrayList;

public class NeuralNetwork {



    // Layers
    Layer[] layers;

    // Training data
    ArrayList<TrainingData> tDataSet;

    public NeuralNetwork(Float[][] trainingData, Float[][] expectedVal){


        this.tDataSet = createTrainingData(trainingData, expectedVal);


        layers = new Layer[3];
        layers[0] = null; // Input Layer 0,2
        layers[1] = new Layer(2,6); // Hidden Layer 2,6
        layers[2] = new Layer(6,1); // Output Layer 6,1
        System.out.println("============");
        System.out.println("Output before training");
        System.out.println("============");
        for(int i = 0; i < tDataSet.size(); i++) {
            forward(tDataSet.get(i).data);
            System.out.println(layers[2].neurons[0].value);
        }
        train(1000,0.001f);
        System.out.println("============");
        System.out.println("Output after training");
        System.out.println("============");
        for(int i = 0; i < tDataSet.size(); i++) {
            forward(tDataSet.get(i).data);
            System.out.println(layers[2].neurons[0].value);
        }
    }

    public  ArrayList<TrainingData> createTrainingData(Float[][] trainingData, Float[][] expectedVal) {

        ArrayList<TrainingData> td = new ArrayList<>();
        for(int i = 0; i<trainingData.length; i++){
            td.add(new TrainingData(trainingData[i],expectedVal[i]));
        }
        return td;
    }

    public void forward(Float[] inputs) {
        // First bring the inputs into the input layer layers[0]
        layers[0] = new Layer(inputs);
        StatUtil statUtil = new StatUtil();
        for(int i = 1; i < layers.length; i++) {
            for(int j = 0; j < layers[i].neurons.length; j++) {
                float sum = 0;
                for(int k = 0; k < layers[i-1].neurons.length; k++) {
                    sum += layers[i-1].neurons[k].value*layers[i].neurons[j].weights[k];
                }
                sum += layers[i].neurons[j].bias;
                layers[i].neurons[j].value = statUtil.Sigmoid(sum);
            }
        }
    }


    // Meaning we do the following:
    // Calculate the output layer weights, calculate the hidden layer weight then update all the weights
    public void backward(float learning_rate,TrainingData tData) {

        int number_layers = layers.length;
        int out_index = number_layers-1;

        // Update the output layers
        // For each output
        for(int i = 0; i < layers[out_index].neurons.length; i++) {
            // and for each of their weights
            float output = layers[out_index].neurons[i].value;
            float target = tData.expectedOutput[i];
            float derivative = output-target;
            float delta = derivative*(output*(1-output));
            layers[out_index].neurons[i].gradient = delta;
            for(int j = 0; j < layers[out_index].neurons[i].weights.length;j++) {
                float previous_output = layers[out_index-1].neurons[j].value;
                float error = delta*previous_output;
                layers[out_index].neurons[i].cache_weights[j] = layers[out_index].neurons[i].weights[j] - learning_rate*error;
            }
        }

        //Update all the subsequent hidden layers
        for(int i = out_index-1; i > 0; i--) {
            // For all neurons in that layers
            for(int j = 0; j < layers[i].neurons.length; j++) {
                float output = layers[i].neurons[j].value;
                float gradient_sum = sumGradient(j,i+1);
                float delta = (gradient_sum)*(output*(1-output));
                layers[i].neurons[j].gradient = delta;
                // And for all their weights
                for(int k = 0; k < layers[i].neurons[j].weights.length; k++) {
                    float previous_output = layers[i-1].neurons[k].value;
                    float error = delta*previous_output;
                    layers[i].neurons[j].cache_weights[k] = layers[i].neurons[j].weights[k] - learning_rate*error;
                }
            }
        }

        // Here we do another pass where we update all the weights
        for(int i = 0; i< layers.length;i++) {
            for(int j = 0; j < layers[i].neurons.length;j++) {
                layers[i].neurons[j].update_weight();
            }
        }

    }

    // This function sums up all the gradient connecting a given neuron in a given layer
    private float sumGradient(int n_index,int l_index) {
        float gradient_sum = 0;
        Layer current_layer = layers[l_index];
        for(int i = 0; i < current_layer.neurons.length; i++) {
            Neuron current_neuron = current_layer.neurons[i];
            gradient_sum += current_neuron.weights[n_index]*current_neuron.gradient;
        }
        return gradient_sum;
    }


    // This function is used to train being forward and backward.
    public void train(int training_iterations,float learning_rate) {



        for(int i = 0; i < training_iterations; i++) {
            for(int j = 0; j < tDataSet.size(); j++) {
                forward(tDataSet.get(j).data);
                backward(learning_rate,tDataSet.get(j));
            }
        }


    }
}