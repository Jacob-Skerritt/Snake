/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package snake;


import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import javax.swing.ImageIcon;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import javax.swing.JPanel;
import javax.swing.Timer;
import java.util.concurrent.ThreadLocalRandom;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.util.Calendar;
import javax.imageio.ImageIO;
import java.io.InputStream;

public class SnakeBoard extends JPanel implements ActionListener {
    
    public static int score;
    public static int size;
    
    
    private int pauseMenuOption;
    
    private final boolean mainMenu[] = new boolean []{false,false,false};
    public static int mainMenuOption;
    
    private boolean playGame;
    private int playMenuOption;
    

    private final int B_WIDTH = 700;
    private final int B_HEIGHT = 700;
    private final int DOT_SIZE = 10;
    private final int ALL_DOTS = 700;
    private final int RAND_POS = 69;
    private final int[] delayOptions= new int[]{120,90,60,30};
    private  int delay = 150;
    private String nickname;

    private final int x[] = new int[ALL_DOTS];
    private final int y[] = new int[ALL_DOTS];
    



    private int dots;
    private int apple_x;
    private int apple_y;
    private int bad_apple_x;
    private int bad_apple_y;
    private int doubleSize;
    private int outputBadApple;
    private  FloatControl gainControl;
    private Calendar cal;
    private Calendar badAppleTimer;
    
    private Clip appleHit;
    private Clip gameOver;
    public static Clip backgroundMusic;
    
    private boolean paused;
   
    
    
    private boolean leftDirection = false;
    private boolean rightDirection = true;
    private boolean upDirection = false;
    private boolean downDirection = false;
    private boolean inGame = true;

    private Timer timer;
    private Image ball;
    private Image apple;
    private Image badApple;
    private Image head;

    public SnakeBoard() throws UnsupportedAudioFileException, IOException {
        
        initBoard();
    }
    
    private void initBoard() throws UnsupportedAudioFileException, IOException {
        
        
        addKeyListener(new TAdapter());
        setBackground(Color.black);
        setFocusable(true);

        setPreferredSize(new Dimension(B_WIDTH, B_HEIGHT));
        loadImages();
        loadMusic();
        initGame();
    }
    
    


    private void loadImages() throws IOException {
        
        
        String pathToImageSortBy = "/images/snake/dot.png";
        BufferedImage img = ImageIO.read(getClass().getResourceAsStream(pathToImageSortBy));
        ImageIcon iid = new ImageIcon(img);
        ball = iid.getImage();
        
        pathToImageSortBy = "/images/snake/apple_edit_1.png";
        img = ImageIO.read(getClass().getResourceAsStream(pathToImageSortBy));
        ImageIcon iia = new ImageIcon(img);
        apple = iia.getImage();
        
        pathToImageSortBy = "/images/snake/head.png";
        img = ImageIO.read(getClass().getResourceAsStream(pathToImageSortBy));
        ImageIcon iih = new ImageIcon(img);
        head = iih.getImage();
        
        pathToImageSortBy = "/images/snake/bad_apple_edit_1.png";
        img = ImageIO.read(getClass().getResourceAsStream(pathToImageSortBy));
        ImageIcon iiba = new ImageIcon(img);
        badApple = iiba.getImage();
    }
    
    private void loadMusic() throws UnsupportedAudioFileException, IOException{
        
        try{

            InputStream in = getClass().getResourceAsStream("/sounds/apple_hit.wav");
            InputStream bufferedIn = new BufferedInputStream(in);
            AudioInputStream ais = AudioSystem.getAudioInputStream(bufferedIn);
            appleHit  = AudioSystem.getClip();
            appleHit.open(ais);
            gainControl = (FloatControl) appleHit.getControl(FloatControl.Type.MASTER_GAIN);
            gainControl.setValue(-10.0f);
            
            in = getClass().getResourceAsStream("/sounds/game_over.wav");
            bufferedIn = new BufferedInputStream(in);
            ais = AudioSystem.getAudioInputStream(bufferedIn);
            gameOver = AudioSystem.getClip();
            gameOver.open(ais);
            
            in = getClass().getResourceAsStream("/sounds/the_resistors.wav");
            bufferedIn = new BufferedInputStream(in);
            ais = AudioSystem.getAudioInputStream(bufferedIn);
            backgroundMusic = AudioSystem.getClip();
            backgroundMusic.open(ais);
            gainControl = (FloatControl) backgroundMusic.getControl(FloatControl.Type.MASTER_GAIN);
            gainControl.setValue(-20.0f);
            
            
            
             

        }catch(Exception e){
        }
    }

