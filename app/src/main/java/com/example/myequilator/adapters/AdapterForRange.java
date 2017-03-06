package com.example.myequilator.adapters;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.myequilator.AllCards;
import com.example.myequilator.R;

import java.io.IOException;
import java.util.List;
import java.util.Set;

/**
 * Created by Vilagra on 06.03.2017.
 */
public class AdapterForRange extends RecyclerView.Adapter<AdapterForRange.ViewHolder> {

    private List<String> mDataset;
    private Set<Integer> choosen;
    Context ctx;
    //MyAdapterForRangeListener listener;

/*    public void setListener(MyAdapterForRangeListener listener) {
        this.listener = listener;
    }

    public interface MyAdapterForRangeListener{
        void onClickByCard(Set<Integer> cards);
    }*/

    public AdapterForRange(Context contexts, Set<Integer> set) {
        ctx = contexts;
        mDataset = AllCards.allCombinations;
        choosen = set;
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView textView;
        CardView cardView;

        public ViewHolder(CardView card, TextView v) {
            super(card);
            cardView = card;
            textView = v;

        }
    }


    // Create new views (invoked by the layout manager)
    @Override
    public AdapterForRange.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                         int viewType) {
        // create a new view
        CardView cardView = (CardView) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.range_item, parent, false);
        ViewHolder vh = new ViewHolder(cardView, (TextView) cardView.findViewById(R.id.combnation));
        return vh;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        String value=mDataset.get(position);
        holder.textView.setText(value);
        if(value.matches("\\w+s")){
            holder.textView.setBackgroundColor(ContextCompat.getColor(ctx,R.color.cyan));
        }
        else if(value.matches("\\w+o")){
            holder.textView.setBackgroundColor(ContextCompat.getColor(ctx,R.color.purple));
        }
        else{
            holder.textView.setBackgroundColor(ContextCompat.getColor(ctx,R.color.blue));
        }

    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}