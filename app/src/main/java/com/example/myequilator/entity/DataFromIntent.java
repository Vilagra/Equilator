package com.example.myequilator.entity;

import android.content.Intent;

import com.example.myequilator.AllCards;
import com.example.myequilator.Constants;

import java.util.Set;

/**
 * Created by Vilagra on 10.03.2017.
 */

public class DataFromIntent {
    private int positionOfAdapter;
    private Set<Integer> indexesDataWasChosen;
    private IndexesDataWasChosen.Type type;

    public DataFromIntent(Intent data,IndexesDataWasChosen.Type type) {
        this.type = type;
        positionOfAdapter = data.getIntExtra(Constants.POSITION_OF_ADAPTER, -1);
        indexesDataWasChosen = (Set<Integer>) data.getSerializableExtra(Constants.INDEXES_DATA_WAS_CHOSEN);
    }

    public String getTextFromTextView(){
        if (type== IndexesDataWasChosen.Type.RANGE){
            return AllCards.getStringFromRange(indexesDataWasChosen);
        }
        if (type== IndexesDataWasChosen.Type.CARD){
            return AllCards.getStringFromCard(indexesDataWasChosen);
        }
        return null;
    }

    public Set<Integer> getIndexesDataWasChosen() {
        return indexesDataWasChosen;
    }

    public int getPositionOfAdapter() {
        return positionOfAdapter;
    }

    public IndexesDataWasChosen.Type getType() {
        return type;
    }


}
