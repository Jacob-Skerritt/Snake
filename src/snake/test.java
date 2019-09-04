/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package snake;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Iterator;
import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONException;
public class test {
    
    public static void main(String[] args){
        
        
           File SNAKEDATAFILE= new File("src/snake/snakeData.txt");
           
           StringBuilder nickname = new StringBuilder();
           int[] topTen = new int[10];
           JSONArray tempArray;
           JSONObject snakeData;

        try {
            BufferedReader br = new BufferedReader(new FileReader(SNAKEDATAFILE));
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();

            while (line != null) {
                sb.append(line);
                line = br.readLine();
            }

            snakeData = new JSONObject(sb.toString());
            nickname.append( snakeData.get("nickname"));
            tempArray = snakeData.getJSONArray("topTenUser");
            nickname = new StringBuilder("eh");
            snakeData.put("nickname", nickname);
            
            for (int i=0; i<tempArray.length(); i++) {
                JSONObject item = tempArray.getJSONObject(i);
                String objectString = item.names().getString(0);
                int num = item.getInt(objectString);
                //System.out.println(topTen[i]);
                tempArray.put(i,i);
                System.out.println(num);
            }     
            snakeData.put("topTenUser", tempArray);
            System.out.println(snakeData);
            
            try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(SNAKEDATAFILE.getAbsolutePath()))) {
                snakeData.write(writer);
                writer.write("\n");
            } catch (Exception e) {
            }
       } catch(IOException | JSONException e) {
       e.printStackTrace();
        }

        
        
    }
    
}
