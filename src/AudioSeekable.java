/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author andreas
 */
public class AudioSeekable {
    /*! position in bytes */
    int position = 0;
    /*! length in bytes */
    int length = 0;
    AudioBuffer buffer;

    public float getPosition()
    {
        return position * 8 / (buffer.sampleRate * buffer.bitsPerSample);
    }

    public void setPosition(float position)
    {
        this.position = (int) (position * buffer.sampleRate * buffer.bitsPerSample) / 8;
    }

    public void setLength(int length)
    {
        this.length = length;
    }
}
