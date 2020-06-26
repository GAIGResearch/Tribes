package core.FMLearner;

public class TrainingData {

    Float[] data;
    Float[] expectedOutput;

    public TrainingData(Float[] data, Float[] expectedOutput) {
        this.data = data;
        this.expectedOutput = expectedOutput;
    }

}