    private void initGame() {
        dots = 3;
        size = dots;
        score = 0;
        doubleSize = size*2;
        outputBadApple = ThreadLocalRandom.current().nextInt(score + score, score + score + 20);
        backgroundMusic.loop(Clip.LOOP_CONTINUOUSLY);
        badAppleTimer = Calendar.getInstance();
        cal = Calendar.getInstance();
        paused = false;
        pauseMenuOption = 0;
        
        

        for (int z = 0; z < dots; z++) {
            x[z] = 50 - z * 10;
            y[z] = 50;
        }
        
        locateApple();
        

        timer = new Timer(delay, this);
        timer.start();
        

        
        
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        doDrawing(g);
    }
    
    private void doDrawing(Graphics g) {
         
        
        if(mainMenu[0] == true){
            
            if(playGame == true){
                
                
                if (inGame) {


                    g.drawImage(apple, apple_x, apple_y, this);

                    if(badAppleTimer.compareTo(cal) > 0 ){
                        g.drawImage(badApple, bad_apple_x, bad_apple_y, this);
                    }

                     if(outputBadApple == score)
                    {

                        badAppleTimer = Calendar.getInstance();
                        badAppleTimer.add(Calendar.SECOND,20);
                        outputBadApple = ThreadLocalRandom.current().nextInt(score + score, score + score + 20);
                    }

                    for (int z = 0; z < dots; z++) {
                        if (z == 0) {
                            g.drawImage(head, x[z], y[z], this);
                        } else {
                            g.drawImage(ball, x[z], y[z], this);
                        }
                    }

                    if(paused){

                        pauseMenu(g);

                    }
                    cal = Calendar.getInstance();

                    Toolkit.getDefaultToolkit().sync();

                }else {

                    gameOver(g);
                }
            }else
                playMenu(g);
        }else if(mainMenu[1] == true){
            
        }else if(mainMenu[2] == true){
            
        }else
            mainMenu(g);
    }
    
    private void pauseMenu(Graphics g){
        String msg = "Game Paused";
        Font small = new Font("Helvetica", Font.BOLD, 22);
        FontMetrics metr = getFontMetrics(small);

        g.setColor(Color.white);
        g.setFont(small);
        g.drawString(msg, (B_WIDTH - metr.stringWidth(msg)) / 2, B_HEIGHT / 6);
        
        
        msg = "_____________";
        g.drawString(msg,(B_WIDTH - metr.stringWidth(msg)) / 2,( B_HEIGHT / 6));
        
        small = new Font("Helvetica", Font.BOLD, 18);
        g.setFont(small);
        metr = getFontMetrics(small);
        
        if(mod(pauseMenuOption,3) == 0){
            msg = "> Resume Game <";
            g.drawString(msg, (B_WIDTH - metr.stringWidth(msg)) /2, (B_HEIGHT/2));
        
        }
        else{
            msg = "Resume Game";
            g.drawString(msg, (B_WIDTH - metr.stringWidth(msg)) /2, (B_HEIGHT/2));
        }
        
        if(mod(pauseMenuOption,3) == 1){
            msg = "> Restart <";
            g.drawString(msg, (B_WIDTH - metr.stringWidth(msg)) /2, (B_HEIGHT/2) + 25);
        
        }
        else{
            msg = "Restart";
            g.drawString(msg, (B_WIDTH - metr.stringWidth(msg)) /2, (B_HEIGHT/2) + 25);
        }
        
        if(mod(pauseMenuOption,3) == 2){
            msg = "> Exit to Main Menu <";
            g.drawString(msg, (B_WIDTH - metr.stringWidth(msg)) /2, (B_HEIGHT/2) + 50);
        
        }
        else{
           msg = "Exit to Main Menu";
            g.drawString(msg, (B_WIDTH - metr.stringWidth(msg)) /2, (B_HEIGHT/2) + 50);
        }
 

    }
    
