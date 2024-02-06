import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.Queue;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class Lab5_65050257 extends JPanel implements Runnable{
    private static BufferedImage buffer = new BufferedImage(600, 600, BufferedImage.TYPE_INT_ARGB);;
    private static BufferedImage testBuffer = new BufferedImage(600, 600, BufferedImage.TYPE_INT_ARGB);;

    boolean isDone = false;

    Lab5_65050257(){
        //paintTestImage();
        //paintImage();
    }
    public static void main(String[] args) {
        Lab5_65050257 m = new Lab5_65050257();
        JFrame f = new JFrame();
        f.add(m);
        //m.paintTestImage();
        f.setTitle("Lab5 65050257");
        m.setPreferredSize(new Dimension(600, 600));
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.pack();
        f.setVisible(true);
        f.setLocationRelativeTo(null);
        (new Thread(m)).start();

    }
    
    double squareRotate = 0;
    double circleMove = 0;
    double squareMove = 500;

    //start animation
    @Override
    public void run() {
        paintTestImage();
        double lastTime = System.currentTimeMillis();
        double currentTime, elapsedTime, startTime;
        double circleVelocity = 100;
        double squareVelocity = -100;
        
        startTime = lastTime;
        while(true)
        {
            currentTime = System.currentTimeMillis();
            elapsedTime = currentTime - lastTime;
            lastTime = currentTime;

            //move circle
            circleMove += circleVelocity * elapsedTime / 1000.0;
            //rotate square
            squareRotate += 0.5 * elapsedTime / 1000.0;
            //move square
            if((currentTime-startTime)/1000.0 >= 3)
                squareMove += squareVelocity * elapsedTime / 1000.0;

            //check cirlce
            if(circleMove >= 500){
                circleMove = 500;
                circleVelocity = -circleVelocity;
            }
            else if(circleMove <= 0){
                circleMove = 0;
                circleVelocity = -circleVelocity;
            }

            //check square
            if(squareMove >= 500){
                squareMove = 500;
                squareVelocity = -squareVelocity;
            }
            else if(squareMove <= 0){
                squareMove = 0;
                squareVelocity = -squareVelocity;
            }

            //Display
            repaint();
            //System.out.println("HIII");
            //while (!isDone) {}
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        
        Graphics2D g2 = (Graphics2D) g;

        //g2.drawImage(buffer, 0, 0, this);
        g2.drawImage(testBuffer, 0, 0, this);
    }
    private void paintTestImage() {
        Graphics2D g = testBuffer.createGraphics();
        g.setColor(new Color(111,111,111,0));
        g.fillRect(0, 0, 600, 600);
        g.setColor(Color.BLACK);
        //g.translate(circleMove, 0);
        g.drawOval(20, 70, 100, 100);
        floodFill(g, 70, 100, new Color(0,0,0,0), Color.BLACK, testBuffer);
    }

    private void paintImage() {
        Graphics2D g = buffer.createGraphics();
        g.setColor(Color.ORANGE);
        g.fillRect(0, 0, 600, 600);
        g.setColor(Color.BLACK);
        g.translate(circleMove, 0);
        g.drawOval(0, 0, 100, 100);
        //floodFill(g, 50+(int)circleMove, 50, Color.WHITE, Color.BLACK);
        //rotate square
        g.translate(-circleMove, 0);
        g.rotate(squareRotate, 300, 300);
        g.drawRect(200, 200, 200, 200);

        //move square
        g.rotate(-squareRotate, 300, 300);
        g.translate(0, squareMove);
        g.drawRect(0, 0, 100, 100);
    }

    private void floodFill(Graphics g, int x, int y, Color targetColor, Color fillColor, BufferedImage buffer) {
        isDone = false;
        int targetRGB = 0;
        if(targetColor != null){
            targetRGB = targetColor.getRGB();
        }
        if (buffer.getRGB(x, y) == targetRGB) {
            Queue<Point> queue = new LinkedList<>();
            queue.add(new Point(x, y));

            while (!queue.isEmpty()) {
                //System.out.println("HIIIII");
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
        isDone = true;
    }
    private void plot(Graphics g, int x, int y) {
        g.fillRect(x, y, 1, 1);
    }
}
