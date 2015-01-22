/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author andreas
 */

import javax.sound.sampled.*;

// all audio data is stored as little endian

public class AudioRecorder extends AudioSeekable implements Runnable {
    TargetDataLine dstLine = null;
    boolean isRecording = false;

    public AudioRecorder(AudioBuffer buffer)
    {
        this.buffer = buffer;
    }

    public void startRecording()
    {
        AudioFormat format = new AudioFormat(buffer.sampleRate, buffer.bitsPerSample, 1, true, false);
        DataLine.Info dstInfo = new DataLine.Info(TargetDataLine.class, format);
        try {
            dstLine = (TargetDataLine) AudioSystem.getLine(dstInfo);
            dstLine.open(format);
        } catch (LineUnavailableException e) {
            System.out.println(e);
            System.exit(1);
        }
        dstLine.start();

        isRecording = true;
        new Thread(this).start();
    }

    public void stopRecording()
    {
        isRecording = false;
    }

    public void run() {
        while(isRecording)
        {
            int toRead = dstLine.available();
            if(toRead == 0)
                dstLine.drain();

            if(toRead > 0)
            {
                System.out.println("available:" + toRead);
                if(toRead + position > buffer.bufSize)
                {
                    toRead = buffer.bufSize - position;
                    isRecording = false;
                    System.out.println("buffer full, stopping the recording");
                }
                int count = dstLine.read(buffer.buffer, position, toRead);
                position += count;
                System.out.println("recorded " + count + " bytes");
            }
        }
        length = position;
        System.out.println("stopped recording after " + getPosition() + " seconds.");
        dstLine.stop();
        dstLine.close();
    }
}
