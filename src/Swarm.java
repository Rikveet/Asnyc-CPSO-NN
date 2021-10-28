import java.util.Arrays;

public class Swarm extends Thread{
    private int i,j,particles;
    private double[][] data;
    private double inertia, context1, context2, maxVelocity, iterations;
    private double[] globalBestError;
    private double[] globalBestWeights;
    private double[][] personalBestError;
    private double[][] Velocities;
    private double[][] Weights;
    private double[][] personalBestWeights;
    private double[][] NeuralNetwork;
    private double[][][] NN;
    private int[] config;

    Swarm(int i, int j, int particleSize, double[][] data, int[] config){
        this.i = i;
        this.j = j;
        this.config = config;
        particles= CPSO.particles;
        inertia = CPSO.inertia;
        context1 = CPSO.context1;
        context2 = CPSO.context2;
        maxVelocity = CPSO.maxVelocity;
        iterations = CPSO.iterations;
        globalBestError = new double[particleSize];
        globalBestWeights = new double[particleSize];
        this.Velocities = new double[particles][particleSize];
        this.Weights = new double[particles][particleSize];
        this.personalBestError = new double[particles][particleSize];
        this.personalBestWeights = new double[particles][particleSize];
        NeuralNetwork = new double[config.length][];
        for(int l =0; l < NeuralNetwork.length; l++){
            NeuralNetwork[l] = new double[config[l]];
        }
        Arrays.fill(globalBestError,Double.MAX_VALUE);
        Arrays.fill(globalBestWeights, (Math.random() * (CPSO.maxW- CPSO.minW)) + CPSO.minW);
        for (double[] velocity : Velocities) {
            Arrays.fill(velocity, 0);
        }
        for (double[] values : Weights) {
            Arrays.fill(values, (Math.random() * (CPSO.maxW- CPSO.minW)) + CPSO.minW);
        }
        for(double[] values : personalBestError){
            Arrays.fill(values, Double.MAX_VALUE);
        }
        for (double[] values : personalBestWeights) {
            Arrays.fill(values, (Math.random() * (CPSO.maxW- CPSO.minW)) + CPSO.minW);
        }
        this.data = new double[data.length][];
        for(int _i = 0; _i < data.length; _i++){
            this.data[_i] = new double[data[_i].length];
            System.arraycopy(data[_i], 0, this.data[_i], 0, data[_i].length);
        }
    }

    public double activate(double input){
        double pve = Math.exp(input);
        double nve = Math.exp(-input);
        return (pve - nve)/(pve + nve);
    }

    public void feedForward(double[] input) {
        for (double[] doubles : NeuralNetwork) {
            Arrays.fill(doubles, 0);
        }
        System.arraycopy(input, 0, NeuralNetwork[0], 0, input.length - 1);
        for(int l =0; l < NeuralNetwork.length-1; l++){
            for(int _n = 0; _n < NeuralNetwork[l].length; _n++){
                NeuralNetwork[l][_n] = activate(NeuralNetwork[l][_n]);
               for (int n =0; n < NeuralNetwork[l+1].length; n++){
                   NeuralNetwork[l][n] += NeuralNetwork[l][_n] * NN[l][_n][n];
               }
            }
        }
        for(int n = 0; n < NeuralNetwork[NeuralNetwork.length-1].length; n++){
            NeuralNetwork[NeuralNetwork.length-1][n] = activate(NeuralNetwork[NeuralNetwork.length-1][n]);
        }
    }

    public void run(){
        for (int _i = 0 ; _i < iterations; _i++){
            System.out.println(i+" "+j+" iteration: "+_i);
            for (int p =0; p < Weights.length; p++){
                for ( int w= 0; w <Weights[p].length; w++){
                    NN = CPSO.getNetwork();
                    NN[i][j][w] = Weights[p][w];
                    double mse = 0;
                    for (double[] input: data){
                        feedForward(input);
                        double[] output = NeuralNetwork[NeuralNetwork.length-1];
                        double expected = input[input.length-1]-1;
                        double error = 0;
                        for (int e = 0; e<output.length; e++){
                            if (e == expected){
                                error +=  ((1-output[e]) * (1-output[e]));
                            }else{
                                error +=  ((0-output[e]) * (0-output[e]));
                            }
                        }
                        error = error/output.length;
                        mse+=error;
                    }
                    mse = mse / data.length;
                    if (personalBestError[p][w] > mse){
                        personalBestError[p][w] = mse;
                        personalBestWeights[p][w] = Weights[p][w];
                    }
                    if(globalBestError[w]>mse){
                        globalBestError[w] = mse;
                        globalBestWeights[w] = Weights[p][w];
                        CPSO.updateWeight(i,j,w,Weights[p][w],mse);
                    }
                    Velocities[p][w] = (inertia * Velocities[p][w]) + ((context1 * Math.random())*(globalBestWeights[w] - Weights[p][w])) + ((context1 * Math.random())*(personalBestWeights[p][w] - Weights[p][w]));
                    if (Velocities[p][w]>maxVelocity){
                        Velocities[p][w] = maxVelocity;
                    }
                    if (Velocities[p][w] < -maxVelocity){
                        Velocities[p][w] = -maxVelocity;
                    }
                    Weights[p][w] += Velocities[p][w];
                }
            }
        }
        System.out.println("Thread for node " + i + " , "+ j + " complete");
    }

}
