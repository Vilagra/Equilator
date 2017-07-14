package com.levenko.myequilator.adapters;

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

import com.levenko.myequilator.AllCards;
import com.levenko.myequilator.R;
import com.levenko.myequilator.entity.Card;

import java.io.IOException;
import java.util.List;
import java.util.Set;

/**
 * Created by Vilagra on 10.01.2017.
 */

@SuppressWarnings("DefaultFileTemplate")
public class MyAdapterForCard extends RecyclerView.Adapter<MyAdapterForCard.ViewHolder>{

    private final List<Card> mDataset;
    private final boolean[] flags;
    private final Set<Integer> choosen;
    private final Context ctx;
    private ColorStateList defaultColor;
    private final ColorStateList marked;
    private MyAdapterForCardListener listener;
    private final int numberOfCardsWhichUserMustChoose;

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
        public final ImageView imageView;
        final CardView cardView;
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
        return new ViewHolder(cardView, (ImageView) cardView.findViewById(R.id.image_card));
    }

    public Set<Integer> getChoosen() {
        return choosen;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        try {
            holder.imageView.setImageDrawable(Drawable.createFromStream(ctx.getAssets().
                    open(mDataset.get(position).getStringOfCard()+".png"), null));
        } catch (IOException e) {
            e.printStackTrace();
        }

        if(choosen.contains(position)){
            holder.cardView.setCardBackgroundColor(marked);
        }else {
            if(flags[position]) {
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
                        choosen.add(holder.getAdapterPosition());
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
