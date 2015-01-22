
import javax.sound.sampled.*;

public class AudioRealTime {

    /**
     * @param args
     */
    public static void main(String[] args) {
        float samples = 22050.0f;
        float seconds = 10.0f;
        int bitsPerSample = 16;
        int lineBufferSize = 1024;

        int bufSize = (int) (seconds * samples * bitsPerSample) / 8;
        System.out.println("bufSize is " + bufSize);
        byte buf[] = new byte[bufSize];

        AudioFormat format = new AudioFormat(samples, bitsPerSample, 1, true, false);

        int selectedMixer = -1;
        Mixer.Info mixerInfo[] = AudioSystem.getMixerInfo();
        for(int i = 0; i < mixerInfo.length; i++)
        {
            System.out.println("Mixer " + i + ":");
            System.out.println(mixerInfo[i].getName());
            System.out.println(mixerInfo[i].getDescription());
            System.out.println(mixerInfo[i].getVendor());
            System.out.println(mixerInfo[i].getVersion());
            String mixerName = "Lexicon I-ONIX U42s";
            // String mixerName = "Built-in Input";
            if(mixerInfo[i].getName().compareTo(mixerName) == 0)
            {
                selectedMixer = i;
                System.out.println("selecting " + mixerName);
            }
        }

        SourceDataLine srcLine = null;
        DataLine.Info srcInfo = new DataLine.Info(SourceDataLine.class, format);
        try {
            // srcLine = srcLine = (SourceDataLine) AudioSystem.getLine(srcInfo);
            srcLine = AudioSystem.getSourceDataLine(format, mixerInfo[0]);
            srcLine.open(format, lineBufferSize);
        } catch (LineUnavailableException e) {
            System.out.println(e);
            System.exit(1);
        } catch (Exception e) {
            System.out.println(e);
            System.exit(1);
        }

        TargetDataLine dstLine = null;
        DataLine.Info dstInfo = new DataLine.Info(TargetDataLine.class, format);
        try {
            if(selectedMixer == -1)
            {
                System.out.println("loading default input mixer");
                dstLine = (TargetDataLine) AudioSystem.getLine(dstInfo);
            }
            else
            {
                System.out.println("loading " + mixerInfo[selectedMixer].getName());
                dstLine = AudioSystem.getTargetDataLine(format, mixerInfo[selectedMixer]);
            }
            dstLine.open(format, lineBufferSize);
        } catch (LineUnavailableException e) {
            System.out.println(e);
            System.exit(1);
        }
        dstLine.start();
        srcLine.start();

        while(true)
        {
            int toRead = dstLine.available();
            if(toRead == 0)
                dstLine.drain();

            if(toRead > 0)
            {
                System.out.println("available:" + toRead);
                int count = dstLine.read(buf, 0, Math.min(toRead, bufSize));
                System.out.println("read " + count + "bytes");
                
                srcLine.write(buf, 0, count);
            }
        }
        //int range = (1 << (bitsPerSample - 1)) - 1;
        // System.out.println("sample range: " + (-range - 1) + " - " + range);
    }
}
