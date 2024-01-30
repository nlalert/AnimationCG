import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class Animation extends JPanel implements Runnable{
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
        (new Thread(m)).start();
    }
    
    int tranparentcy = 255;
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
            
            updateText();

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

    private void updateText() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'updateText'");
    }

    @Override
    public void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        drawBackground(g2);
        drawTextbox(g2);
        drawEffect(g2);
        drawBaby(g2);
    }
   
    private void drawBackground(Graphics g) {

        g.setColor(new Color(0,0,0));
        g.fillRect(0, 0, 600, 600);

        g.setColor(new Color(242,254,236,tranparentcy));
        g.fillRect(0, 0, 600, 600);

        for (int i = 0; i < 40; i++) {

            if (i == 0)       
                g.setColor(new Color(183,222,241,tranparentcy));

            else if (i == 1)
                g.setColor(new Color(195,232,228,tranparentcy));

            else if (i == 2)
                g.setColor(new Color(209,241,222,tranparentcy));

            else if (i == 3)
                g.setColor(new Color(221,249,215,tranparentcy));

            else
                g.setColor(new Color(237,255,212,tranparentcy));

            g.fillRect(0, i*15, 600, 12);

        }

    }
    
    private void drawTextbox(Graphics g) {

        g.setColor(new Color(62,57,70));
        g.fillRect(0, 450, 600, 150);

        g.setColor(new Color(150,58,54));
        g.fillRoundRect(10, 455, 580, 140, 10, 10);

        g.setColor(new Color(104,138,134));
        g.fillRoundRect(20, 460, 560, 130, 10, 10);

    }
    
    private void drawEffect(Graphics g) {
        
    }

    private void drawBaby(Graphics g) {
        
    }
    
}
