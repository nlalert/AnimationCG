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
    private static BufferedImage buffer = new BufferedImage(600, 600, BufferedImage.TYPE_INT_ARGB);;
    private Stage currentStage = Stage.Show;

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

    int pillarLayers = 10;
    int pillarBalls = 4;
    int midpointX = 300;
    int midpointY = 360;
    double[][] pillarSize = new double[pillarLayers][pillarBalls];
    double[][] pillarPositionX = new double[pillarLayers][pillarBalls];
    double[][] pillarPositionY = new double[pillarLayers][pillarBalls+1];
    char[][] pillarDirection = new char[pillarLayers][pillarBalls];
    double transition = 0;


    //start animation
    @Override
    public void run() {
        double lastTime = System.currentTimeMillis();
        double currentTime, elapsedTime, startTime;
        double letterVelocity = 12;

        initializePillar();
        
        startTime = lastTime;
        while (true) {

            currentTime = System.currentTimeMillis();
            elapsedTime = currentTime - lastTime;
            lastTime = currentTime;
            
            double timer = (currentTime-startTime)/1000.0;   //timer since start running the animation in Second Unit

            //0 - 3 second
            if(timer <= 3){
                currentStage = Stage.Show;
            }//0 - 3.416 second
            else if(timer <= 3 + 5/letterVelocity){
                currentStage = Stage.Text;
                letter1 += letterVelocity * elapsedTime / 1000.0;
            }//3.416 - 5.5 second
            else if(timer <= 3 + 30/letterVelocity){
                currentStage = Stage.Text;
                letter2 += letterVelocity * elapsedTime / 1000.0;
            }//5.5 - 6.5 second
            else if(timer <= 6.5 && tranparency < 255){//dark screen transition at the 4th second  
                currentStage = Stage.Evolve;
                updateTransparency(elapsedTime);
            }//6.5 to 999999999999999999999999999999999999999999
            else if(timer <= 999999999 && timer * 1000 % 1 == 0 && pillarPositionY[pillarLayers-1][pillarBalls] >= 0){//dark screen transition at the 4th second
                currentStage = Stage.Evolve;
                updatePillar();
            }   
        
            //Display
            repaint();
        }
    }

    private void initializePillar() {
        for (double[] p : pillarPositionX) {
            p[0] = 300;
            p[1] = 300;
            p[2] = 450;
            p[3] = 150;
        }

        int layerGaps = 0;
        for (double[] p : pillarPositionY) {
            p[4] = 360 + layerGaps;
            layerGaps += 2;
        }

        for (char[] d : pillarDirection) {
            d[0] = 'L';
            d[1] = 'R';
            d[2] = 'L';
            d[3] = 'R';
        }
    }

    private void updatePillar() {
        int baseSize = 12;
        int baseLength = 300;
        int finalLength = 100;

        double veticalSpeed = 0.000006;
        double horizontalSpeed = 0.0002;
        double verticalVelocity = 60;
        double ballGaps = 40;
        
        for (int i = 0; i < pillarLayers; i++) {  
                                    
            double heightRatio = ((midpointY - pillarPositionY[i][4]) / midpointY);
            
            double currentSize = baseSize - heightRatio * baseSize;
            double currentVerlocity =  veticalSpeed * (heightRatio * verticalVelocity);

            double leftBorder = (midpointX - baseLength / 2) + heightRatio * ((baseLength - finalLength) / 2);
            double rightBorder = (midpointX + baseLength / 2) - heightRatio * ((baseLength - finalLength) / 2);

            double a = (rightBorder - leftBorder) / 2;
            double b = ballGaps / 2;

            for (int j = 0; j < pillarBalls+1; j++) {
               
                if (j == 0){
                    if (heightRatio > 0){
                        pillarPositionY[i][4] -= veticalSpeed + currentVerlocity;
                    }
                    else{
                        pillarPositionY[i][4] -= veticalSpeed;
                    }
                }                             
                
                if (j < 4){
                    
                    if(pillarPositionY[i][4] <= midpointY){ 
                        
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
                    double currentPositionY = (Math.sqrt(((a*a) - Math.pow(pillarPositionX[i][j] - midpointX, 2)) * (b*b) / (a*a)));

                    if (pillarDirection[i][j] == 'R') {
                        if (pillarPositionX[i][j] - leftBorder <= (rightBorder - leftBorder) / 3 * 2 && pillarPositionX[i][j] - leftBorder >= (rightBorder - leftBorder) / 3){
                            pillarSize[i][j] -= 3;
                        }
                        else{
                            pillarSize[i][j] -= 2;
                        }
                        pillarPositionY[i][j] = (pillarPositionY[i][4] - b) - currentPositionY;  
                    }

                    else{
                        if (pillarPositionX[i][j] - leftBorder <= (rightBorder - leftBorder) / 3 * 2 && pillarPositionX[i][j] - leftBorder >= (rightBorder - leftBorder) / 3){
                            pillarSize[i][j] -= 1;
                        }
                        pillarPositionY[i][j] = (pillarPositionY[i][4] - b) + currentPositionY; 
                    }

                }

            }

        }
    }

    private void updateTransparency(double elapsedTime) {
        transition += 300 * elapsedTime / 1000.0;
        if((int)transition % 36 == 3){
            tranparency = transition;
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        paintImage();
        g.drawImage(buffer, 0, 0, this);
    }
    
    //paint entire image on buffer
    private void paintImage() {
        Graphics2D g = buffer.createGraphics();
        if(currentStage != Stage.Text)
            drawBackground(g);
        if(currentStage == Stage.Evolve)
            fadeToBlack(g);
        drawTextbox(g);
        drawText(g);
        drawEffect(g);
        if(currentStage == Stage.Show)
            drawBaby(g);
        if(currentStage == Stage.KFC)
            drawKFC(g);
    }

    private void fadeToBlack(Graphics2D g) {
        g.setColor(new Color(0,0,0,(int)tranparency));
        g.fillRect(0, 0, 600, 450);
    }

    private void drawText(Graphics2D g) {
        String text1 = "";
        String text2 = "";
        for (int i = 0; i < letter1 && i < line1Text.length(); i++) {
            text1 += line1Text.charAt(i);
        }
        for (int i = 0; i < letter2 && i < line2Text.length(); i++) {
            if(line2Text.charAt(i) == ' '){
                text2 += line2Text.charAt(i);
            }
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
    
    //draw text box on bottom part of screen
    private void drawTextbox(Graphics2D g) {
        g.setColor(new Color(62,57,70));
        g.fillRect(0, 450, 600, 150);

        g.setColor(new Color(150,58,54));
        g.fillRoundRect(10, 455, 580, 140, 10, 10);

        g.setColor(new Color(93, 138, 147));
        g.fillRoundRect(20, 460, 560, 130, 10, 10);
    }
    
    private void drawEffect(Graphics2D g) {    
        if(pillarPositionY[pillarLayers-1][pillarBalls] >= 0 && pillarPositionY[0][4] < midpointY){
            drawPillar(g);
        }
    }

    private void drawPillar(Graphics2D g) {

        g.setColor(new Color(255,255,255));

        for (int i = 0; i < pillarLayers; i++) {   

            for (int j = 0; j < pillarBalls; j++) {

                if (pillarPositionY[i][4] < midpointY){

                    midpointCircle(g, (int)pillarPositionX[i][j], (int)pillarPositionY[i][j], (int)pillarSize[i][j]);
                    
                    if ((int)pillarSize[i][j] > 1) {   
                        floodFillBorder(g, (int)pillarPositionX[i][j], (int)pillarPositionY[i][j], new Color[]{new Color (255,255,255)}, new Color(255,255,255));
                    }

                }

            }

        }
    }

    private void drawBaby(Graphics2D g) {
        //หงอน
        drawCurve(g, 54, 23, 54, 23, 61, 29, 62, 39);
        drawCurve(g, 60, 46, 60, 46, 65, 17, 96, 1);
        drawCurve(g, 96, 1, 101, 8, 90, 42, 90, 42);
        drawCurve(g, 90, 42, 90, 42, 102, 32, 118, 30);
        drawCurve(g, 118, 30, 118, 30, 109, 51, 78, 61);
        //right face
        g.setColor(Color.yellow.darker());
        drawCurve(g, 62, 54, 62, 54, 65, 43, 73, 39);
        drawCurve(g, 73, 39, 73, 39, 75, 42, 71, 55);
        drawCurve(g, 71, 55, 71, 55, 75, 52, 78, 53);
        drawCurve(g, 78, 53, 78, 53, 78, 55, 75, 60);
        drawCurve(g, 75, 60, 110, 73, 106, 113, 69, 122);
        drawLine(g, 99, 0 ,99, 200);
        //left face
        drawCurve(g, 62, 54, 40, 51, 15, 60, 4, 81);
        //left eye
        midpointElispe(g, 5, 85, 3, 4);
        drawLine(g, 4, 68 ,46, 48);
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
    private void floodFillBorder(Graphics g,int x, int y, Color[] borderColor, Color fillColor) {
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
        float sampleAmnt = 100000;
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

     private void midpointCircle(Graphics g, int xc, int yc, int r) {
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

    private void midpointElispe(Graphics g, int xc, int yc, int a, int b) {
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
    SKY1("FFDA5F"),
    SKY2("FCA29A"),
    SUN("F84434"),
    FUJI("2A4A87"),
    SNOW("FEFEFE"),
    LAND("1B0A12"),
    CLOUD("AFD4E7"),
    BRANCH("953C38"),
    POLLEN("F8BF81"),
    PETAL("FB7D91"),
    PETALSHADOW("F64764"),
    TEXT("000000"), 
    RIBBONBORDER("cc0000"),
    RIBBON("ff3333");

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

