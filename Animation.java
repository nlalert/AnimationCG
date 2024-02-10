import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class Animation extends JPanel implements Runnable,MouseListener{
    //Buffers
    BufferedImage mainBuffer = new BufferedImage(600, 600, BufferedImage.TYPE_INT_ARGB);
    BufferedImage effectBuffer = new BufferedImage(600, 600, BufferedImage.TYPE_INT_ARGB);
    BufferedImage textBoxBuffer = new BufferedImage(600, 600, BufferedImage.TYPE_INT_ARGB);
    BufferedImage babyBuffer = new BufferedImage(600, 600, BufferedImage.TYPE_INT_ARGB);
    BufferedImage KFCBuffer = new BufferedImage(600, 600, BufferedImage.TYPE_INT_ARGB);

    public Animation(){
        addMouseListener(this);
    }

    public static void main(String[] args) {
        Animation m = new Animation();
        JFrame f = new JFrame();
        f.add(m);
        f.setTitle("Lab5 65050257");
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

    Font font;
    Stage currentStage;
    boolean isDraw;
    boolean isText;
    boolean isKFC;
    double transition;
    double whitenOpacity;
    
    double lineCnt[] = new double[4];
    final String lineText[] = new String[4];
    
    //------------------------------------------------------------------------------
    //                                 Background
    //------------------------------------------------------------------------------
    
    double tranparency;

    int starLayers;
    int starPoints;
    int starMidpointX;
    int starMidpointY;
    int starColorSwitch;
    double starOffsetX;
    double starOffsetY;
    boolean isStarStart;
    double[] starColorStatus;
    double[][] starPositionX;
    double[][] starPositionY;

    //------------------------------------------------------------------------------
    //                                 Effects
    //------------------------------------------------------------------------------

    boolean isWaiting;

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

    int flashbangTranparency;
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

    public void run() {
        double lastTime = System.currentTimeMillis();
        double currentTime, elapsedTime, startTime;
        double letterVelocity = 12;
        startTime = lastTime;

        initializeAnimationVar();
        
        initializeStar();

        initializeSpiral();
        initializeDome();
        initializeRing();
        initializeFountain();

        drawBabyBuffer();
        drawWhiteKFC(false);
        
        while (true) {
            currentTime = System.currentTimeMillis();
            elapsedTime = currentTime - lastTime;
            lastTime = currentTime;
            
            timer = (currentTime-startTime)/1000.0;   //timer since start running the animation in Second Unit
            //0 - 3 second
            if(timer <= 3){
                //do nothing
            }
            else if(timer <= 6){
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
            else if(lineCnt[0] < lineText[0].length()){
                //Display Message line 1
                isText = true;
                currentStage = Stage.Text;
                lineCnt[0] += letterVelocity * elapsedTime / 1000.0;
            }
            else if(lineCnt[1] < lineText[1].length()){
                //Display Message line 2
                isText = true;
                currentStage = Stage.Text;
                lineCnt[1] += letterVelocity * elapsedTime / 1000.0;
            }//5.5 - 6.5 second
            else if(timer <= 9.5 && tranparency < 255){//dark screen transition at the 4th second
                isText = false;
                currentStage = Stage.Evolve;
                updateTransparency(elapsedTime);
            }//6.5 - 99999999 second
            else if(timer <= 12.5 && timer * 1000 % 1 == 0 || !isSpiralDone){
                transition = 0;
                isStarStart = true;
                currentStage = Stage.Evolve;
                if(spiralPositionY[spiralLayers-1][spiralBalls] >= spiralEndpointY){ //Moving each balls in the layer of the spiral
                    isWaiting = false;
                    updateSpiral();
                }
                else{
                    isWaiting = true;
                    isSpiralDone = true;
                }
                // if(isStarStart == true && starColorSwitch > 0){
                //     updateStarMovement();
                //     updateStarColor();
                // }
                whitenOpacity += 100 * elapsedTime / 1000.0;
            }
            else if(timer <= 15.5 && timer * 100 % 1 == 0 || !isDomeDone){ //Moving each balls in the layer of the spiral and Make chicken white
                currentStage = Stage.Evolve;
                if (domePositionY[domeLayers-1][domeBalls] <= domeEndpointY){ //Moving each balls in the layer of the dome
                    isWaiting = false;
                    updateDome();
                }
                else{
                    isWaiting = true;
                    isDomeDone = true;
                }
                // if(isStarStart == true && starColorSwitch > 0){
                //     updateStarMovement();
                //     updateStarColor();
                // }
                whitenOpacity += 100 * elapsedTime / 1000.0;
            }
            else if(timer <= 23.5 || chickenScale > 0.1){
                //Scaling Chicken and KFC Bucket inversely
                currentStage = Stage.Evolve;
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
                // if(timer * 100 % 1 == 0 && isStarStart == true && starColorSwitch > 0){
                //     updateStarMovement();
                //     updateStarColor();
                // }
            }
            else if(timer <= 26.5 && timer * 1000 % 1 == 0 || !isRingDone){
                currentStage = Stage.Evolve;
                if (ringPositionY[ringLayers-1][ringBalls] <= ringMidpointY - ringFinalRadius){ //Moving each balls in the layer of the ring
                    isWaiting = false;
                    updateRing();
                }
                else{
                    isWaiting = true;
                    isRingDone = true;
                }
                // if(isStarStart == true && starColorSwitch > 0){
                //     updateStarMovement();
                //     updateStarColor();
                // }
            }
            else if(timer <= 28.5 && timer * 1000 % 1 == 0 || !isFlashbangDone){
                currentStage = Stage.Evolve;
                if (flashbangTranparency <= 0 || !isFlashbangWhite){
                    isWaiting = false;
                    updateFlashbangTransparency();
                    int a = 0;
                }
                else{
                    isWaiting = true;
                    isFlashbangDone = true;
                }
            }
            else if(timer <= 31.5 && timer * 1000 % 1 == 0 || !isFountainDone){
                currentStage = Stage.Evolve;
                if (fountainSize[fountainBalls] > 0){ //Moving each balls in the layer of the ring
                    isWaiting = false;
                    updateFountain();
                }
                else{
                    isWaiting = true;
                    isFountainDone = true;
                }
                // if(isStarStart == true && starColorSwitch > 0){
                //     updateStarMovement();
                //     updateStarColor();
                // }
            }  
            else if(lineCnt[2] < lineText[2].length()){
                //draw KFC Bucket with coloring
                if(!isText){
                    isKFC = true;
                }
                if(isKFC){
                    drawWhiteKFC(true);
                }
                //Display Message line 3
                isText = true;
                currentStage = Stage.Text;
                lineCnt[2] += letterVelocity * elapsedTime / 1000.0;
            }
            else if(lineCnt[3] < lineText[3].length()){
                //Display Message line 4
                isText = true;
                currentStage = Stage.Text;
                lineCnt[3] += letterVelocity * elapsedTime / 1000.0;
            }
        
            //Display
            repaint();
        }
    }

    private void initializeAnimationVar() {
        font = new Font("Segoe UI", Font.PLAIN, 36);

        currentStage = Stage.Show;
        isDraw = false;
        isText = true;
        isKFC = false;
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
        transition = 0;

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

        isWaiting = false;
    
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
    
        fountainBalls = 30;
        fountainBallsMinSize = 8;
        fountainBallsMaxSize = 12;
        fountainMidpointX = 300;
        fountainMidpointY = 360;
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

    private void drawKFC() {
        Graphics2D g = KFCBuffer.createGraphics();

        g.setColor(Color.BLACK);
        //rim
        drawCurve(g, 71+165, 65+165, 100+165, 78+165, 178+165, 78+165, 208+165, 65+165);
        drawCurve(g, 71+165, 62+165, 100+165, 75+165, 178+165, 75+165, 208+165, 62+165);
        //red bar 1
        drawCurve(g, 82+165, 71+165, 82+165, 71+165, 92+165, 144+165, 92+165, 144+165);
        drawCurve(g, 92+165, 144+165, 92+165, 144+165, 105+165, 148+165, 105+165, 148+165);
        drawCurve(g, 105+165, 148+165, 105+165, 148+165, 97+165, 73+165, 97+165, 73+165);
        //red bar 2
        drawCurve(g, 189+165, 71+165, 189+165, 71+165, 182+165, 145+165, 182+165, 145+165);
        drawCurve(g, 182+165, 145+165, 182+165, 145+165, 191+165, 141+165, 191+165, 141+165);
        drawCurve(g, 191+165, 141+165, 191+165, 141+165, 202+165, 68+165, 202+165, 68+165);

        drawCurve(g, 97+165, 45+165, 102+165, 47+165, 106+165, 43+165, 112+165, 45+165);
        drawCurve(g, 112+165, 45+165, 117+165, 52+165, 132+165, 50+165, 132+165, 50+165);
        drawCurve(g, 132+165, 50+165, 132+165, 50+165, 136+165, 56+165, 136+165, 56+165);

        drawCurve(g, 120+165, 70+165, 124+165, 65+165, 130+165, 65+165, 130+165, 65+165);
        drawCurve(g, 130+165, 65+165, 130+165, 65+165, 136+165, 56+165, 136+165, 56+165);
        drawCurve(g, 136+165, 56+165, 143+165, 49+165, 158+165, 50+165, 158+165, 50+165);
        drawCurve(g, 158+165, 50+165, 161+165, 51+165, 157+165, 58+165, 157+165, 58+165);
        drawCurve(g, 157+165, 58+165, 157+165, 58+165, 161+165, 66+165, 153+165, 72+165);

        drawCurve(g, 157+165, 58+165, 163+165, 64+165, 171+165, 61+165, 171+165, 61+165);
        drawCurve(g, 171+165, 61+165, 176+165, 63+165, 179+165, 68+165, 179+165, 68+165);

        drawCurve(g, 151+165, 50+165, 145+165, 47+165, 144+165, 40+165, 144+165, 40+165);

        drawCurve(g, 149+165, 33+165, 155+165, 28+165, 167+165, 32+165, 167+165, 32+165);
        drawCurve(g, 167+165, 32+165, 167+165, 32+165, 178+165, 40+165, 178+165, 40+165);
        drawCurve(g, 178+165, 40+165, 178+165, 40+165, 179+165, 44+165, 179+165, 44+165);
        drawCurve(g, 179+165, 44+165, 190+165, 50+165, 197+165, 65+165, 197+165, 65+165);

        drawCurve(g, 149+165, 25+165, 151+165, 33+165, 144+165, 40+165, 144+165, 40+165);
        drawCurve(g, 144+165, 40+165, 143+165, 49+165, 132+165, 50+165, 132+165, 50+165);
        //Sander
        drawCurve(g, 132+165, 93+165, 138+165, 84+165, 155+165, 85+165, 160+165, 97+165);
        drawCurve(g, 160+165, 97+165, 160+165, 97+165, 163+165, 101+165, 161+165, 107+165);
        drawCurve(g, 161+165, 107+165, 161+165, 107+165, 158+165, 112+165, 158+165, 112+165);
        drawCurve(g, 161+165, 107+165, 161+165, 107+165, 156+165, 99+165, 156+165, 99+165);
        drawCurve(g, 161+165, 107+165, 164+165, 107+165, 161+165, 120+165, 157+165, 120+165);
        drawCurve(g, 157+165, 120+165, 157+165, 128+165, 154+165, 132+165, 148+165, 133+165);
        drawCurve(g, 148+165, 133+165, 148+165, 133+165, 145+165, 138+165, 145+165, 138+165);
        drawCurve(g, 145+165, 138+165, 145+165, 138+165, 139+165, 132+165, 140+165, 124+165);
        drawCurve(g, 139+165, 132+165, 132+165, 132+165, 127+165, 109+165, 129+165, 109+165);
        drawCurve(g, 129+165, 109+165, 124+165, 103+165, 128+165, 97+165, 128+165, 97+165);

        drawCurve(g, 129+165, 109+165, 125+165, 108+165, 124+165, 109+165, 130+165, 119+165);

        drawCurve(g, 128+165, 97+165, 128+165, 97+165, 125+165, 95+165, 125+165, 95+165);
        drawCurve(g, 125+165, 95+165, 135+165, 92+165, 144+165, 96+165, 144+165, 96+165);
        drawCurve(g, 144+165, 96+165, 135+165, 94+165, 130+165, 99+165, 129+165, 109+165);

        drawCurve(g, 135+165, 122+165, 142+165, 126+165, 147+165, 126+165, 152+165, 121+165);

        drawCurve(g, 141+165, 116+165, 142+165, 120+165, 147+165, 120+165, 148+165, 116+165);
        drawCurve(g, 148+165, 116+165, 148+165, 116+165, 152+165, 121+165, 152+165, 121+165);
        drawCurve(g, 135+165, 122+165, 135+165, 122+165, 141+165, 116+165, 141+165, 116+165);

        drawCurve(g, 135+165, 106+165, 135+165, 106+165, 140+165, 110+165, 140+165, 110+165);
        drawCurve(g, 136+165, 113+165, 136+165, 113+165, 140+165, 110+165, 140+165, 110+165);

        drawCurve(g, 153+165, 106+165, 153+165, 106+165, 148+165, 110+165, 148+165, 110+165);
        drawCurve(g, 153+165, 114+165, 153+165, 114+165, 148+165, 110+165, 148+165, 110+165);

        //Logo
        drawCurve(g, 130+165, 160+160, 130+165, 160+160, 129+165, 168+160, 129+165, 168+160);
        drawCurve(g, 129+165, 168+160, 129+165, 168+160, 137+165, 169+160, 137+165, 169+160);
        drawCurve(g, 137+165, 169+160, 137+165, 169+160, 138+165, 160+160, 138+165, 160+160);

        drawCurve(g, 142+165, 169+160, 142+165, 169+160, 143+165, 160+160, 143+165, 160+160);
        drawCurve(g, 143+165, 160+160, 143+165, 160+160, 150+165, 160+160, 150+165, 160+160);
        drawCurve(g, 143+165, 164+160, 143+165, 164+160, 148+165, 164+160, 148+165, 164+160);

        drawCurve(g, 155+165, 168+160, 150+165, 166+160, 152+165, 159+160, 157+165, 159+160);
        drawCurve(g, 155+165, 168+160, 162+165, 167+160, 163+165, 162+160, 157+165, 159+160);
        
        floodFill(g, 255, 257, Color.RED, KFCBuffer);
        floodFill(g, 357, 268, Color.RED, KFCBuffer);
        floodFill(g, 269, 224, Color.ORANGE, KFCBuffer);
        floodFill(g, 288, 198, Color.ORANGE, KFCBuffer);
        floodFill(g, 331, 211, Color.ORANGE, KFCBuffer);
        floodFill(g, 335, 193, Color.ORANGE, KFCBuffer);
        floodFill(g, 309, 227, Color.ORANGE, KFCBuffer);
        floodFill(g, 329, 232, Color.ORANGE, KFCBuffer);
        floodFill(g, 306, 215, Color.ORANGE, KFCBuffer);
        isKFC = false;
    }

    private void drawWhiteKFC(boolean isBlack) {
        Graphics2D g = KFCBuffer.createGraphics();
        
        g.setColor(new Color(255,255,255,1));
        g.fillRect(0, 0, 600, 600);
        if(isBlack){
            g.setColor(Color.black);
        }
        else{
            g.setColor(new Color(255, 255, 255));
        }

        //left
        drawCurve(g, 75+165, 67+165, 75+165, 67+165, 90+165, 168+165, 90+165, 168+165);
        //bottom
        drawCurve(g, 90+165, 168+165, 122+165, 181+165, 159+165, 181+165, 190+165, 168+165);
        //right
        drawCurve(g, 190+165, 168+165, 190+165, 168+165, 205+165, 67+165, 205+165, 67+165);

        //left rim
        drawCurve(g, 75+165, 67+165, 75+165, 67+165, 74+165, 62+165, 74+165, 62+165);
        //right rim
        drawCurve(g, 202+165, 62+165, 202+165, 62+165, 205+165, 67+165, 205+165, 67+165);

        //chicken
        drawCurve(g, 74+165, 62+165, 74+165, 62+165, 79+165, 54+165, 79+165, 54+165);
        drawCurve(g, 79+165, 54+165, 79+165, 54+165, 82+165, 53+165, 83+165, 55+165);
        drawCurve(g, 83+165, 55+165, 83+165, 55+165, 97+165, 45+165, 97+165, 45+165);

        drawCurve(g, 97+165, 45+165, 92+165, 39+165, 91+165, 36+165, 97+165, 34+165);
        drawCurve(g, 97+165, 34+165, 97+165, 34+165, 107+165, 31+165, 111+165, 23+165);
        drawCurve(g, 111+165, 23+165, 111+165, 23+165, 129+165, 17+165, 129+165, 17+165);
        drawCurve(g, 129+165, 17+165, 129+165, 17+165, 149+165, 25+165, 149+165, 25+165);

        drawCurve(g, 149+165, 25+165, 149+165 ,25+165, 150+165, 21+165, 157+165, 25+165);
        drawCurve(g, 157+165, 25+165, 163+165, 21+165, 171+165, 21+165, 182+165, 30+165);
        drawCurve(g, 182+165, 30+165, 187+165, 36+165, 187+165, 40+165, 183+165, 43+165);
        drawCurve(g, 183+165, 43+165, 194+165, 42+165, 202+165, 49+165, 202+165, 62+165);
        if(isBlack){
            drawKFC();
        }else{
            floodFill(g, 143+165, 82+165, Color.WHITE, KFCBuffer);
        }
    }

    private void drawBabyBuffer() {
        Graphics2D g = babyBuffer.createGraphics();
        
        g.setColor(new Color(255,255,255,1));
        g.fillRect(0, 0, 600, 600);
        
        //right face
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
        g.setColor(Palette.BLACK.getColor());
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

        g.setColor(Palette.BLACK.getColor());
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

    private void initializeDome() {

        //Set a vertical position of each layer of the dome
        int layerGaps = 0;
        for (double[] py : domePositionY) {
            py[domeBalls] = domeMidpointY - layerGaps;
            layerGaps += 30;
        }
    }

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

    private void initializeFountain() {

        Random randomNumber = new Random();

        int archMinLength = 0;
        int archMaxLength = 100;
        int archMinHeight = 200;
        int archMaxHeight = 350;
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
    
    private void updateTransparency(double elapsedTime) {
        transition += 300 * elapsedTime / 1000.0;
        if((int)transition % 36 == 3){
            tranparency = transition;
        }
    }

    private void updateFlashbangTransparency() {
        if (!isFlashbangWhite) {            
            transition += 0.004;
            flashbangTranparency = (int)transition;
            if (flashbangTranparency >= 255) {
                isFlashbangWhite = true;
            }
        }
        else{
            transition -= 0.0005;
            flashbangTranparency = (int)transition;
        }
    }

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

    private void updateStarColor() {

        double colorChangeSpeed = 0.000001;

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
        };

    }

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

    private void updateFountain(){

        double shrinkingSpeed = 0.000001;
        double veticalSpeed = 0.0001;

        for (int i = 0; i <= fountainBalls; i++) {

            double archHeight = fountainArchHeight[i];
            double archLength = Math.abs(fountainArchLength[i]);

            double archMidpointX = fountainArchLength[i] + fountainMidpointX;
            double archMidpointY = fountainMidpointY;

            double topBorder = archMidpointY - archHeight;
            double bottomBorder = archMidpointY + archHeight;

            double currentDistantX;

            if (fountainSize[i] >= -1) {

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
                    fountainPositionY[i] += veticalSpeed * (fountainSize[i]/fountainBallsMaxSize);
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


    //=============================================================================================================
    //=============================================================================================================

                                            // Paint Zone

    //=============================================================================================================
    //=============================================================================================================



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

        if(!isWaiting){
            drawEffect();
        }
        g2.drawImage(effectBuffer, 0, 0, this);

        if(isText){
            drawTextbox();
            drawText();
        }
        g2.drawImage(textBoxBuffer, 0, 0, this);

        effectBuffer = new BufferedImage(600, 600, BufferedImage.TYPE_INT_ARGB);
    }

    private void whitenChicken() {
        Graphics2D g = babyBuffer.createGraphics();
        Color currentColor = new Color(babyBuffer.getRGB(306, 249));
        int red = currentColor.getRed();
        int green = currentColor.getGreen();
        int blue = currentColor.getBlue();
        // System.out.println(red);
        // System.out.println(green);
        // System.out.println(blue);
        // if(red < 248 && green < 248 && blue < 248){
        //     Color color = new Color(255,255,255, (int) whitenOpacity);
        //     floodFill(g, 331, 177, color, babyBuffer);
        //     floodFill(g, 309, 220, color, babyBuffer);
        //     floodFill(g, 271, 248, color, babyBuffer);
    
        //     floodFill(g, 294, 325, color, babyBuffer);
        //     floodFill(g, 306, 318, color, babyBuffer);
        //     floodFill(g, 312, 317, color, babyBuffer);
        //     floodFill(g, 321, 316, color, babyBuffer);
            
        //     floodFill(g, 304, 250, color, babyBuffer);
        //     floodFill(g, 255, 241, color, babyBuffer);
    
        //     floodFill(g, 273, 277, color, babyBuffer);
        //     floodFill(g, 322, 284, color, babyBuffer);
        //     floodFill(g, 309, 237, color, babyBuffer);
        //     floodFill(g, 323, 293, color, babyBuffer);
        //     floodFill(g, 307, 203, color, babyBuffer);
        //     floodFill(g, 299, 338, color, babyBuffer);
        // }
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

    private void drawBackground(Graphics2D g) {
        //clean screen
        g.setColor(new Color(0,0,0));
        g.fillRect(0, 0, 600, 450);

        //light green background
        g.setColor(new Color(242,254,236));
        g.fillRect(0, 0, 600, 600);

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

    private void drawEffect() {
        Graphics2D g = effectBuffer.createGraphics();
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
            g.setColor(new Color(225, 225, 225, flashbangTranparency));
            g.fillRect(0,0,600,450);
        }
        if(fountainSize[fountainBalls] >= 0 && isFlashbangDone){
            drawFountain(g);
        }
        
    }

    private void drawSpiral(Graphics2D g) {
        g.setColor(new Color(255,255,255));
        for (int i = 0; i < spiralLayers; i++) {   
            for (int j = 0; j < spiralBalls; j++) {
                if (spiralPositionY[i][spiralBalls] < spiralMidpointY && spiralPositionY[i][spiralBalls] >= spiralEndpointY){
                    g.fillOval((int)spiralPositionX[i][j] - (int)spiralSize[i][j], (int)spiralPositionY[i][j] - (int)spiralSize[i][j], (int)spiralSize[i][j]*2, (int)spiralSize[i][j]*2);
                }
            }
        }
    }

    private void drawDome(Graphics2D g) {
        g.setColor(new Color(255,255,255));
        for (int i = 0; i < domeLayers; i++) {   
            for (int j = 0; j < domeBalls; j++) {
                if (domePositionY[i][j] > domeMidpointY && domePositionY[i][domeBalls] <= domeEndpointY){
                    g.fillOval((int)domePositionX[i][j] - (int)domeSize[i][j], (int)domePositionY[i][j] - (int)domeSize[i][j], (int)domeSize[i][j]*2, (int)domeSize[i][j]*2);
                }
            }
        }
    }

    private void drawRing(Graphics2D g) {
        g.setColor(new Color(255,255,255));
        for (int i = 0; i < ringLayers; i++) {   
            for (int j = 0; j < ringBalls; j++) {
                if (ringPositionY[i][ringBalls] < ringMidpointY - ringFinalRadius){
                    g.fillOval((int)ringPositionX[i][j] - (int)ringSize[i][j], (int)ringPositionY[i][j] - (int)ringSize[i][j], (int)ringSize[i][j]*2, (int)ringSize[i][j]*2);
                }
            }
        }
    }

    private void drawFountain(Graphics2D g) {
        g.setColor(new Color(255,255,255));
        for (int i = 0; i < fountainBalls; i++) {   
            if (fountainPositionY[i] < fountainMidpointY){
                g.fillOval((int)fountainPositionX[i] - (int)fountainSize[i], (int)fountainPositionY[i] - (int)fountainSize[i], (int)fountainSize[i]*2, (int)fountainSize[i]*2);
            }
        }
    }

    private void drawBaby(Graphics2D g) {
        
    }
    

    //=============================================================================================================
    //=============================================================================================================

                                            // Tool Zone

    //=============================================================================================================
    //=============================================================================================================

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


    //floodfill area seed at x,y to fill targetColor nearby with fillColor
    private void floodFill(Graphics g, int x, int y, Color targetColor, Color fillColor, BufferedImage buffer) {
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

    // private final Color color;

    // // Stage(String colorCode) {
    // //     this.color = new Color(Integer.parseInt(colorCode, 16));
    // // }

    // public Color getColor() {
    //     return color;
    // }
}

