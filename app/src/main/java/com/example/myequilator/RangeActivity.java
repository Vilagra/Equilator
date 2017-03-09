package com.example.myequilator;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;

import com.example.myequilator.adapters.AdapterForRange;

import java.util.HashSet;
import java.util.Set;

public class RangeActivity extends AppCompatActivity implements View.OnClickListener {
    Button buttonOk;
    Button buttonCancel;
    AdapterForRange adapterForRange;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.range_matrix);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_range);
        GridLayoutManager manager = new GridLayoutManager(this, 13, GridLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(manager);
        Set<Integer> idexesDataWasChoosen = (Set<Integer>) getIntent().getSerializableExtra(Constants.INDEXES_DATA_WAS_CHOSEN);
        adapterForRange = new AdapterForRange(this, new HashSet<Integer>());
        if (idexesDataWasChoosen != null) {
            adapterForRange.setChoosen(idexesDataWasChoosen);
        }
        recyclerView.setAdapter(adapterForRange);
        buttonOk = (Button) findViewById(R.id.ok);
        buttonCancel = (Button) findViewById(R.id.cancel);
        buttonOk.setOnClickListener(this);
        buttonCancel.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ok:
                int position = getIntent().getIntExtra(Constants.POSITION_OF_ADAPTER, -1);
                Intent resultIntent = new Intent();
                resultIntent.putExtra(Constants.POSITION_OF_ADAPTER, position);
                resultIntent.putExtra(Constants.INDEXES_DATA_WAS_CHOSEN, (HashSet) adapterForRange.getChoosen());
                setResult(RESULT_OK, resultIntent);
                finish();
                break;
            case R.id.cancel:
                setResult(RESULT_CANCELED);
                finish();
                break;
        }
    }
}
