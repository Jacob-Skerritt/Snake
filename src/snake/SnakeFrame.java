/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package snake;

import javax.swing.JFrame;
import java.awt.BorderLayout;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import javax.sound.sampled.Clip;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.FloatControl;


public class SnakeFrame extends JFrame {
    

    
    public SnakeFrame() throws UnsupportedAudioFileException, IOException{
        initSnake();
    }
    
    private void initSnake() throws UnsupportedAudioFileException, IOException{
        
        setLayout(new BorderLayout());
        add(new ScoreBoard(), BorderLayout.NORTH);
        add(new SnakeBoard(), BorderLayout.CENTER);

        setResizable(false);
        pack();
        
        
        setTitle("Snake");
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                SnakeBoard.backgroundMusic.close();
            }
        });
        
        
        
    }


    
}
