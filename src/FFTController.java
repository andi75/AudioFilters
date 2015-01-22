
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author andreas
 */
public class FFTController implements MouseListener, MouseMotionListener
{
    AudioView sampleView, fftView;

    private final int fftLength = 2048;

    public FFTController(AudioView sampleView, AudioView fftView)
    {
        this.sampleView = sampleView;
        this.fftView = fftView;

        sampleView.addMouseListener(this);
        sampleView.addMouseMotionListener(this);

        fftView.addMouseMotionListener(this);

    }
    public void mouseClicked(MouseEvent e) {
        if(e.getSource() != sampleView)
            return;

        // System.out.println(e);
        switch(e.getButton())
        {

            case MouseEvent.BUTTON1:
                sampleView.fixedCursor = sampleView.getSample(e.getX());
                break;
            case MouseEvent.BUTTON3:
                sampleView.fixedCursor = 0;
                break;
        }
        redoFFT();
        fftView.repaint();

        sampleView.repaint();
    }

    public void redoFFT()
    {

        AudioBuffer buffer = sampleView.buffer;
        AudioBuffer fftBuffer = fftView.buffer;

        if(fftBuffer == null)
        {
            // 32-bit precision
            // TODO: create some kind of floating point buffer thingie
            fftBuffer = new AudioBuffer(buffer.sampleRate, 32, buffer.seconds);
            fftView.buffer = fftBuffer;
        }

        int min, max;
        min = buffer.getMinSampleValue();
        max = buffer.getMaxSampleValue();
        System.out.println("signal:     min: " + min / 2048 + ", " + max / 2048);

        doComplexFFT(buffer, fftBuffer, sampleView.fixedCursor, fftLength, true);
        // doFortranFFT(buffer, fftBuffer, 0, fftLength, true);

        min = fftBuffer.getMinSampleValue();
        max = fftBuffer.getMaxSampleValue();

        fftView.maxValue = Math.max(
                max, Math.abs(min));
        System.out.println("fft:     min: " + min / 2048 + ", " + max / 2048);
        System.out.println("maxValue after fft: " + fftView.maxValue);


        fftView.rebuildImage();
    }

    private void doFortranFFT(AudioBuffer buffer, AudioBuffer fftBuffer, int offset, int windowSize, boolean useWindow)
    {
        // if the buffer is too small, downsize the window to the next power of two that fits in the buffer
        if(offset >= buffer.samples)
            return;

        while(windowSize > buffer.samples - offset)
            windowSize /= 2;


        // read the first windowSize bytes,normalize them into the [-1,1] range and store them in the buffer at
        // offset + 1 (old fortran code...)
        float tmp[] = new float[windowSize + 1];
        if(useWindow)
        {
            for(int i = 0; i < windowSize; i++)
                tmp[i + 1] = (float) (window(i, windowSize) *  (double)buffer.read(offset + i) / (double)buffer.maxValue );
        }
        else
        {
            for(int i = 0; i < windowSize; i++)
                tmp[i + 1] = (float) ((double)buffer.read(offset + i) / (double)buffer.maxValue );

        }


        FortranFFT.rfft(tmp, windowSize);
        for(int i = 0; i < windowSize; i++)
        {
            fftBuffer.write(i, (int)(tmp[i+1] * buffer.maxValue));
        }
    }

    public static void doComplexFFT(AudioBuffer buffer, AudioBuffer fftBuffer, int offset, int windowSize, boolean useWindow)
    {
        if(offset >= buffer.samples)
            return;

        // if the buffer is too small, downsize the window to the next power of two that fits in the buffer
        while(windowSize > buffer.samples - offset)
            windowSize /= 2;

        Complex tmp[] = new Complex[windowSize];
        for(int i = 0; i < windowSize; i++)
        {
            tmp[i] = new Complex( (float) (window(i, windowSize) * buffer.read(i + offset)), 0);
        }
        Complex result[] = FFT.fft(tmp);
        for(int i = 0; i < windowSize; i++)
        {
            fftBuffer.write(i, (int) result[i].re());
        }
    }

    public static void addComplexIFFT(double weight, AudioBuffer buffer, AudioBuffer fftBuffer, int offset, int windowSize, boolean useWindow)
    {

        // TODO: check if fftBuffer is power of two and at least of windowSize

        Complex tmp[] = new Complex[windowSize];
        for(int i = 0; i < windowSize; i++)
        {
            tmp[i] = new Complex( (float) fftBuffer.read(i), 0);
        }
        Complex result[] = FFT.ifft(tmp);
        for(int i = 0; i < windowSize; i++)
        {
            int value = buffer.read(i + offset) + (int) (weight * window(i, windowSize) * result[i].re());
            buffer.write(i + offset, value);
        }
        // Copy fftBuffer to tmp
        // Do the inverse fft
        // Add the result to the buffer, multiplied by weight and the window function
        // let's hope the window is sliding...
    }

    private static double window(int i, int windowSize)
    {
        // double x = 4 * (double) i / (double) windowSize - 2;
        // return Math.exp(- x * x);
        double x = 2 * Math.PI * (double)i / (double) windowSize;
        return 0.5 * (1 - Math.cos(x));
    }


    public void mousePressed(MouseEvent e) {}

    public void mouseReleased(MouseEvent e) {}

    public void mouseEntered(MouseEvent e) {}

    public void mouseExited(MouseEvent e) {
        fftView.bShowFrequency = false;
    }

    public void mouseDragged(MouseEvent e) {}

    public void mouseMoved(MouseEvent e) {
        if(e.getSource() == fftView)
        {
            if(fftView.buffer == null)
                return;

            double sample = fftView.getSample(e.getX());
            fftView.fftFrequency = sample * fftView.buffer.sampleRate / fftLength;
            fftView.ptFrequency = e.getPoint();
            fftView.bShowFrequency = true;
            fftView.repaint();
        }
    }
}
