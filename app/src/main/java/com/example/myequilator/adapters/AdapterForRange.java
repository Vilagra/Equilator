package com.example.myequilator.adapters;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.myequilator.AllCards;
import com.example.myequilator.R;
import com.example.myequilator.entity.Combination;
import com.example.myequilator.entity.IndexesDataWasChosen;

import java.util.List;
import java.util.Set;

/**
 * Created by Vilagra on 06.03.2017.
 */
public class AdapterForRange extends RecyclerView.Adapter<AdapterForRange.ViewHolder> {

    private List<String> mDataset;
    private Set<Integer> choosen;
    Context ctx;

    int choosenColor;
    int pocketColor;
    int suitColor;
    int offsuitColor;

    public AdapterForRange(Context contexts, Set<Integer> set) {
        ctx = contexts;
        mDataset = AllCards.allCombinationsInRecyclerOrderInStrings;
        choosen = set;
        choosenColor=ContextCompat.getColor(ctx, R.color.yellow);
        suitColor=ContextCompat.getColor(ctx,R.color.cyan);
        offsuitColor=ContextCompat.getColor(ctx,R.color.purple);
        pocketColor = ContextCompat.getColor(ctx,R.color.blue);
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
        final String value=mDataset.get(position);
        holder.textView.setText(value);
        final Combination.Kind kind=AllCards.combinationsMap.get(value).getKind();
        setColor(kind,holder.textView,position);
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(choosen.remove(position)){
                    setColor(kind,holder.textView,position);
                }
                else {
                    choosen.add(position);
                    setColor(kind,holder.textView,position);
                }
            }
        });

    }

    void setColor(Combination.Kind kind, View v, int position){
        if(choosen.contains(position)){
            v.setBackgroundColor(choosenColor);
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

    public Set<Integer> getChoosen() {
        return choosen;
    }

    public void setChoosen(Set<Integer> choosen) {
        this.choosen = choosen;
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

}