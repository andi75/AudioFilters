
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author andreas
 */
public class EnvelopeControl extends JPanel implements ChangeListener
{
    Envelope envelope;
    EnvelopeView view;
    SimpleSynthControl ctrl;
    
    JSlider sustainLevelSlider;
    JSlider attackSlider;
    JSlider decaySlider;
    JSlider sustainSlider;
    JSlider releaseSlider;

    // TODO: don't save view or ctrl, switch to a notification mechanism
    public EnvelopeControl(Envelope envelope, EnvelopeView view,
            SimpleSynthControl ctrl)
    {
        this.envelope = envelope;
        this.view = view;
        this.ctrl = ctrl;

        this.setLayout(new GridBagLayout());
        GridBagConstraints gc;

        JLabel l;

        l = new JLabel("Sustain Level:");
        l.setHorizontalAlignment(JLabel.RIGHT);
        gc = new GridBagConstraints();
        gc.gridx = 0; gc.gridy = 0; gc.insets = new Insets(0, 6, 0, 3);
        this.add(l, gc);

        sustainLevelSlider = new JSlider(0, 100, (int) (envelope.sustainLevel * 100));
        sustainLevelSlider.addChangeListener(this);
        gc = new GridBagConstraints();
        gc.gridx = 1; gc.gridy = 0;
        this.add(sustainLevelSlider, gc);

        l = new JLabel("Attack:");
        l.setHorizontalAlignment(JLabel.RIGHT);
        gc = new GridBagConstraints();
        gc.gridx = 0; gc.gridy = 1; gc.insets = new Insets(0, 6, 0, 3);
        this.add(l, gc);
        attackSlider = new JSlider(0, 100, (int) (envelope.attack * 100));
        attackSlider.addChangeListener(this);
        gc = new GridBagConstraints();
        gc.gridx = 1; gc.gridy = 1;
        this.add(attackSlider, gc);

        l = new JLabel("Decay:");
        l.setHorizontalAlignment(JLabel.RIGHT);
        gc = new GridBagConstraints();
        gc.gridx = 0; gc.gridy = 2; gc.insets = new Insets(0, 6, 0, 3);
        this.add(l, gc);
        decaySlider = new JSlider(0, 100, (int) (envelope.decay * 100));
        decaySlider.addChangeListener(this);
        gc = new GridBagConstraints();
        gc.gridx = 1; gc.gridy = 2;
        this.add(decaySlider, gc);

        l = new JLabel("Sustain:");
        l.setHorizontalAlignment(JLabel.RIGHT);
        gc = new GridBagConstraints();
        gc.gridx = 0; gc.gridy = 3; gc.insets = new Insets(0, 6, 0, 3);
        this.add(l, gc);
        sustainSlider = new JSlider(0, 100, (int) (envelope.sustain * 100));
        sustainSlider.addChangeListener(this);
        gc = new GridBagConstraints();
        gc.gridx = 1; gc.gridy = 3;
        this.add(sustainSlider, gc);

        l = new JLabel("Release:");
        l.setHorizontalAlignment(JLabel.RIGHT);
        gc = new GridBagConstraints();
        gc.gridx = 0; gc.gridy = 4; gc.insets = new Insets(0, 6, 0, 3);
        this.add(l, gc);
        releaseSlider = new JSlider(0, 100, (int) (envelope.release * 100));
        releaseSlider.addChangeListener(this);
        gc = new GridBagConstraints();
        gc.gridx = 1; gc.gridy = 4;
        this.add(releaseSlider, gc);
    }

    public void stateChanged(ChangeEvent e) {
        if(e.getSource() == sustainLevelSlider)
        {
            envelope.sustainLevel = ((JSlider) e.getSource()).getValue() / 100.0;
        }
        if(e.getSource() == attackSlider)
        {
            envelope.attack = ((JSlider) e.getSource()).getValue() / 100.0;
        }
        if(e.getSource() == decaySlider)
        {
            envelope.decay = ((JSlider) e.getSource()).getValue() / 100.0;
        }
        if(e.getSource() == sustainSlider)
        {
            envelope.sustain = ((JSlider) e.getSource()).getValue() / 100.0;
        }
        if(e.getSource() == releaseSlider)
        {
            envelope.release = ((JSlider) e.getSource()).getValue() / 100.0;
        }
        ctrl.redoWave();
        view.repaint();
    }
}
