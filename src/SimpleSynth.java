/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author andreas
 */

import javax.swing.*;
import java.awt.*;

public class SimpleSynth {
    public static void main(String argv[])
    {
        new AudioRecorderUI();
        
        float freq = 22050;
        AudioBuffer buffer = new AudioBuffer(freq, 16, 1.0);

        SimpleSynthControl ctrl = new SimpleSynthControl(440,buffer);
        Envelope e = new Envelope(0.6, 0.1, 0.1, 0.2, 0.1);
        ctrl.envelope = e;
        ctrl.hasEnvelope = true;
        ctrl.redoWave();

        ctrl.view.setPreferredSize(new Dimension(600, 200));
        ctrl.fftView.setPreferredSize(new Dimension(600, 200));

        JFrame frame = new JFrame("SympleSynth - build 167");
        frame.setLocation(50, 50);
        frame.setLayout(new BorderLayout());

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(ctrl.getStartButton());
        buttonPanel.add(ctrl.getStopButton());
        buttonPanel.add(ctrl.getRepeatButton());
        buttonPanel.add(ctrl.getFilterButton());
        buttonPanel.add(ctrl.getEnvelopeButton());

        JPanel sliderPanel = new JPanel();
        sliderPanel.add(ctrl.getWaveComboBox());
        sliderPanel.add(new JLabel("Frequency: "));
        sliderPanel.add(ctrl.getFreqLabel());
        sliderPanel.add(ctrl.getFreqSlider());

        JPanel viewPanel = new JPanel();

        JPanel sampleViewPanel = new JPanel();
        sampleViewPanel.setLayout(new BorderLayout());
        sampleViewPanel.add(ctrl.view, BorderLayout.NORTH);
        AudioViewControl viewCtrl = new AudioViewControl(ctrl.view);
        sampleViewPanel.add(viewCtrl.getOffsetBar(), BorderLayout.CENTER);

        JPanel barPanel = new JPanel();
        // barPanel.add(new JLabel("Position:"));
        barPanel.add(new JLabel("Zoom:  "));
        barPanel.add(new JLabel("+"));
        barPanel.add(viewCtrl.getZoomSlider());
        barPanel.add(new JLabel("-"));
        sampleViewPanel.add(barPanel, BorderLayout.SOUTH);

        viewPanel.setLayout(new BorderLayout());
        viewPanel.add(sampleViewPanel, BorderLayout.NORTH);
        viewPanel.add(new JSeparator(JSeparator.HORIZONTAL), BorderLayout.CENTER);
        viewPanel.add(ctrl.fftView, BorderLayout.SOUTH);

        frame.add(buttonPanel, BorderLayout.NORTH);
        frame.add(viewPanel, BorderLayout.CENTER);
        frame.add(sliderPanel, BorderLayout.SOUTH);

        frame.pack();
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JFrame eFrame = new JFrame("Envelope");
        eFrame.setLocation(680, 50);
        EnvelopeView eView = new EnvelopeView(e);
        eView.setPreferredSize(new Dimension(100, 80));
        eFrame.setLayout(new GridLayout(2, 1));
        eFrame.add(eView);
        eFrame.add(new EnvelopeControl(e, eView, ctrl));
        eFrame.pack();
        eFrame.setVisible(true);

        JFrame overtoneFrame = new JFrame("Overtone");
        overtoneFrame.setLocation(680, 380);
        OvertoneSet oSet = new OvertoneSet(13);
        OvertoneControl oCtl = new OvertoneControl(oSet, overtoneFrame, ctrl);
        ctrl.oSet = oSet;
        ctrl.hasOvertoneSet = true;
        overtoneFrame.pack();
        overtoneFrame.setVisible(true);

        // frame.requestFocus();
        ctrl.startButton.requestFocus();
        /*     */
    }
}
