package com.stevebrecher.showdown;

/**
 * Created by Vilagra on 26.04.2017.
 */

@SuppressWarnings("DefaultFileTemplate")
public interface CalculatingInProgressListener {
    void sendProgress(long[] wins, double[] pars, long trail);
}
