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

    private static BufferedImage buffer;

    double letter1 = 0;
    double letter2 = 0;
    String line1 = "What?";
    String line2 = "Baby Chicken is evolving!";
    
    double tranparency = 0;

    int pillarLayers = 10;
    int pillarBalls = 4;
    int midpointX = 300;
    int midpointY = 360;
    double[][] pillarSize = new double[pillarLayers][pillarBalls];
    double[][] pillarPositionX = new double[pillarLayers][pillarBalls];
    double[][] pillarPositionY = new double[pillarLayers][pillarBalls+1];
    char[][] pillarDirection = new char[pillarLayers][pillarBalls];

    //start animation
    @Override
    public void run() {
        double lastTime = System.currentTimeMillis();
        double currentTime, elapsedTime, startTime;
        double letterVelocity = 12;
        double transition = 0;

        for (double[] p : pillarPositionX) {
            p[0] = 300;
            p[1] = 300;
            p[2] = 450;
            p[3] = 150;
        }

        int layerGaps = 0;
        double ballGaps = 40;
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
        
        startTime = lastTime;
        while (true) {

            currentTime = System.currentTimeMillis();
            elapsedTime = currentTime - lastTime;
            lastTime = currentTime;
            
            double timer = (currentTime-startTime)/1000.0;   //timer since start running the animation in second unit

            if(timer <= 5/letterVelocity){//first text line
                letter1 += letterVelocity * elapsedTime / 1000.0;
            }
            else if(timer <= 30/letterVelocity){//second text line
                letter2 += letterVelocity * elapsedTime / 1000.0;
            }

            if(timer >= 4 && timer < 5 && tranparency < 255){   //dark screen transition at the 4th second  
                transition += 300 * elapsedTime / 1000.0;
                if((int)transition % 36 == 3){
                    tranparency = transition;
                }
            }

            if(timer >= 5 && pillarPositionY[pillarLayers-1][pillarBalls] >= 0){   //dark screen transition at the 4th second

                int baseSize = 12;
                int baseLength = 300;
                int finalLength = 100;

                double veticalSpeed = 0.000006;
                double horizontalSpeed = 0.0002;
                double verticalVelocity = 60;

                if ((currentTime-startTime) * 1000 % 1 == 0) {

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

            }   
            //Display
            repaint();
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
        buffer = new BufferedImage(600, 600, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = buffer.createGraphics();
        drawBackground(g);
        drawTextbox(g);
        drawText(g);
        drawEffect(g);
        drawBaby(g);
    }
   
    private void drawText(Graphics2D g) {
        String text1 = "";
        String text2 = "";
        for (int i = 0; i < letter1 && i < line1.length(); i++) {
            text1 += line1.charAt(i);
        }
        for (int i = 0; i < letter2 && i < line2.length(); i++) {
            text2 += line2.charAt(i);
        }
        g.setFont(font);

        //draw text Shadow
        g.setColor(new Color(86, 73, 84));
        g.drawString(text1, 38, 514);
        g.drawString(text2, 38, 564);

        //draw text Real
        g.setColor(new Color(200, 194, 205));
        g.drawString(text1, 35, 510);
        g.drawString(text2, 35, 560);
    }

    private void drawBackground(Graphics2D g) {
        //clean screen
        g.setColor(new Color(242,254,236));
        g.fillRect(0, 0, 600, 600);

        for (int i = 0; i < 40; i++) {

            if (i == 0)       
                g.setColor(new Color(183,222,241));

            else if (i == 1)
                g.setColor(new Color(195,232,228));

            else if (i == 2)
                g.setColor(new Color(209,241,222));

            else if (i == 3)
                g.setColor(new Color(221,249,215));

            else
                g.setColor(new Color(237,255,212));

            g.fillRect(0, i*15, 600, 12);

        }

        g.setColor(new Color(0,0,0,(int)tranparency));
        g.fillRect(0, 0, 600, 600);
    }
    
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
        
    }

    private void midpointCircle(Graphics g, int xc, int yc, int r) {
        int x = 0;
        int y = r;
        int dx = 2*x;
        int dy = 2*y;
        int d = 1-r;
        while (x <= y) {
            plot(g, x+xc, y+yc);
            plot(g, -x+xc, y+yc);
            plot(g, x+xc, -y+yc);
            plot(g, -x+xc, -y+yc);

            plot(g, y+xc, x+yc);
            plot(g, -y+xc, x+yc);
            plot(g, y+xc, -x+yc);
            plot(g, -y+xc, -x+yc);

            x++;
            dx += 2;
            d += dx + 1;

            if (d >= 0) {
                y--;
                dy -= 2;
                d -= dy;
            }
        }

    }

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

    private boolean isIn(int color, int[] borderColor, int fillColor){
        for (int i : borderColor) {
            if(color == i || color == 0 || color == fillColor)
                return true;
        }
        return false;
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
