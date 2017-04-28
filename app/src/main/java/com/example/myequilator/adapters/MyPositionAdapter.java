package com.example.myequilator.adapters;

import android.app.Activity;
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
import com.example.myequilator.Constants;
import com.example.myequilator.R;
import com.example.myequilator.RangeActivity;
import com.example.myequilator.entity.DataFromIntent;
import com.example.myequilator.entity.IndexesDataWasChosen;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.HashSet;
import java.util.TreeSet;

/**
 * Created by Vilagra on 10.01.2017.
 */

public class MyPositionAdapter extends MyAdapter<MyPositionAdapter.ViewHolder> {

    private String[] mDataset;

    public double[] getEquity() {
        return equity;
    }

    private double[] equity;
    Context ctx;
    private String[] textFromTextView;
    private IndexesDataWasChosen[] arrayIndexesDataWhichWasChoosen;

    public void replacedIndexesDataWasChosen(DataFromIntent dataFromIntent){
        int position = dataFromIntent.getPositionOfAdapter();
        IndexesDataWasChosen perviousIndexesDataWasChosen=arrayIndexesDataWhichWasChoosen[position];
        if(perviousIndexesDataWasChosen!=null&&perviousIndexesDataWasChosen.getType()== IndexesDataWasChosen.Type.HAND){
            AllCards.unCheckFlags(perviousIndexesDataWasChosen.getIndexesDataWasChosen());
        }
        arrayIndexesDataWhichWasChoosen[position]=new IndexesDataWasChosen(dataFromIntent.getIndexesDataWasChosen(), dataFromIntent.getType());
        if(dataFromIntent.getType()== IndexesDataWasChosen.Type.HAND){
            AllCards.checkFlags(dataFromIntent.getIndexesDataWasChosen());
        }
    }
    public void replacedToTextFromTextView(DataFromIntent dataFromIntent){
        textFromTextView[dataFromIntent.getPositionOfAdapter()]=dataFromIntent.getTextFromTextView();
    }

    public void removedDataByCurrentPosition(int position){
        IndexesDataWasChosen indexesDataWasChosen=arrayIndexesDataWhichWasChoosen[position];
        if(indexesDataWasChosen!=null&&indexesDataWasChosen.getType()== IndexesDataWasChosen.Type.HAND){
            AllCards.unCheckFlags(indexesDataWasChosen.getIndexesDataWasChosen());
        }
        arrayIndexesDataWhichWasChoosen[position]=null;
        textFromTextView[position]="";
        equity[position] = -1.0;
    }


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

    public void setEquity(double[] equity) {
        this.equity = equity;
    }

    public int amountPlayers(){
        int res=0;
        for (IndexesDataWasChosen indexesDataWasChosen : arrayIndexesDataWhichWasChoosen) {
            if(indexesDataWasChosen!=null){
                res++;
            }
        }
        return res;
    }

    public MyPositionAdapter(Context ctx, String[] data) {
        this.ctx = ctx;
        mDataset = data;
        textFromTextView = new String[data.length];
        equity = new double[data.length];
        arrayIndexesDataWhichWasChoosen = new IndexesDataWasChosen[data.length];
        Arrays.fill(textFromTextView, "");
        Arrays.fill(equity, -1.0);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        // each data item is just a string in this case
        CardView cardView;
        TextView mTextView;
        TextView rangeOrHandTextView;
        TextView result;
        ImageButton range;
        ImageButton hand;
        ImageButton remove;
        ImageButton random;

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
            random = (ImageButton) card.findViewById(R.id.random);
            hand.setOnClickListener(this);
            remove.setOnClickListener(this);
            range.setOnClickListener(this);
            random.setOnClickListener(this);
        }

        public void onClick(View v) {
            int position = getAdapterPosition();
            IndexesDataWasChosen indexes=arrayIndexesDataWhichWasChoosen[position];

            switch (v.getId()) {
                case R.id.hand:
                    FragmentTransaction ft = ((Activity) ctx).getFragmentManager().beginTransaction();
                    CardsDialogFragment newFragment = new CardsDialogFragment();
                    if(indexes!=null&&indexes.getType()==IndexesDataWasChosen.Type.HAND){
                        AllCards.unCheckFlags(indexes.getIndexesDataWasChosen());
                        newFragment.setPositionOfChoosenCard(new TreeSet<>(indexes.getIndexesDataWasChosen()));
                    }
                    newFragment.setPositionOfAdapter(getAdapterPosition());
                    newFragment.setNumberOfCardsWhichUserMustChoose(2);
                    newFragment.setKindOfAdapter(Constants.POSITION_ADAPTER);
                    ft.addToBackStack(null);
                    newFragment.show(ft, "dialog");
                    break;
                case R.id.range:
                    Intent intent = new Intent(ctx, RangeActivity.class);
                    intent.putExtra(Constants.POSITION_OF_ADAPTER,position);
                    if(indexes!=null&&indexes.getType()==IndexesDataWasChosen.Type.RANGE){
                        intent.putExtra(Constants.INDEXES_DATA_WAS_CHOSEN,(HashSet)indexes.getIndexesDataWasChosen());
                    }
                    ((Activity)ctx).startActivityForResult(intent, Constants.REQUEST_CODE_RANGE);
                    break;
                case R.id.remove:
                    if(indexes!=null) {
                        removedDataByCurrentPosition(position);
                        rangeOrHandTextView.setText("");
                        result.setText("");
                    }
                    break;
                case R.id.random:
                    arrayIndexesDataWhichWasChoosen[position]=new IndexesDataWasChosen(AllCards.getIndexesByRecyclerBaseOnRanking(169), IndexesDataWasChosen.Type.RANGE);
                    textFromTextView[position]="random";
                    rangeOrHandTextView.setText("random");
                    break;
            }
        }
    }


    // Create new views (invoked by the layout manager)
    @Override
    public MyPositionAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
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
        if (equity[position] != -1.0) {
            holder.result.setText(new DecimalFormat("#.##").format(equity[position])+"%");
        } else {
            holder.result.setText("");
        }
    }

    @Override
    public int getItemCount() {
        return mDataset.length;
    }


}
