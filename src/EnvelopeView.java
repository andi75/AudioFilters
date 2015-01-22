
import java.awt.Color;
import java.awt.Graphics;
import javax.swing.JPanel;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author andreas
 */
public class EnvelopeView extends JPanel
{
    Envelope envelope;
    
    int width, height;
    int border = 10;
    int pointRadius = 4;
    
    public EnvelopeView(Envelope envelope)
    {
        this.envelope = envelope;
    }
    
    public void drawLine(Graphics g, double t1, double level1,
            double t2, double level2)
    {
        double length = envelope.getLength();
        
        g.setColor(Color.black);
        g.drawLine(
                border + (int) ((width - 2 * border) * t1 / length),
                border + (int)((1 - level1) * (height - 2 * border)),
                border + (int)((width - 2 * border) * t2 / length),
                border + (int)((1-level2) * (height - 2 * border)));
    }

    private void drawPoint(Graphics g, double t, double level) {
        double length = envelope.getLength();

        g.setColor(Color.blue);
        g.fillOval(
                border - pointRadius + (int) ((width - 2 * border) * t / length),
                border - pointRadius + (int)((1 - level) * (height - 2 * border)),
                2 * pointRadius, 2 * pointRadius);
    }

    @Override
    public void paint(Graphics g)
    {
        width = this.getWidth();
        height = this.getHeight();

        g.clearRect(0, 0, width, height);
        // drawPoint(g, t, level);

        if(envelope.getLength() < 1.0)
        {
            width *= envelope.getLength() / 1.0;
        }
        double t = 0;
        double level = 0;

        double t_next, level_next;

        t_next = envelope.attack;
        level_next = 1;

        drawLine(g, t, level, t_next, level_next);

        t = t_next;
        t_next += envelope.decay;
        level = level_next;
        level_next = envelope.sustainLevel;

        drawLine(g, t, level, t_next, level_next);

        t = t_next;
        t_next += envelope.sustain;
        level = level_next;
        level_next = envelope.sustainLevel;

        drawLine(g, t, level, t_next, level_next);

        t = t_next;
        t_next += envelope.release;
        level = level_next;
        level_next = 0;

        drawLine(g, t, level, t_next, level_next);
    }

}
