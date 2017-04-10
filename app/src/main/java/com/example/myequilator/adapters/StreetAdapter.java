package com.example.myequilator.adapters;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.myequilator.AllCards;
import com.example.myequilator.CardsDialogFragment;
import com.example.myequilator.Constants;
import com.example.myequilator.MainActivity;
import com.example.myequilator.R;
import com.example.myequilator.entity.Card;
import com.example.myequilator.entity.DataFromIntent;
import com.example.myequilator.entity.IndexesDataWasChosen;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

/**
 * Created by Vilagra on 10.01.2017.
 */

public class StreetAdapter extends MyAdapter<StreetAdapter.ViewHolder> {

    private String[] mDataset;
    Context ctx;
    private String[] textFromEditViewStreet;
    private IndexesDataWasChosen[] arrayIndexesDataWhichWasChoosen;

    public void setArrayIndexesDataWhichWasChoosen(IndexesDataWasChosen[] arrayIndexesDataWhichWasChoosen) {
        this.arrayIndexesDataWhichWasChoosen = arrayIndexesDataWhichWasChoosen;
    }

    public IndexesDataWasChosen[] getArrayIndexesDataWhichWasChoosen() {

        return arrayIndexesDataWhichWasChoosen;
    }

    public void replacedIndexesDataWasChosen(DataFromIntent dataFromIntent) {
        int position = dataFromIntent.getPositionOfAdapter();
        IndexesDataWasChosen perviousIndexesDataWasChosen = arrayIndexesDataWhichWasChoosen[position];
        if (perviousIndexesDataWasChosen != null) {
            AllCards.unCheckFlags(perviousIndexesDataWasChosen.getIndexesDataWasChosen());
        }
        arrayIndexesDataWhichWasChoosen[position] = new IndexesDataWasChosen(dataFromIntent.getIndexesDataWasChosen(), dataFromIntent.getType());
        AllCards.checkFlags(dataFromIntent.getIndexesDataWasChosen());

    }

    public void replacedToTextFromTextView(DataFromIntent dataFromIntent) {
        textFromEditViewStreet[dataFromIntent.getPositionOfAdapter()] = dataFromIntent.getTextFromTextView();
    }

    public String[] getTextFromEditViewStreet() {
        return textFromEditViewStreet;
    }

    public void setTextFromEditViewStreet(String[] textFromEditViewStreet) {
        this.textFromEditViewStreet = textFromEditViewStreet;
    }


    public StreetAdapter(Context ctx, String[] data) {
        this.ctx = ctx;
        mDataset = data;
        textFromEditViewStreet = new String[data.length];
        arrayIndexesDataWhichWasChoosen = new IndexesDataWasChosen[data.length];
        Arrays.fill(textFromEditViewStreet, "");
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        // each data item is just a string in this case
        CardView cardView;
        TextView mTextView;
        TextView handText;
        ImageButton hand;
        ImageButton remove;

        public ViewHolder(CardView card) {
            super(card);
            cardView = card;
            mTextView = (TextView) card.findViewById(R.id.street);
            handText = (TextView) card.findViewById(R.id.hand_text);
            hand = (ImageButton) card.findViewById(R.id.hand_street);
            remove = (ImageButton) card.findViewById(R.id.remove_street);
            hand.setOnClickListener(this);
            remove.setOnClickListener(this);

        }

        public void onClick(View v) {
            int position = getAdapterPosition();
            IndexesDataWasChosen indexes=arrayIndexesDataWhichWasChoosen[position];
            switch (v.getId()) {
                case R.id.hand_street:
                    FragmentTransaction ft = ((Activity)ctx).getFragmentManager().beginTransaction();
                    CardsDialogFragment newFragment = new CardsDialogFragment();
                    if (indexes != null) {
                        AllCards.unCheckFlags(indexes.getIndexesDataWasChosen());
                        newFragment.setPositionOfChoosenCard(new TreeSet<>(indexes.getIndexesDataWasChosen()));
                    }

                    newFragment.setPositionOfAdapter(position);
                    switch (mTextView.getText().toString()) {
                        case "Flop":
                            newFragment.setNumberOfCardsWhichUserMustChoose(3);
                            break;
                        default:
                            newFragment.setNumberOfCardsWhichUserMustChoose(1);
                    }
                    newFragment.setKindOfAdapter(Constants.STREET_ADAPTER);
                    ft.addToBackStack(null);
                    newFragment.show(ft, "dialog");
                    break;
                case R.id.remove_street:
                    removedDataByCurrentPosition(position);
                    handText.setText("");
                    break;
            }
        }

/*
        @Override
        public void onDialogOkClick(DialogFragment dialog, Set<Integer> positionOfChoosenCard) {
            String s = "";
            for (Integer integer : positionOfChoosenCard) {
                AllCards.wasChosen[integer] = true;
                    s += AllCards.allCards.get(integer).getStringOfCard();
            }
            editText.setText(s);
            textFromEditViewStreet[getAdapterPosition()] = s;
        }

        @Override
        public void onDialogCancelClick(DialogFragment dialog) {
        }
*/


    }

    private void removedDataByCurrentPosition(int position) {
        IndexesDataWasChosen indexesDataWasChosen=arrayIndexesDataWhichWasChoosen[position];
        if(indexesDataWasChosen!=null) {
            AllCards.unCheckFlags(indexesDataWasChosen.getIndexesDataWasChosen());
        }
        arrayIndexesDataWhichWasChoosen[position]=null;
        textFromEditViewStreet[position]=null;
    }


    // Create new views (invoked by the layout manager)
    @Override
    public StreetAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                       int viewType) {
        // create a new view
        CardView cardView = (CardView) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.street_item, parent, false);
        ViewHolder vh = new ViewHolder(cardView);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (mDataset[position].equals("Flop")) {
            holder.handText.getLayoutParams().width = 170;
        }
        holder.mTextView.setText(mDataset[position]);
        String s = textFromEditViewStreet[position];
        holder.handText.setText(s);
    }

    @Override
    public int getItemCount() {
        return mDataset.length;
    }


}

