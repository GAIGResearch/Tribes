package core.FMLearner;
public class Layer {
    public Neuron[] neurons;

    // Constructor for the hidden and output layer
    public Layer(int inNeurons,int numberNeurons) {
        this.neurons = new Neuron[numberNeurons];
        StatUtil statUtil = new StatUtil();
        for(int i = 0; i < numberNeurons; i++) {
            float[] weights = new float[inNeurons];
            for(int j = 0; j < inNeurons; j++) {
                weights[j] = statUtil.RandomFloat(0, 1);
            }
            neurons[i] = new Neuron(weights,statUtil.RandomFloat(0, 1));
        }
    }


    // Constructor for the input layer
    public Layer(Float input[]) {
        this.neurons = new Neuron[input.length];
        for(int i = 0; i < input.length; i++) {
            this.neurons[i] = new Neuron(input[i]);
        }
    }
}