import java.util.Arrays;

public class Swarm {
    private double[][][] NeuralNetwork;
    private int i,j,particles;
    private double[][] data;
    private double inertia, context1, context2, maxVelocity;
    private double[] bestError;
    private double[][] Velocities;
    private double[][] Weights;
    private double[][] BestWeights;

    Swarm(int i, int j, int particleSize, double[][] data){
        this.i = i;
        this.j = j;
        particles= CPSO.particles;
        inertia = CPSO.inertia;
        context1 = CPSO.context1;
        context2 = CPSO.context2;
        maxVelocity = CPSO.maxVelocity;
        bestError = new double[particles];
        this.Velocities = new double[particles][particleSize];
        this.Weights = new double[particles][particleSize];
        this.BestWeights = new double[particles][particleSize];
        for (double[] velocity : Velocities) {
            Arrays.fill(velocity, 0);
        }
        for (double[] values : Weights) {
            Arrays.fill(values, (Math.random() * (CPSO.maxW- CPSO.minW)) + CPSO.minW);
        }
        for (double[] values : BestWeights) {
            Arrays.fill(values, Double.MAX_VALUE);
        }

        this.data = new double[data.length][];
        for(int _i = 0; _i < data.length; _i++){
            this.data[_i] = new double[data[_i].length];
            System.arraycopy(data[_i], 0, this.data[_i], 0, data[_i].length);
        }
    }

    public void train(){

    }
}
