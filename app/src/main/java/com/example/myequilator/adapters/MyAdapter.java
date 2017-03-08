package com.example.myequilator.adapters;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.myequilator.AllCards;
import com.example.myequilator.CardsDialogFragment;
import com.example.myequilator.Constants;
import com.example.myequilator.MainActivity;
import com.example.myequilator.R;
import com.example.myequilator.RangeActivity;
import com.example.myequilator.entity.Card;
import com.example.myequilator.entity.IndexesDataWasChosen;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

/**
 * Created by Vilagra on 10.01.2017.
 */

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {

    private String[] mDataset;
    private double[] result;
    Context ctx;
    private String[] textFromTextView;
    private IndexesDataWasChosen[] arrayIndexesDataWhichWasChoosen;

    public IndexesDataWasChosen[] getArrayIndexesDataWhichWasChoosen() {
        return arrayIndexesDataWhichWasChoosen;
    }

    public void setArrayIndexesDataWhichWasChoosen(IndexesDataWasChosen[] arrayIndexesDataWhichWasChoosen) {
        this.arrayIndexesDataWhichWasChoosen = arrayIndexesDataWhichWasChoosen;
    }

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
        mDataset = data;
        textFromTextView = new String[data.length];
        result = new double[data.length];
        arrayIndexesDataWhichWasChoosen = new IndexesDataWasChosen[data.length];
        Arrays.fill(textFromTextView, "");
        Arrays.fill(result, -1.0);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements CardsDialogFragment.CardDialogFragmentListener, View.OnClickListener {
        // each data item is just a string in this case
        CardView cardView;
        TextView mTextView;
        TextView rangeOrHandTextView;
        TextView result;
        ImageButton range;
        ImageButton hand;
        ImageButton remove;

        //CardsDialogFragment newFragment;
        public ViewHolder(CardView card) {
            super(card);
            cardView = card;
            mTextView = (TextView) card.findViewById(R.id.position);
            rangeOrHandTextView = (TextView) card.findViewById(R.id.hand_range);
            result = (TextView) card.findViewById(R.id.eqiuty);
            hand = (ImageButton) card.findViewById(R.id.hand);
            range = (ImageButton) card.findViewById(R.id.range);
            remove = (ImageButton) card.findViewById(R.id.remove);
            hand.setOnClickListener(this);
            remove.setOnClickListener(this);
            range.setOnClickListener(this);
        }

        public void onClick(View v) {
            int position = getAdapterPosition();
            Set<Integer> setPositioWasChoosen=new HashSet<>();
            IndexesDataWasChosen indexes=arrayIndexesDataWhichWasChoosen[position];
            switch (v.getId()) {
                case R.id.hand:
                    if(indexes!=null) {
                        setPositioWasChoosen = indexes.getIndexesDataWasChosen();
                        for (Integer integer : setPositioWasChoosen) {
                            AllCards.wasChosen[integer] = false;
                        }
                        textFromTextView[position] = "";
                        rangeOrHandTextView.setText("");
                    }
                    FragmentTransaction ft = ((Activity) ctx).getFragmentManager().beginTransaction();
                    CardsDialogFragment newFragment = new CardsDialogFragment();
                    newFragment.setPositionOfChoosenCard(setPositioWasChoosen);
                    newFragment.setPositionOfAdapter(getAdapterPosition());
                    newFragment.setNumberOfCardsWhichUserMustChoose(2);
                    newFragment.setmListener(this);
                    ft.addToBackStack(null);
                    newFragment.show(ft, "dialog");
                    break;
                case R.id.range:
                    Bundle bundle = new Bundle();
                    bundle.putInt(Constants.POSITION_OF_ADAPTER,position);
                    ((Activity)ctx).startActivityForResult(new Intent(ctx, RangeActivity.class), Constants.REQUEST_CODE_RANGE);
                    //ctx.startActivity(new Intent(ctx, RangeActivity.class));
                    break;
                case R.id.remove:
                    if(indexes!=null) {
                        setPositioWasChoosen=indexes.getIndexesDataWasChosen();
                        for (Integer integer : setPositioWasChoosen) {
                            AllCards.wasChosen[integer] = false;
                        }
                        arrayIndexesDataWhichWasChoosen[position] = null;
                        textFromTextView[position] = "";
                        rangeOrHandTextView.setText("");
                        break;
                    }
            }
        }

        @Override
        public void onDialogOkClick(DialogFragment dialog, Set<Integer> positionOfChoosenCard) {
            String s = "";
            positionOfChoosenCard = new TreeSet<Integer>(positionOfChoosenCard);
            for (Integer index : positionOfChoosenCard) {
                AllCards.wasChosen[index] = true;
                s += AllCards.allCards.get(index).getStringOfCard();
            }
            rangeOrHandTextView.setText(s);
            textFromTextView[getAdapterPosition()] = s;
            arrayIndexesDataWhichWasChoosen[getAdapterPosition()] = new IndexesDataWasChosen(positionOfChoosenCard, IndexesDataWasChosen.Type.CARD);
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
        String s = textFromTextView[position];
        holder.rangeOrHandTextView.setText(s);
        if (result[position] != -1.0) {
            holder.result.setText(new DecimalFormat("#.#").format(result[position] * 100));
        } else {
            holder.result.setText("");
        }
    }

    @Override
    public int getItemCount() {
        return mDataset.length;
    }


}
