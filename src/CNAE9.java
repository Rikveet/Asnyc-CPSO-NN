import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class CNAE9 {
    double[][] LoadData(){
        try{
            File cnae = new File("src/Data/CNAE-9.data");
            Scanner reader = new Scanner(cnae);
            ArrayList<String> rawData = new ArrayList<>();
            while (reader.hasNextLine()){
                rawData.add(reader.nextLine());
            }
            double[][] data = new double[rawData.size()-1][857];

            for(int i=0; i < data.length; i++){
                String s = rawData.get(i);
                char prefix = s.charAt(0);
                s = s.substring(2);
                s += ",";
                s += prefix;
                String[] S = s.split(",");

                double[] inputs = Arrays.stream(S).mapToDouble(Double::parseDouble).toArray();
                data[i]= inputs.clone();
            }
            System.out.println(Colors.TEXT_GREEN + "CNAE-9 data loaded");
            return data;
        }catch (Exception e){
            System.out.println(Colors.TEXT_RED+e.getMessage());
        }
        return null;
    }
}
