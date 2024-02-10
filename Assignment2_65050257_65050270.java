import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class Assignment2_65050257_65050270 extends JPanel implements Runnable{
    //Buffers
    BufferedImage mainBuffer = new BufferedImage(600, 600, BufferedImage.TYPE_INT_ARGB);
    BufferedImage effectBuffer = new BufferedImage(600, 600, BufferedImage.TYPE_INT_ARGB);
    BufferedImage babyBuffer = new BufferedImage(600, 600, BufferedImage.TYPE_INT_ARGB);
    BufferedImage KFCBuffer = new BufferedImage(600, 600, BufferedImage.TYPE_INT_ARGB);
    BufferedImage textBoxBuffer = new BufferedImage(600, 600, BufferedImage.TYPE_INT_ARGB);

    //main method
    public static void main(String[] args) {
        Assignment2_65050257_65050270 m = new Assignment2_65050257_65050270();
        JFrame f = new JFrame();
        f.add(m);
        f.setTitle("Animation");
        m.setPreferredSize(new Dimension(600, 600));
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.pack();
        f.setVisible(true);
        f.setLocationRelativeTo(null);
        f.setResizable(false);
        (new Thread(m)).start();
    }

    // =============================================================================
    // =============================================================================
    //                             animation variable
    // =============================================================================
    // =============================================================================

    double lastTime;
    double currentTime, elapsedTime, startTime;
    double letterVelocity;
    
    Font font;
    Stage currentStage;
    boolean isDraw;
    boolean isText;
    boolean isEvolving;
    boolean isKFC;
    double whitenOpacity;
    
    double lineCnt[] = new double[4];
    String lineText[] = new String[4];
    
    //------------------------------------------------------------------------------
    //                                 Background
    //------------------------------------------------------------------------------
    
    double tranparency;
    boolean isBlack;
    boolean isClear;

    int starLayers;
    int starPoints;
    int starMidpointX;
    int starMidpointY;
    int starColorSwitch;
    double starOffsetX;
    double starOffsetY;
    double[] starColorStatus;
    double[][] starPositionX;
    double[][] starPositionY;
    boolean isStarStart;
    boolean isStarDone;

    //------------------------------------------------------------------------------
    //                                 Effects
    //------------------------------------------------------------------------------

    int spiralLayers;
    int spiralBalls;
    int spiralMidpointX;
    int spiralMidpointY;
    int spiralEndpointY;
    double[][] spiralSize;
    double[][] spiralPositionX;
    double[][] spiralPositionY;
    char[][] spiralDirection;
    boolean isSpiralDone;

    int domeLayers;
    int domeBalls;
    int domeMidpointX;
    int domeMidpointY;
    int domeEndpointY;
    double[][] domeSize;
    double[][] domePositionX;
    double[][] domePositionY;
    boolean isDomeDone;

    int ringLayers;
    int ringBalls;
    int ringMidpointX;
    int ringMidpointY;
    int ringBaseRadius;
    int ringFinalRadius;
    double[][] ringSize;
    double[][] ringPositionX;
    double[][] ringPositionY;
    double[][] ringAngle;
    boolean isRingDone;

    int fountainBalls;
    int fountainBallsMinSize;
    int fountainBallsMaxSize;
    int fountainMidpointX;
    int fountainMidpointY;
    char[] fountainDirection;
    double[] fountainSize;
    double[] fountainPositionX;
    double[] fountainPositionY;
    double[] fountainArchLength;
    double[] fountainArchHeight;
    boolean isFountainDone;

    double flashbangTranparency;
    boolean isFlashbangWhite;
    boolean isFlashbangDone;

    //------------------------------------------------------------------------------
    //                                Chicken
    //------------------------------------------------------------------------------

    double chickenMove;
    double chickenVelocity;
    double chickenScale;
    double chickenScaleVelocity;
    double chickenScaleAccelerate;
    double KFCScale;
    double KFCScaleVelocity;
    double KFCScaleAccelerate;
    double timer;

    // =============================================================================
    // =============================================================================
    //                             Animation Thread
    // =============================================================================
    // =============================================================================


    //running animation thread
    public void run() {
        initializeAnimationVar();
        
        initializeStar();
        initializeSpiral();
        initializeDome();
        initializeRing();
        initializeFountain();

        initializeBabyBuffer();
        drawKFC(true);
        
        while (true) {
            //Calculate animation
            updateAnimation();
            //Display
            repaint();
        }
    }

    private void updateAnimation() {
            currentTime = System.currentTimeMillis();
            elapsedTime = currentTime - lastTime;
            lastTime = currentTime;
            timer = (currentTime-startTime)/1000.0;   //timer since start running the animation in Second Unit
            //0 - 3 second
            if(timer <= 3){
                //do nothing
            }
            //make baby chick jump for 3 second
            else if(timer <= 6 || chickenMove > 0.01){
                //Jumping Chick
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
            }
            //display message first line until finish
            else if(lineCnt[0] < lineText[0].length()){
                //Display Message line 1
                isText = true;
                currentStage = Stage.Text;
                lineCnt[0] += letterVelocity * elapsedTime / 1000.0;
            }
            //display message second line until finish
            else if(lineCnt[1] < lineText[1].length()){
                //Display Message line 2
                isText = true;
                currentStage = Stage.Text;
                lineCnt[1] += letterVelocity * elapsedTime / 1000.0;
            }
            //dark screen transition
            else if(!isBlack){
                isText = false;
                currentStage = Stage.Evolve;
                updateTransparencyToBlack();
            }
            //Moving each balls in the layer of the spiral , start Making chicken white , start Moving and changing color of background stars
            else if(!isSpiralDone){ 
                isStarStart = true;
                currentStage = Stage.Evolve;
                //Moving each balls in the layer of the spiral
                if(spiralPositionY[spiralLayers-1][spiralBalls] >= spiralEndpointY){
                    updateSpiral();
                }
                else{
                    isSpiralDone = true;
                }
                //Move and change color of background stars
                if(isStarStart == true && starColorSwitch > 0){
                    updateStarMovement();
                    updateStarColor();
                }
                //Make chicken white
                whitenOpacity += 100 * elapsedTime / 1000.0;
            }
            //Moving each balls in the layer of the dome and Make chicken white
            else if(!isDomeDone){
                currentStage = Stage.Evolve;
                //Moving each balls in the layer of the dome
                if (domePositionY[domeLayers-1][domeBalls] <= domeEndpointY){
                    updateDome();
                }
                else{
                    isDomeDone = true;
                }
                //Move and change color of background stars
                if(isStarStart == true && starColorSwitch > 0){
                    updateStarMovement();
                    updateStarColor();
                }
                //Make chicken white
                whitenOpacity += 100 * elapsedTime / 1000.0;
            }
            //Scaling Chicken and KFC Bucket inversely
            else if(!isStarDone || chickenScale > 0.1){
                //Scaling Chicken and KFC Bucket inversely
                currentStage = Stage.Evolve;
                updateChickenScale(elapsedTime);
                updateKFCScale(elapsedTime);
                //Move and change color of background stars
                if(isStarStart == true){
                    updateStarMovement();
                    updateStarColor();
                }
            }
            //moving ring effect
            else if(!isRingDone){
                currentStage = Stage.Evolve;
                if (ringPositionY[ringLayers-1][ringBalls] <= ringMidpointY - ringFinalRadius){ //Moving each balls in the layer of the ring
                    updateRing();
                }
                else{
                    isRingDone = true;
                }
                if(isStarStart == true && starColorSwitch > 0){
                    updateStarMovement();
                    updateStarColor();
                }
            }
            //flashbang flash and disappear
            else if(!isFlashbangDone){
                //draw KFC Bucket with coloring
                currentStage = Stage.Evolve;
                updateFlashbangTransparency();
                if(isEvolving && isFlashbangWhite){
                    isKFC = true;
                }
                if(isKFC){
                    isEvolving = false;
                    drawKFC(false);
                }
            }
            //fountain appear
            else if(!isFountainDone){
                currentStage = Stage.Evolve;
                if (fountainSize[fountainBalls] > 0){ //Moving each balls in the layer of the ring
                    updateFountain();
                }
                else{
                    isFountainDone = true;
                }
            }
            else if(!isClear){//dark screen transition at the 4th second
                currentStage = Stage.Evolve;
                updateTransparencyToClear();
            }
            //display message first line until finish
            else if(lineCnt[2] < lineText[2].length()){
                //Display Message line 3
                isText = false;
                isText = true;
                currentStage = Stage.Text;
                lineCnt[2] += letterVelocity * elapsedTime / 1000.0;
            }
            //display message second line until finish
            else if(lineCnt[3] < lineText[3].length()){
                //Display Message line 4
                currentStage = Stage.Text;
                lineCnt[3] += letterVelocity * elapsedTime / 1000.0;
            }
    }

    //initialize value of global variable for animation
    private void initializeAnimationVar() {
        font = new Font("Segoe UI", Font.PLAIN, 36);

        lastTime = System.currentTimeMillis();
        letterVelocity = 12;
        startTime = lastTime;

        currentStage = Stage.Show;
        isDraw = false;
        isText = true;
        isKFC = false;
        isEvolving = true;
        whitenOpacity = 20;

        lineCnt[0] = 0;
        lineCnt[1] = 0;
        lineCnt[2] = 0;
        lineCnt[3] = 0;
        lineText[0] = "What?";
        lineText[1] = "BABY CHICK is evolving!";
        lineText[2] = "Congratulations! BABY CHICK";
        lineText[3] = "evolved into CHUD-JUJAI!";

        chickenMove = 0;
        chickenVelocity = -100;
        chickenScale = 1;
        chickenScaleVelocity = 2;
        chickenScaleAccelerate = 0.7;
        KFCScale = 0;
        KFCScaleVelocity = 2;
        KFCScaleAccelerate = 0.7;
        timer = 0;

        tranparency = 0;
        isBlack = false;
        isClear = false;

        starLayers = 10;
        starPoints = 8;
        starMidpointX = 300;
        starMidpointY = 225;
        starColorSwitch = 6;
        starOffsetX = 0;
        starOffsetY = 10;
        starColorStatus = new double[starLayers];
        starPositionX = new double[starLayers][starPoints*2];
        starPositionY = new double[starLayers][starPoints*2];
        isStarStart = false;
        isStarDone = false;
    
        spiralLayers = 10;
        spiralBalls = 4;
        spiralMidpointX = 300;
        spiralMidpointY = 360;
        spiralEndpointY = 0;
        spiralSize = new double[spiralLayers][spiralBalls];
        spiralPositionX = new double[spiralLayers][spiralBalls];
        spiralPositionY = new double[spiralLayers][spiralBalls+1];
        spiralDirection = new char[spiralLayers][spiralBalls];
        isSpiralDone = false;

        domeLayers = 6;
        domeBalls = 8;
        domeMidpointX = 300;
        domeMidpointY = 50;
        domeEndpointY = 360;
        domeSize = new double[domeLayers][domeBalls];
        domePositionX = new double[domeLayers][domeBalls];
        domePositionY = new double[domeLayers][domeBalls+1];
        isDomeDone = false;

        ringLayers = 2;
        ringBalls = 16;
        ringMidpointX = 300;
        ringMidpointY = 225;
        ringBaseRadius = 400;
        ringFinalRadius = 25;
        ringSize = new double[ringLayers][ringBalls];
        ringPositionX = new double[ringLayers][ringBalls];
        ringPositionY = new double[ringLayers][ringBalls+1];
        ringAngle = new double[ringLayers][ringBalls];
        isRingDone = false;
    
        fountainBalls = 40;
        fountainBallsMinSize = 8;
        fountainBallsMaxSize = 12;
        fountainMidpointX = 300;
        fountainMidpointY = 225;
        fountainDirection = new char[fountainBalls+1];
        fountainSize = new double[fountainBalls+1];
        fountainPositionX = new double[fountainBalls+1];
        fountainPositionY = new double[fountainBalls+1];
        fountainArchLength = new double[fountainBalls+1];
        fountainArchHeight = new double[fountainBalls+1];
        isFountainDone = false;

        flashbangTranparency = 0;
        isFlashbangWhite = false;
        isFlashbangDone = false;
    }

    //initialize value for position for each star 
    private void initializeStar() {
        double layerGaps = 0;
        double starBaseInnerRadius = 11.5;
        double starBaseOuterRadius = 15;
        double[] starAngle = new double[starPoints*2];

        for (int i = 0; i < starLayers; i++) {
            starColorStatus[i] = 0;
            for (int j = 0; j < starPoints*2; j++) {  

                starAngle[j] = (360 / ((double)starPoints * 2)) * j;

                if(j % 2 == 0){
                    starPositionX[i][j] = starMidpointX - ((starBaseOuterRadius * layerGaps) * Math.cos(Math.PI * 2 * starAngle[j] / 360));
                    starPositionY[i][j] = starMidpointY - ((starBaseOuterRadius * layerGaps) * Math.sin(Math.PI * 2 * starAngle[j] / 360));
                }
                else{
                    starPositionX[i][j] = starMidpointX - ((starBaseInnerRadius * layerGaps) * Math.cos(Math.PI * 2 * starAngle[j] / 360));
                    starPositionY[i][j] = starMidpointY - ((starBaseInnerRadius * layerGaps) * Math.sin(Math.PI * 2 * starAngle[j] / 360));
                }
            }
            layerGaps += 3.2;
        }
    }

    //initialize position and direction of balls for spiral
    private void initializeSpiral() {

        //Set a horizontal position of each balls in the layer of the spiral
        for (double[] px : spiralPositionX) {
            px[3] = 150;            //4th ball of each layer at x = 150
            px[0] = px[2] = 300;    //1st ball and 3rd ball of each layer at x = 300
            px[1] = 450;            //2nd ball of each layer at x = 450
        }

        //Set a vertical position of each layer of the spiral
        int layerGaps = 0;
        for (double[] py : spiralPositionY) {
            py[spiralBalls] = spiralMidpointY + layerGaps;
            layerGaps += 2;
        }

        //Set a horizontal direction of each balls in the layer of the spiral
        for (char[] d : spiralDirection) {
            d[0] = d[1] = 'L'; //1st ball and 2st ball of each layer move to the left
            d[2] = d[3] = 'R'; //3rd ball and 4th ball of each layer move to the right
        }
    }

    //initialize position of each layer of the dome
    private void initializeDome() {

        //Set a vertical position of each layer of the dome
        int layerGaps = 0;
        for (double[] py : domePositionY) {
            py[domeBalls] = domeMidpointY - layerGaps;
            layerGaps += 30;
        }
    }

    //initialize position of each layer of the ring
    private void initializeRing() {
     
        //Set a vertical position of each layer of the spiral
        int layerGaps = 0;
        for (double[] py : ringPositionY) {
            py[ringBalls] = (ringMidpointY - ringBaseRadius) - layerGaps;
            layerGaps += 400;
        } 
        
        //Set an angle, a vertical position and a horizontal position of each balls in the layer of the ring
        for (int i = 0; i < ringLayers; i++) { 
            double radius = ringMidpointY - ringPositionY[i][ringBalls];
            for (int j = 0; j < ringBalls; j++) { 
                ringAngle[i][j] = (360 / (double)ringBalls) * j;
                ringPositionX[i][j] = ringMidpointX - (radius * Math.cos(Math.PI * 2 * ringAngle[i][j] / 360));
                ringPositionY[i][j] = ringMidpointY - (radius * Math.sin(Math.PI * 2 * ringAngle[i][j] / 360));
            }
        }
    }

    //initialize position balls in fountain
    private void initializeFountain() {

        Random randomNumber = new Random();

        int archMinLength = 0;
        int archMaxLength = 125;
        int archMinHeight = 150;
        int archMaxHeight = 200;
        int archMaxGaps = 50;

        for (int i = 0; i < fountainBalls; i++) {
            fountainSize[i] = fountainBallsMinSize + (fountainBallsMaxSize - fountainBallsMinSize) * randomNumber.nextDouble();
            fountainArchHeight[i] = archMinHeight + randomNumber.nextInt(archMaxHeight - archMinHeight - 1);
            fountainArchLength[i] = (archMinLength - randomNumber.nextInt(archMaxLength - archMinLength - 1)) * (Math.pow((-1) , randomNumber.nextInt(2)));
            fountainPositionY[i] = fountainMidpointY + randomNumber.nextInt(archMaxGaps + 1);
            fountainDirection[i] = 'U';
        }

        fountainSize[fountainBalls] = fountainBallsMaxSize + 1;
        fountainArchHeight[fountainBalls] = archMaxHeight;
        fountainArchLength[fountainBalls] = archMaxLength;
        fountainPositionY[fountainBalls] = fountainMidpointY + archMaxGaps;
        fountainDirection[fountainBalls] = 'U';
        
    }

    //draw baby chicken on babyBuffer
    private void initializeBabyBuffer() {
        Graphics2D g = babyBuffer.createGraphics();
        
        g.setColor(new Color(255,255,255,1));
        g.fillRect(0, 0, 600, 600);
        
        //right face
        g.setColor(Palette.BLACK.getColor());
        drawCurve(g, 312, 204, 312, 204, 315, 193, 323, 189);
        drawCurve(g, 323, 189, 323, 189, 325, 192, 321, 205);
        drawCurve(g, 321, 205, 321, 205, 325, 202, 328, 203);
        drawCurve(g, 328, 203, 328, 203, 328, 205, 325, 210);
        drawCurve(g, 325, 210, 360, 223, 356, 263, 319, 272);

        //left face
        drawCurve(g, 312, 204, 290, 201, 265, 210, 254, 228);

        //left eye
        drawElipse(g, 255, 236, 4, 10);
        drawElipse(g, 255, 232, 4, 6);

        //right eye
        drawElipse(g, 305, 244, 8, 9);
        drawElipse(g, 305, 241, 5, 6);

        //left face
        drawCurve(g, 256, 248, 256, 248, 263, 264, 277, 268);

        //bottom
        drawCurve(g, 270, 286, 275, 323, 320, 323, 337, 295);
        drawCurve(g, 337, 295, 337, 295, 331, 288, 331, 288);
        g.setColor(Palette.HARDPART.getColor());

        //beak
        drawCurve(g, 260, 251, 267, 255, 280, 256, 287, 252);
        drawCurve(g, 287, 252, 286, 248, 277, 247, 275, 243);
        drawCurve(g, 275, 243, 273, 240, 269, 240, 266, 243);
        drawCurve(g, 266, 243, 261, 245, 258, 248, 260, 251);
        g.setColor(Palette.BLACK.getColor());

        //left feet?
        drawCurve(g, 294, 314, 294, 319, 291, 322, 291, 322);
        drawCurve(g, 291, 322, 276, 319, 271, 322, 268, 328);
        drawCurve(g, 268, 328, 266, 334, 267, 336, 271, 337);
        drawCurve(g, 271, 337, 276, 329, 283, 329, 283, 329);

        drawCurve(g, 290, 325, 280, 327, 277, 336, 285, 343);
        drawCurve(g, 285, 343, 286, 334, 288, 333, 292, 333);

        drawCurve(g, 297, 327, 290, 330, 288, 339, 296, 345);
        drawCurve(g, 296, 345, 299, 346, 300, 345, 299, 337);
        drawCurve(g, 299, 337, 301, 331, 304, 329, 309, 332);
        drawCurve(g, 309, 332, 312, 333, 314, 331, 312, 329);
        drawCurve(g, 312, 329, 311, 325, 308, 323, 302, 324);
        drawCurve(g, 302, 324, 298, 324, 297, 322, 300, 315);

        //right feet?
        drawCurve(g, 303, 314, 305, 315, 302, 322, 310, 322);
        drawCurve(g, 308, 315, 310, 323, 310, 326, 320, 321);
        drawCurve(g, 315, 314, 316, 319, 319, 321, 325, 320);
        drawCurve(g, 324, 320, 330, 315, 329, 312, 326, 313);
        drawCurve(g, 326, 313, 321, 313, 321, 313, 321, 311);

        g.setColor(Palette.BLACK.getColor());
        //Comb
        drawCurve(g, 303, 203, 296, 192, 304, 173, 304, 173);
        drawCurve(g, 304, 173, 304, 173, 311, 179, 312, 189);
        drawCurve(g, 310, 196, 310, 196, 315, 167, 346, 151);
        drawCurve(g, 346, 151, 351, 158, 340, 192, 340, 192);
        drawCurve(g, 340, 192, 340, 192, 352, 182, 368, 180);
        drawCurve(g, 368, 180, 368, 180, 359, 201, 328, 211);

        //left wing?
        drawCurve(g, 277, 268, 268, 268, 259, 275, 259, 275);
        drawCurve(g, 259, 275, 259, 275, 265, 275, 265, 275);
        drawCurve(g, 265, 275, 265, 275, 261, 281, 261, 281);
        drawCurve(g, 261, 281, 261, 281, 267, 281, 267, 281);
        drawCurve(g, 267, 281, 267, 281, 265, 288, 265, 288);
        drawCurve(g, 265, 288, 265, 288, 271, 286, 271, 286);
        drawCurve(g, 271, 286, 271, 286, 274, 291, 274, 291);
        drawCurve(g, 274, 291, 274, 291, 281, 280, 281, 280);
        drawCurve(g, 274, 291, 274, 291, 281, 280, 281, 280);

        //right wing?
        drawCurve(g, 296, 283, 296, 283, 301, 295, 301, 295);
        drawCurve(g, 301, 295, 301, 295, 303, 288, 303, 288);
        drawCurve(g, 303, 288, 303, 288, 312, 299, 312, 299);
        drawCurve(g, 312, 299, 312, 299, 312, 292, 312, 292);
        drawCurve(g, 312, 292, 312, 292, 323, 299, 323, 299);
        drawCurve(g, 323, 299, 323, 299, 323, 290, 323, 290);
        drawCurve(g, 323, 290, 325, 293, 331, 292, 331, 292);
        drawCurve(g, 331, 292, 331, 292, 329, 286, 329, 286);
        drawCurve(g, 331, 288, 331, 288, 339, 287, 339, 287);
        drawCurve(g, 339, 287, 339, 287, 333, 278, 333, 278);
        drawCurve(g, 333, 278, 333, 278, 337, 275, 337, 275);
        drawCurve(g, 337, 275, 337, 275, 326, 270, 326, 270);

        g.setColor(Palette.FEATHER.getColor());
        //left upper wing
        drawCurve(g, 281, 280, 281, 280, 285, 273, 285, 273);
        drawCurve(g, 285, 273, 285, 273, 282, 274, 282, 274);
        drawCurve(g, 282, 274, 282, 274, 278, 269, 278, 269);

        //right upper wing
        drawCurve(g, 296, 283, 296, 283, 294, 272, 294, 272);
        drawCurve(g, 294, 272, 294, 272, 298, 277, 298, 277);
        drawCurve(g, 298, 277, 298, 277, 298, 271, 298, 271);
        drawCurve(g, 298, 271, 298, 271, 307, 278, 307, 278);
        drawCurve(g, 307, 278, 307, 278, 306, 273, 306, 273);
        drawCurve(g, 306, 273, 306, 273, 318, 272, 318, 272);

        //coloring
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

    //draw KFCBucket on KFCBuffer (white or with colors)
    private void drawKFC(boolean isWhite) {
        Graphics2D g = KFCBuffer.createGraphics();
        
        g.setColor(new Color(255,255,255,1));
        g.fillRect(0, 0, 600, 600);
        if(!isWhite){
            g.setColor(Color.black);
        }
        else{
            g.setColor(new Color(255, 255, 255));
        }

        //left
        drawCurve(g, 240, 232, 240, 232, 255, 333, 255, 333);

        //bottom
        drawCurve(g, 255, 333, 287, 346, 324, 346, 355, 333);

        //right
        drawCurve(g, 355, 333, 355, 333, 370, 232, 370, 232);

        //left rim
        drawCurve(g, 240, 232, 240, 232, 239, 227, 239, 227);

        //right rim
        drawCurve(g, 367, 227, 367, 227, 370, 232, 370, 232);

        //chicken
        drawCurve(g, 239, 227, 239, 227, 244, 219, 244, 219);
        drawCurve(g, 244, 219, 244, 219, 247, 218, 248, 220);
        drawCurve(g, 248, 220, 248, 220, 262, 210, 262, 210);

        drawCurve(g, 262, 210, 257, 204, 256, 201, 262, 199);
        drawCurve(g, 262, 199, 262, 199, 272, 196, 276, 188);
        drawCurve(g, 276, 188, 276, 188, 294, 182, 294, 182);
        drawCurve(g, 294, 182, 294, 182, 314, 190, 314, 190);

        drawCurve(g, 314, 190, 314 ,190, 315, 186, 322, 190);
        drawCurve(g, 322, 190, 328, 186, 336, 186, 347, 195);
        drawCurve(g, 347, 195, 352, 201, 352, 205, 348, 208);
        drawCurve(g, 348, 208, 359, 207, 367, 214, 367, 227);
        if(!isWhite){
            drawKFCDetail();
        }else{
            floodFill(g, 307, 247, Color.WHITE, KFCBuffer);
        }
    }

    //draw KFC detail and coloring
    private void drawKFCDetail() {
        Graphics2D g = KFCBuffer.createGraphics();

        g.setColor(Color.BLACK);
        //rim
        drawCurve(g, 236, 230, 265, 243, 343, 243, 373, 230);
        drawCurve(g, 236, 227, 265, 240, 343, 240, 373, 227);

        //red bar 1
        drawCurve(g, 247, 236, 247, 236, 257, 309, 257, 309);
        drawCurve(g, 257, 309, 257, 309, 270, 313, 270, 313);
        drawCurve(g, 270, 313, 270, 313, 262, 238, 262, 238);

        //red bar 2
        drawCurve(g, 354, 236, 354, 236, 347, 310, 347, 310);
        drawCurve(g, 347, 310, 347, 310, 356, 306, 356, 306);
        drawCurve(g, 356, 306, 356, 306, 367, 233, 367, 233);

        drawCurve(g, 262, 210, 267, 212, 271, 208, 277, 210);
        drawCurve(g, 277, 210, 282, 217, 297, 215, 297, 215);
        drawCurve(g, 297, 215, 297, 215, 301, 221, 301, 221);

        drawCurve(g, 285, 235, 289, 230, 295, 230, 295, 230);
        drawCurve(g, 295, 230, 295, 230, 301, 221, 301, 221);
        drawCurve(g, 301, 221, 307, 214, 323, 215, 323, 215);
        drawCurve(g, 323, 215, 326, 216, 322, 223, 322, 223);
        drawCurve(g, 322, 223, 322, 223, 326, 231, 318, 237);

        drawCurve(g, 322, 223, 328, 229, 336, 226, 336, 226);
        drawCurve(g, 336, 226, 341, 228, 344, 233, 344, 233);

        drawCurve(g, 316, 215, 310, 212, 309, 205, 309, 205);

        drawCurve(g, 314, 198, 320, 193, 332, 197, 332, 197);
        drawCurve(g, 332, 197, 332, 197, 343, 205, 343, 205);
        drawCurve(g, 343, 205, 343, 205, 344, 209, 344, 209);
        drawCurve(g, 344, 209, 355, 215, 362, 230, 362, 230);

        drawCurve(g, 314, 190, 316, 198, 309, 205, 309, 205);
        drawCurve(g, 309, 205, 307, 214, 297, 215, 297, 215);
        //Sander
        drawCurve(g, 297, 258, 303, 249, 320, 250, 325, 262);
        drawCurve(g, 325, 262, 325, 262, 328, 266, 326, 272);
        drawCurve(g, 326, 272, 326, 272, 323, 277, 323, 277);
        drawCurve(g, 326, 272, 326, 272, 321, 264, 321, 264);
        drawCurve(g, 326, 272, 329, 272, 326, 285, 322, 285);
        drawCurve(g, 322, 285, 322, 293, 319, 297, 313, 298);
        drawCurve(g, 313, 298, 313, 298, 310, 303, 310, 303);
        drawCurve(g, 310, 303, 310, 303, 304, 297, 305, 289);
        drawCurve(g, 304, 297, 297, 297, 292, 274, 294, 274);
        drawCurve(g, 294, 274, 289, 268, 293, 262, 293, 262);

        drawCurve(g, 294, 274, 290, 273, 289, 274, 295, 284);

        drawCurve(g, 293, 262, 293, 262, 290, 260, 290, 260);
        drawCurve(g, 290, 260, 300, 257, 309, 261, 309, 261);
        drawCurve(g, 309, 261, 300, 259, 295, 264, 294, 274);

        drawCurve(g, 300, 287, 307, 291, 312, 291, 317, 286);

        drawCurve(g, 306, 281, 307, 285, 312, 285, 313, 281);
        drawCurve(g, 313, 281, 313, 281, 317, 286, 317, 286);
        drawCurve(g, 300, 287, 300, 287, 306, 281, 306, 281);

        drawCurve(g, 300, 271, 300, 271, 305, 275, 305, 275);
        drawCurve(g, 301, 278, 301, 278, 305, 275, 305, 275);

        drawCurve(g, 318, 271, 318, 271, 313, 275, 313, 275);
        drawCurve(g, 318, 279, 318, 279, 313, 275, 313, 275);

        //Logo
        drawCurve(g, 295, 320, 295, 320, 294, 328, 294, 328);
        drawCurve(g, 294, 328, 294, 328, 302, 329, 302, 329);
        drawCurve(g, 302, 329, 302, 329, 303, 320, 303, 320);

        drawCurve(g, 307, 329, 307, 329, 307, 320, 307, 320);
        drawCurve(g, 307, 320, 307, 320, 315, 320, 315, 320);
        drawCurve(g, 307, 324, 307, 324, 313, 324, 313, 324);

        drawCurve(g, 320, 328, 315, 326, 317, 319, 322, 319);
        drawCurve(g, 320, 328, 327, 327, 328, 322, 322, 319);
        Color chickenColor = new Color(253,183,97);
        floodFill(g, 255, 257, Color.RED, KFCBuffer);
        floodFill(g, 357, 268, Color.RED, KFCBuffer);
        floodFill(g, 269, 224, chickenColor, KFCBuffer);
        floodFill(g, 288, 198, chickenColor, KFCBuffer);
        floodFill(g, 331, 211, chickenColor, KFCBuffer);
        floodFill(g, 335, 193, chickenColor, KFCBuffer);
        floodFill(g, 309, 227, chickenColor, KFCBuffer);
        floodFill(g, 329, 232, chickenColor, KFCBuffer);
        floodFill(g, 306, 215, chickenColor, KFCBuffer);
        isKFC = false;
    }
    
    //increase tranparency of the blacksceen
    private void updateTransparencyToBlack() {
        tranparency += 0.00001;
        if(tranparency >= 255){
            isBlack = true;
        }
    }

    //decrease transparency of background
    private void updateTransparencyToClear() {
        tranparency -= 0.00001;
        if(tranparency <= 0){
            isClear = true;
        }
    }


    //add/decrease flashbang transparency
    private void updateFlashbangTransparency() {
        if (!isFlashbangWhite) {            
            flashbangTranparency += 0.0002;
            if (flashbangTranparency >= 255) {
                isFlashbangWhite = true;
            }
        }
        else{
            flashbangTranparency -= 0.00001;
            if (flashbangTranparency <= 0) {
                isFlashbangDone = true;
            }
        }
    }

    //update star postition
    private void updateStarMovement() {
        
        int minOffsetX = -10;
        int minOffsetY = -10;
        int maxOffsetX = 10;
        int maxOffsetY = 10;
        
        double movingSpeed = 0.00001;
        
        if(starOffsetY >= maxOffsetY && starOffsetX > minOffsetX){
            starOffsetX -= movingSpeed;
        }
        else if(starOffsetX <= minOffsetX && starOffsetY > minOffsetY){
            starOffsetY -= movingSpeed;
        }
        else if(starOffsetY <= minOffsetY && starOffsetX < maxOffsetX){
            starOffsetX += movingSpeed;
        }
        else if(starOffsetX >= maxOffsetX && starOffsetY < maxOffsetY){
            starOffsetY += movingSpeed;
        }

    }

    //change star color
    private void updateStarColor() {

        double colorChangeSpeed = 0.00000095;

        if (starColorStatus[starLayers-1] < 1) {          
            for (int i = 0; i < starLayers; i++) {
                if (starColorStatus[i] < 1) {
                    starColorStatus[i] += colorChangeSpeed;
                    break;
                }
            }
        }
        
        else if(starColorStatus[starLayers-1] > 0) {          
            for (int i = 0; i < starLayers; i++) {
                if (starColorStatus[i] > 0) {
                    starColorStatus[i] -= colorChangeSpeed;
                    if (i == starLayers-1) {
                        starColorSwitch--;
                    }
                    break;
                }
            }
        }
        
        if (starColorSwitch == 0) {
            isStarDone = true;
        }
        
    }

    //update spiral position
    private void updateSpiral() {
        int baseSize = 12;
        int finalSize = 3;
        int layerHeight = 40;
        int baseLength = 300;
        int finalLength = 75;

        double veticalSpeed = 0.000005;
        double horizontalSpeed = 0.0002;
        double verticalVelocity = 60;

        for (int i = 0; i < spiralLayers; i++) {  
                                    
            double heightRatio = ((spiralMidpointY - spiralPositionY[i][spiralBalls]) / spiralMidpointX);
            
            double currentSize = baseSize - heightRatio * (baseSize - finalSize);
            double currentVerlocity =  veticalSpeed * (heightRatio * verticalVelocity);

            if (heightRatio > 0){
                spiralPositionY[i][spiralBalls] -= veticalSpeed + currentVerlocity;
            }
            else{
                spiralPositionY[i][spiralBalls] -= veticalSpeed;
            }

            double leftBorder = (spiralMidpointX - baseLength / 2) + heightRatio * ((baseLength - finalLength) / 2);
            double rightBorder = (spiralMidpointX + baseLength / 2) - heightRatio * ((baseLength - finalLength) / 2);
            double layerLength = (rightBorder - leftBorder);

            double a = layerLength / 2;
            double b = layerHeight / 2;

            for (int j = 0; j < spiralBalls; j++) {                          
                    
                if(spiralPositionY[i][spiralBalls] <= spiralMidpointY){ 
                    
                    if(spiralPositionX[i][j] <= leftBorder){
                        spiralDirection[i][j] = 'R';
                    }
                    
                    else if(spiralPositionX[i][j] >= rightBorder){
                        spiralDirection[i][j] = 'L';
                    }
                    
                    if (spiralDirection[i][j] == 'L'){
                        spiralPositionX[i][j] -= horizontalSpeed;
                    }
                    
                    else if (spiralDirection[i][j] == 'R'){
                        spiralPositionX[i][j] += horizontalSpeed;
                    }
                    
                }
                
                spiralSize[i][j] = currentSize;
                double currentDistantY = Math.sqrt(((a*a) - Math.pow(spiralPositionX[i][j] - spiralMidpointX, 2)) * (b*b) / (a*a)); //(a^2-(x-h)^2)(b^2)/(a^2)

                if (spiralDirection[i][j] == 'R'){

                    if (spiralPositionX[i][j] - leftBorder <= layerLength / 3 * 2 && spiralPositionX[i][j] - leftBorder >= layerLength / 3){
                        spiralSize[i][j] -= 3;
                    }
                    else{
                        spiralSize[i][j] -= 2;
                    }
                    spiralPositionY[i][j] = (spiralPositionY[i][spiralBalls]) - currentDistantY; 

                }

                else{

                    if (spiralPositionX[i][j] - leftBorder > layerLength / 3 * 2 || spiralPositionX[i][j] - leftBorder < layerLength / 3){
                        spiralSize[i][j] -= 1;
                    }
                    spiralPositionY[i][j] = (spiralPositionY[i][spiralBalls]) + currentDistantY; 

                }

            }

        }
    }

    //update Dome Position
    private void updateDome(){

        int baseSize = 8;
        int layerHeight = 40;
        int baseLength = 75;
        int finalLength = 300;

        double domeHeight = domeEndpointY - domeMidpointY;
        double domeLength = (finalLength - baseLength) / 2;
        double veticalSpeed = 0.0001;
        
        for (int i = 0; i < domeLayers; i++) {

            domePositionY[i][domeBalls] += veticalSpeed;

            double layerHalfLength;

            if(domeHeight < domeLength){
                layerHalfLength = Math.sqrt(((domeLength*domeLength) - Math.pow(domePositionY[i][domeBalls] - domeEndpointY, 2)) * (domeHeight*domeHeight) / (domeLength*domeLength));
            } //(b^2-(y-k)^2)(a^2)/(b^2)
            else {
                layerHalfLength = Math.sqrt(((domeHeight*domeHeight) - Math.pow(domePositionY[i][domeBalls] - domeEndpointY, 2)) * (domeLength*domeLength) / (domeHeight*domeHeight));
            } //(a^2-(y-k)^2)(b^2)/(a^2)

            double heightRatio = (domeEndpointY - domePositionY[i][domeBalls]) / domeEndpointY;

            double leftBorder = domeMidpointX - layerHalfLength; 
            double rightBorder = domeMidpointX + layerHalfLength;
            double layerLength = layerHalfLength * 2;
            double a = layerHalfLength;
            double b = layerHeight / 2;

            for (int j = 0; j < domeBalls; j++) {

                domeSize[i][j] = baseSize;

                if(j == 0){
                    domePositionX[i][j] = leftBorder;
                    domePositionY[i][j] = domePositionY[i][domeBalls];
                }
                else if(j == domeBalls - 1){
                    domePositionX[i][j] = rightBorder;
                    domePositionY[i][j] = domePositionY[i][domeBalls];
                }
                else{
                    domePositionX[i][j] = (((double)j/(domeBalls-1)) * (layerLength)) + leftBorder;
                }

                if(j < (domeBalls + 1) / 2 && j != 0 && j!= domeBalls - 1) {
                    double currentDistantY = Math.sqrt(((a*a) - Math.pow(domePositionX[i][j] - domeMidpointX, 2)) * (b*b) / (a*a)); //(a^2-(x-h)^2)(b^2)/(a^2)
                    domePositionY[i][j] = (domePositionY[i][domeBalls]) - currentDistantY * heightRatio;
                    domePositionY[i][domeBalls-(j+1)] = domePositionY[i][j];
                }
                
                if (domePositionX[i][j] - leftBorder > layerLength / 3 * 2 || domePositionX[i][j] - leftBorder < layerLength / 3){
                    domeSize[i][j] -= 1;
                }

            }

        }
    }

    //update ring position
    private void updateRing(){

        int baseSize = 8;
        double veticalSpeed = 0.0004;
        double spinningSpeed = 0.0001;

        for (int i = 0; i < ringLayers; i++) {

            ringPositionY[i][ringBalls] += veticalSpeed;
            double currentRadius = ringMidpointY - ringPositionY[i][ringBalls];

            for (int j = 0; j < ringBalls; j++) {             
                ringSize[i][j] = baseSize;
                if(ringPositionY[i][ringBalls] >= ringMidpointY - ringBaseRadius && ringPositionY[i][ringBalls] <= ringMidpointY - ringFinalRadius){ 
                    ringAngle[i][j] = (ringAngle[i][j] + spinningSpeed) % 360;
                    ringPositionX[i][j] = ringMidpointX - (currentRadius * Math.cos(Math.PI * 2 * ringAngle[i][j] / 360));
                    ringPositionY[i][j] = ringMidpointY - (currentRadius * Math.sin(Math.PI * 2 * ringAngle[i][j] / 360));
                }
            }

        }
    }

    //update fountain position
    private void updateFountain(){
        double shrinkingSpeed = 0.000001;
        double veticalSpeed = 0.0001;

        for (int i = 0; i <= fountainBalls; i++) {

            double archHeight = fountainArchHeight[i];
            double archLength = Math.abs(fountainArchLength[i]);
            double archFloor = 360;

            double archMidpointX = fountainArchLength[i] + fountainMidpointX;
            double archMidpointY = fountainMidpointY;

            double topBorder = archMidpointY - archHeight;
            double bottomBorder = archMidpointY + archHeight;

            double currentDistantX;

            if (fountainSize[i] > -1) {

                fountainSize[i] -= shrinkingSpeed;

                if(fountainPositionY[i] <= topBorder){
                    fountainDirection[i] = 'D';
                }
                
                else if(fountainPositionY[i] >= bottomBorder){
                    fountainDirection[i] = 'U';
                }
                
                if (fountainDirection[i] == 'U'){
                    fountainPositionY[i] -= veticalSpeed * (fountainSize[i]/fountainBallsMaxSize);
                }
                
                else {
                    if(fountainPositionY[i] < archFloor){
                        fountainPositionY[i] += veticalSpeed * (fountainSize[i]/fountainBallsMaxSize);
                    }
                }

                if(archHeight < archLength){
                    currentDistantX = Math.sqrt(((archLength*archLength) - Math.pow(fountainPositionY[i] - archMidpointY, 2)) * (archHeight*archHeight) / (archLength*archLength));
                } //(b^2-(y-k)^2)(a^2)/(b^2)
                else{
                    currentDistantX = Math.sqrt(((archHeight*archHeight) - Math.pow(fountainPositionY[i] - archMidpointY, 2)) * (archLength*archLength) / (archHeight*archHeight));
                } //(a^2-(y-k)^2)(b^2)/(a^2)

                if (fountainDirection[i] == 'U'){
                    if(archMidpointX > fountainMidpointX){
                        fountainPositionX[i] = archMidpointX - currentDistantX;
                    }
                    else{
                        fountainPositionX[i] = archMidpointX + currentDistantX;
                    }
                }
                
                else {
                    if(archMidpointX > fountainMidpointX){
                        fountainPositionX[i] = archMidpointX + currentDistantX;
                    }
                    else{
                        fountainPositionX[i] = archMidpointX - currentDistantX;
                    }
                }
            
            }

        }
    }

    //change scale of KFC
    private void updateKFCScale(double elapsedTime) {
        KFCScale += KFCScaleVelocity * elapsedTime / 1000.0;
        KFCScaleVelocity += KFCScaleAccelerate * elapsedTime / 1000.0;
        if(KFCScale >= 1){
            KFCScale = 1;
            KFCScaleVelocity = -KFCScaleVelocity;
            KFCScaleAccelerate = -KFCScaleAccelerate;
        }
        else if(KFCScale <= 0.0000000000000000000001){
            KFCScale = 0.0000000000000000000001;
            KFCScaleVelocity = -KFCScaleVelocity;
            KFCScaleAccelerate = -KFCScaleAccelerate;
        }
    }

    //change scale of Baby Chicken
    private void updateChickenScale(double elapsedTime) {
        chickenScale += chickenScaleVelocity * elapsedTime / 1000.0;
        chickenScaleVelocity += chickenScaleAccelerate * elapsedTime / 1000.0;
        if(chickenScale >= 1){
            chickenScale = 1;
            chickenScaleVelocity = -chickenScaleVelocity;
            chickenScaleAccelerate = -chickenScaleAccelerate;
        }
        else if(chickenScale <= 0.0000000000000000000001){
            chickenScale = 0.0000000000000000000001;    
            chickenScaleVelocity = -chickenScaleVelocity;
            chickenScaleAccelerate = -chickenScaleAccelerate;
        }
    }


    //=============================================================================================================
    //=============================================================================================================

                                            // Paint Zone

    //=============================================================================================================
    //=============================================================================================================


    //paint all buffer
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        AffineTransform originalTransform = g2.getTransform();
        paintImage();
        g2.drawImage(mainBuffer, 0, 0, this);

        //babyBuffer
        if(currentStage == Stage.Evolve){
            whitenChicken();
        }
        int chickenTranslate = (int) chickenMove;
        g2.translate(300, 280);
        g2.scale(chickenScale, chickenScale);
        g2.translate(-300, -280);

        g2.translate(0, chickenTranslate);
        g2.drawImage(babyBuffer, 0, 0, this);

        g2.setTransform(originalTransform);

        g2.translate(300, 280);
        g2.scale(KFCScale, KFCScale);
        g2.translate(-300, -280);

        g2.drawImage(KFCBuffer, 0, 0, this);

        g2.setTransform(originalTransform);

        drawEffect();
        g2.drawImage(effectBuffer, 0, 0, this);

        if(isText){
            drawTextbox();
            drawText();
        }
        g2.drawImage(textBoxBuffer, 0, 0, this);

        effectBuffer = new BufferedImage(600, 600, BufferedImage.TYPE_INT_ARGB);
    }

    //make Baby Chicken turn to white color
    private void whitenChicken() {
        Graphics2D g = babyBuffer.createGraphics();
        Color currentColor = new Color(babyBuffer.getRGB(306, 249));
        int red = currentColor.getRed();
        int green = currentColor.getGreen();
        int blue = currentColor.getBlue();
        if(red < 248 && green < 248 && blue < 248){
            Color color = new Color(255,255,255, 255-(int)whitenOpacity*10);
            floodFill(g, 331, 177, color, babyBuffer);
            floodFill(g, 309, 220, color, babyBuffer);
            floodFill(g, 271, 248, color, babyBuffer);
    
            floodFill(g, 294, 325, color, babyBuffer);
            floodFill(g, 306, 318, color, babyBuffer);
            floodFill(g, 312, 317, color, babyBuffer);
            floodFill(g, 321, 316, color, babyBuffer);
            
            floodFill(g, 304, 250, color, babyBuffer);
            floodFill(g, 255, 241, color, babyBuffer);
    
            floodFill(g, 273, 277, color, babyBuffer);
            floodFill(g, 322, 284, color, babyBuffer);
            floodFill(g, 309, 237, color, babyBuffer);
            floodFill(g, 323, 293, color, babyBuffer);
            floodFill(g, 307, 203, color, babyBuffer);
            floodFill(g, 299, 338, color, babyBuffer);
        }
    }

    //paint entire image on buffer
    private void paintImage() {
        Graphics2D g = mainBuffer.createGraphics();
        if(currentStage != Stage.Text && !isDraw)
            drawBackground(g);
        if(currentStage == Stage.Evolve)
            fadeToBlack(g);
        if (isStarStart == true && starColorSwitch > 0){
            drawStar(g);
        }
    }

    //draw pokemon background
    private void drawBackground(Graphics2D g) {
        //clean screen
        g.setColor(new Color(0,0,0, (255-(int)tranparency)));
        g.fillRect(0, 0, 600, 450);

        //light green background
        g.setColor(new Color(242,254,236, (255-(int)tranparency)));
        g.fillRect(0, 0, 600, 600);

        //color lines background
        for (int i = 0; i < 40; i++) {
            Color color;
            //switch-case for 4 color line on top screen
            switch (i) {
                case 0:
                    color = new Color(183,222,241, (255-(int)tranparency));
                    break;
                case 1:
                    color = new Color(195,232,228, (255-(int)tranparency));
                    break;
                case 2:
                    color = new Color(209,241,222, (255-(int)tranparency));
                    break;
                case 3:
                    color = new Color(221,249,215, (255-(int)tranparency));
                    break;
                default:
                    color = new Color(237,255,212, (255-(int)tranparency));
                    break;
            }
            g.setColor(color);
            g.fillRect(0, i*15, 600, 12);
        }

    }

    //fade background to black screen
    private void fadeToBlack(Graphics2D g) {
        g.setColor(new Color(0,0,0,(int)tranparency));
        g.fillRect(0, 0, 600, 450);

        if (isBlack) {
            drawBackground(g);
        }
    }
    
    //draw star on buffer
    private void drawStar(Graphics2D g) {
        for (int i = 0; i < starLayers; i++) {
            if(starColorStatus[i] >= 1){
                g.setColor(new Color(140,190,235,20));
            }
            else if(starColorStatus[i] <= 0){
                g.setColor(new Color(0,0,0,20));
            }
            int[] starIntPositionX_1 = new int[starPoints*2];
            int[] starIntPositionY_1 = new int[starPoints*2];
            int[] starIntPositionX_2 = new int[starPoints*2];
            int[] starIntPositionY_2 = new int[starPoints*2];
            for (int j = 0; j < starPoints*2; j++) {
                starIntPositionX_1[j] = (int)(starPositionX[i][j] + starOffsetX);
                starIntPositionY_1[j] = (int)(starPositionY[i][j] + starOffsetY);
                starIntPositionX_2[j] = (int)(starPositionX[i][j] - starOffsetX);
                starIntPositionY_2[j] = (int)(starPositionY[i][j] - starOffsetY);
            };
            g.fillPolygon(starIntPositionX_1, starIntPositionY_1, starPoints*2);
            g.fillPolygon(starIntPositionX_2, starIntPositionY_2, starPoints*2);
        }
    }

    //draw text box on bottom part of screen
    private void drawTextbox() {
        Graphics2D g = textBoxBuffer.createGraphics();
        g.setColor(new Color(62,57,70));
        g.fillRect(0, 450, 600, 150);

        g.setColor(new Color(150,58,54));
        g.fillRoundRect(10, 455, 580, 140, 10, 10);

        g.setColor(new Color(93, 138, 147));
        g.fillRoundRect(20, 460, 560, 130, 10, 10);
    }

    //draw text on the line
    private void drawText() {
        Graphics2D g = textBoxBuffer.createGraphics();
        String text1 = "";
        String text2 = "";
        if(lineCnt[1] > lineText[1].length()){
            for (int i = 0; i < lineCnt[2] && i < lineText[2].length(); i++) 
                text1 += lineText[2].charAt(i);
            for (int i = 0; i < lineCnt[3] && i < lineText[3].length(); i++){
                if(lineText[3].charAt(i) == ' ')
                    text2 += lineText[3].charAt(i);
                text2 += lineText[3].charAt(i);
            }
        }
        else{
            for (int i = 0; i < lineCnt[0] && i < lineText[0].length(); i++) 
                text1 += lineText[0].charAt(i);
            for (int i = 0; i < lineCnt[1] && i < lineText[1].length(); i++){
                if(lineText[1].charAt(i) == ' ')
                    text2 += lineText[1].charAt(i);
                text2 += lineText[1].charAt(i);
            }
        }
        
        g.setFont(font);

        //draw Shadow of Text
        g.setColor(new Color(86, 73, 84));
        g.drawString(text1, 38, 514);
        g.drawString(text2, 38, 564);

        //draw Real Text for reading
        g.setColor(new Color(220, 214, 225));
        g.drawString(text1, 35, 510);
        g.drawString(text2, 35, 560);
    }

    //draw special effect
    private void drawEffect() {
        Graphics2D g = effectBuffer.createGraphics();
        g.setColor(new Color(0, 0, 0, 1));
        g.fillRect(0,0,600,600);
        if(spiralPositionY[spiralLayers-1][spiralBalls] >= spiralEndpointY && spiralPositionY[0][spiralBalls] < spiralMidpointY){
            drawSpiral(g);
        }
        if(domePositionY[domeLayers-1][domeBalls] <= domeEndpointY && domePositionY[0][domeBalls] > domeMidpointY && isSpiralDone){
            drawDome(g);
        }
        if(ringPositionY[ringLayers-1][ringBalls] <= ringMidpointY - ringFinalRadius && isDomeDone){
            drawRing(g);
        }
        if(!isFlashbangDone && isRingDone){
            g.setColor(new Color(225, 225, 225, (int)flashbangTranparency));
            g.fillRect(0,0,600,450);
        }
        if(fountainSize[fountainBalls] >= 0 && isFlashbangDone){
            drawFountain(g);
        }
        
    }

    //draw spiral balls effect
    private void drawSpiral(Graphics2D g) {
        g.setColor(new Color(255,255,255));
        for (int i = 0; i < spiralLayers; i++) {   
            for (int j = 0; j < spiralBalls; j++) {
                if (spiralPositionY[i][spiralBalls] < spiralMidpointY && spiralPositionY[i][spiralBalls] >= spiralEndpointY){
                    drawCircle(g, (int)spiralPositionX[i][j], (int)spiralPositionY[i][j], (int)spiralSize[i][j]);
                    if(canFill((int)spiralPositionX[i][j], (int)spiralPositionY[i][j], (int)spiralSize[i][j])){
                        floodFillBorder(g, (int)spiralPositionX[i][j], (int)spiralPositionY[i][j],new Color[]{new Color(255,255,255)} ,new Color(255,255,255), effectBuffer);
                    }
                }
            }
        }
    }

    //draw falling dome
    private void drawDome(Graphics2D g) {
        g.setColor(new Color(255,255,255));
        for (int i = 0; i < domeLayers; i++) {   
            for (int j = 0; j < domeBalls; j++) {
                if (domePositionY[i][j] > domeMidpointY && domePositionY[i][domeBalls] <= domeEndpointY){
                    drawCircle(g, (int)domePositionX[i][j], (int)domePositionY[i][j], (int)domeSize[i][j]);
                    if(canFill((int)domePositionX[i][j], (int)domePositionY[i][j], (int)domeSize[i][j])){
                        floodFillBorder(g, (int)domePositionX[i][j], (int)domePositionY[i][j],new Color[]{new Color(255,255,255)} ,new Color(255,255,255), effectBuffer);
                    }
                }
            }
        }
    }

    //draw balls ring
    private void drawRing(Graphics2D g) {
        g.setColor(new Color(255,255,255));
        for (int i = 0; i < ringLayers; i++) {   
            for (int j = 0; j < ringBalls; j++) {
                if (ringPositionY[i][ringBalls] < ringMidpointY - ringFinalRadius){
                    drawCircle(g, (int)ringPositionX[i][j], (int)ringPositionY[i][j], (int)ringSize[i][j]);
                    if(canFill((int)ringPositionX[i][j], (int)ringPositionY[i][j], (int)ringSize[i][j])){
                        floodFillBorder(g, (int)ringPositionX[i][j], (int)ringPositionY[i][j],new Color[]{new Color(255,255,255)} ,new Color(255,255,255), effectBuffer);
                    }
                }
            }
        }
    }

    //draw balls fountain
    private void drawFountain(Graphics2D g) {
        g.setColor(new Color(255,255,255));
        for (int i = 0; i < fountainBalls; i++) {   
            if ((fountainPositionY[i] < fountainMidpointY && fountainDirection[i] == 'U') || fountainDirection[i] == 'D'){
                drawCircle(g, (int)fountainPositionX[i], (int)fountainPositionY[i], (int)fountainSize[i]);
                if(canFill((int)fountainPositionX[i], (int)fountainPositionY[i], (int)fountainSize[i])){
                    floodFillBorder(g, (int)fountainPositionX[i], (int)fountainPositionY[i],new Color[]{new Color(255,255,255)} ,new Color(255,255,255), effectBuffer);
                }
            }
        }
    }

    //check if can fill
    private boolean canFill(int x, int y, int size) {
        if (x > 0 && x < 600 && y > 0 && y < 600 && size > 1) {
            return true;
        }
        return false;
    }
    
    //=============================================================================================================
    //=============================================================================================================

                                            // Tool Zone

    //=============================================================================================================
    //=============================================================================================================

    //floodfill without specifying seed point color
    private void floodFill(Graphics g, int x, int y, Color fillColor, BufferedImage buffer) {
        int targetRGB = buffer.getRGB(x, y);
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

    //draw circle by midpoint algorithm
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

    //drawElipse by midpoint algorithm
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

    //plot 4 point
    private void plotQuadrant(Graphics g, int x, int y, int xc, int yc){
        plot(g, x+xc, y+yc);
        plot(g, x+xc, -y+yc);
        plot(g, -x+xc, y+yc);
        plot(g, -x+xc, -y+yc);
    }

    //plot 8 point
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

    //plot a pixel
    private void plot(Graphics g, int x, int y) {
        g.fillRect(x, y, 1, 1);
    }
}

//Color Palette for drawing in this assignment
enum Palette {
    //HEX Color Code without prefix "#" only number and letter
    BLACK("09040b"),
    BODY("f1a044"),
    BODY_OULINE("000000"),
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
}

