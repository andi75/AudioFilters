
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import javax.swing.JScrollBar;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;


class AudioViewControl implements AdjustmentListener, ChangeListener
{
    AudioView view;

    JScrollBar offsetBar;
    JSlider zoomSlider;

    AudioViewControl(AudioView view)
    {
        this.view = view;
    }

    public void adjustmentValueChanged(AdjustmentEvent e) {
        if(e.getSource() != offsetBar)
            return;

        if(view.buffer == null)
            return;

        view.offset = e.getValue() * view.buffer.samples / 100;
        System.out.println("setting offset to " + view.offset);
        view.rebuildImage();
    }

    public JSlider getZoomSlider()
    {
        zoomSlider = new JSlider(JSlider.HORIZONTAL, 1, 100, 1);
        zoomSlider.addChangeListener(this);

        zoomSlider.setValue(80);
        return zoomSlider;

    }

    public JScrollBar getOffsetBar()
    {
        offsetBar = new JScrollBar(JScrollBar.HORIZONTAL, 0, 1, 0, 100);
        offsetBar.addAdjustmentListener(this);
        return offsetBar;
    }

    public void stateChanged(ChangeEvent e) {
        if(e.getSource() != zoomSlider)
            return;

        view.zoom = 4.0 / Math.pow(zoomSlider.getValue(), 1.45);

        System.out.println("setting zoom to " + view.zoom);
        view.rebuildImage();
    }
}