    private void mainMenu(Graphics g){
        String msg;
        Font small = new Font("Helvetica", Font.BOLD, 36);
        FontMetrics metr = getFontMetrics(small);

        g.setColor(Color.white);
        g.setFont(small);
        
        if(mod(mainMenuOption,4) == 0){
            msg = "> Play < ";
            g.drawString(msg, (B_WIDTH - metr.stringWidth(msg))/2, (B_HEIGHT/2) - B_HEIGHT /10);
        }else
        {
            msg = "Play";
            g.drawString(msg, (B_WIDTH - metr.stringWidth(msg))/2, (B_HEIGHT/2) - B_HEIGHT/10);
        }
        
        if(mod(mainMenuOption,4) == 1 ){
            msg = "> Enter Nickname < ";
            g.drawString(msg, (B_WIDTH - metr.stringWidth(msg))/2, (B_HEIGHT/2) - B_HEIGHT/33);
        }else
        {
            msg = "Enter Nickname";
            g.drawString(msg, (B_WIDTH - metr.stringWidth(msg))/2, (B_HEIGHT/2) - B_HEIGHT/33);
        }
        
        if(mod(mainMenuOption,4) == 2){
            msg = "> HighScores < ";
            g.drawString(msg, (B_WIDTH - metr.stringWidth(msg))/2, (B_HEIGHT/2) + B_HEIGHT/33);
        }else
        {
            msg = "HighScores";
            g.drawString(msg, (B_WIDTH - metr.stringWidth(msg))/2, (B_HEIGHT/2) + B_HEIGHT/33);
        }
        
        if(mod(mainMenuOption,4) == 3){
            msg = "> Exit Game < ";
            g.drawString(msg, (B_WIDTH - metr.stringWidth(msg))/2, (B_HEIGHT/2) + B_HEIGHT/10);
        }else
        {
            msg = "Exit Game";
            g.drawString(msg, (B_WIDTH - metr.stringWidth(msg))/2, (B_HEIGHT/2)  + B_HEIGHT/10);
        }
        
        
    }
    
    private void playMenu(Graphics g){
        
        String msg;
        Font small = new Font("Helvetica", Font.BOLD, 26);
        FontMetrics metr = getFontMetrics(small);

        g.setColor(Color.white);
        g.setFont(small);
        
        if(mod(playMenuOption,5) == 0){
            msg = "> Easy (Muliplyer x1) < ";
            g.drawString(msg, (B_WIDTH - metr.stringWidth(msg))/2, (B_HEIGHT/2) - B_HEIGHT/5);
        }else
        {
            msg = "Easy";
            g.drawString(msg, (B_WIDTH - metr.stringWidth(msg))/2, (B_HEIGHT/2) - B_HEIGHT/5);
        }
        
        if(mod(playMenuOption,5) == 1){
            msg = "> Normal (Muliplyer x1.5) < ";
            g.drawString(msg, (B_WIDTH - metr.stringWidth(msg))/2, (B_HEIGHT/2) - B_HEIGHT/10);
        }else
        {
            msg = "Normal";
            g.drawString(msg, (B_WIDTH - metr.stringWidth(msg))/2, (B_HEIGHT/2) - B_HEIGHT/10);
        }
        
        
        if(mod(playMenuOption,5) == 2){
            msg = "> Hard (Muliplyer x2) < ";
            g.drawString(msg, (B_WIDTH - metr.stringWidth(msg))/2, (B_HEIGHT/2));
        }else
        {
            msg = "Hard";
            g.drawString(msg, (B_WIDTH - metr.stringWidth(msg))/2, (B_HEIGHT/2));
        }
        
        
        if(mod(playMenuOption,5) == 3){
            msg = "> Why?? (Muliplyer x3) < ";
            g.drawString(msg, (B_WIDTH - metr.stringWidth(msg))/2, (B_HEIGHT/2) + B_HEIGHT/10);
        }else
        {
            msg = "Why??";
            g.drawString(msg, (B_WIDTH - metr.stringWidth(msg))/2, (B_HEIGHT/2) + B_HEIGHT/10);
        }
        if(mod(playMenuOption,5) == 4){
            msg = "> Main Menu < ";
            g.drawString(msg, (B_WIDTH - metr.stringWidth(msg))/2, (B_HEIGHT/2) + B_HEIGHT/5);
        }else
        {
            msg = "Main Menu";
            g.drawString(msg, (B_WIDTH - metr.stringWidth(msg))/2, (B_HEIGHT/2) + B_HEIGHT/5);
        }
        
    }

    private void gameOver(Graphics g) {
        
        gameOver.setFramePosition(0);
        gameOver.loop(0);
        gameOver.start();
        
        Font small = new Font("Helvetica", Font.BOLD, 20);
        FontMetrics metr = getFontMetrics(small);
       
        g.setColor(Color.white);
        g.setFont(small);
        
        String msg = "Game Over";
        g.drawString(msg, (B_WIDTH - metr.stringWidth(msg)) / 2, B_HEIGHT / 2);
        
        msg = "Final Score : " + score;
        g.drawString(msg, (B_WIDTH - metr.stringWidth(msg)) / 2,( B_HEIGHT / 2) + small.getSize());
        
        msg = "Final Size: " + size;
        g.drawString(msg,(B_WIDTH - metr.stringWidth(msg))/2, (B_HEIGHT/2)  + small.getSize()*2);
    }

