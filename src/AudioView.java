/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author andreas
 */

import java.awt.*;
import javax.swing.*;

public class AudioView extends JPanel
{
    public boolean debug = false;
    
    int offset;
    double maxValue;
    double zoom = 2;

    Image surface = null;
    boolean rebuildImage = true;

    int fixedCursor = 0;
    int draggedCursor = 0;
    boolean isMovingCursor = false;

    AudioBuffer buffer;

    Color fixedCursorColor;
    Color draggedCursorColor;

    boolean bShowFrequency = false;
    Point ptFrequency;
    double fftFrequency;

    public AudioView(AudioBuffer buffer)
    {
        this.buffer = buffer;

        this.fixedCursorColor = new Color(255, 0, 0);
        this.draggedCursorColor = new Color(255, 127, 127);
    }

    public void rebuildImage()
    {
        rebuildImage = true;
        repaint();
    }

    private void rebuild()
    {
        if(debug) System.out.println("[rebuild] started");

        int width = getWidth();
        int height = getHeight();

        Graphics g = surface.getGraphics();
        
        g.clearRect(0, 0, width, height);

        int n = (int) (width / zoom) + 1;
        int last_x = -1;
        int color = 0;
        for(int i = 0; i < n; i++)
        {
            int x = (int) (i * zoom);
            if(x == last_x)
            {
                color += 10;
                if(color > Math.random() * 255)
                    continue;
            }
            else
            {
                color = 0;
            }
            g.setColor(new Color(color, color, color));

            if(i + offset >= buffer.samples)
            {
                g.setColor(Color.gray);
                g.fillRect(x, 0, Math.max(1, (int)(zoom + 0.5)), height);
            }
            else
            {
                double maxValue = (this.maxValue != 0) ? this.maxValue : buffer.maxValue;
                float value = (float)(buffer.read(i + offset));
                if(debug) System.out.println("[rebuild] " + value);
                int y = (int) (height * value / (2 * maxValue));
                // float value = (float)(buffer.read(i + offset));
                // value = (value - buffer.minValue) / (buffer.maxValue - buffer.minValue);
                // int y = (int) (height * value / 2); 
                
                g.drawRect(x, height / 2 - Math.max(0, y), Math.max(1, (int)(zoom + 0.5)), Math.abs(y));
            }
            last_x = x;
        }
    }

    @Override
    public void paint(Graphics g)
    {
        // check if offscreen buffer is still valid
        // redraw into offscreen buffer
        // paint offscreen buffer
        // paint position marker

        if(buffer == null)
            return;


        int width = getWidth();
        int height = getHeight();

        if(surface == null || surface.getWidth(null) != width
                || surface.getHeight(null) != height
                )
        {
            // this.getToolkit().createImage(width, height);
            surface = this.createImage(width, height);
            rebuildImage = true;
        }

        if(rebuildImage)
            rebuild();

        g.drawImage(surface, 0, 0, null);
        if(fixedCursor != 0)
        {
            int x = getX(fixedCursor);
            g.setColor(fixedCursorColor);
            g.drawLine(x, 0, x, height);
            System.out.println("fixedCursor: " + fixedCursor + ", x: " + x);
        }
        if(draggedCursor != 0)
        {
            int x = getX(draggedCursor);
            g.setColor(draggedCursorColor);
            g.drawLine(x, 0, x, height);
            System.out.println("dragged" + x);
        }
        if(bShowFrequency)
        {
            g.drawString("" + fftFrequency, ptFrequency.x, ptFrequency.y);
        }
    }

    int getSample(int x) {
        return (int) (x / zoom + offset);
    }

    int getX(int sample)
    {
        return (int) ((sample - offset) * zoom);
    }

    void setPlayPosition(int position) {
        draggedCursor = position;
        repaint();
    }
}


