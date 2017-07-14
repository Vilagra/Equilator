package com.levenko.myequilator;

import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.levenko.myequilator.adapters.MyAdapterForCard;

import java.util.ArrayList;
import java.util.Set;
import java.util.TreeSet;

/**
 * Created by Vilagra on 12.01.2017.
 */

@SuppressWarnings("DefaultFileTemplate")
public class CardsDialogFragment extends DialogFragment implements MyAdapterForCard.MyAdapterForCardListener{

    private MyAdapterForCard myAdapterForCard;
    private CardDialogFragmentListener mListener;
    private Set<Integer> positionOfChoosenCard = new TreeSet<>();
    private int positionOfAdapter;
    private int numberOfCardsWhichUserMustChoose;
    private String kindOfAdapter;

    private Button buttonOk;

    public CardDialogFragmentListener getmListener() {
        return mListener;
    }

    public void setmListener(CardDialogFragmentListener mListener) {
        this.mListener = mListener;
    }

    public void setNumberOfCardsWhichUserMustChoose(int numberOfCardsWhichUserMustChoose) {
        this.numberOfCardsWhichUserMustChoose = numberOfCardsWhichUserMustChoose;
    }

    public void setPositionOfAdapter(int positionOfAdapter) {
        this.positionOfAdapter = positionOfAdapter;
    }
    public void setPositionOfChoosenCard(Set<Integer> positionOfChoosenCard) {
        this.positionOfChoosenCard = positionOfChoosenCard;
    }

    public void setKindOfAdapter(String kindOfAdapter) {
        this.kindOfAdapter = kindOfAdapter;
    }

    public interface CardDialogFragmentListener {
        void onDialogOkClick(Intent data);
        void onDialogCancelClick(int positionOfAdapter, String kindOfAdapter);
    }

    @Override
    public void onClickByCard(Set<Integer> positionOfChoosenCard) {
        if(positionOfChoosenCard.size()==numberOfCardsWhichUserMustChoose){
            buttonOk.setEnabled(true);
        }
        else{
            buttonOk.setEnabled(false);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putIntegerArrayList(Constants.WAS_CHOSEN, new ArrayList<>(myAdapterForCard.getChoosen()));
        outState.putInt(Constants.POSITION_OF_ADAPTER,positionOfAdapter);
        outState.putInt(Constants.NUMBER_OF_CARDS,numberOfCardsWhichUserMustChoose);
        outState.putString(Constants.KIND_OF_ADAPTER,kindOfAdapter);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int theme = android.R.style.Theme_Holo_Light_DialogWhenLarge;
        setStyle(DialogFragment.STYLE_NORMAL, theme);
        mListener = (CardDialogFragmentListener) getActivity();


    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if(savedInstanceState!=null) {
            positionOfChoosenCard = new TreeSet<>(savedInstanceState.getIntegerArrayList(Constants.WAS_CHOSEN));
            numberOfCardsWhichUserMustChoose = savedInstanceState.getInt(Constants.NUMBER_OF_CARDS);
            positionOfAdapter = savedInstanceState.getInt(Constants.POSITION_OF_ADAPTER);
            kindOfAdapter= savedInstanceState.getString(Constants.KIND_OF_ADAPTER);
        }
        getDialog().setTitle(getActivity().getString(R.string.card_selection));
        View v = inflater.inflate(R.layout.card_matrix, container, false);
        RecyclerView recyclerView = (RecyclerView) v.findViewById(R.id.recycler_card);
        GridLayoutManager manager = new GridLayoutManager(this.getActivity(), 4, GridLayoutManager.HORIZONTAL,false);
        recyclerView.setLayoutManager(manager);
        myAdapterForCard=new MyAdapterForCard(getActivity(),positionOfChoosenCard,numberOfCardsWhichUserMustChoose);
        myAdapterForCard.setListener(this);
        recyclerView.setAdapter(myAdapterForCard);
        buttonOk= (Button) v.findViewById(R.id.ok);
        if(positionOfChoosenCard.size()==numberOfCardsWhichUserMustChoose){
            buttonOk.setEnabled(true);
        }
        Button buttonCancel = (Button) v.findViewById(R.id.cancel);
        buttonOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent resultIntent = new Intent();
                resultIntent.putExtra(Constants.INDEXES_DATA_WAS_CHOSEN,(TreeSet)myAdapterForCard.getChoosen());
                resultIntent.putExtra(Constants.POSITION_OF_ADAPTER, positionOfAdapter);
                resultIntent.putExtra(Constants.KIND_OF_ADAPTER,kindOfAdapter);
                mListener.onDialogOkClick(resultIntent);
                dismiss();
            }
        });
        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onDialogCancelClick(positionOfAdapter,kindOfAdapter);
                dismiss();
            }
        });
        return v;
    }

}



