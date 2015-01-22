/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author andreas
 */
public class AudioFilter {
    AudioBuffer src;
    AudioBuffer dst;

    public AudioFilter(AudioBuffer src)
    {
        this.src = src;
    }

    void pitchShift(double factor)
    {
        // shift the signal by factor

        // idea: multiply each frequency intensity
        // by factor

        // do FFT
        // apply transformation
        // do inverse FFT

    }

    void filter()
    {
        dst = new AudioBuffer(src.sampleRate, src.bitsPerSample, src.seconds);
        upshiftFilter();
    }

    void upshiftFilter()
    {       
        for(int i = 0; i < src.samples / 2; i++)
        {
            int value = src.read(2 * i + 0) + src.read(2 * i + 1);
            value *= 0.5;
            dst.write(i, value);
        }
    }

    void delayFilter()
    {
        for(int i = 0; i < src.samples; i++)
        {
            int value = src.read(i);
            for(int j = 0; j < 8; j++)
            {
                int dt = 600;
                if(i > j * dt)
                {
                    value += src.read(i - j * dt) * Math.pow(0.8, j / 4.0 + 1);
                }
            }
            dst.write(i, value);
        }
    }

    

    void convolutionFilter()
    {
        // double kernel[] = { 0.0625, 0.125, 0.5, 1, 0.5, 0.125, 0.0625 };
        double kernel[] = { 0, 1, 0 };

        double kernelWeight = 0;
        for(int i = 0; i < kernel.length; i++)
        {
            kernelWeight += kernel[i];
        }
        int kernelOffset = kernel.length / 2;

        // copy values around the edges
        for(int i = 0; i < kernelOffset; i++)
        {
            dst.write(i, src.read(i));
            dst.write(dst.samples - i - 1, src.read(dst.samples - i - 1));
        }

        //  filter in the middle
        for(int i = 0; i < dst.samples - kernel.length; i++)
        {
            double result = 0;
            for(int j = 0; j < kernel.length; j++)
            {
                result += kernel[j] * src.read(i + j);
            }
            if(kernelWeight != 0)
                result /= kernelWeight;

            dst.write(i + kernelOffset, (int)result);

            // System.out.println(src.read(i + kernelOffset) + " => " + dst.read(i + kernelOffset));
        }
        // System.out.println("filtered " + dst.samples + " samples.");
    }
}
