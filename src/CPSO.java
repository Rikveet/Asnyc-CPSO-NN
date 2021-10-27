public class CPSO {

    public double[][][] NeuralNetwork;
    public double[][] data;
    public static double maxW = 1;
    public static double minW = -1;
    public static double inertia = 0.729844;
    public static double context1 = 1.49618;
    public static double context2 = 1.49618;
    public static double maxVelocity = 0.7;
    public double bestError = Double.MIN_VALUE;
    public static int particles = 5;

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
                swarms[s] = new Swarm(i,n,config[i+1],data);
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
    }

    public static void main(String [] args){
        CPSO obj = new CPSO();
        obj.iris();
    }
}