    private void checkApple() {

        if ((x[0] == apple_x) && (y[0] == apple_y)) {
            
            appleHit.setFramePosition(0);  // Must always rewind!
            appleHit.loop(0);
            appleHit.start();
                
            
            dots++;
            size++;
            score++;
            locateApple();
            
            if(size == doubleSize){
                backgroundColor();
                doubleSize = size*2;
            }
            
            if(score == outputBadApple){
                locateBadApple();
                
                
            }
            
        }
        
        if((x[0] == bad_apple_x) && (y[0] == bad_apple_y)){
            appleHit.setFramePosition(0);  // Must always rewind!
            appleHit.loop(0);
            appleHit.start();
            dots/=2;
            size/=2;
            bad_apple_x = -10;
            bad_apple_y = -10;
            badAppleTimer = Calendar.getInstance();
            badAppleTimer.add(Calendar.SECOND, -1);
            
        }
        
    }
    
    public void backgroundColor(){
        
        
        switch(ThreadLocalRandom.current().nextInt(1,3)){
            case 1:
                setBackground(Color.gray);
                break;
            case 2:
                setBackground(Color.black);
                break;
            case 3:
                setBackground(Color.blue);
                break;
            default:
                System.out.println("Invalid Case!");
        }
    }

    private void move() {

        for (int z = dots; z > 0; z--) {
            x[z] = x[(z - 1)];
            y[z] = y[(z - 1)];
        }

        if (leftDirection) {
            x[0] -= DOT_SIZE;
        }

        if (rightDirection) {
            x[0] += DOT_SIZE;
        }

        if (upDirection) {
            y[0] -= DOT_SIZE;
        }

        if (downDirection) {
            y[0] += DOT_SIZE;
        }
    }

    private void checkCollision() {

        for (int z = dots; z > 0; z--) {

            if ((z > 4) && (x[0] == x[z]) && (y[0] == y[z])) {
                inGame = false;
            }
        }

        if (y[0] > B_HEIGHT) {
            y[0] = 0;
        }

        if (y[0] < 0) {
            y[0] = B_HEIGHT;
        }

        if (x[0] > B_WIDTH) {
            x[0] = 0;
        }

        if (x[0] < 0) {
            x[0] = B_WIDTH;
        }
        
        if (!inGame) {
            timer.stop();
        }
    }


    private void locateApple() {
        
        
         int r= (int) (Math.random() * RAND_POS);
        apple_x = (r * DOT_SIZE);
        
        if(x[apple_x] == apple_x )
            locateApple();
        
        
        r = (int) (Math.random() * RAND_POS);
        apple_y = (r * DOT_SIZE);
       
        if( y[apple_y] == apple_y)
            locateApple();
        
    }
    
    private void locateBadApple(){
        
        int r = (int) (Math.random() *RAND_POS);
        bad_apple_x = (r*DOT_SIZE);
        
        if(bad_apple_x == x[bad_apple_x] || bad_apple_x == apple_x)
            locateBadApple();
        
        r = (int) (Math.random() * RAND_POS);
        bad_apple_y = (r*DOT_SIZE);
        
        if(bad_apple_y == y[bad_apple_y] || bad_apple_y == apple_y)
            locateBadApple();
    }
    


    @Override
    public void actionPerformed(ActionEvent e) {
        
        if(mainMenu[0] == true){
            
            if(playGame == true)
            {
                if (inGame) {
                    
                

                    if(!paused){
                    checkCollision();
                    checkApple();

                    move();
                    }
                }
            }
        }

        repaint();
    }

    private class TAdapter extends KeyAdapter {

