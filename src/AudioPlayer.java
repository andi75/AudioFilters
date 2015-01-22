
import javax.sound.sampled.*;

public class AudioPlayer extends AudioSeekable implements Runnable
{
    SourceDataLine srcLine = null;
    boolean isPlaying = false;
    boolean isPaused = false;
    boolean loop = false;

    int lineBufferSize = 8192;

   AudioView view;

    AudioPlayer(AudioBuffer buffer)
    {
        this.buffer = buffer;
    }

    public void startPlayback(LineListener listener)
    {

        AudioFormat format = new AudioFormat(
                buffer.sampleRate, buffer.bitsPerSample,
                1, true, false); // mono, signed, little endian

        System.out.println("playback format: " + format);
        DataLine.Info srcInfo = new DataLine.Info(SourceDataLine.class, format);
        try {
            srcLine = (SourceDataLine) AudioSystem.getLine(srcInfo);
            srcLine.open(format, lineBufferSize);
        } catch (LineUnavailableException e) {
            System.out.println(e);
            System.exit(1);
        }
        srcLine.addLineListener(listener);
        srcLine.start();

        isPlaying = true;
        new Thread(this).start();
    }

    public void stopPlayback()
    {
        isPlaying = false;
    }

    public void run()
    {
        int chunkSize = 2048;
        byte zeros[] = new byte[chunkSize];

        while(isPlaying)
        {
            int toWrite = chunkSize;
            
            int remaining = srcLine.getBufferSize() - srcLine.available();
            if(remaining > toWrite)
            {
                try
                {
                    Thread.sleep(1);
                }
                catch (InterruptedException e)
                {
                    System.out.println(e);
                }
            }

            if(isPaused)
            {
                srcLine.write(zeros, 0, zeros.length);
                continue;
            }

            if(position + toWrite > length)
            {
                toWrite = length - position;
                if(!loop)
                {
                    isPlaying = false;
                    System.out.println("end reached, stopped the playback");
                }
                else
                {
                    System.out.println("end reached, looping");
                }
                System.out.println("writing " + toWrite + " bytes");
            }
            if(view != null)
            {
                view.setPlayPosition(position);
            }
            srcLine.write(buffer.buffer, position, toWrite);
            position += toWrite;

            if(!isPlaying)
            {
                System.out.println("stopped playback after " + getPosition() + " seconds and ." + position + " bytes");
            }
            if (position >= length)
                position = 0;
        }
        srcLine.drain();
        srcLine.stop();
        srcLine.close();

        if(view != null)
        {
            view.setPlayPosition(0);
        }
    }

}
