package snake;


import java.awt.BasicStroke;
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
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.sampled.LineUnavailableException;
import javax.swing.JLabel;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class SnakeBoard extends JPanel implements ActionListener {
    
    private int score;
    private int size;
    
    
    private int pauseMenuOption;
    private int menu;
    
    private final boolean mainMenu[] = new boolean []{false,false,false};
    public static int mainMenuOption;
    
    private boolean playGame;
    private boolean updateTopScore = true;
    private int playMenuOption;
    
    private final int SCOREBOARD_HEIGHT = 60;
    private final int B_WIDTH = 700;
    private final int B_HEIGHT = 700 + SCOREBOARD_HEIGHT;
    
    private final int DOT_SIZE = 10;
    private final int ALL_DOTS = 4900;
    private final int RAND_POS = 69;
    private final int[] delayOptions= new int[]{120,90,60,30};
    private  int delay = 150;
    private StringBuilder nickname;

    private final int x[] = new int[ALL_DOTS];
    private final int y[] = new int[ALL_DOTS];
    
    private int showCursor;
    
    private List<JSONObject> topTen;
    



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
    private final File SNAKEDATAFILE= new File("src/snake/snakeData.txt");
    private JSONObject snakeData;
    
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

    public SnakeBoard() throws UnsupportedAudioFileException, IOException, ParseException {
        
        initBoard();
    }
    
    private void initBoard() throws UnsupportedAudioFileException, IOException, ParseException {
        
        
        addKeyListener(new TAdapter());
        setBackground(Color.black);
        setFocusable(true);
        

        setPreferredSize(new Dimension(B_WIDTH, B_HEIGHT));
        loadImages();
        loadMusic();
        initFiles();
        initGame();
        
        

        
    }
    
    

    private void initFiles() throws IOException, ParseException{
        
        
        File f = new File("src/snake/snakeData.txt");
    
        if(!f.exists())
        {
            
           
            SNAKEDATAFILE.createNewFile();
            JSONObject snakeData = new JSONObject();
            JSONObject topTenUser = new JSONObject();
            snakeData.put("nickname", "N/A");
                
            JSONArray array = new JSONArray();
            
            
            for(int i =1; i <= 10; i++){
                
                topTenUser.put("N/A" , 0);
                array.put(topTenUser);
                topTenUser = new JSONObject();
            }
            snakeData.put("topTenUser", array);
            
            
            
            try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(SNAKEDATAFILE.getAbsolutePath()))) {
                snakeData.write(writer);
                writer.write("\n");
            } catch (Exception e) {
            }
            
            
        }
        else
        {
            nickname = new StringBuilder();
            
            try {
                BufferedReader br = new BufferedReader(new FileReader(SNAKEDATAFILE));
                StringBuilder sb = new StringBuilder();
                String line = br.readLine();
            
                while (line != null) {
                    sb.append(line);
                    line = br.readLine();
                }
            
                snakeData = new JSONObject(sb.toString());
                nickname.append(snakeData.get("nickname"));
           } catch(IOException | JSONException e) {
           e.printStackTrace();
            }
            
            JSONArray tempArray = (JSONArray) snakeData.get("topTenUser");
            topTen = new ArrayList<>();
            for (int i = 0; i < tempArray.length(); i++)
            topTen.add(tempArray.getJSONObject(i));
         
            
            


           
            
        }
        
        sortTopTen();
       
        
         for(int i = 0; i < topTen.size();i++){
                
                System.out.println(topTen.get(i).toString());
                
            }   
    }
    
    private void sortTopTen(){
         Collections.sort(topTen, new Comparator<JSONObject>() {
                
                @Override
                public int compare(JSONObject jsonObjectA, JSONObject jsonObjectB) {
                    int compare = 0;
                    try
                    {   

                        Iterator keys = jsonObjectA.keys();
                        Iterator keys2 = jsonObjectB.keys();
                        int keyA = jsonObjectA.getInt((String) keys.next());
                        int keyB = jsonObjectB.getInt((String) keys2.next());
                        compare = Integer.compare(keyA, keyB);
                    }
                    catch(JSONException e)
                    {
                        e.printStackTrace();
                    }
                    return compare;
                }
            });
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
            
            
            
             

        }catch(IOException | LineUnavailableException | UnsupportedAudioFileException e){
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
        menu = 0;
        showCursor = 5;
        
        

        for (int z = 0; z < dots; z++) {
            x[z] = 50 - z * 10;
            y[z] = 70;
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
                    
                    drawScoreboard(g);
                    cal = Calendar.getInstance();

                    Toolkit.getDefaultToolkit().sync();

                }else {
                    gameOverMenu(g);
                    //gameOver(g);
                }
            }else
                playMenu(g);
        }else if(mainMenu[1] == true){
            
            drawNicknameGUI(g);
            
        }else if(mainMenu[2] == true){
            
            drawHighScores(g);
            
        }else
            mainMenu(g);
    }
    
    private void pauseMenu(Graphics g){
        
        Font small = new Font("Helvetica", Font.BOLD, 44);
        FontMetrics metr = getFontMetrics(small);

        g.setColor(Color.white);
        g.setFont(small);
        
        g.drawLine(B_WIDTH/4,B_HEIGHT/3, B_WIDTH/4, B_HEIGHT - B_HEIGHT/3);
        g.drawLine(B_WIDTH - B_WIDTH/4,B_HEIGHT/3, B_WIDTH - B_WIDTH/4, B_HEIGHT - B_HEIGHT/3);
        
        g.drawLine(B_WIDTH/4,B_HEIGHT/3, B_WIDTH - B_WIDTH/4,B_HEIGHT/3);
        g.drawLine(B_WIDTH - B_WIDTH/4, B_HEIGHT - B_HEIGHT/3, B_WIDTH/4, B_HEIGHT - B_HEIGHT/3);
        
        
        small = new Font("Helvetica", Font.BOLD, 40);
        metr = getFontMetrics(small);

        
        g.setFont(small);
        
        String msg = "Game Paused";
        g.drawString(msg, (B_WIDTH - metr.stringWidth(msg)) / 2, B_HEIGHT / 6);
        
        
        
        small = new Font("Helvetica", Font.BOLD, 30);
        g.setFont(small);
        metr = getFontMetrics(small);
        
        if(mod(menu,3) == 0){
            msg = "> Resume Game <";
            g.drawString(msg, (B_WIDTH - metr.stringWidth(msg)) /2, (B_HEIGHT/2)- B_HEIGHT/12);
        
        }
        else{
            msg = "Resume Game";
            g.drawString(msg, (B_WIDTH - metr.stringWidth(msg)) /2, (B_HEIGHT/2)- B_HEIGHT/12);
        }
        
        if(mod(menu,3) == 1){
            msg = "> Restart <";
            g.drawString(msg, (B_WIDTH - metr.stringWidth(msg)) /2, (B_HEIGHT/2));
        
        }
        else{
            msg = "Restart";
            g.drawString(msg, (B_WIDTH - metr.stringWidth(msg)) /2, (B_HEIGHT/2));
        }
        
        if(mod(menu,3) == 2){
            msg = "> Exit to Main Menu <";
            g.drawString(msg, (B_WIDTH - metr.stringWidth(msg)) /2, (B_HEIGHT/2)+ B_HEIGHT/12);
        
        }
        else{
           msg = "Exit to Main Menu";
            g.drawString(msg, (B_WIDTH - metr.stringWidth(msg)) /2, (B_HEIGHT/2 + B_HEIGHT/12));
        }
 

    }
    
    private void mainMenu(Graphics g){
        String msg;
        g.setColor(Color.white);
        Font small = new Font("Helvetica", Font.BOLD, 44);
        FontMetrics metr = getFontMetrics(small);
        g.setFont(small);
        msg = "Snake!";
        g.drawString(msg,(B_WIDTH - metr.stringWidth(msg))/2, B_HEIGHT/8);
        
        
        
        g.drawLine(B_WIDTH/4,B_HEIGHT/4, B_WIDTH/4, B_HEIGHT - B_HEIGHT/4);
        g.drawLine(B_WIDTH - B_WIDTH/4,B_HEIGHT/4, B_WIDTH - B_WIDTH/4, B_HEIGHT - B_HEIGHT/4);
        
        g.drawLine(B_WIDTH/4,B_HEIGHT/4, B_WIDTH - B_WIDTH/4,B_HEIGHT/4);
        g.drawLine(B_WIDTH - B_WIDTH/4, B_HEIGHT - B_HEIGHT/4, B_WIDTH/4, B_HEIGHT - B_HEIGHT/4);
        
        

        small = new Font("Helvetica", Font.BOLD, 34);
        
        g.setFont(small);
        
         metr = getFontMetrics(small);
        
        if(mod(menu,4) == 0){
            msg = "> Play <";
            g.drawString(msg, (B_WIDTH - metr.stringWidth(msg))/2, (B_HEIGHT/2) - B_HEIGHT /8);
        }else
        {
            msg = "Play";
            g.drawString(msg, (B_WIDTH - metr.stringWidth(msg))/2, (B_HEIGHT/2) - B_HEIGHT/8);
        }
        
        if(mod(menu,4) == 1 ){
            msg = "> Enter Nickname <";
            g.drawString(msg, (B_WIDTH - metr.stringWidth(msg))/2, (B_HEIGHT/2) - B_HEIGHT/24);
        }else
        {
            msg = "Enter Nickname";
            g.drawString(msg, (B_WIDTH - metr.stringWidth(msg))/2, (B_HEIGHT/2) - B_HEIGHT/24);
        }
        
        if(mod(menu,4) == 2){
            msg = "> HighScores <";
            g.drawString(msg, (B_WIDTH - metr.stringWidth(msg))/2, (B_HEIGHT/2) + B_HEIGHT/24);
        }else
        {
            msg = "HighScores";
            g.drawString(msg, (B_WIDTH - metr.stringWidth(msg))/2, (B_HEIGHT/2) + B_HEIGHT/24);
        }
        
        if(mod(menu,4) == 3){
            msg = "> Exit Game <";
            g.drawString(msg, (B_WIDTH - metr.stringWidth(msg))/2, (B_HEIGHT/2) + B_HEIGHT/8);
        }else
        {
            msg = "Exit Game";
            g.drawString(msg, (B_WIDTH - metr.stringWidth(msg))/2, (B_HEIGHT/2)  + B_HEIGHT/8);
        }
        
        
    }
    
    private void gameOverMenu(Graphics g){
        
        Font small = new Font("Helvetica", Font.BOLD, 44);
        FontMetrics metr;
        
        g.setColor(Color.white);
        g.setFont(small);

        
        g.drawLine(B_WIDTH/5,B_HEIGHT/4, B_WIDTH/5, B_HEIGHT/4 + B_HEIGHT/18);
        g.drawLine(B_WIDTH - B_WIDTH/5,B_HEIGHT/4,B_WIDTH - B_WIDTH/5, B_HEIGHT/4 + B_HEIGHT/18);
        
        g.drawLine(B_WIDTH/5,B_HEIGHT/4, B_WIDTH - B_WIDTH/5,B_HEIGHT/4);
        g.drawLine(B_WIDTH/5, B_HEIGHT/4 + B_HEIGHT/18,  B_WIDTH - B_WIDTH/5, B_HEIGHT/4 + B_HEIGHT/18);
        
        
        
        
        g.drawLine(B_WIDTH/3,B_HEIGHT/3 + B_HEIGHT/15, B_WIDTH/3, B_HEIGHT - B_HEIGHT/3);
        g.drawLine(B_WIDTH - B_WIDTH/3,B_HEIGHT/3 + B_HEIGHT/15, B_WIDTH - B_WIDTH/3, B_HEIGHT - B_HEIGHT/3);
        
        g.drawLine(B_WIDTH/3,B_HEIGHT/3 + B_HEIGHT/15, B_WIDTH - B_WIDTH/3,B_HEIGHT/3 + B_HEIGHT/15);
        g.drawLine(B_WIDTH - B_WIDTH/3, B_HEIGHT - B_HEIGHT/3, B_WIDTH/3, B_HEIGHT - B_HEIGHT/3);
        
        
        small = new Font("Helvetica", Font.BOLD, 40);
        metr = getFontMetrics(small);

        
        g.setFont(small);
        
        String msg = "Game Over";
        g.drawString(msg, (B_WIDTH - metr.stringWidth(msg)) / 2, B_HEIGHT / 6);
        
        
        
        small = new Font("Helvetica", Font.BOLD, 30);
        g.setFont(small);
        metr = getFontMetrics(small);
        
        if(Integer.toString(score).length() < 5)
            msg = "Score: " + score;
        else
        {
            msg = "Score: ";
            String tempScore = Integer.toString(score);
            for(int i = 0; i < 5;i++)
            {
                msg += tempScore.charAt(i);
            }
            msg += "..";
        }
        g.drawString(msg , B_WIDTH/5 + 10, B_HEIGHT/4 + B_HEIGHT/18 - 10);
        
        
        
        if(Integer.toString(size).length() < 5)
            msg = "Size: " + size;
        else
        {
            msg = "Size: ";
            String tempSize = Integer.toString(size);
            
            for(int i = 0; i < 5;i++)
            {
                msg += tempSize.charAt(i);
            }
            
            msg += "..";
        }
        g.drawString(msg , B_WIDTH/2+ 20,B_HEIGHT/4 + B_HEIGHT/18 - 10);
        
        
        if(mod(menu,2) == 0){
            msg = "> New Game <";
            g.drawString(msg, (B_WIDTH - metr.stringWidth(msg)) /2, B_HEIGHT/2);
        
        }
        else{
            msg = "New Game";
            g.drawString(msg, (B_WIDTH - metr.stringWidth(msg)) /2, B_HEIGHT/2);
        }
        
        if(mod(menu,2) == 1){
            msg = "> Main Menu <";
            g.drawString(msg, (B_WIDTH - metr.stringWidth(msg)) /2, (B_HEIGHT/2) + B_HEIGHT/10);
        
        }
        else{
            msg = "Main Menu";
            g.drawString(msg, (B_WIDTH - metr.stringWidth(msg)) /2, (B_HEIGHT/2) + B_HEIGHT/10);
        }
        
        
        
        
    }
    
    private void playMenu(Graphics g){
        
        String msg;
        Font small = new Font("Helvetica", Font.BOLD, 44);
        FontMetrics metr = getFontMetrics(small);
        g.setColor(Color.white);
        g.setFont(small);
        
        msg = "Difficulty";
        g.drawString(msg,(B_WIDTH - metr.stringWidth(msg))/2, B_HEIGHT/8);

        
        
        g.drawLine(B_WIDTH/4,B_HEIGHT/4, B_WIDTH/4, B_HEIGHT - B_HEIGHT/4);
        g.drawLine(B_WIDTH - B_WIDTH/4,B_HEIGHT/4, B_WIDTH - B_WIDTH/4, B_HEIGHT - B_HEIGHT/4);
        
        g.drawLine(B_WIDTH/4,B_HEIGHT/4, B_WIDTH - B_WIDTH/4,B_HEIGHT/4);
        g.drawLine(B_WIDTH - B_WIDTH/4, B_HEIGHT - B_HEIGHT/4, B_WIDTH/4, B_HEIGHT - B_HEIGHT/4);
        
        small = new Font("Helvetica", Font.BOLD, 30);
        metr = getFontMetrics(small);
        g.setFont(small);
        
        if(mod(menu,5) == 0){
            msg = "> Easy (Score x1) <";
            g.drawString(msg, (B_WIDTH - metr.stringWidth(msg))/2, (B_HEIGHT/2) - B_HEIGHT/5);
        }else
        {
            msg = "Easy";
            g.drawString(msg, (B_WIDTH - metr.stringWidth(msg))/2, (B_HEIGHT/2) - B_HEIGHT/5);
        }
        
        if(mod(menu,5) == 1){
            msg = "> Normal (Score x1.5) < ";
            g.drawString(msg, (B_WIDTH - metr.stringWidth(msg))/2, (B_HEIGHT/2) - B_HEIGHT/10);
        }else
        {
            msg = "Normal";
            g.drawString(msg, (B_WIDTH - metr.stringWidth(msg))/2, (B_HEIGHT/2) - B_HEIGHT/10);
        }
        
        
        if(mod(menu,5) == 2){
            msg = "> Hard (Score x2) <";
            g.drawString(msg, (B_WIDTH - metr.stringWidth(msg))/2, (B_HEIGHT/2));
        }else
        {
            msg = "Hard";
            g.drawString(msg, (B_WIDTH - metr.stringWidth(msg))/2, (B_HEIGHT/2));
        }
        
        
        if(mod(menu,5) == 3){
            msg = "> Why?? (Score x3) <";
            g.drawString(msg, (B_WIDTH - metr.stringWidth(msg))/2, (B_HEIGHT/2) + B_HEIGHT/10);
        }else
        {
            msg = "Why??";
            g.drawString(msg, (B_WIDTH - metr.stringWidth(msg))/2, (B_HEIGHT/2) + B_HEIGHT/10);
        }
        if(mod(menu,5) == 4){
            msg = "> Main Menu <";
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
        
       gameOverMenu(g);
    }
    
    private void drawNicknameGUI(Graphics g){
        String line = "_______________________";
        String msg = "Enter Nickname";
        Font font = new Font("Helvetica", Font.BOLD, 40);
        FontMetrics metr = getFontMetrics(font);

        g.setColor(Color.white);
        g.setFont(font);
        g.drawString(msg, (B_WIDTH - metr.stringWidth(msg)) / 2, B_HEIGHT / 4);
        g.drawString(line,(B_WIDTH - metr.stringWidth(line)) / 2,( B_HEIGHT / 2));
        
        font = new Font("Helvetica", Font.BOLD, 32);
        metr = getFontMetrics(font);
        g.setFont(font);
        
        
        if(nickname.length() < line.length() -3)
        {
            g.drawString(nickname.toString(),(B_WIDTH - metr.stringWidth(line)) / 2,( B_HEIGHT / 2));
            if(showCursor == 5)
            {
                msg = "|";
                g.drawString(msg,((B_WIDTH - metr.stringWidth(line)) / 2) + metr.stringWidth(nickname.toString()),( B_HEIGHT / 2));

                showCursor = 0;
            }else
                showCursor++;
        }  
        else
        {
            
            StringBuilder longNickname = new StringBuilder("...");
            g.drawString(longNickname.toString(),(B_WIDTH / 2)- metr.stringWidth(longNickname.toString()),( B_HEIGHT / 2));
            for(int i = (Math.abs(line.length() -nickname.length())) ; i < nickname.length(); i++){
                longNickname.append(nickname.charAt(i));
                
            }
            g.drawString(longNickname.toString(),(B_WIDTH - metr.stringWidth(longNickname.toString())) / 2,( B_HEIGHT / 2));
            
            if(showCursor == 5)
            {
                msg = "|";
                g.drawString(msg,((B_WIDTH - metr.stringWidth(line)) / 2) + metr.stringWidth(longNickname.toString() + "..."),( B_HEIGHT / 2));

                showCursor = 0;
            }else
                showCursor++;
            
            
        }
        
        
        
        
        
         msg = "> Return to Main Menu <";
        g.drawString(msg,(B_WIDTH - metr.stringWidth(msg)) / 2, B_HEIGHT - B_HEIGHT/3);
        
        
    }

    private void drawScoreboard(Graphics g){
        String msg;
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, 700, SCOREBOARD_HEIGHT-1);
        g.setColor(Color.WHITE);
        g.drawLine(0,SCOREBOARD_HEIGHT-1,700,SCOREBOARD_HEIGHT-1);  
                 
        cal = Calendar.getInstance();
        Font font = new Font("Verdana", Font.BOLD, 15);
        g.setFont(font);
        g.setColor(Color.white);
        
        msg = "Increase Volume: +";
        g.drawString(msg,B_WIDTH-g.getFontMetrics().stringWidth(msg),SCOREBOARD_HEIGHT - 3 -  font.getSize());
        
        msg="Decrease Volume: -";
        g.drawString(msg,B_WIDTH-g.getFontMetrics().stringWidth(msg)-1,SCOREBOARD_HEIGHT-4);
        
        
        
        font = new Font("Verdana", Font.BOLD, 18);
        g.setFont(font);
        msg = "Score: "+ score;
        g.drawString(msg ,0,57); 
        msg = "Size: "+ size;
        g.drawString(msg,0,38);
        
        if(nickname.length() < 15)
            g.drawString("User: " + nickname.toString(), 0,font.getSize() );
        else{
            String tempNickname="";
            
            for(int i = 0; i < 13; i++){
                tempNickname += nickname.charAt(i);
            }
            tempNickname += "..";
            g.drawString("User: " + tempNickname, 0,font.getSize() );
            
        }
        
        font = new Font("Verdana", Font.BOLD, 40);
        g.setFont(font);
        msg = "Snake!";
        g.drawString(msg,(700 - g.getFontMetrics().stringWidth(msg))/2,45);
        
        
        font = new Font("Verdana", Font.BOLD, 15);
        g.setFont(font);
        DateFormat df = new SimpleDateFormat("HH:mm:ss");
        msg = (df.format(cal.getTime()));
        g.drawString(msg, (700 - g.getFontMetrics().stringWidth(msg)),15);
    }
    
    private void drawHighScores(Graphics g){
        String msg = "HighScores";
        int padding = 0;
        int nicknameScoreLine = (B_WIDTH/2 + B_WIDTH/10);
        Font font = new Font("Helvetica",Font.BOLD, 36);
        FontMetrics metr = getFontMetrics(font);
        g.setColor(Color.WHITE);
        g.setFont(font);
        
        g.drawString(msg, (B_WIDTH - metr.stringWidth(msg)) / 2, B_HEIGHT / 8);
        msg = "> Main Menu <";
        g.drawString(msg,(B_WIDTH - metr.stringWidth(msg))/2, B_HEIGHT - B_HEIGHT/12);
        
        
        font = new Font("Helvetica",Font.BOLD, 28);
        g.setFont(font);
        g.drawLine(B_WIDTH/7, B_HEIGHT/7, B_WIDTH - B_WIDTH/7,B_HEIGHT/7);
        g.drawLine(B_WIDTH/7, B_HEIGHT/7, B_WIDTH/7,B_HEIGHT - B_HEIGHT/7);
        g.drawLine(B_WIDTH - B_WIDTH/7,B_HEIGHT/7, B_WIDTH - B_WIDTH/7,B_HEIGHT - B_HEIGHT/7);
        g.drawLine(B_WIDTH/7 + metr.stringWidth("10"), B_HEIGHT/7, B_WIDTH/7+ metr.stringWidth("10"),B_HEIGHT - B_HEIGHT/7);
        g.drawLine(nicknameScoreLine,B_HEIGHT/7, nicknameScoreLine, B_HEIGHT - B_HEIGHT/7);
        
        for(int i = 0; i < 9; i++){
            
            padding +=(((B_HEIGHT - B_HEIGHT/7) -B_HEIGHT/7))/(10);
            
            g.drawString(i+1+"",B_WIDTH/7+10, (B_HEIGHT/7) + padding- 10 );
            
            if(topTen.get(i).names().getString(0).length() < 20)
                g.drawString(topTen.get(i).names().getString(0),B_WIDTH/7 + metr.stringWidth("10")+10, (B_HEIGHT/7) + padding- 10 );
            else
                g.drawString(shortString(topTen.get(i).names().getString(0), 18), B_WIDTH/7 + metr.stringWidth("10")+10, (B_HEIGHT/7) + padding- 10 );
            
            
            String scoreOutput = Integer.toString(topTen.get(i).getInt(topTen.get(i).names().getString(0)));
            g.drawString(scoreOutput,nicknameScoreLine + 10 , (B_HEIGHT/7) + padding- 10 );
            
            g.drawLine(B_WIDTH/7, (B_HEIGHT/7) + padding, B_WIDTH - B_WIDTH/7,(B_HEIGHT/7) + padding);
        }
        
        
        g.drawString(Integer.toString(10),B_WIDTH/7, (B_HEIGHT/7) + padding + (((B_HEIGHT - B_HEIGHT/7) -B_HEIGHT/7))/(10)- 10 );
        
        
        if(topTen.get(topTen.size()-1).names().getString(0).length() < 20)
              g.drawString(topTen.get(9).names().getString(0),B_WIDTH/7 + metr.stringWidth("10")+10, (B_HEIGHT/7) + padding +(((B_HEIGHT - B_HEIGHT/7) -B_HEIGHT/7))/(10) - 10 );
        else
             g.drawString(shortString(topTen.get(9).names().getString(0),18),B_WIDTH/7 + metr.stringWidth("10")+10, (B_HEIGHT/7) + padding +(((B_HEIGHT - B_HEIGHT/7) -B_HEIGHT/7))/(10) - 10 );
        
        String scoreOutput = Integer.toString(topTen.get(9).getInt(topTen.get(9).names().getString(0)));
            g.drawString(scoreOutput,nicknameScoreLine + 10 , (B_HEIGHT/7) + padding +(((B_HEIGHT - B_HEIGHT/7) -B_HEIGHT/7))/(10) - 10 );
        
        g.drawLine(B_WIDTH/7, B_HEIGHT -  B_HEIGHT/7, B_WIDTH - B_WIDTH/7,B_HEIGHT -B_HEIGHT/7);
        
        
        
        
    }
    
    private String shortString(String line, int length){
        
        StringBuilder newLine  = new StringBuilder();
        for(int i=0; i < length;i++)
        {
            newLine.append((line.charAt(i)));
        }
        
        newLine.append("..");
        
        
        return newLine.toString();
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

        if (y[0] >= B_HEIGHT) {
            y[0] = SCOREBOARD_HEIGHT;
        }

        if (y[0] < 60) {
            y[0] = B_HEIGHT;
        }

        if (x[0] > B_WIDTH) {
            x[0] = 0;
        }

        if (x[0] < 0) {
            x[0] = B_WIDTH;
        }
        
        
    }


    private void locateApple() {
        
        
         int r= (int) (Math.random() * RAND_POS);
        apple_x = (r * DOT_SIZE);
        
        if(x[apple_x] == apple_x )
            locateApple();
        
        
        r = (int) (Math.random() * RAND_POS ) + (SCOREBOARD_HEIGHT/DOT_SIZE) ;
        apple_y = (r * DOT_SIZE);
        if( y[apple_y] == apple_y)
            locateApple();

        
    }
    
    private void locateBadApple(){
        
        int r = (int) (Math.random() *RAND_POS);
        bad_apple_x = (r*DOT_SIZE);
        
        if(bad_apple_x == x[bad_apple_x] || bad_apple_x == apple_x)
            locateBadApple();
        
        r = (int) (Math.random() * RAND_POS ) + (SCOREBOARD_HEIGHT/DOT_SIZE);
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
                else{
                    
                    
                    if(updateTopScore == true)
                    {
                        System.out.println("hi hi");
                        updateTopScore = false;
                    }
                }
            }
        }
        

        repaint();
    }

    private class TAdapter extends KeyAdapter  {

        @Override
        public void keyPressed(KeyEvent e) {
            
            int key = e.getKeyCode();
            
            if(mainMenu[0] == false && mainMenu[1] == false && mainMenu[2] == false){
                
                if(key == KeyEvent.VK_ENTER){
                    
                    if(mod(menu,4) ==3)
                        System.exit(0);          
                    else
                    {
                       mainMenu[mod(menu,4)] = true; 
                       menu = 0;
                    }
                }
                
                if ((key == KeyEvent.VK_UP || key == KeyEvent.VK_W) ) {
                            menu--;
                           
                        }

                if ((key == KeyEvent.VK_DOWN || key == KeyEvent.VK_S) ) {
                    menu++;
                }
                
                
            }else if(mainMenu[0] == true){
                    
                
               
                
                if(playGame == true){
                    
                    if(inGame == false){
                        
                        
                        if ((key == KeyEvent.VK_UP || key == KeyEvent.VK_W) ) {
                            
                            menu--;
                        }

                        if ((key == KeyEvent.VK_DOWN || key == KeyEvent.VK_S) ) {
                            menu++;
                        }
                        
                        if(key == KeyEvent.VK_ENTER){
                            

                           

                            if(mod(menu,2) == 0){
                                timer.stop();
                                initGame();
                                menu = 0;
                                inGame = true;
                                x[0] = 20;
                                y[0] = 70;
                                
                            }

                            if(mod(menu,2) == 1){
                                mainMenu[0] = false;
                                playGame = false;
                                timer.stop();
                                initGame();
                                menu = 0;
                                paused = false;
                                inGame = true;
                                x[0] = 20;
                                y[0] = 70;
                                
                                
                                
                            }

                        }
                    }
                    else if(!paused){
                      
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
                            menu--;
                        }

                        if ((key == KeyEvent.VK_DOWN || key == KeyEvent.VK_S) ) {
                            menu++;
                        }

                        if (key == KeyEvent.VK_ESCAPE) {
                            paused = false;
                        }

                        if(key == KeyEvent.VK_ENTER){

                            if(mod(menu,3) == 0)
                                paused = false;

                            if(mod(menu,3) == 1){
                                timer.stop();
                                initGame();
                                menu = 0;
                            }

                            if(mod(menu,3) == 2){
                                timer.stop();
                                initGame();
                                mainMenu[0] = false;
                                playGame = false;
                                timer.setDelay(150);
                                menu = 0;
                                paused = false;
                                x[0] = 20;
                                y[0] = 70;
                            }

                        }

                    }
                }
                else
                {
                    if(key == KeyEvent.VK_ENTER){
                    
                        if(mod(menu,5) == 4){
                            mainMenu[0] = false;
                            
                        }else{
                            delay = delayOptions[menu];
                            timer.setDelay(delay);

                            playGame = true;
                        }
                        
                        menu = 0;
                        
                    
                    }
                
                    if ((key == KeyEvent.VK_UP || key == KeyEvent.VK_W) ) {
                        menu--;            
                    }

                    if ((key == KeyEvent.VK_DOWN || key == KeyEvent.VK_S) ) {
                       menu++;
                    } 
                }
            }else if(mainMenu[1] == true){
                
                if(key == KeyEvent.VK_ENTER){
                    mainMenu[1] = false;
                    
                    snakeData.put("nickname", nickname);
                    try {
                        updateSnakeData();
                    } catch (IOException ex) {
                        Logger.getLogger(SnakeBoard.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }else if(key == KeyEvent.VK_BACK_SPACE){
                    if(nickname.length() > 0)
                        nickname.setLength(nickname.length() - 1);
                }
                    
                else
                {
                    if(nickname.length() ==0 || nickname.charAt(nickname.length()-1) == ' ')
                        nickname.append((char) key);
                    else
                        nickname.append(Character.toLowerCase((char) key));
                }
                
            }else if (mainMenu[2] == true){
                
                if(key == KeyEvent.VK_ENTER)
                    mainMenu[2] = false;
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
    
    private int mod(int num, int mod){
        int ans = num%mod;
        
        
        while(ans < 0){
            
            ans+= mod;
            
        }
        
        
        return ans;
    }
    
    private void updateSnakeData() throws IOException{
        SNAKEDATAFILE.delete();
        SNAKEDATAFILE.createNewFile();
        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(SNAKEDATAFILE.getAbsolutePath()))) {
            snakeData.write(writer);
            writer.write("\n");
        } catch (Exception e) {
        }
    }

    
    
}