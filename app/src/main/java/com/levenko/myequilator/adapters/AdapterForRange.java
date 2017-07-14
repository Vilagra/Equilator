package com.levenko.myequilator.adapters;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.levenko.myequilator.AllCards;
import com.levenko.myequilator.R;
import com.levenko.myequilator.entity.Combination;

import java.util.List;
import java.util.Set;

/**
 * Created by Vilagra on 06.03.2017.
 */
@SuppressWarnings("DefaultFileTemplate")
public class AdapterForRange extends RecyclerView.Adapter<AdapterForRange.ViewHolder> {

    private final List<String> mDataset;
    private Set<Integer> chosen;

    private final int chosenColor;
    private final int pocketColor;
    private final int suitColor;
    private final int offsuitColor;

    public AdapterForRange(Context contexts, Set<Integer> set) {
        Context ctx = contexts;
        mDataset = AllCards.allCombinationsInRecyclerOrderInStrings;
        chosen = set;
        chosenColor =ContextCompat.getColor(ctx, R.color.yellow);
        suitColor=ContextCompat.getColor(ctx,R.color.cyan);
        offsuitColor=ContextCompat.getColor(ctx,R.color.purple);
        pocketColor = ContextCompat.getColor(ctx,R.color.blue);
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public final TextView textView;
        final CardView cardView;

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
        return new ViewHolder(cardView, (TextView) cardView.findViewById(R.id.combnation));
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final String value=mDataset.get(position);
        holder.textView.setText(value);
        final Combination.Kind kind=AllCards.combinationsMap.get(value).getKind();
        setColor(kind,holder.textView,holder.getAdapterPosition());
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(chosen.remove(position)){
                    setColor(kind,holder.textView,position);
                }
                else {
                    chosen.add(position);
                    setColor(kind,holder.textView,position);
                }
            }
        });

    }

    private void setColor(Combination.Kind kind, View v, int position){
        if(chosen.contains(position)){
            v.setBackgroundColor(chosenColor);
        }
        else if(kind== Combination.Kind.SUITED){
            v.setBackgroundColor(suitColor);
        }
        else if(kind== Combination.Kind.OFFSUITED){
            v.setBackgroundColor(offsuitColor);
        }
        else{
            v.setBackgroundColor(pocketColor);
        }

    }

    public Set<Integer> getChosen() {
        return chosen;
    }

    public void setChosen(Set<Integer> chosen) {
        this.chosen = chosen;
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

}