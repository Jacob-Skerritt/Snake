package snake;

//Imports
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
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.sampled.LineUnavailableException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;



public class SnakeBoard extends JPanel implements ActionListener {
    
    //JPanel variablers, used to set the size of the panel
    //SCOREBOARD_HEIGHT is used to segment the top of the snake game space for data dispaly
    private final int SCOREBOARD_HEIGHT = 60;
    private final int B_WIDTH = 700;
    private final int B_HEIGHT = 700 + SCOREBOARD_HEIGHT;
    
    
    //Snake Game Variables (Variables used by the game itself)
    private int score;
    private int size;
    private final int DOT_SIZE = 10;
    private final int ALL_DOTS = 4900;
    private final int RAND_POS = 69;
    private final int x[] = new int[ALL_DOTS];
    private final int y[] = new int[ALL_DOTS];
    private int dots;
    private int apple_x;
    private int apple_y;
    private int bad_apple_x;
    private int bad_apple_y;
    private int doubleSize;
    private int outputBadApple; 
    private boolean leftDirection = false;
    private boolean rightDirection = true;
    private boolean upDirection = false;
    private boolean downDirection = false;
    private boolean inGame = true;
    
    
    //Menu Variables
    private int menu;
    private final boolean mainMenu[] = new boolean []{false,false,false};
    public static int mainMenuOption;
    
    //Timer variables
    private final int[] delayOptions= new int[]{120,90,60,30}; //used to chane how fast the snake game plays based on difficulty setting
    private Timer timer;
    
    

    
    //Sound variables
    private  FloatControl gainControl;
    public static Clip backgroundMusic;
    private Clip appleHit;
    private Clip gameOver;
    
    //Image Variables
    private Image ball;
    private Image apple;
    private Image badApple;
    private Image head;
    
    //Calendar variables
    private Calendar cal;
    private Calendar badAppleTimer;//used to determine how long the "bad apple" should stay visible
    
    //File variables
    private final File SNAKEDATAFILE= new File("src/snake/snakeData.txt");//File that contains all data for the snake game(top ten highscores, nickname);
    
    //Game Data stored in game file "snakeData"
    private JSONObject snakeData; //Holds all of the data from the file "snakeData"
    private JSONObject[] topTen; //Stores the top ten local scores
    private StringBuilder nickname;//Stores user nickname
    
    //Other Variables used throughout the application
    private boolean playGame; // used to determine if the gasme is being played or not
    private int showCursor = 5;//counter used to dictate the pace of the cursor for the nickname input
    private boolean paused;//used to puase the game




    //Method call to initiate snakeGame
    public SnakeBoard() throws UnsupportedAudioFileException, IOException, ParseException {
        //Calls the private function initBoard()
        initBoard();
    }
    
