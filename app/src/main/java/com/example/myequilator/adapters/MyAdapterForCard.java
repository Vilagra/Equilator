package com.example.myequilator.adapters;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.myequilator.AllCards;
import com.example.myequilator.R;
import com.example.myequilator.entity.Card;

import java.io.IOException;
import java.util.List;
import java.util.Set;

/**
 * Created by Vilagra on 10.01.2017.
 */

public class MyAdapterForCard extends RecyclerView.Adapter<MyAdapterForCard.ViewHolder>{

    private List<Card> mDataset;
    private boolean[] flags;
    private Set<Integer> choosen;
    Context ctx;
    ColorStateList defaultColor;
    ColorStateList marked;
    MyAdapterForCardListener listener;
    int numberOfCardsWhichUserMustChoose;

    public void setListener(MyAdapterForCardListener listener) {
        this.listener = listener;
    }

    public interface MyAdapterForCardListener{
        void onClickByCard(Set<Integer> cards);
    }

    public MyAdapterForCard(Context contexts, Set<Integer> set, int numberOfCardsWhichUserMustChoose) {
        ctx=contexts;
        mDataset= AllCards.allCards;
        flags=AllCards.wasChosen;
        choosen =set;
        marked=ColorStateList.valueOf(ContextCompat.getColor(ctx,R.color.cyan));
        this.numberOfCardsWhichUserMustChoose=numberOfCardsWhichUserMustChoose;
    }



    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public ImageView imageView;
        CardView cardView;
        public ViewHolder(CardView card,ImageView v) {
            super(card);
            cardView=card;
            imageView = v;

        }
    }


    // Create new views (invoked by the layout manager)
    @Override
    public MyAdapterForCard.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                          int viewType) {
        CardView cardView = (CardView) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_item, parent, false);
        if(defaultColor ==null) {
            defaultColor = cardView.getCardBackgroundColor();
        }
        ViewHolder vh = new ViewHolder(cardView, (ImageView) cardView.findViewById(R.id.image_card));
        return vh;
    }

    public Set<Integer> getChoosen() {
        return choosen;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        try {
            holder.imageView.setImageDrawable(Drawable.createFromStream(ctx.getAssets().open(mDataset.get(position).getStringOfCard()+".png"), null));
        } catch (IOException e) {
            e.printStackTrace();
        }

        if(choosen.contains(position)){
            holder.cardView.setCardBackgroundColor(marked);
        }else {
            if(flags[position]==true) {
                holder.cardView.setEnabled(false);
                //holder.cardView.setVisibility(View.INVISIBLE);
                holder.cardView.setAlpha(0.3f);
            }
            else {
                holder.cardView.setEnabled(true);
                holder.cardView.setAlpha(1.0f);
                //holder.cardView.setVisibility(View.VISIBLE);
                holder.cardView.setCardBackgroundColor(defaultColor);
            }

        }
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(choosen.size()<numberOfCardsWhichUserMustChoose){
                    if(choosen.remove(position)){
                        holder.cardView.setCardBackgroundColor(defaultColor);
                    }else {
                        holder.cardView.setCardBackgroundColor(marked);
                        choosen.add(position);
                        listener.onClickByCard(choosen);
                    }
                }
                else{
                    if(choosen.remove(position)){
                        holder.cardView.setCardBackgroundColor(defaultColor);
                        listener.onClickByCard(choosen);
                    }
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

}
