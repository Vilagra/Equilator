package com.example.myequilator.adapters;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.myequilator.AllCards;
import com.example.myequilator.CardsDialogFragment;
import com.example.myequilator.R;
import com.example.myequilator.RangeActivity;
import com.example.myequilator.entity.Card;
import com.example.myequilator.entity.IndexesDataWasChosen;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

/**
 * Created by Vilagra on 10.01.2017.
 */

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {

    private String[] mDataset;
    private  double[] result;
    Context ctx;
    private String[] textFromTextView;
    private IndexesDataWasChosen[] indexesDataWhichWasChoosen;

    public String[] getTextFromTextView() {
        return textFromTextView;
    }

    public void setTextFromTextView(String[] textFromTextView) {
        this.textFromTextView = textFromTextView;
    }

    public void setResult(double[] result) {
        this.result = result;
    }

    public MyAdapter(Context ctx, String[] data) {
        this.ctx = ctx;
        mDataset=data;
        textFromTextView =new String[data.length];
        result=new double[data.length];
        Arrays.fill(textFromTextView,"");
        Arrays.fill(result,-1.0);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements CardsDialogFragment.CardDialogFragmentListener,View.OnClickListener {
        // each data item is just a string in this case
        CardView cardView;
        TextView mTextView;
        TextView editText;
        TextView result;
        ImageButton range;
        ImageButton hand;
        ImageButton remove;
        //CardsDialogFragment newFragment;
        public ViewHolder(CardView card) {
            super(card);
            cardView=card;
            mTextView = (TextView) card.findViewById(R.id.position);
            editText = (TextView) card.findViewById(R.id.hand_range);
            result = (TextView) card.findViewById(R.id.eqiuty);
            hand = (ImageButton) card.findViewById(R.id.hand);
            range = (ImageButton) card.findViewById(R.id.range);
            remove = (ImageButton) card.findViewById(R.id.remove);
            hand.setOnClickListener(this);
            remove.setOnClickListener(this);
            range.setOnClickListener(this);
        }
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.hand:
                    Set<Integer> setPositioWasChoosen = new HashSet<>();
                    if(!editText.getText().toString().equals("")) {
                        String s = editText.getText().toString();
                        for (int i = 0; i < s.length(); i +=2) {
                            Card card = AllCards.findCardByString(s.substring(i, i + 2));
                            int position = AllCards.allCards.indexOf(card);
                            AllCards.wasChosen[position] = false;
                            setPositioWasChoosen.add(position);
                        }
                        editText.setText("");
                    }
                    FragmentTransaction ft = ((Activity)ctx).getFragmentManager().beginTransaction();
                    CardsDialogFragment newFragment = new CardsDialogFragment();
                    newFragment.setPositionOfChoosenCard(setPositioWasChoosen);
                    newFragment.setPositionOfAdapter(getAdapterPosition());
                    newFragment.setNumberOfCardsWhichUserMustChoose(2);
                    newFragment.setmListener(this);
                    ft.addToBackStack(null);
                    newFragment.show(ft, "dialog");
                    break;
                case R.id.range:
                    ctx.startActivity(new Intent(ctx, RangeActivity.class));
                    break;
                case R.id.remove:
                    String s = editText.getText().toString();
                    for (int i = 0; i < s.length(); i +=2) {
                        Card card = AllCards.findCardByString(s.substring(i, i + 2));
                        int position = AllCards.allCards.indexOf(card);
                        AllCards.wasChosen[position] = false;
                    }
                    textFromTextView[getAdapterPosition()]="";
                    editText.setText("");
                    break;
            }
        }
        @Override
        public void onDialogOkClick(DialogFragment dialog, Set<Integer> positionOfChoosenCard) {
            String s = "";
            positionOfChoosenCard=new TreeSet<Integer>(positionOfChoosenCard);
            for (Integer index : positionOfChoosenCard) {
                AllCards.wasChosen[index] = true;
                s += AllCards.allCards.get(index).getStringOfCard();
                }
            editText.setText(s);
            textFromTextView[getAdapterPosition()]=s;
        }

        @Override
        public void onDialogCancelClick(DialogFragment dialog) {
        }


    }


    // Create new views (invoked by the layout manager)
    @Override
    public MyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        CardView cardView = (CardView) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.data_item, parent, false);
        ViewHolder vh = new ViewHolder(cardView);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.mTextView.setText(mDataset[position]);
        String s= textFromTextView[position];
        holder.editText.setText(s);
        if(result[position]!=-1.0){
            holder.result.setText(new DecimalFormat("#.#").format(result[position]*100));
        }
        else{
            holder.result.setText("");
        }
    }

    @Override
    public int getItemCount() {
        return mDataset.length;
    }



}
