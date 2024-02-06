import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.LinkedList;
import java.util.Queue;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class Animation extends JPanel implements Runnable,MouseListener{

    static Font font;
    private static BufferedImage buffer = new BufferedImage(600, 600, BufferedImage.TYPE_INT_ARGB);
    private static BufferedImage babyBuffer = new BufferedImage(600, 600, BufferedImage.TYPE_INT_ARGB);
    private Stage currentStage = Stage.Show;
    boolean isDraw = false;
    boolean isText = true;
    Animation(){
        addMouseListener(this);
    }

    public static void main(String[] args) {
        try {
            font = Font.createFont(Font.TRUETYPE_FONT, new File("pokemon-emerald.ttf")).deriveFont(50f);
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            ge.registerFont(Font.createFont(Font.TRUETYPE_FONT, new File("pokemon-emerald.ttf")));
        } catch (Exception e) {
            System.err.println("NO FONT");
        }

        Animation m = new Animation();
        JFrame f = new JFrame();
        f.add(m);
        f.setTitle("Lab5 65050257");
        m.setPreferredSize(new Dimension(600, 600));
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.pack();
        f.setVisible(true);
        f.setLocationRelativeTo(null);
        (new Thread(m)).start();
    }

    // =============================================================================
    // =============================================================================
    //                             animation variable
    // =============================================================================
    // =============================================================================

    double letter1 = 0;
    double letter2 = 0;
    String line1Text = "What?";
    String line2Text = "Baby Chicken is evolving!";
    
    double tranparency = 0;
    double transition = 0;

    int pillarLayers = 10;
    int pillarBalls = 4;
    int pillarMidpointX = 300;
    int pillarMidpointY = 360;
    int pillarEndpointY = 1;
    double[][] pillarSize = new double[pillarLayers][pillarBalls];
    double[][] pillarPositionX = new double[pillarLayers][pillarBalls];
    double[][] pillarPositionY = new double[pillarLayers][pillarBalls+1];
    char[][] pillarDirection = new char[pillarLayers][pillarBalls];

    int domeLayers = 6;
    int domeBalls = 8;
    int domeMidpointX = 300;
    int domeMidpointY = 1;
    int domeEndpointY = 360;
    double[][] domeSize = new double[domeLayers][domeBalls];
    double[][] domePositionX = new double[domeLayers][domeBalls];
    double[][] domePositionY = new double[domeLayers][domeBalls+1];

    double chickenMove = 0;
    double chickenVelocity = -100;


    //start animation
    @Override
    public void run() {
        double lastTime = System.currentTimeMillis();
        double currentTime, elapsedTime, startTime;
        double letterVelocity = 10;

        initializePillar();
        initializeDome();
        drawBabyBuffer();
        
        startTime = lastTime;
        while (true) {

            currentTime = System.currentTimeMillis();
            elapsedTime = currentTime - lastTime;
            lastTime = currentTime;
            
            double timer = (currentTime-startTime)/1000.0;   //timer since start running the animation in Second Unit

            //0 - 3 second
            if(timer <= 3){
                //do nothing
            }//3-6
            else if(timer <= 6){
                currentStage = Stage.Show;
                chickenMove += chickenVelocity * elapsedTime / 1000.0;
                if(chickenMove >= 0){
                    chickenMove = 0;
                    chickenVelocity = -chickenVelocity;
                }
                else if(chickenMove <= -30){
                    chickenMove = -30;
                    chickenVelocity = -chickenVelocity;
                }

            }//0 - 3.416 second
            else if(timer <= 6 + 5/letterVelocity){
                currentStage = Stage.Text;
                letter1 += letterVelocity * elapsedTime / 1000.0;
            }//3.416 - 5.5 second
            else if(timer <= 6 + 30/letterVelocity){
                currentStage = Stage.Text;
                letter2 += letterVelocity * elapsedTime / 1000.0;
            }//5.5 - 6.5 second
            else if(timer <= 9.5 && tranparency < 255){//dark screen transition at the 4th second  
                isText = false;
                currentStage = Stage.Evolve;
                updateTransparency(elapsedTime);
            }//6.5 - 99999999 second
            else if(timer <= 9999999 && timer * 1000 % 1 == 0){ //Moving each balls in the layer of the pillar
                currentStage = Stage.Evolve;
                if(pillarPositionY[pillarLayers-1][pillarBalls] >= pillarEndpointY){ //Moving each balls in the layer of the pillar
                    updatePillar();
                }
                else if (domePositionY[domeLayers-1][domeBalls] <= domeEndpointY){ //Moving each balls in the layer of the dome
                    updateDome();
                }
            }
        
            //Display
            repaint();
        }
    }

    private void drawBabyBuffer() {
        Graphics2D g = babyBuffer.createGraphics();
        //g.scale(2, 2);
        //right face
        g.setColor(new Color(111,111,111,1));
        g.fillRect(0, 0, 600, 600);
        
        g.setColor(Palette.BLACK.getColor());
        drawCurve(g, 62+250, 54+150, 62+250, 54+150, 65+250, 43+150, 73+250, 39+150);
        drawCurve(g, 73+250, 39+150, 73+250, 39+150, 75+250, 42+150, 71+250, 55+150);
        drawCurve(g, 71+250, 55+150, 71+250, 55+150, 75+250, 52+150, 78+250, 53+150);
        drawCurve(g, 78+250, 53+150, 78+250, 53+150, 78+250, 55+150, 75+250, 60+150);
        drawCurve(g, 75+250, 60+150, 110+250, 73+150, 106+250, 113+150, 69+250, 122+150);
        //left face
        drawCurve(g, 62+250, 54+150, 40+250, 51+150, 15+250, 60+150, 4+250, 78+150);
        //left eye
        drawElipse(g, 5+250, 86+150, 4, 10);
        drawElipse(g, 5+250, 82+150, 4, 6);
        //drawElipse(g, 6, 92, 2, 4);
        //right eye
        drawElipse(g, 55+250, 94+150, 8, 9);
        drawElipse(g, 55+250, 91+150, 5, 6);
        //drawElipse(g, 57, 99, 4, 4);
        //left face
        drawCurve(g, 6+250, 98+150, 6+250, 98+150, 13+250, 114+150, 27+250, 118+150);
        //bottom
        drawCurve(g, 20+250, 136+150, 25+250, 173+150, 70+250, 173+150, 87+250, 145+150);
        drawCurve(g, 87+250, 145+150, 87+250, 145+150, 81+250, 138+150, 81+250, 138+150);
        g.setColor(Palette.HARDPART.getColor());
        //beak
        drawCurve(g, 10+250, 101+150, 17+250, 105+150, 30+250, 106+150, 37+250, 102+150);
        drawCurve(g, 37+250, 102+150, 36+250, 98+150, 27+250, 97+150, 25+250, 93+150);
        drawCurve(g, 25+250, 93+150, 23+250, 90+150, 19+250, 90+150, 16+250, 93+150);
        drawCurve(g, 16+250, 93+150, 11+250, 95+150, 8+250, 98+150, 10+250, 101+150);
        g.setColor(Palette.HARDPART_OUTLINE.getColor());
        //left feet?
        drawCurve(g, 44+250, 164+150, 44+250, 169+150, 41+250, 172+150, 41+250, 172+150);
        drawCurve(g, 41+250, 172+150, 26+250, 169+150, 21+250, 172+150, 18+250, 178+150);
        drawCurve(g, 18+250, 178+150, 16+250, 184+150, 17+250, 186+150, 21+250, 187+150);
        drawCurve(g, 21+250, 187+150, 26+250, 179+150, 33+250, 179+150, 33+250, 179+150);

        drawCurve(g, 40+250, 175+150, 30+250, 177+150, 27+250, 186+150, 35+250, 193+150);
        drawCurve(g, 35+250, 193+150, 36+250, 184+150, 38+250, 183+150, 42+250, 183+150);

        drawCurve(g, 47+250, 177+150, 40+250, 180+150, 38+250, 189+150, 46+250, 195+150);
        drawCurve(g, 46+250, 195+150, 49+250, 196+150, 50+250, 195+150, 49+250, 187+150);
        drawCurve(g, 49+250, 187+150, 51+250, 181+150, 54+250, 179+150, 59+250, 182+150);
        drawCurve(g, 59+250, 182+150, 62+250, 183+150, 64+250, 181+150, 62+250, 179+150);
        drawCurve(g, 62+250, 179+150, 61+250, 175+150, 58+250, 173+150, 52+250, 174+150);
        drawCurve(g, 52+250, 174+150, 48+250, 174+150, 47+250, 172+150, 50+250, 165+150);
        //right feet?
        drawCurve(g, 53+250, 164+150, 55+250, 165+150, 52+250, 172+150, 60+250, 172+150);
        drawCurve(g, 58+250, 165+150, 60+250, 173+150, 60+250, 176+150, 70+250, 171+150);
        drawCurve(g, 65+250, 164+150, 66+250, 169+150, 69+250, 171+150, 75+250, 170+150);
        drawCurve(g, 74+250, 170+150, 80+250, 165+150, 79+250, 162+150, 76+250, 163+150);
        drawCurve(g, 76+250, 163+150, 71+250, 163+150, 71+250, 163+150, 71+250, 161+150);

        g.setColor(Palette.FEATHER_OUTLINE.getColor());
        //Comb
        drawCurve(g, 53+250, 53+150, 46+250, 42+150, 54+250, 23+150, 54+250, 23+150);
        drawCurve(g, 54+250, 23+150, 54+250, 23+150, 61+250, 29+150, 62+250, 39+150);
        drawCurve(g, 60+250, 46+150, 60+250, 46+150, 65+250, 17+150, 96+250, 1+150);
        drawCurve(g, 96+250, 1+150, 101+250, 8+150, 90+250, 42+150, 90+250, 42+150);
        drawCurve(g, 90+250, 42+150, 90+250, 42+150, 102+250, 32+150, 118+250, 30+150);
        drawCurve(g, 118+250, 30+150, 118+250, 30+150, 109+250, 51+150, 78+250, 61+150);
        //left wing?
        drawCurve(g, 27+250, 118+150, 18+250, 118+150, 9+250, 125+150, 9+250, 125+150);
        drawCurve(g, 9+250, 125+150, 9+250, 125+150, 15+250, 125+150, 15+250, 125+150);
        drawCurve(g, 15+250, 125+150, 15+250, 125+150, 11+250, 131+150, 11+250, 131+150);
        drawCurve(g, 11+250, 131+150, 11+250, 131+150, 17+250, 131+150, 17+250, 131+150);
        drawCurve(g, 17+250, 131+150, 17+250, 131+150, 15+250, 138+150, 15+250, 138+150);
        drawCurve(g, 15+250, 138+150, 15+250, 138+150, 21+250, 136+150, 21+250, 136+150);
        drawCurve(g, 21+250, 136+150, 21+250, 136+150, 24+250, 141+150, 24+250, 141+150);
        drawCurve(g, 24+250, 141+150, 24+250, 141+150, 31+250, 130+150, 31+250, 130+150);
        drawCurve(g, 24+250, 141+150, 24+250, 141+150, 281, 280, 281, 280);
        //right wing?
        drawCurve(g, 46+250, 133+150, 46+250, 133+150, 51+250, 145+150, 51+250, 145+150);
        drawCurve(g, 51+250, 145+150, 51+250, 145+150, 53+250, 138+150, 53+250, 138+150);
        drawCurve(g, 53+250, 138+150, 53+250, 138+150, 62+250, 149+150, 62+250, 149+150);
        drawCurve(g, 62+250, 149+150, 62+250, 149+150, 62+250, 142+150, 62+250, 142+150);
        drawCurve(g, 62+250, 142+150, 62+250, 142+150, 73+250, 149+150, 73+250, 149+150);
        drawCurve(g, 73+250, 149+150, 73+250, 149+150, 73+250, 140+150, 73+250, 140+150);
        drawCurve(g, 73+250, 140+150, 75+250, 143+150, 81+250, 142+150, 81+250, 142+150);
        drawCurve(g, 81+250, 142+150, 81+250, 142+150, 79+250, 136+150, 79+250, 136+150);
        drawCurve(g, 81+250, 138+150, 81+250, 138+150, 89+250, 137+150, 89+250, 137+150);
        drawCurve(g, 89+250, 137+150, 89+250, 137+150, 83+250, 128+150, 83+250, 128+150);
        drawCurve(g, 83+250, 128+150, 83+250, 128+150, 87+250, 125+150, 87+250, 125+150);
        drawCurve(g, 87+250, 125+150, 87+250, 125+150, 76+250, 120+150, 76+250, 120+150);

        g.setColor(Palette.FEATHER.getColor());
        //left upper wing
        drawCurve(g, 281, 280, 281, 280, 35+250, 123+150, 35+250, 123+150);
        drawCurve(g, 35+250, 123+150, 35+250, 123+150, 32+250, 124+150, 32+250, 124+150);
        drawCurve(g, 32+250, 124+150, 32+250, 124+150, 28+250, 119+150, 28+250, 119+150);
        //right upper wing
        drawCurve(g, 46+250, 133+150, 46+250, 133+150, 44+250, 122+150, 44+250, 122+150);
        drawCurve(g, 44+250, 122+150, 44+250, 122+150, 48+250, 127+150, 48+250, 127+150);
        drawCurve(g, 48+250, 127+150, 48+250, 127+150, 48+250, 121+150, 48+250, 121+150);
        drawCurve(g, 48+250, 121+150, 48+250, 121+150, 57+250, 128+150, 57+250, 128+150);
        drawCurve(g, 57+250, 128+150, 57+250, 128+150, 56+250, 123+150, 56+250, 123+150);
        drawCurve(g, 56+250, 123+150, 56+250, 123+150, 68+250, 122+150, 68+250, 122+150);

        //g.scale(0.5, 0.5);
        floodFillBorder(g, 331, 177, new Color[]{Palette.BLACK.getColor(),Palette.FEATHER_OUTLINE.getColor()}, Palette.FEATHER.getColor(), babyBuffer);
        floodFillBorder(g, 309, 220, new Color[]{Palette.BLACK.getColor(),Palette.FEATHER_OUTLINE.getColor()
                                                    ,Palette.HARDPART.getColor(), Palette.HARDPART_OUTLINE.getColor()
                                                    ,Palette.FEATHER.getColor()}, Palette.BODY.getColor(), babyBuffer);
        floodFillBorder(g, 271, 248, new Color[]{Palette.HARDPART.getColor()}, Palette.HARDPART.getColor(), babyBuffer);

        floodFillBorder(g, 294, 325, new Color[]{Palette.BLACK.getColor(), Palette.HARDPART_OUTLINE.getColor()}, Palette.HARDPART.getColor(), babyBuffer);
        floodFillBorder(g, 306, 318, new Color[]{Palette.BLACK.getColor(), Palette.HARDPART_OUTLINE.getColor()}, Palette.HARDPART.getColor(), babyBuffer);
        floodFillBorder(g, 312, 317, new Color[]{Palette.BLACK.getColor(), Palette.HARDPART_OUTLINE.getColor()}, Palette.HARDPART.getColor(), babyBuffer);
        floodFillBorder(g, 321, 316, new Color[]{Palette.BLACK.getColor(), Palette.HARDPART_OUTLINE.getColor()}, Palette.HARDPART.getColor(), babyBuffer);
        
        floodFillBorder(g, 304, 250, new Color[]{Palette.BLACK.getColor()}, Palette.BLACK.getColor(), babyBuffer);
        floodFillBorder(g, 255, 241, new Color[]{Palette.BLACK.getColor()}, Palette.BLACK.getColor(), babyBuffer);

        floodFillBorder(g, 273, 277, new Color[]{Palette.BLACK.getColor(),Palette.FEATHER.getColor()}, Palette.FEATHER.getColor(), babyBuffer);
        floodFillBorder(g, 322, 284, new Color[]{Palette.BLACK.getColor(),Palette.FEATHER.getColor()}, Palette.FEATHER.getColor(), babyBuffer);

        floodFillBorder(g, 255, 232, new Color[]{Palette.BLACK.getColor()}, Color.WHITE, babyBuffer);
        floodFillBorder(g, 305, 240, new Color[]{Palette.BLACK.getColor()}, Color.WHITE, babyBuffer);
        isDraw = true;
    }

    private void initializePillar() {

        //Set a horizontal position of each balls in the layer of the pillar
        for (double[] px : pillarPositionX) {
            px[0] = 300; //1st ball of each layer at x = 300
            px[1] = 300; //2nd ball of each layer at x = 300
            px[2] = 450; //3rd ball of each layer at x = 450
            px[3] = 150; //4th ball of each layer at x = 150
        }

        //Set a vertical position of each layer of the pillar
        int layerGaps = 0;
        for (double[] py : pillarPositionY) {
            py[pillarBalls] = pillarMidpointY + layerGaps;
            layerGaps += 2;
        }

        //Set a horizontal direction of each balls in the layer of the pillar
        for (char[] d : pillarDirection) {
            d[0] = 'L'; //1st ball of each layer move to the left
            d[1] = 'R'; //2st ball of each layer move to the right
            d[2] = 'L'; //3rd ball of each layer move to the left
            d[3] = 'R'; //4th ball of each layer move to the right
        }
    }

    private void initializeDome() {

        //Set a vertical position of each layer of the dome
        int layerGaps = 0;
        for (double[] py : domePositionY) {
            py[domeBalls] = domeMidpointY - layerGaps;
            layerGaps += 30;
        }
    }

    private void updateTransparency(double elapsedTime) {
        transition += 300 * elapsedTime / 1000.0;
        if((int)transition % 36 == 3){
            tranparency = transition;
        }
    }

    private void updatePillar() {
        int baseSize = 8;
        int finalSize = 3;
        int layerHeight = 40;
        int baseLength = 300;
        int finalLength = 75;

        double veticalSpeed = 0.000005;
        double horizontalSpeed = 0.0002;
        double verticalVelocity = 60;

        for (int i = 0; i < pillarLayers; i++) {  
                                    
            double heightRatio = ((pillarMidpointY - pillarPositionY[i][pillarBalls]) / pillarMidpointX);
            
            double currentSize = baseSize - heightRatio * (baseSize - finalSize);
            double currentVerlocity =  veticalSpeed * (heightRatio * verticalVelocity);

            if (heightRatio > 0){
                pillarPositionY[i][pillarBalls] -= veticalSpeed + currentVerlocity;
            }
            else{
                pillarPositionY[i][pillarBalls] -= veticalSpeed;
            }

            double leftBorder = (pillarMidpointX - baseLength / 2) + heightRatio * ((baseLength - finalLength) / 2);
            double rightBorder = (pillarMidpointX + baseLength / 2) - heightRatio * ((baseLength - finalLength) / 2);
            double layerLength = (rightBorder - leftBorder);

            double a = layerLength / 2;
            double b = layerHeight / 2;

            for (int j = 0; j < pillarBalls; j++) {                          
                    
                if(pillarPositionY[i][pillarBalls] <= pillarMidpointY){ 
                    
                    if(pillarPositionX[i][j] <= leftBorder){
                        pillarDirection[i][j] = 'R';
                    }
                    
                    else if(pillarPositionX[i][j] >= rightBorder){
                        pillarDirection[i][j] = 'L';
                    }
                    
                    if (pillarDirection[i][j] == 'L'){
                        pillarPositionX[i][j] -= horizontalSpeed;
                    }
                    
                    else if (pillarDirection[i][j] == 'R'){
                        pillarPositionX[i][j] += horizontalSpeed;
                    }
                    
                }
                
                pillarSize[i][j] = currentSize;
                double currentPositionY = (Math.sqrt(((a*a) - Math.pow(pillarPositionX[i][j] - pillarMidpointX, 2)) * (b*b) / (a*a))); //(a^2-(x-h)^2)(b^2)/(a^2)

                if (pillarDirection[i][j] == 'R'){

                    if (pillarPositionX[i][j] - leftBorder <= layerLength / 3 * 2 && pillarPositionX[i][j] - leftBorder >= layerLength / 3){
                        pillarSize[i][j] -= 3;
                    }
                    else{
                        pillarSize[i][j] -= 2;
                    }
                    pillarPositionY[i][j] = (pillarPositionY[i][pillarBalls]) - currentPositionY; 

                }

                else{

                    if (pillarPositionX[i][j] - leftBorder > layerLength / 3 * 2 || pillarPositionX[i][j] - leftBorder < layerLength / 3){
                        pillarSize[i][j] -= 1;
                    }
                    pillarPositionY[i][j] = (pillarPositionY[i][pillarBalls]) + currentPositionY; 

                }

            }

        }
    }

    private void updateDome(){

        int baseSize = 5;
        int layerHeight = 40;
        int baseLength = 75;
        int finalLength = 300;

        double domeHeight = domeEndpointY - domeMidpointY;
        double domeLenght = (finalLength - baseLength) / 2;
        double veticalSpeed = 0.0001;
        
        for (int i = 0; i < domeLayers; i++) {

            domePositionY[i][domeBalls] += veticalSpeed;

            double layerHalfLength;

                if(domeHeight > domeLenght){
                    layerHalfLength = (Math.sqrt(((domeHeight*domeHeight) - Math.pow(domePositionY[i][domeBalls] - domeEndpointY, 2)) * (domeLenght*domeLenght) / (domeHeight*domeHeight)));
                } //(a^2-(x-h)^2)(b^2)/(a^2)
                else {
                    layerHalfLength = (Math.sqrt(((domeLenght*domeLenght) - Math.pow(domePositionY[i][domeBalls] - domeEndpointY, 2)) * (domeHeight*domeHeight) / (domeLenght*domeLenght)));
                } //(b^2-(x-h)^2)(a^2)/(b^2)

            double leftBorder = domeMidpointX - layerHalfLength;
            double layerLength = layerHalfLength * 2;
            double a = layerHalfLength;
            double b = layerHeight / 2;

            for (int j = 0; j < domeBalls; j++) {

                domeSize[i][j] = baseSize;

                domePositionX[i][j] = (((double)j/(domeBalls-1)) * (layerLength)) + leftBorder;

                if(j < (domeBalls + 1) / 2) {
                    double currentPositionY = (Math.sqrt(((a*a) - Math.pow(domePositionX[i][j] - domeMidpointX, 2)) * (b*b) / (a*a))); //(a^2-(x-h)^2)(b^2)/(a^2)
                    domePositionY[i][j] = (domePositionY[i][domeBalls]) + currentPositionY;
                    domePositionY[i][domeBalls-(j+1)] = domePositionY[i][j];
                }
                
                if (domePositionX[i][j] - leftBorder > layerLength / 3 * 2 || domePositionX[i][j] - leftBorder < layerLength / 3){
                    domeSize[i][j] -= 1;
                }

            }

        }
    }

    //=============================================================================================================
    //=============================================================================================================

                                            // Paint Zone

    //=============================================================================================================
    //=============================================================================================================



    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        paintImage();
        Graphics2D g2 = (Graphics2D) g;
        g2.drawImage(buffer, 0, 0, this);

        //babyBuffer
        g2.translate(0, (int)chickenMove);
        g2.drawImage(babyBuffer, 0, 0, this);
        g2.translate(0, (int)-chickenMove);
    }
    
    //paint entire image on buffer
    private void paintImage() {
        Graphics2D g = buffer.createGraphics();
        if(currentStage != Stage.Text && !isDraw)
            drawBackground(g);
        if(currentStage == Stage.Evolve)
            fadeToBlack(g);
            drawEffect(g);
        if(isText){
            drawTextbox(g);
            drawText(g);
        }
        // if(currentStage == Stage.Show && !isDraw)
        //     drawBaby(g);
        if(currentStage == Stage.KFC)
            drawKFC(g);
    }

    private void drawBackground(Graphics2D g) {
        //clean screen
        g.setColor(new Color(0,0,0));
        g.fillRect(0, 0, 600, 450);

        //light green background
        g.setColor(new Color(242,254,236));
        g.fillRect(0, 0, 600, 450);

        //color lines background
        for (int i = 0; i < 40; i++) {
            Color color;
            //switch-case for 4 color line on top screen
            switch (i) {
                case 0:
                    color = new Color(183,222,241);
                    break;
                case 1:
                    color = new Color(195,232,228);
                    break;
                case 2:
                    color = new Color(209,241,222);
                    break;
                case 3:
                    color = new Color(221,249,215);
                    break;
                default:
                    color = new Color(237,255,212);
                    break;
            }
            g.setColor(color);
            g.fillRect(0, i*15, 600, 12);
        }
    }

    private void fadeToBlack(Graphics2D g) {
        g.setColor(new Color(0,0,0,(int)tranparency));
        g.fillRect(0, 0, 600, 450);
    }
    
    //draw text box on bottom part of screen
    private void drawTextbox(Graphics2D g) {
        g.setColor(new Color(62,57,70));
        g.fillRect(0, 450, 600, 150);

        g.setColor(new Color(150,58,54));
        g.fillRoundRect(10, 455, 580, 140, 10, 10);

        g.setColor(new Color(93, 138, 147));
        g.fillRoundRect(20, 460, 560, 130, 10, 10);
    }

    private void drawText(Graphics2D g) {
        String text1 = "";
        String text2 = "";
        for (int i = 0; i < letter1 && i < line1Text.length(); i++) 
            text1 += line1Text.charAt(i);
        for (int i = 0; i < letter2 && i < line2Text.length(); i++){
            if(line2Text.charAt(i) == ' ')
                text2 += line2Text.charAt(i);
            text2 += line2Text.charAt(i);
        }
        g.setFont(font);

        //draw Shadow of Text
        g.setColor(new Color(86, 73, 84));
        g.drawString(text1, 38, 514);
        g.drawString(text2, 38, 564);

        //draw Real Text for reading
        g.setColor(new Color(200, 194, 205));
        g.drawString(text1, 35, 510);
        g.drawString(text2, 35, 560);
    }
    
    private void drawEffect(Graphics2D g) {    
        if(pillarPositionY[pillarLayers-1][pillarBalls] >= pillarEndpointY && pillarPositionY[0][pillarBalls] < pillarMidpointY){
            drawPillar(g);
        }
        if(domePositionY[domeLayers-1][domeBalls] <= domeEndpointY && domePositionY[0][domeBalls] > domeMidpointY){
            drawDome(g);
        }
    }

    private void drawPillar(Graphics2D g) {
        g.setColor(new Color(255,255,255));
        for (int i = 0; i < pillarLayers; i++) {   
            for (int j = 0; j < pillarBalls; j++) {
                if (pillarPositionY[i][pillarBalls] < pillarMidpointY && pillarPositionY[i][pillarBalls] >= pillarEndpointY){
                    drawCircle(g, (int)pillarPositionX[i][j], (int)pillarPositionY[i][j], (int)pillarSize[i][j]);
                    if ((int)pillarSize[i][j] > 1) {   
                        floodFillBorder(g, (int)pillarPositionX[i][j], (int)pillarPositionY[i][j], new Color[]{new Color (255,255,255)}, new Color(255,255,255), buffer);
                    }
                }
            }
        }
    }

    private void drawDome(Graphics2D g) {
        g.setColor(new Color(255,255,255));
        for (int i = 0; i < domeLayers; i++) {   
            for (int j = 0; j < domeBalls; j++) {
                if (domePositionY[i][domeBalls] > domeMidpointY && domePositionY[i][domeBalls] <= domeEndpointY){
                    drawCircle(g, (int)domePositionX[i][j], (int)domePositionY[i][j], (int)domeSize[i][j]);
                    if ((int)domeSize[i][j] > 1) {   
                        floodFillBorder(g, (int)domePositionX[i][j], (int)domePositionY[i][j], new Color[]{new Color (255,255,255)}, new Color(255,255,255), buffer);
                    }
                }
            }
        }
    }

    private void drawBaby(Graphics2D g) {
        
    }

    private void drawKFC(Graphics2D g) {
        //หงอน
        g.setColor(Color.BLACK);
        // //left side
        // drawCurve(g, 220, 245, 220, 245, 240, 360, 240, 360);
        // //bottom
        // drawCurve(g, 240, 360, 265, 370, 345, 370, 360, 360);
        // //right side
        // drawCurve(g, 380, 245, 380, 245, 360, 360, 360, 360);
        // //top
        // drawCurve(g, 220, 245, 260, 255, 340, 255, 380, 245);
        // //chicken
        // drawCurve(g, 220, 245, 218, 226, 236, 215, 251, 223);
        // drawCurve(g, 251, 223, 263, 228, 265, 249, 265, 249);
        // drawCurve(g, 265, 249, 263, 259, 267, 264, 275, 267);
        // drawCurve(g, 275, 267, 285, 270, 279, 280, 270, 277);
        // drawCurve(g, 275, 267, 285, 270, 279, 280, 270, 277);
        // drawCurve(g, 54, 23, 54, 23, 61, 29, 62, 39);
        // drawCurve(g, 60, 46, 60, 46, 65, 17, 96, 1);
        // drawCurve(g, 96, 1, 101, 8, 90, 42, 90, 42);
        // drawCurve(g, 90, 42, 90, 42, 102, 32, 118, 30);
        // drawCurve(g, 118, 30, 118, 30, 109, 51, 78, 61);
        // //right face
        // g.setColor(Color.yellow.darker());
        // drawCurve(g, 62, 54, 62, 54, 65, 43, 73, 39);
        // drawCurve(g, 73, 39, 73, 39, 75, 42, 71, 55);
        // drawCurve(g, 71, 55, 71, 55, 75, 52, 78, 53);
        // drawCurve(g, 78, 53, 78, 53, 78, 55, 75, 60);
        // drawCurve(g, 75, 60, 110, 73, 106, 113, 69, 122);
        // drawLine(g, 99, 0 ,99, 200);
        // //left face
        // drawCurve(g, 62, 54, 40, 51, 15, 60, 4, 81);
        //     //left eye
        // midpointElispe(g, 5, 85, 3, 4);
        // drawLine(g, 4, 68 ,46, 48);
    }
    

    //=============================================================================================================
    //=============================================================================================================

                                            // Tool Zone

    //=============================================================================================================
    //=============================================================================================================


    //floodfill area seed at x,y to fill targetColor nearby with fillColor
    private void floodFill(Graphics g, int x, int y, Color targetColor, Color fillColor) {
        int targetRGB = 0;
        if(targetColor != null){
            targetRGB = targetColor.getRGB();
        }
        if (buffer.getRGB(x, y) == targetRGB) {
            Queue<Point> queue = new LinkedList<>();
            queue.add(new Point(x, y));

            while (!queue.isEmpty()) {
                Point point = queue.poll();
                x = (int) point.getX();
                y = (int) point.getY();

                if (buffer.getRGB(x, y) == targetRGB) {
                    g.setColor(fillColor);
                    plot(g, x, y);

                    // Enqueue adjacent pixels
                    if (x - 1 >= 0) queue.add(new Point(x - 1, y));
                    if (x + 1 < 600) queue.add(new Point(x + 1, y));
                    if (y - 1 >= 0) queue.add(new Point(x, y - 1));
                    if (y + 1 < 600) queue.add(new Point(x, y + 1));
                }
            }
        }
    }

    //floodfill area seed at x,y to fill any color nearby that is not borderColor with fillColor 
    private void floodFillBorder(Graphics g,int x, int y, Color[] borderColor, Color fillColor, BufferedImage buffer) {
        int[] borderRGB;
        if(borderColor != null){
            borderRGB = new int[borderColor.length];
            for (int i = 0; i < borderRGB.length; i++) {
                borderRGB[i] = borderColor[i].getRGB();
            }
        }
        else{
            borderRGB = new int[] {-1};
        }

        if (!isIn(buffer.getRGB(x, y), borderRGB, fillColor.getRGB())) {
            Queue<Point> queue = new LinkedList<>();
            queue.add(new Point(x, y));

            while (!queue.isEmpty()) {
                Point point = queue.poll();
                x = (int) point.getX();
                y = (int) point.getY();

                if (!isIn(buffer.getRGB(x, y), borderRGB, fillColor.getRGB())) {
                    g.setColor(fillColor);
                    plot(g, x, y);

                    // Enqueue adjacent pixels
                    if (x - 1 >= 0) queue.add(new Point(x - 1, y));
                    if (x + 1 < 600) queue.add(new Point(x + 1, y));
                    if (y - 1 >= 0) queue.add(new Point(x, y - 1));
                    if (y + 1 < 600) queue.add(new Point(x, y + 1));
                }
            }
        }
    }

    //check if value is the element of array
    private boolean isIn(int color, int[] borderColor, int fillColor){
        for (int i : borderColor) {
            if(color == i || color == 0 || color == fillColor)
                return true;
        }
        return false;
    }

    //fill rectangle by drawing each line with different color to make gradient effect
    private void gradientFill(Graphics g, int x1, int y1, int x2, int y2, Color startColor, Color endColor) {      
        int startR = startColor.getRed();   
        int startG = startColor.getGreen(); 
        int startB = startColor.getBlue();
    
        int endR = endColor.getRed();     
        int endG  = endColor.getGreen();   
        int endB  = endColor.getBlue();
    
        int range = y2 - y1;
        for (int y = y1; y <= y2; y++) {
            int R = interpolateColor(startR, endR, range, y - y1);
            int G = interpolateColor(startG, endG, range, y - y1);
            int B = interpolateColor(startB, endB, range, y - y1);
            g.setColor(new Color(R, G, B));
            drawLine(g, x1, y, x2, y);
        }
        
    }
    
    //find Color value that are between start and end 
    private int interpolateColor(int start, int end, int range, int position) {
        return clampRGB(start + position * (end - start) / range);
    }
    
    //clamp value to make it in rgb value rangee (0-255)
    private int clampRGB(int value) {
        //if value < 0 calmp to 0
        //if value > 255 clamp to 255
        return Math.max(0, Math.min(value, 255));
    }

    //draw half circle with bezier curve
    private void drawHalfCircle(Graphics2D g, int x, int y, int r,String d) {
        if(d == "L"){
            drawCurve(g, x-r, y, x-r, (int)(y - (0.552 * r)), (int)(x - (0.552 * r)), y-r, x, y-r);
            drawCurve(g, x-r, y, x-r, (int)(y - (0.552 *-r)), (int)(x - (0.552 * r)), y+r, x, y+r);
        }
        else if(d == "R"){
            drawCurve(g, x+r, y, x+r, (int)(y - (0.552 * r)), (int)(x - (0.552 *-r)), y-r, x, y-r);
            drawCurve(g, x+r, y, x+r, (int)(y - (0.552 *-r)), (int)(x - (0.552 *-r)), y+r, x, y+r);
        }
    }

    //draw a triangle with java fillPolygon
    private void fillTriangle(Graphics g, int x1, int y1, int x2, int y2, int x3, int y3){
        g.fillPolygon(new int[]{x1,x2,x3}, new int[]{y1,y2,y3}, 3);
    }
    
    //draw curve line with bezier's curve algorithm
    private void drawCurve(Graphics2D g, int x1,int y1,int x2,int y2, int x3,int y3, int x4,int y4){
        float sampleAmnt = 10000;
        for (int i = 0; i < sampleAmnt; i++) {
            float t = i/sampleAmnt;
            int x = (int)(Math.pow((1-t), 3)*x1 + 
                    3*t*Math.pow((1-t), 2)*x2 +
                    3*t*t*(1-t)*x3+
                    t*t*t*x4);
            int y = (int)(Math.pow((1-t), 3)*y1 + 
                    3*t*Math.pow((1-t), 2)*y2 +
                    3*t*t*(1-t)*y3+
                    t*t*t*y4);
            plot(g, x, y);
        }
    }

    //draw straight line with bresenham's algorithm
    private void drawLine(Graphics g, int x1, int y1, int x2, int y2){
        int dx = Math.abs(x2 - x1);
        int dy = Math.abs(y2 - y1);
        int sx = (x1 < x2) ? 1 : -1;
        int sy = (y1 < y2) ? 1 : -1;
        boolean isSwap = false;
        if(dy > dx)
        {
            int temp = dx;
            dx = dy;
            dy = temp;
            isSwap = true;
        }
        int D = 2 * dy - dx;
        int x = x1;
        int y = y1;
        for (int i = 1; i <= dx; i++){
            plot(g, x, y);
            if (D >= 0)
            {
                if (isSwap) 
                    x += sx;
                else 
                    y += sy;
                D -= 2 * dx;
            }
            if (isSwap) 
                y += sy;
            else 
                x += sx;
            D += 2 * dy;
        }
    }

     private void drawCircle(Graphics g, int xc, int yc, int r) {
        int x = 0;
        int y = r;
        int Dx = 2 * x;
        int Dy = 2 * y;
        int D = 1 - r;
        while(x <= y) {
            //plot(g, x+xc, y+yc);
            plotOctant(g, x, y, xc, yc);
            x++;
            Dx += 2;
            D += Dx + 1;
            if (D >= 0){
                y--;
                Dy-= 2;
                D -= Dy;
            }
        }
    }

    private void drawElipse(Graphics g, int xc, int yc, int a, int b) {
        int a2 = a*a, b2 = b*b;
        int twoA2 = 2*a2, twoB2 = 2*b2;

        //REGION 1
        int x = 0;
        int y = b;
        int D = Math.round(b2 - a2*b + a2/4);
        int Dx = 0, Dy = twoA2*y;

        while(Dx <= Dy){
            //plot(g, x+xc, y+yc);
            plotQuadrant(g, x, y, xc, yc);
            x++;
            Dx += twoB2;
            D += Dx + b2;
            if (D >= 0){
                y--;
                Dy -= twoA2;
                D -= Dy;
            }
        }

        //REGION 2
        x = a;
        y = 0;
        D = Math.round(a2 - b2*a + b2/4);
        Dx = twoB2*x; 
        Dy = 0;

        while(Dx >= Dy){
            //plot(g, x+xc, y+yc);
            plotQuadrant(g, x, y, xc, yc);
            y--;
            Dy += twoA2;
            D += Dy + a2;
            if (D >= 0){
                x--;
                Dx -= twoB2;
                D -= Dx;
            }
        }
    }

    private void plotQuadrant(Graphics g, int x, int y, int xc, int yc){
        plot(g, x+xc, y+yc);
        plot(g, x+xc, -y+yc);
        plot(g, -x+xc, y+yc);
        plot(g, -x+xc, -y+yc);
    }

    private void plotOctant(Graphics g, int x, int y, int xc, int yc) {
        plot(g, x+xc, y+yc);
        plot(g, x+xc, -y+yc);
        plot(g, -x+xc, y+yc);
        plot(g, -x+xc, -y+yc);
        plot(g, y+xc, x+yc);
        plot(g, y+xc, -x+yc);
        plot(g, -y+xc, x+yc);
        plot(g, -y+xc, -x+yc);
    }

    private void plot(Graphics g, int x, int y) {
        g.fillRect(x, y, 1, 1);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        System.out.println("Click at "+ e.getX() +", "+e.getY());
    }
    @Override
    public void mousePressed(MouseEvent e) {
    }
    @Override
    public void mouseReleased(MouseEvent e) {
    }
    @Override
    public void mouseEntered(MouseEvent e) {
    }
    @Override
    public void mouseExited(MouseEvent e) {
    }
}

//Color Palette for drawing in this assignment
enum Palette {
    //HEX Color Code without prefix "#" only number and letter
    BLACK("09040b"),
    BODY("f1a044"),
    HARDPART("f5e997"),
    HARDPART_OUTLINE("09040b"),
    FEATHER("fad457"),
    FEATHER_OUTLINE("09040b");

    private final Color color;

    Palette(String colorCode) {
        this.color = new Color(Integer.parseInt(colorCode, 16));
    }

    public Color getColor() {
        return color;
    }
}

enum Stage {
    Show,
    Text, 
    Evolve,
    KFC;

    // private final Color color;

    // // Stage(String colorCode) {
    // //     this.color = new Color(Integer.parseInt(colorCode, 16));
    // // }

    // public Color getColor() {
    //     return color;
    // }
}

