package com.example.myequilator;

import android.app.DialogFragment;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.myequilator.adapters.MyAdapterForCard;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Vilagra on 12.01.2017.
 */

public class CardsDialogFragment extends DialogFragment implements MyAdapterForCard.MyAdapterForCardListener{

    static final String WAS_CHOSEN= "was chosen";
    static final String ADAPTER_POSITION= "adapter position";
    static final String NUMBER_OF_CARDS= "number cards";

    //MyAdapterForCard myAdapterForCard;
    CardDialogFragmentListener mListener;
    Set<Integer> positionOfChoosenCard;
    int positionOfAdapter;
    int numberOfCardsWhichUserMustChoose;

    public void setNumberOfCardsWhichUserMustChoose(int numberOfCardsWhichUserMustChoose) {
        this.numberOfCardsWhichUserMustChoose = numberOfCardsWhichUserMustChoose;
    }

    public void setPositionOfAdapter(int positionOfAdapter) {
        this.positionOfAdapter = positionOfAdapter;
    }

    Button buttonOk;
    Button buttonCancel;

    public interface CardDialogFragmentListener {
        public void onDialogOkClick(DialogFragment dialog, Set<Integer> card);
        public void onDialogCancelClick(DialogFragment dialog);
    }
    public interface SetterPositionOfAdapter{
        void setPosition(int i);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putIntegerArrayList(WAS_CHOSEN,new ArrayList<Integer>(positionOfChoosenCard));
        outState.putInt(ADAPTER_POSITION,positionOfAdapter);
        outState.putInt(NUMBER_OF_CARDS,numberOfCardsWhichUserMustChoose);
    }

    public void setmListener(CardDialogFragmentListener mListener) {
        this.mListener = mListener;
    }

    public void setPositionOfChoosenCard(Set<Integer> positionOfChoosenCard) {
        this.positionOfChoosenCard = positionOfChoosenCard;
    }

    @Override
    public void onClickByCard(Set<Integer> positionOfChoosenCard) {
        if(positionOfChoosenCard.size()==numberOfCardsWhichUserMustChoose){
            buttonOk.setEnabled(true);
            setPositionOfChoosenCard(positionOfChoosenCard);
        }
        else{
            buttonOk.setEnabled(false);
        }
    }


    //AlertDialog alertDialog;



    @Override
    public void onStop() {

        super.onStop();
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(MainActivity.MY_LOG,"createfrag");
        // Pick a style based on the num.
        int style = DialogFragment.STYLE_NORMAL, theme = android.R.style.Theme_Holo_Light_DialogWhenLarge;
        setStyle(style, theme);

    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(MainActivity.MY_LOG,"createfragview");
        if(savedInstanceState!=null) {
            positionOfChoosenCard = new HashSet<>(savedInstanceState.getIntegerArrayList(WAS_CHOSEN));
            numberOfCardsWhichUserMustChoose = savedInstanceState.getInt(NUMBER_OF_CARDS);
        }
        getDialog().setTitle(getActivity().getString(R.string.card_selection));
        View v = inflater.inflate(R.layout.activity_card_matrix, container, false);
        RecyclerView recyclerView = (RecyclerView) v.findViewById(R.id.recycler_card);
        GridLayoutManager manager = new GridLayoutManager(this.getActivity(), 4, GridLayoutManager.HORIZONTAL,false);
        recyclerView.setLayoutManager(manager);
        MyAdapterForCard myAdapterForCard=new MyAdapterForCard(getActivity(),positionOfChoosenCard,numberOfCardsWhichUserMustChoose);
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
                if(mListener==null){
                    RecyclerView recyclerViewFromActivity = (RecyclerView) getActivity().findViewById(R.id.recycler);
                    for (int i = 0; i < recyclerViewFromActivity.getAdapter().getItemCount(); i++) {
                        if(recyclerViewFromActivity.findViewHolderForLayoutPosition(i)!=null){
                            Log.d(MainActivity.MY_LOG,recyclerViewFromActivity.findViewHolderForLayoutPosition(i).toString());
                        }
                    }
                    setmListener((CardDialogFragmentListener) recyclerViewFromActivity.findViewHolderForAdapterPosition(positionOfAdapter));
                }
                mListener.onDialogOkClick(CardsDialogFragment.this, positionOfChoosenCard);
                dismiss();
            }
        });
        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mListener==null){
                    RecyclerView recyclerView = (RecyclerView) getActivity().findViewById(R.id.recycler);
                    setmListener((CardDialogFragmentListener) recyclerView.findViewHolderForAdapterPosition(2));
                    Log.d(MainActivity.MY_LOG, recyclerView.findViewHolderForAdapterPosition(2).toString());
                }
                mListener.onDialogCancelClick(CardsDialogFragment.this);
                dismiss();
            }
        });
        if(savedInstanceState!=null){
            //positionOfChoosenCard=new HashSet<>(savedInstanceState.getIntegerArrayList(WAS_CHOSEN));
            positionOfAdapter = savedInstanceState.getInt(ADAPTER_POSITION);
            SetterPositionOfAdapter setterOfAdapter = (SetterPositionOfAdapter) getActivity();
            setterOfAdapter.setPosition(positionOfAdapter);

        }
        return v;
    }

    @Override
    public void onStart() {
        super.onStart();

    }


}