        @Override
        public void keyPressed(KeyEvent e) {
            
            int key = e.getKeyCode();
            
            if(mainMenu[0] == false && mainMenu[0] == false && mainMenu[0] == false){
                
                if(key == KeyEvent.VK_ENTER){
                    
                    if(mod(mainMenuOption,4) ==3)
                        System.exit(0);          
                    else
                       mainMenu[mod(mainMenuOption,4)] = true; 
                }
                
                if ((key == KeyEvent.VK_UP || key == KeyEvent.VK_W) ) {
                            mainMenuOption--;
                           
                        }

                if ((key == KeyEvent.VK_DOWN || key == KeyEvent.VK_S) ) {
                    mainMenuOption++;
                }
                
                
            }else if(mainMenu[0] == true){
                    
                
               
                
                if(playGame == true){
                    if(!paused){

                        if ((key == KeyEvent.VK_LEFT || key == KeyEvent.VK_A) && (!rightDirection)) {

                            leftDirection = true;
                            upDirection = false;
                            downDirection = false;
                        }

                        if ((key == KeyEvent.VK_RIGHT || key == KeyEvent.VK_D) && (!leftDirection)) {
                            rightDirection = true;
                            upDirection = false;
                            downDirection = false;
                        }

                        if ((key == KeyEvent.VK_UP || key == KeyEvent.VK_W) && (!downDirection)) {
                            upDirection = true;
                            rightDirection = false;
                            leftDirection = false;
                        }

                        if ((key == KeyEvent.VK_DOWN || key == KeyEvent.VK_S) && (!upDirection)) {
                            downDirection = true;
                            rightDirection = false;
                            leftDirection = false;
                        }

                        if (key == KeyEvent.VK_ESCAPE) {
                            paused = true;
                        }

                    }else if(paused){


                        if ((key == KeyEvent.VK_UP || key == KeyEvent.VK_W) ) {
                            pauseMenuOption--;
                        }

                        if ((key == KeyEvent.VK_DOWN || key == KeyEvent.VK_S) ) {
                            pauseMenuOption++;
                        }

                        if (key == KeyEvent.VK_ESCAPE) {
                            paused = false;
                        }

                        if(key == KeyEvent.VK_ENTER){

                            if(mod(pauseMenuOption,3) == 0)
                                paused = false;

                            if(mod(pauseMenuOption,3) == 1){
                                timer.stop();
                                initGame();
                            }

                            if(mod(pauseMenuOption,3) == 2){
                                mainMenu[0] = false;
                                playGame = false;
                                timer.setDelay(150);
                            }

                        }

                    }
                }
                else
                {
                    if(key == KeyEvent.VK_ENTER){
                    
                    if(mod(playMenuOption,5) == 4){
                        mainMenu[0] = false;
                        playMenuOption =0;
                    }else{
                        timer.setDelay(delayOptions[playMenuOption]);
                        
                        playGame = true;
                    }
                        
                    
                }
                
                if ((key == KeyEvent.VK_UP || key == KeyEvent.VK_W) ) {
                    playMenuOption--;            
                }

                if ((key == KeyEvent.VK_DOWN || key == KeyEvent.VK_S) ) {
                    playMenuOption++;
                } 
                }
                }
            
            if(key == KeyEvent.VK_MINUS){
                 
                 gainControl = (FloatControl) gameOver.getControl(FloatControl.Type.MASTER_GAIN);
                 if(gainControl.getValue() > -80.0)
                    gainControl.setValue(gainControl.getValue() - 10.0f);
                 
                 gainControl = (FloatControl) appleHit.getControl(FloatControl.Type.MASTER_GAIN);
                 if(gainControl.getValue() > -80.0)
                    gainControl.setValue(gainControl.getValue() - 10.0f);
                 
                 gainControl = (FloatControl) backgroundMusic.getControl(FloatControl.Type.MASTER_GAIN);
                 if(gainControl.getValue() > -80.0)
                    gainControl.setValue(gainControl.getValue() - 10.0f);
             }
             
             if(key == KeyEvent.VK_EQUALS){
                 
                 
                 gainControl = (FloatControl) gameOver.getControl(FloatControl.Type.MASTER_GAIN);
                 if(gainControl.getValue() < 0)
                    gainControl.setValue(gainControl.getValue() + 10.0f);
                 
                 gainControl = (FloatControl) appleHit.getControl(FloatControl.Type.MASTER_GAIN);
                 if(gainControl.getValue() < 0)
                    gainControl.setValue(gainControl.getValue() + 10.0f);
                 
                 gainControl = (FloatControl) backgroundMusic.getControl(FloatControl.Type.MASTER_GAIN);
                 if(gainControl.getValue() < 0)
                    gainControl.setValue(gainControl.getValue() + 10.0f);
                 
                 
             }
            

            
            
        }
    }
    
    public int mod(int num, int mod){
        int ans = num%mod;
        
        
        while(ans < 0){
            
            ans+= mod;
            
        }
        
        
        return ans;
    }
    
    
}