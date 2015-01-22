
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.sound.sampled.*;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;

public class SimpleSynthControl implements ActionListener, ChangeListener, LineListener
{
    private AudioBuffer buffer;
    private AudioBuffer fftBuffer;
    private AudioPlayer player;
    public AudioView view;
    public AudioView fftView;


    final int eSineWave = 0;
    final int eTriangleWave = 1;
    final int eSquareWave = 2;
    final int eSawtoothWave = 3;
    final String waveNames[] = { "SineWave", "TriangleWave", "SquareWave","SawtoothWave" };

    Envelope envelope;
    boolean hasEnvelope = false;
    boolean hasFilter = false;

    private int waveType = eSineWave;
    private double frequency;

    private JSlider freqSlider;
    private JLabel freqLabel;
    JButton startButton;
    private JButton stopButton;
    private JCheckBox repeatButton;
    private JCheckBox filterButton;
    private JCheckBox envelopeButton;

    private JComboBox waveBox;
    OvertoneSet oSet;
    boolean hasOvertoneSet = false;

    FFTController fftCtrl;

    SimpleSynthControl(double frequency,
            AudioBuffer buffer)
    {
        this.frequency = frequency;
        this.buffer = buffer;

        view = new AudioView(buffer);
        fftView = new AudioView(fftBuffer);

        fftCtrl = new FFTController(view, fftView);

        player = new AudioPlayer(buffer);
        player.view = view;
    }

    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == repeatButton)
        {
            player.loop = !player.loop;
            repeatButton.setSelected(player.loop);
        }
        if(e.getSource() == startButton)
        {
            player.startPlayback(this);
            startButton.setEnabled(false);
            stopButton.setEnabled(true);
        }
        if(e.getSource() == stopButton)
        {
            player.stopPlayback();
            startButton.setEnabled(true);
            stopButton.setEnabled(false);
        }
        if(e.getSource() == waveBox)
        {
            waveType = waveBox.getSelectedIndex();
            redoWave();
        }
        if(e.getSource() == filterButton)
        {
            hasFilter = !hasFilter;
            repeatButton.setSelected(hasFilter);
            redoWave();
        }
        if(e.getSource() == envelopeButton)
        {
            hasEnvelope = !hasEnvelope;
            envelopeButton.setSelected(hasEnvelope);
            redoWave();
        }
    }

    void redoWave()
    {
            boolean isPaused = player.isPaused;
            player.isPaused = true;
            applyWave();
            player.isPaused = isPaused;
            view.rebuildImage();

            fftCtrl.redoFFT();
    }

    public void stateChanged(ChangeEvent e) {
        if(e.getSource() == freqSlider)
        {
            frequency = freqSlider.getValue();
            freqLabel.setText("" + frequency);
            redoWave();
        }
    }

    JSlider getFreqSlider() {
        freqSlider = new JSlider(JSlider.HORIZONTAL, 110, 440 * 4, (int)frequency);
        freqSlider.addChangeListener(this);
        return freqSlider;
    }

    JLabel getFreqLabel() {
        freqLabel = new JLabel("" + frequency);
        return freqLabel;
    }

    JButton getStartButton() {
        startButton = new JButton("Start");
        startButton.addActionListener(this);
        return startButton;
    }

    JButton getStopButton() {
        stopButton = new JButton("Stop");
        stopButton.addActionListener(this);
        stopButton.setEnabled(false);
        return stopButton;
    }

    JCheckBox getRepeatButton() {
        repeatButton = new JCheckBox("repeat");
        repeatButton.addActionListener(this);
        repeatButton.setSelected(player.loop);
        return repeatButton;
    }

    JCheckBox getFilterButton()
    {
        filterButton = new JCheckBox("filter");
        filterButton.addActionListener(this);
        filterButton.setSelected(hasFilter);
        return filterButton;
    }

    JCheckBox getEnvelopeButton()
    {
        envelopeButton = new JCheckBox("envelope");
        envelopeButton.addActionListener(this);
        envelopeButton.setSelected(hasEnvelope);
        return envelopeButton;
    }

    JComboBox getWaveComboBox()
    {
        waveBox = new JComboBox(waveNames);
        waveBox.addActionListener(this);
        return waveBox;
    }

    private void applyWave()
    {
        for(int i = 0; i < buffer.samples; i++)
        {
            double t = i / buffer.sampleRate;
            double value = 0;
            if(!hasOvertoneSet)
            {
                switch(waveType)
                {

                    case eSineWave:
                        value = WaveForms.sineWave(t * frequency);
                        break;
                    case eSquareWave:
                        value = WaveForms.squareWave(t * frequency);
                        break;
                    case eTriangleWave:
                        value = WaveForms.triangleWave( t * frequency);
                        break;
                    case eSawtoothWave:
                        value = WaveForms.sawtoothWave( t * frequency);
                        break;
                }
            }
            else
            {
                for(int j = 0; j < oSet.coefficients.length; j++)
                {
                    double oPart = 0;
                    switch(waveType)
                    {

                        case eSineWave:
                            oPart = WaveForms.sineWave(t * frequency * (j + 1));
                            break;
                        case eSquareWave:
                            oPart = WaveForms.squareWave(t * frequency * (j + 1));
                            break;
                        case eTriangleWave:
                            oPart = WaveForms.triangleWave( t * frequency * (j + 1));
                            break;
                        case eSawtoothWave:
                            oPart = WaveForms.sawtoothWave( t * frequency * (j + 1));
                            break;
                    }
                    value += oPart * oSet.coefficients[j];
                }
            }
            if(hasEnvelope)
            {
                value *= Modulation.modulate(t, envelope);
            }
            // double value = buffer.maxValue / 2 * triangleWave(t * 3000); //  * envelope(t, 0.2, 0.01, 0.05, 0.2, 0.1);
            // double value = buffer.maxValue / 30 * squareWave(t * 880) * envelope(t, 0.8, 0.2, 0.05, 0.2, 0.1);
            // double value = maxValue / 2 * noise(t * 110) * envelope(t, 0.8, 0.005, 0.01, 0.01, 0.01);
            // double value = maxValue / 2 * sineWave(t * 440);
            value *= (1 << (buffer.bitsPerSample - 1)) - 1;
            value *= 0.5;
            // System.out.println(value);
            buffer.write(i, (int) value);
            player.setLength(buffer.samples * buffer.bitsPerSample / 8);
        }
        if(hasFilter)
        {
            AudioFilter filter = new AudioFilter(buffer);
            filter.filter();
            player.buffer = filter.dst;
            view.buffer = filter.dst;
            buffer = filter.dst;
        }

        fftCtrl.redoFFT();
    }

    public void update(LineEvent event) {
        if(event.getType() == LineEvent.Type.STOP)
        {
            startButton.setEnabled(true);
            stopButton.setEnabled(false);
        }
    }

}

