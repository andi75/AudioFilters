
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author andreas
 */
public class FFTTest {
    public static void main(String argv[])
    {
        AudioBuffer fftBuffer = new AudioBuffer(22050, 32, 2);
        AudioBuffer buffer = new AudioBuffer(22050, 16, 20);

        double freq = 22050.0;
        int windowSize = 2048;
        int pos = (int) (220 / (freq / windowSize));
        fftBuffer.write(pos, fftBuffer.maxValue / 10000 / pos);
        // fftBuffer.write(pos * 2, fftBuffer.maxValue / 1000 / (pos * 2));

        int N = 1;
        for(int i = 0; i < 1; i++)
        {
            int offset = i * windowSize / N;
            double weight = 1 / N;
            FFTController.addComplexIFFT(weight, buffer, fftBuffer, offset, windowSize, true);
            buffer.write((i+1) * windowSize, buffer.maxValue);
        }
        
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        AudioView view = new AudioView(buffer);
        AudioViewControl viewCtrl = new AudioViewControl(view);

        JPanel viewPanel = new JPanel();
        viewPanel.setLayout(new BorderLayout());
        viewPanel.add(view, BorderLayout.NORTH);
        
        JPanel barPanel = new JPanel();
        // barPanel.add(new JLabel("Position:"));
        barPanel.add(new JLabel("Zoom:  "));
        barPanel.add(new JLabel("+"));
        barPanel.add(viewCtrl.getZoomSlider());
        barPanel.add(new JLabel("-"));
        viewPanel.add(barPanel, BorderLayout.SOUTH);

        view.zoom = 0.05;
        AudioView fftView = new AudioView(fftBuffer);
        fftView.zoom = 0.5;
        view.setPreferredSize(new Dimension(800, 200));
        fftView.setPreferredSize(new Dimension(800, 200));
        frame.setLayout(new GridLayout(2, 1));
        frame.add(viewPanel);
        frame.add(fftView);
        frame.pack();
        frame.setVisible(true);
    }
}
