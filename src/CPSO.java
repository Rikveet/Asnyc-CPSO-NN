public class CPSO {

    public static double[][][] NeuralNetwork;
    public double[][] data;
    public static double maxW = 1;
    public static double minW = -1;
    public static double inertia = 0.729844;
    public static double context1 = 1.49618;
    public static double context2 = 1.49618;
    public static double maxVelocity = 0.7;
    public static int iterations = 50;
    public static double bestError = Double.MAX_VALUE;
    public static int particles = 5;
    public static volatile boolean updateCopyFlipFlop = false;

    public double[][][] createNeuralNetwork(int[] config){
        double[][][] NeuralNetwork = new double[config.length-1][][];
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

    public void iris(){
        int []config = {4,4,3};
        Iris iris = new Iris();
        data = iris.LoadData();
        NeuralNetwork = createNeuralNetwork(config);
        System.out.println(Colors.TEXT_GREEN + "Neural Network Generated");
        Swarm[] swarms = createSwarms(config);
        System.out.println(Colors.TEXT_GREEN + "Swarms Generated");
        for (Swarm s: swarms){
            s.start();
        }
        System.out.println("Activated all swarms");
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

    public static void main(String [] args){
        CPSO obj = new CPSO();
        obj.iris();
    }
}
