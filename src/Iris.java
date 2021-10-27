import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class Iris {
    double[][] LoadData(){
        try{
            File irisData = new File("src/Data/iris.data");
            Scanner reader = new Scanner(irisData);
            ArrayList<String> rawData = new ArrayList<>();
            while (reader.hasNextLine()){
                rawData.add(reader.nextLine());
            }
            double[][] data = new double[rawData.size()-1][5];

            for(int i=0; i < data.length; i++){
                String s = rawData.get(i);
                s = s.replace("Iris-setosa","1").replace("Iris-versicolor","2").replace("Iris-virginica","3");
                String[] S = s.split(",");
                double[] inputs = Arrays.stream(S).mapToDouble(Double::parseDouble).toArray();
                data[i]= inputs.clone();
            }
            System.out.println(Colors.TEXT_GREEN + "Iris data loaded");
            return data;
        }catch (Exception e){
            System.out.println(Colors.TEXT_RED+e.getMessage());
        }
        return null;
    }
}
