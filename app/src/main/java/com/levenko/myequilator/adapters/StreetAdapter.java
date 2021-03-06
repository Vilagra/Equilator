package com.levenko.myequilator.adapters;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.levenko.myequilator.AllCards;
import com.levenko.myequilator.CardsDialogFragment;
import com.levenko.myequilator.Constants;
import com.levenko.myequilator.R;
import com.levenko.myequilator.entity.DataFromIntent;
import com.levenko.myequilator.entity.IndexesDataWasChosen;

import java.util.Arrays;
import java.util.TreeSet;

/**
 * Created by Vilagra on 10.01.2017.
 */

@SuppressWarnings("DefaultFileTemplate")
public class StreetAdapter extends MyAdapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_FOOTER = 0;
    private static final int VIEW_TYPE_CELL = 1;
    private final String[] mDataset;
    private final Context ctx;
    private String[] textFromEditViewStreet;
    private IndexesDataWasChosen[] arrayIndexesDataWhichWasChoosen;
    private final Fragment fragment;

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


    public StreetAdapter(Context ctx, String[] data, Fragment fragment) {
        this.ctx = ctx;
        mDataset = data;
        textFromEditViewStreet = new String[data.length];
        arrayIndexesDataWhichWasChoosen = new IndexesDataWasChosen[data.length];
        this.fragment = fragment;
        Arrays.fill(textFromEditViewStreet, "");
    }

    public void clean() {
        arrayIndexesDataWhichWasChoosen = new IndexesDataWasChosen[mDataset.length];
        Arrays.fill(textFromEditViewStreet, "");
        notifyDataSetChanged();
    }

    public class ButtonHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        final Button button;

        public ButtonHolder(Button itemView) {
            super(itemView);
            button = itemView;
            button.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            ((Calculation)fragment).calculation();
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        // each data item is just a string in this case
        final CardView cardView;
        final TextView mTextView;
        final TextView handText;
        final ImageButton hand;
        final ImageButton remove;

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
            IndexesDataWasChosen indexes = arrayIndexesDataWhichWasChoosen[position];
            switch (v.getId()) {
                case R.id.hand_street:
                    FragmentTransaction ft = ((Activity) ctx).getFragmentManager().beginTransaction();
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
                    break;
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        return (position == mDataset.length) ? VIEW_TYPE_FOOTER : VIEW_TYPE_CELL;
    }


    private void removedDataByCurrentPosition(int position) {
        for (int i = position; i < mDataset.length; i++) {
            IndexesDataWasChosen indexesDataWasChosen = arrayIndexesDataWhichWasChoosen[i];
            if (indexesDataWasChosen != null) {
                AllCards.unCheckFlags(indexesDataWasChosen.getIndexesDataWasChosen());
            }
            arrayIndexesDataWhichWasChoosen[i] = null;
            textFromEditViewStreet[i] = "";
            this.notifyDataSetChanged();
        }

    }


    // Create new views (invoked by the layout manager)
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                      int viewType) {
        RecyclerView.ViewHolder vh;
        // create a new view
        if (viewType == VIEW_TYPE_CELL) {
            CardView cardView = (CardView) LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.street_item, parent, false);
            vh = new ViewHolder(cardView);
        } else {
            Button buttonView = (Button) LayoutInflater.from(parent.getContext()).inflate(R.layout.button, parent, false);
            vh = new ButtonHolder(buttonView);
        }
        return vh;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ViewHolder) {
            ViewHolder sHolder = (ViewHolder) holder;
            if (mDataset[position].equals("Flop")) {
                sHolder.handText.getLayoutParams().width = 170;
            }
            sHolder.mTextView.setText(mDataset[position]);
            String s = textFromEditViewStreet[position];
            sHolder.handText.setText(s);
            if (position == 1 || position == 2) {
                if (arrayIndexesDataWhichWasChoosen[position - 1] == null) {
                    setImageButtonEnabled(ctx, false, sHolder.hand);
                }
                else {
                    setImageButtonEnabled(ctx,true,sHolder.hand);
                }
            }
        }
    }

    private static void setImageButtonEnabled(Context ctxt, boolean enabled, ImageButton item) {
        item.setEnabled(enabled);
        @SuppressWarnings("deprecation") Drawable originalIcon = ctxt.getResources().getDrawable(R.drawable.ic_poker);
        Drawable icon = enabled ? originalIcon : convertDrawableToGrayScale(originalIcon);
        item.setImageDrawable(icon);
    }

    private static Drawable convertDrawableToGrayScale(Drawable drawable) {
        if (drawable == null) {
            return null;
        }
        Drawable res = drawable.mutate();
        res.setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_IN);
        return res;
    }

    @Override
    public int getItemCount() {
        return mDataset.length + 1;
    }

    public interface Calculation{
        void calculation();
    }


}

