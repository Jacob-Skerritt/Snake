/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package snake;

import java.awt.EventQueue;
import java.io.IOException;
import java.text.ParseException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.sampled.UnsupportedAudioFileException;

/**
 *
 * @author Jacob
 */
public class Snake {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        
        EventQueue.invokeLater(() -> {
            SnakeFrame snake;
                   try {
                       snake = new SnakeFrame();
                       snake.setVisible(true);
                   } catch (UnsupportedAudioFileException | IOException | ParseException ex) {
                       Logger.getLogger(Snake.class.getName()).log(Level.SEVERE, null, ex);
                   }
            
        });
    }
    
}
