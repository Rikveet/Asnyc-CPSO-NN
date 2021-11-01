import org.ejml.simple.SimpleMatrix;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class CPSO {

    public static SimpleMatrix[] NeuralNetwork;
    public static SimpleMatrix[] NN;
    public static List<double[]> data;
    public static double maxW = 1;
    public static double minW = -1;
    public static double inertia = 1.6;
    public static double context1 = 1.8;
    public static double context2 = 1.8;
    public static double maxVelocity = 0.65;
    public static int iterations = 1000;
    public static double bestError = Double.MAX_VALUE;
    public static int particles = 50;
    public static volatile boolean updateCopyFlipFlop = false;
    public static int threadLimit = 10;

    public SimpleMatrix[] createNeuralNetwork(int[] config){
        double[][][] NeuralNetwork = new double[config.length-1][][];
        NN = new SimpleMatrix[config.length];

        for (int i = 0; i < NN.length; i++) {
            double[] nodeVals = new double[config[i]];
            Arrays.fill(nodeVals,0);
            NN[i] = new SimpleMatrix(1,config[i],true,nodeVals);
        }
        for(int i=0; i < NeuralNetwork.length; i++){
            NeuralNetwork[i]= new double[config[i]][];
            for (int j=0;j<config[i];j++){
                NeuralNetwork[i][j] = new double[config[i+1]];
                for (int k=0;k<NeuralNetwork[i][j].length;k++){
                    NeuralNetwork[i][j][k] = (Math.random() * (maxW-minW)) + minW;
                }
            }
        }
        SimpleMatrix[] nn = new SimpleMatrix[NeuralNetwork.length];
        for(int i=0; i < NeuralNetwork.length; i++){
            nn[i] = new SimpleMatrix(NeuralNetwork[i]);
        }
        return nn;
    }

    public Swarm[] createSwarms(int[] config){
        int totalNodes = 0;
        for (int i=0; i < config.length-1;i++){
            totalNodes+=config[i];
        }
        Swarm[] swarms = new Swarm[totalNodes];
        int s = 0;
        for(int i=0; i < config.length-1; i++){
            for(int n = 0; n < config[i];n++){
                swarms[s] = new Swarm(i,n,config[i+1],config);
                s++;
            }
        }
        return swarms;
    }

    public double activate(double input){
        double pve = Math.exp(input);
        double nve = Math.exp(-input);
        return (pve - nve)/(pve + nve);
    }

    public void feedForward(double[] input) {
        System.out.println(Arrays.toString(input));

        double[] output = new double[NN[NN.length-1].numCols()];
        for(int _n = 0; _n<NN[0].numCols(); _n++){
            NN[0].set(0,_n,input[_n]);
        }
        for(int l =0; l < NN.length-1; l++){
            for( int _n = 0; _n < NN[l].numCols(); _n++){
                NN[l].set(0,_n,activate(NN[l].get(0,_n)));
            }
            NN[l+1] = NN[l].mult(NeuralNetwork[l]);
        }

        for(int n = 0; n < NN[NN.length-1].numCols(); n++){
            output[n] = activate(NN[NN.length-1].get(0,n));
        }
        System.out.println(Arrays.toString(output));
    }


    public static synchronized void updateWeight(int i, int j,int _j, double w,double bError){
        if (updateCopyFlipFlop){
            while (updateCopyFlipFlop) Thread.onSpinWait();
        }
        NeuralNetwork[i].set(j,_j,w);
        bestError = bError;
        System.out.println(Colors.TEXT_GREEN+" MSE: "+bError);
    }

    public static synchronized SimpleMatrix[] getNetwork(){
        updateCopyFlipFlop = true;
        SimpleMatrix[] NN = new SimpleMatrix[NeuralNetwork.length];
        System.arraycopy(NeuralNetwork, 0, NN, 0, NN.length);
        updateCopyFlipFlop = false;
        return NN;
    }

    public static synchronized List<double[]> getData(){
        return new ArrayList<>(data);
    }

    public void train(int []config){
        Swarm[] swarms = createSwarms(config);
        System.out.println(Colors.TEXT_GREEN + "Swarms Generated");
        for(int _s = 0; _s < swarms.length;_s+=threadLimit){
            int _tc = 0;
            for(int _t = 0; (_s*threadLimit)+_t < swarms.length && _t < threadLimit; _t++){
                swarms[(_s*threadLimit)+_t].start();
                _tc++;
            }
            System.out.println("Activated "+_tc+" swarms");
            for(int _t = 0; (_s*threadLimit)+_t < swarms.length && _t < threadLimit; _t++){
                try{
                    swarms[(_s*threadLimit)+_t].join();
                }
                catch (Exception e){
                    System.out.println(Colors.TEXT_RED + e.getMessage());
                }
            }
        }
        System.out.println("All swarms complete.");
        for (double[] input: data) {
            feedForward(input);
        }
    }

    public void iris(){
        maxW = 0.5;
        minW = -0.5;
        inertia = 0.7;
        context1 = 0.8;
        context2 = 0.8;
        maxVelocity = 0.2;
        iterations = 500;
        bestError = Double.MAX_VALUE;
        particles = 20;
        int []config = {4,4,3};
        Iris iris = new Iris();
        data = iris.LoadData();
        NeuralNetwork = createNeuralNetwork(config);
        System.out.println(Colors.TEXT_GREEN + "Neural Network Generated");
        train(config);
    }

    public void cnae(){
        int []config = {856,30,9};
        CNAE9 cnae9 = new CNAE9();
        data = cnae9.LoadData();
        NeuralNetwork = createNeuralNetwork(config);
        System.out.println(Colors.TEXT_GREEN + "Neural Network Generated");
        train(config);
    }



    public static void main(String [] args){
        CPSO obj = new CPSO();
        obj.iris();
        //obj.cnae();
    }
}
