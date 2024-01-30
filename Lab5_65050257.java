import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class Lab5_65050257 extends JPanel implements Runnable{
    public static void main(String[] args) {
        Lab5_65050257 m = new Lab5_65050257();
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
    
    double squareRotate = 0;
    double circleMove = 0;
    double squareMove = 500;

    //start animation
    @Override
    public void run() {
        double lastTime = System.currentTimeMillis();
        double currentTime, elapsedTime, startTime;
        double circleVelocity = 100;
        double squareVelocity = -100;
        
        startTime = lastTime;
        while (true)
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
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setColor(Color.WHITE);
        g2.fillRect(0, 0, 600, 600);
        g2.setColor(Color.BLACK);
        g2.translate(circleMove, 0);
        g2.drawOval(0, 0, 100, 100);
        //rotate square
        g2.translate(-circleMove, 0);
        g2.rotate(squareRotate, 300, 300);
        g2.drawRect(200, 200, 200, 200);

        //move square
        g2.rotate(-squareRotate, 300, 300);
        g2.translate(0, squareMove);
        g2.drawRect(0, 0, 100, 100);
    }
}
