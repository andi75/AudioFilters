
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author andreas
 */
public class AudioBuffer {
    float sampleRate;
    int bitsPerSample;
    int maxValue;
    int minValue;
    double seconds;
    int samples;

    byte buffer[];
    // ByteBuffer buf;
    int bufSize;

    public AudioBuffer(float sampleRate, int bitsPerSample, double seconds)
    {
        this.bitsPerSample = bitsPerSample;
        minValue = -(1 << (bitsPerSample - 1));
        maxValue = (1 << (bitsPerSample - 1)) - 1;

        this.sampleRate = sampleRate;
        this.seconds = seconds;

        bufSize = (int) (seconds * sampleRate * bitsPerSample) / 8;
        buffer = new byte[bufSize];
        // buf = ByteBuffer.wrap(buffer);
        // buf.order(ByteOrder.LITTLE_ENDIAN);

        samples = bufSize * 8 / bitsPerSample;
        System.out.println("bufSize = " + bufSize);
        System.out.println("samples = " + samples);
    }

    int clamp(int value)
    {
        if(value < minValue) return minValue;
        if(value > maxValue) return maxValue;
        return value;
    }

    public int read(int offset)
    {
        switch(bitsPerSample)
        {
            case 8: return read8(offset);
            case 16: return read16(offset);
            case 32: return read32(offset);
        }
        return 0;
    }
    public void write(int offset, int value)
    {
        switch(bitsPerSample)
        {
            case 8: write8(offset, value); break;
            case 16: write16(offset, value); break;
            case 32: write32(offset, value); break;
        }
    }

    public int read16(int offset)
    {
        // return (int) buf.getShort(offset);
        // The & 0xff is very important and is what I was missing before...
        return (buffer[2 * offset + 0] & 0xff) | (buffer[2 * offset + 1] << 8);
    }

    public void write16(int offset, int value)
    {
        short data = (short) clamp(value);
        // buf.putShort(offset, (short) value);
        buffer[2 * offset + 0] = (byte) ((data & 0xff));
        buffer[2 * offset + 1] = (byte) (((data & 0xff00) >> 8));
    }

    public int read8(int offset)
    {
        return buffer[offset];
    }
    public void write8(int offset, int value)
    {
        byte data = (byte) clamp(value);
        buffer[offset] = data;
    }

    public int getMaxSampleValue()
    {
        int max = Integer.MIN_VALUE;
        for(int i = 0; i < samples; i++)
        {
            int value = read(i);
            if(value > max)
                max = value;
        }
        return max;
    }

    public int getMinSampleValue()
    {
        int min = Integer.MAX_VALUE;
        for(int i = 0; i < samples; i++)
        {
            int value = read(i);
            if(value < min)
                min = value;
        }
        return min;
    }

    private int read32(int offset) {
        return
                (buffer[2 * offset + 0] & 0xff) |
                ((buffer[2 * offset + 1] & 0xff) << 8) |
                ((buffer[2 * offset + 2] & 0xff) << 16) |
                ((buffer[2 * offset + 3] & 0xff) << 24)
                ;
    }

    private void write32(int offset, int value) {
        buffer[2 * offset + 0] = (byte) ((value & 0xff));
        buffer[2 * offset + 1] = (byte) (((value & 0xff00) >> 8));
        buffer[2 * offset + 2] = (byte) (((value & 0xff0000) >> 16));
        buffer[2 * offset + 3] = (byte) (((value & 0xff000000) >> 24));
    }
}
