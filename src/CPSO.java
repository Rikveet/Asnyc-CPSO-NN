import java.util.Arrays;

public class CPSO {

    public static double[][][] NeuralNetwork;
    public static double[][] NN;
    public double[][] data;
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

    public double[][][] createNeuralNetwork(int[] config){
        double[][][] NeuralNetwork = new double[config.length-1][][];
        NN = new double[config.length][];
        for (int i = 0; i < NN.length; i++) {
            NN[i] = new double[config[i]];
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
        return NeuralNetwork;
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
                swarms[s] = new Swarm(i,n,config[i+1],data,config);
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
        for (double[] doubles : NN) {
            Arrays.fill(doubles, 0);
        }
        System.arraycopy(input, 0, NN[0], 0, input.length - 1);
        for(int l =0; l < NN.length-1; l++){
            for(int _n = 0; _n < NN[l].length; _n++){
                NN[l][_n] = activate(NN[l][_n]);
                for (int n =0; n < NN[l+1].length; n++){
                    NN[l+1][n] += NN[l][_n] * NeuralNetwork[l][_n][n];
                }
            }
        }
        for(int n = 0; n < NN[NN.length-1].length; n++){
            NN[NN.length-1][n] = activate(NN[NN.length-1][n]);
        }
        System.out.println(Arrays.toString(NN[NN.length-1]));
    }


    public static synchronized void updateWeight(int i, int j,int _j, double w,double bError){
        NeuralNetwork[i][j][_j] = w;
        bestError = bError;
        System.out.println(Colors.TEXT_GREEN+" MSE: "+bError);
        updateCopyFlipFlop = true;

    }

    public static synchronized double[][][] getNetwork(){
        double[][][] NN = new double[NeuralNetwork.length][][];
        for (int l = 0; l < NN.length; l++){
            NN[l] = new double[NeuralNetwork[l].length][];
            for (int n = 0; n < NN[l].length; n++){
                NN[l][n] = new double[NeuralNetwork[l][n].length];
                System.arraycopy(NeuralNetwork[l][n], 0, NN[l][n], 0, NN[l][n].length);
            }
        }
        updateCopyFlipFlop = false;
        return NN;
    }

    public void train(int []config){
        Swarm[] swarms = createSwarms(config);
        System.out.println(Colors.TEXT_GREEN + "Swarms Generated");
        for (Swarm s: swarms){
            s.start();
        }
        for (Swarm s: swarms){
            try{
                s.join();
            }
            catch (Exception e){
                System.out.println(Colors.TEXT_RED + e.getMessage());
            }

        }
        System.out.println("Activated all swarms");
        for (double[] input: data) {
            feedForward(input);
        }
    }

    public void iris(){
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
        //obj.iris();
        obj.cnae();
    }
}
