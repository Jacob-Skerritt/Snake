/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package snake;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import javax.swing.BorderFactory;
import javax.swing.JPanel;

/**
 *
 * @author anyone
 */
public class ScoreBoard extends JPanel {
    private int num = -1;
    private Calendar cal;
    
    public ScoreBoard(){
        initScoreBoard();
    }
    
    private void initScoreBoard(){
        
        setBackground(Color.BLACK);
        setBorder(BorderFactory.createLineBorder(Color.white));
        setPreferredSize(new Dimension(700,65));
       
        
    }
    
        @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        doDrawing(g);
    }
    
    private void doDrawing(Graphics g) {
        String msg;
        cal = Calendar.getInstance();
        Font font = new Font("Verdana", Font.BOLD, 15);
        g.setFont(font);
        g.setColor(Color.white);
        
        msg = "Increase Volume: +";
        g.drawString(msg,0,15);
        
        msg="Decrease Volume: -";
        g.drawString(msg, 0,30);
        
        
        
        font = new Font("Verdana", Font.BOLD, 20);
        g.setFont(font);
        msg = "Score: "+ SnakeBoard.score;
        g.drawString(msg ,0,60); 
        msg = "Size: "+ SnakeBoard.size;
        g.drawString(msg,700-g.getFontMetrics().stringWidth(msg),60);
        
        font = new Font("Verdana", Font.BOLD, 40);
        g.setFont(font);
        msg = "Snake!";
        g.drawString(msg,(700 - g.getFontMetrics().stringWidth(msg))/2,60);
        
        
        font = new Font("Verdana", Font.BOLD, 15);
        g.setFont(font);
        DateFormat df = new SimpleDateFormat("HH:mm:ss");
        msg = (df.format(cal.getTime()));
        g.drawString(msg, (700 - g.getFontMetrics().stringWidth(msg)),15);
        
        
          
        repaint();
         
         
    }
    
}
