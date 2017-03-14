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
        mDataset = AllCards.allCombinationsInRecyclerOrderInStrings;
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
        final String value=mDataset.get(position);
        holder.textView.setText(value);
        setColor(value,holder.textView,position);
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(choosen.remove(position)){
                    setColor(value,holder.textView,position);
                }
                else {
                    choosen.add(position);
                    setColor(value,holder.textView,position);
                }
            }
        });

    }

    void setColor(String value, View v,int position){
        if(choosen.contains(position)){
            v.setBackgroundColor(ContextCompat.getColor(ctx, R.color.yellow));
            return;
        }
        if(value.matches("\\w+s")){
            v.setBackgroundColor(ContextCompat.getColor(ctx,R.color.cyan));
        }
        else if(value.matches("\\w+o")){
            v.setBackgroundColor(ContextCompat.getColor(ctx,R.color.purple));
        }
        else{
            v.setBackgroundColor(ContextCompat.getColor(ctx,R.color.blue));
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