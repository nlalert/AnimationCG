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
    
    int tranparency = 255;
    double squareRotate = 0;
    double circleMove = 0;
    double squareMove = 500;

    //start animation
    @Override
    public void run() {       
        while (true)
        {

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

        g.setColor(new Color(0,0,0, 0));
        g.fillRect(0, 0, 600, 600);

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

    private void plot(Graphics g, int x, int y) {
        g.fillRect(x, y, 1, 1);
    }
    
}
