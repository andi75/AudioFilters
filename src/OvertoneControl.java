
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import javax.swing.JFrame;
import javax.swing.JLabel;
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
class OvertoneControl implements ChangeListener {
    JSlider sliders[];
    JLabel valueLabels[];
    double values;
    OvertoneSet oSet;
    SimpleSynthControl ctrl;

    public OvertoneControl(OvertoneSet oSet, JFrame overtoneFrame, SimpleSynthControl ctrl) {
        this.oSet = oSet;
        this.ctrl = ctrl;

        int n = oSet.coefficients.length;
        sliders = new JSlider[n];
        valueLabels = new JLabel[n];

        GridBagLayout gbl = new GridBagLayout();
        overtoneFrame.setLayout(gbl);
        for(int i = 0; i < n; i++)
        {
            JLabel label = new JLabel("k=" + (i + 1));
            label.setHorizontalAlignment(JLabel.RIGHT);

            GridBagConstraints gc;

            gc = new GridBagConstraints();
            gc.gridx = 0; gc.gridy = i; gc.insets = new Insets(0, 6, 0, 3);
            overtoneFrame.add(label, gc);

            sliders[i] = new JSlider(JSlider.HORIZONTAL, 0, 200, (int)((oSet.coefficients[i] + 1) * 100));
            gc = new GridBagConstraints();
            gc.gridx = 1; gc.gridy = i;
            overtoneFrame.add(sliders[i], gc);
            
            sliders[i].addChangeListener(this);

            valueLabels[i] = new JLabel(" " + oSet.coefficients[i] + "     ");
            gc = new GridBagConstraints();
            gc.gridx = 2; gc.gridy = i;
            gc.insets = new Insets(0, 3, 0, 6);
            gc.ipadx = 2;
            overtoneFrame.add(valueLabels[i], gc);
        }
    }

    public void stateChanged(ChangeEvent e) {
        for(int i = 0; i < sliders.length; i++)
        {
            if(e.getSource() == sliders[i])
            {
                oSet.coefficients[i] = Math.round(100 * (sliders[i].getValue() / 100.0 - 1)) / 100.0;
                valueLabels[i].setText(" " + oSet.coefficients[i] + " ");
                ctrl.redoWave();
                break;
            }
        }
    }

}
