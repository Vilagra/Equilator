package com.levenko.myequilator.entity;

import android.os.Bundle;

import com.levenko.myequilator.Constants;

/**
 * Created by Vilagra on 26.04.2017.
 */

@SuppressWarnings("ALL")
public class Progress {
    private final long[] wins;
    private final double[] partial;
    private final long trail;

    public Progress(Bundle bundle) {
        this.partial = bundle.getDoubleArray(Constants.PARTIAL_POTS);
        this.wins = bundle.getLongArray(Constants.WINS);;
        this.trail = bundle.getLong(Constants.TRAIL);;
    }


    public double[] result(){
        double[] result = new double[wins.length];
        for (int i = 0; i < result.length; i++) {
            result[i]= (wins[i]+partial[i]) * 100.0 / trail;
        }
        return result;
    }
}
