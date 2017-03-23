package com.example.myequilator;

import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.myequilator.adapters.MyAdapterForCard;
import com.example.myequilator.entity.Combination;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

/**
 * Created by Vilagra on 12.01.2017.
 */

public class CardsDialogFragment extends DialogFragment implements MyAdapterForCard.MyAdapterForCardListener{

    MyAdapterForCard myAdapterForCard;
    CardDialogFragmentListener mListener;
    Set<Integer> positionOfChoosenCard = new TreeSet<>();
    int positionOfAdapter;
    int numberOfCardsWhichUserMustChoose;
    String kindOfAdapter;

    Button buttonOk;
    Button buttonCancel;

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
        public void onDialogOkClick(DialogFragment dialog, Intent data);
        public void onDialogCancelClick(DialogFragment dialog, int positionOfAdapter, String kindOfAdapter);
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
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putIntegerArrayList(Constants.WAS_CHOSEN,new ArrayList<Integer>(myAdapterForCard.getChoosen()));
        outState.putInt(Constants.POSITION_OF_ADAPTER,positionOfAdapter);
        outState.putInt(Constants.NUMBER_OF_CARDS,numberOfCardsWhichUserMustChoose);
        outState.putString(Constants.KIND_OF_ADAPTER,kindOfAdapter);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int style = DialogFragment.STYLE_NORMAL, theme = android.R.style.Theme_Holo_Light_DialogWhenLarge;
        setStyle(style, theme);
        mListener= (CardDialogFragmentListener) getActivity();

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
        buttonCancel = (Button) v.findViewById(R.id.cancel);
        buttonOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent resultIntent = new Intent();
                resultIntent.putExtra(Constants.INDEXES_DATA_WAS_CHOSEN,(TreeSet)myAdapterForCard.getChoosen());
                resultIntent.putExtra(Constants.POSITION_OF_ADAPTER, positionOfAdapter);
                resultIntent.putExtra(Constants.KIND_OF_ADAPTER,kindOfAdapter);
                mListener.onDialogOkClick(CardsDialogFragment.this,resultIntent);
                dismiss();
            }
        });
        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onDialogCancelClick(CardsDialogFragment.this,positionOfAdapter,kindOfAdapter);
                dismiss();
            }
        });
        return v;
    }

}



