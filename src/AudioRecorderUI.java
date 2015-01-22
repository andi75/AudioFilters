
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class AudioRecorderUI implements ActionListener
{
    JButton startRecording;
    JButton stopOrPlayRecording;
    JButton applyFilter;
    boolean isRecording = false;
    boolean isPlaying = false;

    AudioRecorder recorder;
    AudioPlayer player;
    AudioView view;
    AudioView fftView;

    public static void main(String argv[])
    {
        new AudioRecorderUI();
    }

    AudioRecorderUI()
    {
        JFrame frame = new JFrame("Audiorecorder");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        view = new AudioView(null);
        view.setPreferredSize(new Dimension(400, 200));

        fftView = new AudioView(null);
        fftView.setPreferredSize(new Dimension(400, 200));

        // TODO: add fftView somehow

        JPanel sampleViewPanel = new JPanel();
        sampleViewPanel.setLayout(new BorderLayout());
        sampleViewPanel.add(view, BorderLayout.NORTH);
        AudioViewControl viewCtrl = new AudioViewControl(view);
        sampleViewPanel.add(viewCtrl.getOffsetBar(), BorderLayout.CENTER);
        JPanel barPanel = new JPanel();
        // barPanel.add(new JLabel("Position:"));
        barPanel.add(new JLabel("Zoom:  "));
        barPanel.add(new JLabel("+"));
        barPanel.add(viewCtrl.getZoomSlider());
        barPanel.add(new JLabel("-"));
        sampleViewPanel.add(barPanel, BorderLayout.SOUTH);

        frame.add(sampleViewPanel, BorderLayout.NORTH);

        frame.add(fftView, BorderLayout.CENTER);

        FFTController ctrl = new FFTController(view, fftView);

        JPanel buttonPanel = new JPanel();
        frame.add(buttonPanel, BorderLayout.SOUTH);

        startRecording = new JButton("Record");
        stopOrPlayRecording = new JButton("Play");
        stopOrPlayRecording.setEnabled(false);
        applyFilter = new JButton("ApplyFilter");
        applyFilter.setEnabled(false);
        buttonPanel.add(stopOrPlayRecording);
        buttonPanel.add(startRecording);
        buttonPanel.add(applyFilter);

        startRecording.addActionListener(this);
        stopOrPlayRecording.addActionListener(this);
        applyFilter.addActionListener(this);

        /*
        JPanel samplePanel = new JPanel();
        samplePanel.setPreferredSize(new Dimension(600, 200));
        frame.add(samplePanel, BorderLayout.CENTER);
        */
        
        frame.pack();
        frame.setVisible(true);

        float samples = 22050.0f;
        float seconds = 30.0f;

        AudioBuffer buffer = new AudioBuffer(samples, 8, seconds);

        view.buffer = buffer;
        recorder = new AudioRecorder(buffer);
        player = new AudioPlayer(buffer);
        player.view = view;
    }

    public void actionPerformed(ActionEvent e) {
        if(!isRecording && e.getSource() == startRecording)
        {
            startRecording.setEnabled(false);
            applyFilter.setEnabled(false);
            isRecording = true;
            stopOrPlayRecording.setText("Stop");
            stopOrPlayRecording.setEnabled(true);

            recorder.setPosition(0);
            recorder.startRecording();
            view.buffer = recorder.buffer;
        }
        else if(isRecording && e.getSource() == stopOrPlayRecording)
        {
            startRecording.setEnabled(true);
            applyFilter.setEnabled(true);
            isRecording = false;
            stopOrPlayRecording.setText("Play");

            recorder.stopRecording();
            view.repaint();
        }
        else if(!isRecording && !isPlaying && e.getSource() == stopOrPlayRecording)
        {
            startRecording.setEnabled(false);
            isPlaying = true;
            stopOrPlayRecording.setText("Stop");

            player.length = recorder.length;
            player.setPosition(0);
            player.startPlayback(null);
        }
        else if(isPlaying && e.getSource() == stopOrPlayRecording)
        {
            startRecording.setEnabled(true);
            isPlaying = false;
            stopOrPlayRecording.setText("Play");

            player.stopPlayback();
        }
        else if(!isRecording && !isPlaying && e.getSource() == applyFilter)
        {
            AudioFilter filter = new AudioFilter(recorder.buffer);
            filter.filter();
            player.buffer = filter.dst;
            view.buffer = filter.dst;
            recorder.buffer = filter.dst;
            view.repaint();
        }
    }
}
