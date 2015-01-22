/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author andreas
 */
public class Modulation {
    static double modulate(double t, Envelope envelope)
    {

        if(t < envelope.attack)
            return t / envelope.attack;
        t = t - envelope.attack;
        if(t < envelope.decay)
            return (envelope.sustainLevel * t + 1.0 * (envelope.decay - t) ) / envelope.decay;
        t = t - envelope.decay;
        if(t < envelope.sustain)
            return envelope.sustainLevel;
        t = t - envelope.sustain;
        if(t < envelope.release)
            return envelope.sustainLevel * (envelope.release - t) / envelope.release;

        return 0;
    }
}