    private void initBoard() throws UnsupportedAudioFileException, IOException, ParseException {
        
        
        addKeyListener(new TAdapter());
        
        //Setting the background for the applicaiton
        setBackground(Color.black);
        //making the Jpanel Focusable
        setFocusable(true);
        //Setting the size, defined in the vairalbe, no scaling;
        setPreferredSize(new Dimension(B_WIDTH, B_HEIGHT));
        
        
        
        //Method call for sound initialisation
        initMusic();
        
        //Method call for file creation or location
        initFiles();
        
        //Method call for loading the data stored in the file "snakeData"
        initData();
        
        //Method call for image initialisation
        initImages();
        //Method call to initialise game(should be changed in future improvements)
        
        
        timer = new Timer(delayOptions[0], this);

        
        
       
        
    }
    
    
    //Method for ensuring essnetial files are available
    //If files are not present, creating the base file and adding the important data
    private void initFiles() throws IOException, ParseException{
        
        
        File f = new File("src/snake/snakeData.txt");
    
        if(!f.exists())
        {
            
           
            SNAKEDATAFILE.createNewFile();
            JSONObject snakeData = new JSONObject();
            JSONObject topTenUser = new JSONObject();
            snakeData.put("nickname", "N/A");
                
            JSONArray array = new JSONArray();
            
            
            for(int i =10; i >0; i--){
                
                topTenUser.put("nickname" , "N/A");
                topTenUser.put("score", i);
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

             
    }
    
    //Method to load the data in the file "snakeData" into the appropriate variables
    private void initData(){
       
        try {
            BufferedReader br = new BufferedReader(new FileReader(SNAKEDATAFILE));
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();
            
            nickname = new StringBuilder();
            topTen = new JSONObject[10];

            while (line != null) {
                sb.append(line);
                line = br.readLine();
            }

            snakeData = new JSONObject(sb.toString());
            nickname.append(snakeData.get("nickname"));
            
            JSONArray tempArray = (JSONArray) snakeData.get("topTenUser");
            for (int i=0; i<tempArray.length(); i++) {
                topTen[i]  = tempArray.getJSONObject(i);
                
            } 
            
       } catch(IOException | JSONException e) {
       e.printStackTrace();
        }


    
        
    }
    
    //Load all of the images used in the game
    private void initImages() throws IOException {
        
        try{
            
        //Creating file path string for image "dot" used for t he body of the snake
        String pathToImageSortBy = "/images/snake/dot.png";
        //Loading fiel data into BufferedImage using ImageIO.read();
        BufferedImage img = ImageIO.read(getClass().getResourceAsStream(pathToImageSortBy));
        //Creating an ImageIcon using the bufferedImage
        ImageIcon iid = new ImageIcon(img);
        //Passing the ImageIcon to the Image varualbe "ball"
        ball = iid.getImage();
           
        //Same steps as previously described, image "apple_edit1" is the apple(green) using the game to gain points
        pathToImageSortBy = "/images/snake/apple_edit_1.png";
        img = ImageIO.read(getClass().getResourceAsStream(pathToImageSortBy));
        ImageIcon iia = new ImageIcon(img);
        apple = iia.getImage();
        
         //Same steps as previously described, image "head" is the head of the snake which differes in colour from the body(dot)
        pathToImageSortBy = "/images/snake/head.png";
        img = ImageIO.read(getClass().getResourceAsStream(pathToImageSortBy));
        ImageIcon iih = new ImageIcon(img);
        head = iih.getImage();
        
         //Same steps as previously described, "bad_apple_edit_1" is the other apple(red) used in the game
        pathToImageSortBy = "/images/snake/bad_apple_edit_1.png";
        img = ImageIO.read(getClass().getResourceAsStream(pathToImageSortBy));
        ImageIcon iiba = new ImageIcon(img);
        badApple = iiba.getImage();
        
        }catch(IOException e)
        {
            System.out.println(e);
        }
    }
    
    //Load all of the sound clips used for the game
    private void initMusic() throws UnsupportedAudioFileException, IOException{
        
        //declaring stream variables
        InputStream in = null;
        InputStream bufferedIn;
        AudioInputStream ais ;
        try{
            
            //Using inputStream to read a .wav audio file. this clip is played when the snkae colides with an apple
            in = getClass().getResourceAsStream("/sounds/apple_hit.wav");
            
            //Creating a bufferdInputStream using the inputstream "in"
            bufferedIn = new BufferedInputStream(in);
            
            //Creating an AudioInputStream using the bufferedinputstream "buffededIn"
             ais = AudioSystem.getAudioInputStream(bufferedIn);
            
            //Obtaining a clip that can be used to play an audio stream
            appleHit  = AudioSystem.getClip();
            
            //Opening the AudioInputStream ais
            appleHit.open(ais);
            
            //Creating a floarControl variable to control the volume of clip
            gainControl = (FloatControl) appleHit.getControl(FloatControl.Type.MASTER_GAIN);
            //reducing the sound of the clip
            gainControl.setValue(-10.0f);
            
            //Repeating the above steps, this clip is played when the snkae game is over
            in = getClass().getResourceAsStream("/sounds/game_over.wav");
            bufferedIn = new BufferedInputStream(in);
            ais = AudioSystem.getAudioInputStream(bufferedIn);
            gameOver = AudioSystem.getClip();
            gameOver.open(ais);
            
            
            //Repeating the above steps, this clip is played from the start to the finish of the application
            in = getClass().getResourceAsStream("/sounds/the_resistors.wav");
            bufferedIn = new BufferedInputStream(in);
            ais = AudioSystem.getAudioInputStream(bufferedIn);
            backgroundMusic = AudioSystem.getClip();
            backgroundMusic.open(ais);
            gainControl = (FloatControl) backgroundMusic.getControl(FloatControl.Type.MASTER_GAIN);
            gainControl.setValue(-20.0f);
            
            

        }catch(IOException | LineUnavailableException | UnsupportedAudioFileException e){
            System.out.println(e);
        }finally{
            //closing the input stream "in"
            if(in != null){
                try{
                    in.close();
                }catch(IOException e){
                    System.out.println(e);
                }
            }
                
        
        }
    }
    
    //Initialize all the variables used in the Snake game itself(Significant changes required) 
    private void initGame() {
        
        //Initalising variables used in Snake Game
        dots = 3;
        size = dots;
        score = 0;
        doubleSize = size*2;
        outputBadApple = ThreadLocalRandom.current().nextInt(score + score, score + score + 20);
        backgroundMusic.loop(Clip.LOOP_CONTINUOUSLY);
        badAppleTimer = Calendar.getInstance();
        cal = Calendar.getInstance();
        paused = false;
        menu = 0;
        
        
        //initializing the snake;
        for (int z = 0; z < dots; z++) {
            x[z] = 50 - z * 10;
            y[z] = 70;
        }
        
        //initializing apple(green, red)
        locateApple();
        

        
        

        
        
    }

    //paintComponent, used to display everyting on the jPanel, can be called publicly
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        //Calling the private mehtod doDrawing 
        doDrawing(g);
    }
    
    //Method is reponsible for the flow and display of all aspects of the application
    private void doDrawing(Graphics g) {
        
        //upon loading the game a user will be greeted with a "main menu", hitting enter of any of these options will result in the below:     
            //If a user selects the first option, "play" is  highlighted they will enter the playMeny to choose a game difficulty
            //Else if a user selects the second option "Nickname" they will be taken to a screen where they are promoted to enter a new nickname
            //Else if a user selects the third option "Highscores" they will be taken to a screen where they can view the top ten users and scores of the snake game
            //Finally if the choose the fourht option "Exit Game" the game will shutdown
        
        
        if(mainMenu[0] == true){
            
            //if a user has selected one of the difficulties, we proceed to the game, otherwise display the playGameMenu()
            if(playGame == true){
                
                //if inGame is true, continue dsiplaying the agme, otherwise, display gamoverMenu
                if (inGame) {
                    
                    //Drawing the apple image
                    g.drawImage(apple, apple_x, apple_y, this);
                       
                    //if the the badAppleTimer has been set and is graeter than the current time, output the bad apple(red)
                    if(badAppleTimer.compareTo(cal) > 0 ){
                        g.drawImage(badApple, bad_apple_x, bad_apple_y, this);
                    }
                    
                    //If the outputbadApple variableis equal to the score, do the following
                     if(outputBadApple == score)
                    {
                        //Create an instance of Calendar
                        badAppleTimer = Calendar.getInstance();
                        //add 20 seconds to the calenar instance
                        badAppleTimer.add(Calendar.SECOND,20);
                        //set the ouputbadApple variable to be randomly in the range of double the current score to double the current score + 20
                        outputBadApple = ThreadLocalRandom.current().nextInt(score + score, score + score + 20);
                    }
                    
                     //Draw the snake
                    for (int z = 0; z < dots; z++) {
                        if (z == 0) {
                            g.drawImage(head, x[z], y[z], this);
                        } else {
                            g.drawImage(ball, x[z], y[z], this);
                        }
                    }
                    
                    
                    //If the user pauses the game (using ESC), display pauseMenu
                    if(paused){

                        pauseMenu(g);

                    }
                    
                    //Draw the scoreboard at the top of the scrrn
                    drawScoreboard(g);
                    //update the calendar variable
                    cal = Calendar.getInstance();

                    Toolkit.getDefaultToolkit().sync();

                }else {
                    //if the game is over, display gameOverMenu
                    gameOverMenu(g);
                   
                }
            }else
                //Display playMenu
                playMenu(g);
            
        }else if(mainMenu[1] == true){
            //Calls the DrawNicknameGUI method
            drawNicknameGUI(g);
            
        }else if(mainMenu[2] == true){
            //Calls the drawaHighScores mehtod 
            drawHighScores(g);
            
        }else
            //Calls the mainMenu method
            mainMenu(g);
    }
    
    //Method for the main menu display 
    private void mainMenu(Graphics g){
        
        //Creating the settings and message to display the heading for the Main Menu
        String msg;
        g.setColor(Color.white);
        Font small = new Font("Helvetica", Font.BOLD, 44);
        FontMetrics metr = getFontMetrics(small);
        g.setFont(small);
        msg = "Snake!";
        g.drawString(msg,(B_WIDTH - metr.stringWidth(msg))/2, B_HEIGHT/8);
        
        
        //The following four drawLines create the container for the menu
        g.drawLine(B_WIDTH/4,B_HEIGHT/4, B_WIDTH/4, B_HEIGHT - B_HEIGHT/4);
        g.drawLine(B_WIDTH - B_WIDTH/4,B_HEIGHT/4, B_WIDTH - B_WIDTH/4, B_HEIGHT - B_HEIGHT/4);
        
        g.drawLine(B_WIDTH/4,B_HEIGHT/4, B_WIDTH - B_WIDTH/4,B_HEIGHT/4);
        g.drawLine(B_WIDTH - B_WIDTH/4, B_HEIGHT - B_HEIGHT/4, B_WIDTH/4, B_HEIGHT - B_HEIGHT/4);
        
        

        small = new Font("Helvetica", Font.BOLD, 34);
        
        g.setFont(small);
        
         metr = getFontMetrics(small);
        
         
         //using the mod(menu,num) method to determine which menu option the user is currently selecting, surrounds current message with "> <" 
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
    
    
    //Method for outputing the menu when a user pauses the snake game
    private void pauseMenu(Graphics g){
        
        //The following method is used to output the menu while the snake game is paused
        Font small = new Font("Helvetica", Font.BOLD, 44);
        g.setColor(Color.white);//setting colour to white
        g.setFont(small);//setting the font
        
        
        //the following four drawLines are used to draw the container for the menu
        g.drawLine(B_WIDTH/4,B_HEIGHT/3, B_WIDTH/4, B_HEIGHT - B_HEIGHT/3);
        g.drawLine(B_WIDTH - B_WIDTH/4,B_HEIGHT/3, B_WIDTH - B_WIDTH/4, B_HEIGHT - B_HEIGHT/3);
        
        g.drawLine(B_WIDTH/4,B_HEIGHT/3, B_WIDTH - B_WIDTH/4,B_HEIGHT/3);
        g.drawLine(B_WIDTH - B_WIDTH/4, B_HEIGHT - B_HEIGHT/3, B_WIDTH/4, B_HEIGHT - B_HEIGHT/3);
        
        
       
        small = new Font("Helvetica", Font.BOLD, 40); //New Font
        
       
        FontMetrics metr = getFontMetrics(small);

        
        g.setFont(small);
        
        
        //Outputing a "Game Paused" message to inform user game is still paused
        String msg = "Game Paused"; 
        g.drawString(msg, (B_WIDTH - metr.stringWidth(msg)) / 2, B_HEIGHT / 6);
        
        
        
        small = new Font("Helvetica", Font.BOLD, 30); 
        g.setFont(small);
        metr = getFontMetrics(small);
        
        
        //using the mod(menu,num) method to determine which menu option the user is currently selecting, surrounds current message with "> <" 
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

    private void gameOverMenu(Graphics g){
        
        //GameOver clip that plays when the user loses the game, sound plays once
        gameOver.setFramePosition(0);
        gameOver.loop(0);
        gameOver.start();
        
        Font small = new Font("Helvetica", Font.BOLD, 44);
        FontMetrics metr;
        
        g.setColor(Color.white);
        g.setFont(small);

        //Creting grid for end of game stats (score, size) 
        g.drawLine(B_WIDTH/5,B_HEIGHT/4, B_WIDTH/5, B_HEIGHT/4 + B_HEIGHT/18);
        g.drawLine(B_WIDTH - B_WIDTH/5,B_HEIGHT/4,B_WIDTH - B_WIDTH/5, B_HEIGHT/4 + B_HEIGHT/18);
        
        g.drawLine(B_WIDTH/5,B_HEIGHT/4, B_WIDTH - B_WIDTH/5,B_HEIGHT/4);
        g.drawLine(B_WIDTH/5, B_HEIGHT/4 + B_HEIGHT/18,  B_WIDTH - B_WIDTH/5, B_HEIGHT/4 + B_HEIGHT/18);
        
        
        
        //Creating grid for GameOver menu
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
        
        //If the user gets a score greater than 99,999, shorten the message size using shortString
        if(Integer.toString(score).length() < 5)
            msg = "Score: " + score;
        else
        {
            msg = shortString("Score: " + Integer.toString(score), 3);
            
        }
        g.drawString(msg , B_WIDTH/5 + 10, B_HEIGHT/4 + B_HEIGHT/18 - 10);
        
        
        
        if(Integer.toString(size).length() < 5)
            msg = "Size: " + size;
        else
        {
            msg = shortString("Size: " + Integer.toString(size), 3);
        }
        g.drawString(msg , B_WIDTH/2+ 20,B_HEIGHT/4 + B_HEIGHT/18 - 10);
        
        
        //using the mod(menu,num) method to determine which menu option the user is currently selecting, surrounds current message with "> <" 
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

        
        //Creating the grid that contains the menu
        g.drawLine(B_WIDTH/4,B_HEIGHT/4, B_WIDTH/4, B_HEIGHT - B_HEIGHT/4);
        g.drawLine(B_WIDTH - B_WIDTH/4,B_HEIGHT/4, B_WIDTH - B_WIDTH/4, B_HEIGHT - B_HEIGHT/4);
        
        g.drawLine(B_WIDTH/4,B_HEIGHT/4, B_WIDTH - B_WIDTH/4,B_HEIGHT/4);
        g.drawLine(B_WIDTH - B_WIDTH/4, B_HEIGHT - B_HEIGHT/4, B_WIDTH/4, B_HEIGHT - B_HEIGHT/4);
        
        small = new Font("Helvetica", Font.BOLD, 30);
        metr = getFontMetrics(small);
        g.setFont(small);
        
        
        //using the mod(menu,num) method to determine which menu option the user is currently selecting, surrounds current message with "> <" 
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

    //the following method is used to create the nickname input screen
    private void drawNicknameGUI(Graphics g){
        
        //Variable containg the line the user will "type" onto
        String line = "_______________________";
        String msg = "Enter Nickname";
        Font font = new Font("Helvetica", Font.BOLD, 40);
        FontMetrics metr = getFontMetrics(font);

        g.setColor(Color.white);
        g.setFont(font);
        //Header for the nickname page
        g.drawString(msg, (B_WIDTH - metr.stringWidth(msg)) / 2, B_HEIGHT / 4);
        g.drawString(line,(B_WIDTH - metr.stringWidth(line)) / 2,( B_HEIGHT / 2));
        
        font = new Font("Helvetica", Font.BOLD, 32);
        metr = getFontMetrics(font);
        g.setFont(font);
        
        //check to determine if the user has entered a name longer than the supplied input line, if so, shortn the nickname to the end elements
        if(nickname.length() < line.length() -3)
        {
            g.drawString(nickname.toString(),(B_WIDTH - metr.stringWidth(line)) / 2,( B_HEIGHT / 2));
            //custome "Cursor" element that imatates a flashing cursor infront of the word
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
        
        
        
        
        //Return message
         msg = "> Return to Main Menu <";
        g.drawString(msg,(B_WIDTH - metr.stringWidth(msg)) / 2, B_HEIGHT - B_HEIGHT/3);
        
        
    }
    
    //Method for dispalying the score board that is used to display all game info during a game of snkae
    private void drawScoreboard(Graphics g){
        //Declaring a variable for displaying messages
        String msg;
        
        //Settings for the Graphic g variable,
        g.setColor(Color.BLACK);//Setting background colour black
        g.fillRect(0, 0, 700, SCOREBOARD_HEIGHT-1);//creating a rectangle starting in the top left corner for the entire width of the game and 59 pixles in height
        g.setColor(Color.WHITE);//setting font colour to white
        g.drawLine(0,SCOREBOARD_HEIGHT-1,700,SCOREBOARD_HEIGHT-1); //Drawing a line between the game board and the scoreboard
                 
        cal = Calendar.getInstance();//esnruing the calendar variable is at current data and time
        Font font = new Font("Verdana", Font.BOLD, 15);//Creating a new Font variable to change the font of Graphics g
        g.setFont(font);//setting font
        
        //outputting the message for increasing volume in the top right corner of the scoreboard
        msg = "Increase Volume: +";
        g.drawString(msg,B_WIDTH-g.getFontMetrics().stringWidth(msg),SCOREBOARD_HEIGHT - 3 -  font.getSize());
        
        //outputting the message for decreasing volume just under the increase volume message
        msg="Decrease Volume: -";
        g.drawString(msg,B_WIDTH-g.getFontMetrics().stringWidth(msg)-1,SCOREBOARD_HEIGHT-4);
        
        
        
        font = new Font("Verdana", Font.BOLD, 18);//creating a new instance of font to make the font bigger
        g.setFont(font);//setting font
        msg = "Score: "+ score;//Outputting the score at the bottom left of the scoreboard
        g.drawString(msg ,0,57); 
        
        msg = "Size: "+ size;//outputting the size just above the score
        g.drawString(msg,0,38);
        
        //Outputting the nickname of the user in the top left corner, if the size of the nickname is greater than 15 characters we shorten it using the method (
        if(nickname.length() < 15)
            g.drawString("User: " + nickname.toString(), 0,font.getSize() );
        else{

            g.drawString("User: " + shortString(nickname.toString(), 13), 0,font.getSize() );
            
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
    
    //Method for displaying the top ten highscores(local)
    private void drawHighScores(Graphics g){
        
        //The number of lines and outputs on  this page is greater due to a grid being required to containt the #,score,nickname
        
        String msg = "HighScores";//Page title
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
        String outputName;
        String outputScore;
        for(int i = 0; i < 9; i++){
            
            padding +=(((B_HEIGHT - B_HEIGHT/7) -B_HEIGHT/7))/(10);
            
            g.drawString(i+1+"",B_WIDTH/7+10, (B_HEIGHT/7) + padding- 10 );
            
            outputName = topTen[i].getString("nickname");
            outputScore = Integer.toString(topTen[i].getInt("score"));
            
            if(outputName.length() < 20)
                g.drawString(outputName,B_WIDTH/7 + metr.stringWidth("10")+10, (B_HEIGHT/7) + padding- 10 );
            else
                g.drawString(shortString(outputName, 18), B_WIDTH/7 + metr.stringWidth("10")+10, (B_HEIGHT/7) + padding- 10 );
            
            
            
           g.drawString(outputScore,nicknameScoreLine + 10 , (B_HEIGHT/7) + padding- 10 );
            
            g.drawLine(B_WIDTH/7, (B_HEIGHT/7) + padding, B_WIDTH - B_WIDTH/7,(B_HEIGHT/7) + padding);
        }
        
        
        g.drawString(Integer.toString(10),B_WIDTH/7, (B_HEIGHT/7) + padding + (((B_HEIGHT - B_HEIGHT/7) -B_HEIGHT/7))/(10)- 10 );
        
        outputName = topTen[topTen.length-1].getString("nickname");
        outputScore = Integer.toString(topTen[topTen.length-1].getInt("score"));
        if(outputName.length() < 20)
              g.drawString(outputName,B_WIDTH/7 + metr.stringWidth("10")+10, (B_HEIGHT/7) + padding +(((B_HEIGHT - B_HEIGHT/7) -B_HEIGHT/7))/(10) - 10 );
        else
             g.drawString(shortString(outputName,18),B_WIDTH/7 + metr.stringWidth("10")+10, (B_HEIGHT/7) + padding +(((B_HEIGHT - B_HEIGHT/7) -B_HEIGHT/7))/(10) - 10 );
        
    
       
            g.drawString(outputScore,nicknameScoreLine + 10 , (B_HEIGHT/7) + padding +(((B_HEIGHT - B_HEIGHT/7) -B_HEIGHT/7))/(10) - 10 );
        
        g.drawLine(B_WIDTH/7, B_HEIGHT -  B_HEIGHT/7, B_WIDTH - B_WIDTH/7,B_HEIGHT -B_HEIGHT/7);
        
        
        
        
    }
    
    
    //Simple method that takes a string and an int, shortens the string to the size of the int and appends ".." to the end of the stirng
    private String shortString(String line, int length){
        
        StringBuilder newLine  = new StringBuilder();
        for(int i=0; i < length;i++)
        {
            newLine.append((line.charAt(i)));
        }
        
        newLine.append("..");
        
        
        return newLine.toString();
    }
    
    
    //collisoin check to determine if the snakes head has collided with the apple
    private void checkApple() {

        //if true, increment snake, reset apple
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
        
        //check to determine if the snake head has collided with the bad apple
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
    
    //Changes the colour of the map randomly every time the method is called
    //This method can be discarded
    public void backgroundColor(){
        
        
        switch(ThreadLocalRandom.current().nextInt(1,4)){
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
                
        }
    }
    
    //Moves the snake by assigning values to the x & y arrays
    //Done using the true of flase values for the up/down/left/right directions
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
    
    
    //Determines if the snake has collided with the bounderies of the field or itself
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

        if (x[0] >= B_WIDTH) {
            x[0] = 0;
        }

        if (x[0] < 0) {
            x[0] = B_WIDTH;
        }
        
        
    }

    //Method for generating the location for the apple, ensuring it is not inside  the snake 
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
    
    //Method for genrating the location of the bad apple, ensuring it is not within the snake and it is not behind the apple(green)
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
    
    //Method used to update the data held within the tepTen JSONObject array
    private void updateLeaderBoard(){
        
        for (int i=0; i<topTen.length; i++) {
            
            
            int topTenScore = topTen[i].getInt("score");
            if(score > topTenScore){
                
                for(int j= topTen.length-1; j > i; j--){
                    JSONObject tempObject = topTen[j-1];
                    topTen[j] = tempObject;
                    
                }
                topTen[i].put("nickname", nickname.toString());
                topTen[i].put("score", score);
                break;
                
            }
            
            //updating the snakeData JSONObject that is used to edit the "snakeData" textfile
            snakeData.put("topTenUser", topTen);
            
            
      
        }
        

    }
    

    //When an aciton occurs, repaint and carry out any called mehtods
    @Override
    public void actionPerformed(ActionEvent e) {

        
            
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

        repaint();
    }
    
    //Method used to return the posative value of a num%mod
    private int mod(int num, int mod){
        int ans = num%mod;
        
        
        while(ans < 0){
            
            ans+= mod;
            
        }
        
        
        return ans;
    }
    
    //Method used to update the data in the "snakeData" file
    private void updateSnakeData() throws IOException{
        SNAKEDATAFILE.delete();
        SNAKEDATAFILE.createNewFile();
        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(SNAKEDATAFILE.getAbsolutePath()))) {
            snakeData.write(writer);
            
            writer.write("\n");
        } catch (Exception e) {
        }
    }
    
    //KeyAdapter class used to recieve keyboard inputs and deteremine what is required 
    //Requires significant changes
    private class TAdapter extends KeyAdapter  {
        

        @Override
        public void keyPressed(KeyEvent e) {
           timer.start();
            int key = e.getKeyCode();
            
            if(mainMenu[0] == false && mainMenu[1] == false && mainMenu[2] == false){
                mainMenu(key);  
            }
            else if(mainMenu[0] == true){
                    
                if(playGame == true){
                    
                    if(!inGame ){
                        
                        gameOverMenu(key);

                    }
                    else if(!paused){
                      
                        gameInProgress(key);

                    }else if(paused){
                       
                        gamePausedMenu(key);

                    }
                }
                else
                {
                    gameDifficultyMenu(key);
                }
            }else if(mainMenu[1] == true){
                
                nicknameInterface(key);
                
            }else if (mainMenu[2] == true){
                //Exit the "highscore" seciton of the applciation, no other interaction occurs  
                if(key == KeyEvent.VK_ENTER)
                    mainMenu[2] = false;
            }
            
            volumeControls(key);
            

            
            
        }
        
        //Method for MainMenu navigation and interaction
        private void mainMenu(int key){
            
                
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
                
               
                
        }
        
        //Method for Game Over menu nagivation and interaction
        private void gameOverMenu(int key){
               
                        if ((key == KeyEvent.VK_UP || key == KeyEvent.VK_W) ) {
                            
                            menu--;
                        }

                        if ((key == KeyEvent.VK_DOWN || key == KeyEvent.VK_S) ) {
                            menu++;
                        }
                        
                        if(key == KeyEvent.VK_ENTER){
                            

                           

                            if(mod(menu,2) == 0){
                                
                                updateLeaderBoard();
                                try {
                                    updateSnakeData();
                                } catch (IOException ex) {
                                    Logger.getLogger(SnakeBoard.class.getName()).log(Level.SEVERE, null, ex);
                                }
                                initGame();
                                menu = 0;
                                inGame = true;
                                x[0] = 20;
                                y[0] = 70;
                                
                                
                            }

                            if(mod(menu,2) == 1){
                                mainMenu[0] = false;
                                updateLeaderBoard();
                                try {
                                    updateSnakeData();
                                } catch (IOException ex) {
                                    Logger.getLogger(SnakeBoard.class.getName()).log(Level.SEVERE, null, ex);
                                }
                                playGame = false;
                                
                                initGame();
                                menu = 0;
                                paused = false;
                                inGame = true;
                                x[0] = 20;
                                y[0] = 70;
                                
                                
                                
                                
                            }

                        }
                        
        }
        
        //Method for game intercaction while the snake game is in progres
        private void gameInProgress(int key){
            
            
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
            
        }
        
        //Method for Paused menu navigation and interaction
        private void gamePausedMenu(int key){
            
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

                    initGame();
                    menu = 0;
                }

                if(mod(menu,3) == 2){

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
        
        //Method for Game Difficulty naviagation and interaction
        private void gameDifficultyMenu(int key){
                    if(key == KeyEvent.VK_ENTER){
                    
                        if(mod(menu,5) == 4){
                            mainMenu[0] = false;
                            
                        }else{
                             
                            timer.setDelay(delayOptions[menu]);

                            playGame = true;
                        }
                        
                        menu = 0;
                        
                        initGame();
                    }
                
                    if ((key == KeyEvent.VK_UP || key == KeyEvent.VK_W) ) {
                        menu--;            
                    }

                    if ((key == KeyEvent.VK_DOWN || key == KeyEvent.VK_S) ) {
                       menu++;
                    }
        }
        
        //Method for the creation and edit of a user nickname
        private void nicknameInterface(int key){
            if(key == KeyEvent.VK_ENTER){
                 mainMenu[1] = false;

                 snakeData.put("nickname", nickname.toString());
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
        }
        
        //Method for lowering and increasing volume throughout the entire applicaiton
        private void volumeControls(int key){
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
    
}