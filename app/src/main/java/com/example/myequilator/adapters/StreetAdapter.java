package com.example.myequilator.adapters;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.FragmentTransaction;
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
import com.example.myequilator.MainActivity;
import com.example.myequilator.R;
import com.example.myequilator.entity.Card;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Vilagra on 10.01.2017.
 */

public class StreetAdapter extends RecyclerView.Adapter<StreetAdapter.ViewHolder> {

    private String[] mDataset;
    Activity act;
    private String[] textFromEditViewStreet;

    public String[] getTextFromEditViewStreet() {
        return textFromEditViewStreet;
    }

    public void setTextFromEditViewStreet(String[] textFromEditViewStreet) {
        this.textFromEditViewStreet = textFromEditViewStreet;
    }


    public StreetAdapter(Activity act, String[] data) {
        this.act = act;
        mDataset = data;
        textFromEditViewStreet = new String[data.length];
        Arrays.fill(textFromEditViewStreet, "");
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        // each data item is just a string in this case
        CardView cardView;
        TextView mTextView;
        EditText editText;
        ImageButton hand;
        ImageButton remove;

        public ViewHolder(CardView card) {
            super(card);
            cardView = card;
            mTextView = (TextView) card.findViewById(R.id.street);
            editText = (EditText) card.findViewById(R.id.hand_range_street);
            hand = (ImageButton) card.findViewById(R.id.hand_street);
            remove = (ImageButton) card.findViewById(R.id.remove_street);
            hand.setOnClickListener(this);
            remove.setOnClickListener(this);

        }

        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.hand_street:
                    Set<Integer> setPositioWasChoosen = new HashSet<>();
                    if (!editText.getText().toString().equals("")) {
                        String s = editText.getText().toString();
                        for (int i = 0; i < s.length(); i +=2) {
                            Card card = AllCards.findCardByString(s.substring(i, i + 2));
                            int position = AllCards.allCards.indexOf(card);
                            AllCards.wasChosen[position] = false;
                            setPositioWasChoosen.add(position);
                        }
                        editText.setText("");
                    }
                    FragmentTransaction ft = StreetAdapter.this.act.getFragmentManager().beginTransaction();
                    CardsDialogFragment newFragment = new CardsDialogFragment();
                    newFragment.setPositionOfChoosenCard(setPositioWasChoosen);
                    newFragment.setPositionOfAdapter(getAdapterPosition());
                    switch (mTextView.getText().toString()){
                        case "Flop":
                            newFragment.setNumberOfCardsWhichUserMustChoose(3);
                            break;
                        default:
                            newFragment.setNumberOfCardsWhichUserMustChoose(1);
                    }
                    //newFragment.setmListener(this);
                    ft.addToBackStack(null);
                    newFragment.show(ft, "dialog");
                    break;
                case R.id.remove_street:
                    String s = editText.getText().toString();
                    for (int i = 0; i < s.length(); i +=2) {
                        Card card = AllCards.findCardByString(s.substring(i, i + 2));
                        int position = AllCards.allCards.indexOf(card);
                        AllCards.wasChosen[position] = false;
                    }
                    textFromEditViewStreet[getAdapterPosition()] = "";
                    editText.setText("");
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
        if(mDataset[position].equals("Flop")){
            holder.editText.getLayoutParams().width=200;
        }
        holder.mTextView.setText(mDataset[position]);
        String s = textFromEditViewStreet[position];
        holder.editText.setText(s);
    }

    @Override
    public int getItemCount() {
        return mDataset.length;
    }


}

