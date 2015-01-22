/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author andreas
 */
public class Envelope {
    double attack, decay, sustain, release;
    double sustainLevel;

    public Envelope(double sustainLever, double attack, double decay,
            double sustain, double release)
    {
        this.sustainLevel = sustainLever;
        this.attack = attack;
        this.decay = decay;
        this.sustain = sustain;
        this.release = release;
    }

    double getLength()
    {
        return attack + decay + sustain + release;
    }
}
