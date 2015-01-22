import javax.swing.JFrame;

/**
 *
 * @author andreas
 */
public class TestAudioView {
    public static void main(String argv[])
    {
        int data[] = { 0, -10000, -30000, 10000, 50000, -50000, 30000 };

        AudioBuffer buffer = new AudioBuffer(data.length, 16, 1);
        for(int i = 0; i < data.length; i++)
        {
            buffer.write(i, data[i]);
        }
        AudioView view = new AudioView(buffer);
        view.zoom = 800 / buffer.samples;
        JFrame frame = new JFrame("TestAudioView");
        frame.add(view);
        frame.setBounds(200, 200, 800, 600);
        frame.setVisible(true);
        
    }
}
