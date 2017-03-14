package com.example.myequilator;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.myequilator.adapters.AdapterForRange;
import com.example.myequilator.entity.Combination;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class RangeActivity extends AppCompatActivity implements View.OnClickListener,SeekBar.OnSeekBarChangeListener {
    Button buttonOk;
    Button buttonCancel;
    AdapterForRange adapterForRange;
    SeekBar seekBar;
    TextView procent;

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
        seekBar = (SeekBar) findViewById(R.id.sbWeight);
        seekBar.setMax(1000);
        procent = (TextView) findViewById(R.id.procent);
        seekBar.setOnSeekBarChangeListener(this);
        procent.setOnClickListener(this);
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
            case R.id.procent:
                final EditText input = new EditText(this);
                input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                //input.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);
                //input.setRawInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);
                AlertDialog.Builder alert = new AlertDialog.Builder(this);
                alert.setTitle("Range").
                        setMessage("EnterRange").
                        setView(input).
                        setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String str = input.getText().toString();
                        double res;
                        if(str.matches("[-+]?\\d*\\.?\\d+")){
                            res=Double.valueOf(str);
                            res=res>100?100:res;
                            procent.setText(String.valueOf(res));
                            setInAdapterRangeByProcent(res);
                        }
                    }
                });
                alert.show();
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        double progress2 = progress/10.0;
        procent.setText(String.valueOf(progress2));
        setInAdapterRangeByProcent(progress2);

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    private void setInAdapterRangeByProcent(double progress){
        int index= Collections.binarySearch(AllCards.allCombinationsInRankingOrder,new Combination(null,-1,null,progress));
        index=index<0?(-(index)-2):index+1;
        adapterForRange.setChoosen(AllCards.getIndexesByRecyclerBaseOnRanking(index));
        adapterForRange.notifyDataSetChanged();
    }
}
