import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;

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
    
    int tranparentcy = 255;
    double letter1 = 0;
    double letter2 = 0;
    String line1 = "What?";
    String line2 = "Baby Chicken is evolving!";

    //start animation
    @Override
    public void run() {
        double lastTime = System.currentTimeMillis();
        double currentTime, elapsedTime, startTime;
        double letterVelocity = 12;
        
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
        
            //Display
            repaint();
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        drawBackground(g2);
        drawTextbox(g2);
        drawText(g2);
        drawEffect(g2);
        drawBaby(g2);
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
    
    private void drawTextbox(Graphics2D g) {

        g.setColor(new Color(62,57,70));
        g.fillRect(0, 450, 600, 150);

        g.setColor(new Color(150,58,54));
        g.fillRoundRect(10, 455, 580, 140, 10, 10);

        g.setColor(new Color(93, 138, 147));
        g.fillRoundRect(20, 460, 560, 130, 10, 10);

    }
    
    private void drawEffect(Graphics2D g) {
        
    }

    private void drawBaby(Graphics2D g) {
        
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
