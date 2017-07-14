package com.levenko.myequilator.adapters;

import android.support.v7.widget.RecyclerView;

import com.levenko.myequilator.entity.DataFromIntent;

/**
 * Created by Vilagra on 22.03.2017.
 */

@SuppressWarnings("DefaultFileTemplate")
public abstract class MyAdapter<T extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<T> {
    abstract public void replacedIndexesDataWasChosen(DataFromIntent dataFromIntent);
    abstract public void replacedToTextFromTextView(DataFromIntent dataFromIntent);
}
