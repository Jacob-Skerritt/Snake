/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package snake;

/**
 *
 * @author Jacob
 */
public class test {
    
    public static void main(String[] args){
        int r = 60, count = 0;
        while(r > 5 && r < 77){
            r = (int) (Math.random() * 69) + (60/10);
            System.out.println(count);
            count++;
        }
    }
    
}
