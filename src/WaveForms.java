/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author andreas
 */
public class WaveForms {

    static double sineWave(double t)
    {
        return Math.sin(t * 2 * Math.PI);
    }

    static double noise(double t)
    {
        return Math.random();
    }

    static double squareWave(double t)
    {
        t = t - Math.floor(t);
        if(t < 0.5)
            return -1;
        else
            return 1;
    }
    static double triangleWave(double t)
    {
        t = t - Math.floor(t);
        if(t < 0.25) return t * 4;
        if(t < 0.75) return 2 - t * 4;
        return -4 + t * 4;
    }

    static double sawtoothWave(double t)
    {
        t = t - Math.floor(t);
        if(t < 0.5) return 2 * t;
        return 2 * t - 2;
    }
}
