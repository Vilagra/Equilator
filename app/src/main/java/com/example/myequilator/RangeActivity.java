package com.example.myequilator;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.myequilator.adapters.AdapterForRange;
import com.example.myequilator.entity.Combination;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class RangeActivity extends AppCompatActivity implements View.OnClickListener,SeekBar.OnSeekBarChangeListener {
    Button buttonOk;
    Button buttonCancel;
    AdapterForRange adapterForRange;
    SeekBar seekBar;
    TextView procent;
    private AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.range_matrix);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);

        LayoutInflater inflator = (LayoutInflater) this .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflator.inflate(R.layout.ads, null);

        mAdView = (AdView) v.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().addTestDevice("BC44035CB7EB870A409150BDE200B894").build();
        mAdView.loadAd(adRequest);
        actionBar.setCustomView(v);

        buttonOk = (Button) findViewById(R.id.ok);
        buttonCancel = (Button) findViewById(R.id.cancel);
        seekBar = (SeekBar) findViewById(R.id.sbWeight);
        seekBar.setMax(1000);
        procent = (TextView) findViewById(R.id.procent);
        seekBar.setOnSeekBarChangeListener(this);
        procent.setOnClickListener(this);
        buttonOk.setOnClickListener(this);
        buttonCancel.setOnClickListener(this);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_range);
        GridLayoutManager manager = new GridLayoutManager(this, 13, GridLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(manager);
        Set<Integer> idexesDataWasChoosen = (Set<Integer>) getIntent().getSerializableExtra(Constants.INDEXES_DATA_WAS_CHOSEN);
        adapterForRange = new AdapterForRange(this, new HashSet<Integer>());
        if (idexesDataWasChoosen != null) {
            adapterForRange.setChoosen(idexesDataWasChoosen);
            int lastIndex = AllCards.areAllHandsInRankingOrder(new HashSet(idexesDataWasChoosen));
            if(lastIndex!=-1){
                double currenProcent=AllCards.allCombinationsInRankingOrder.get(lastIndex).getRankingOfHand();
                setViewByProcent(currenProcent);
            }
        }
        recyclerView.setAdapter(adapterForRange);

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
                input.setWidth(30);
                input.setPadding(15,8,8,15);
                input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                input.setHint("00.0");
                input.setBackground(getResources().getDrawable(R.drawable.blue_out_line));
                input.setMaxWidth(40);
                AlertDialog.Builder alert = new AlertDialog.Builder(this);
                alert.setTitle(getString(R.string.range2)).
                        setMessage(getString(R.string.enter_range)).
                        setView(input).
                        setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String str = input.getText().toString();
                        if(str.matches("[-+]?\\d*\\.?\\d*")){
                            if(str.matches("\\d*\\.\\d\\d+")){
                                str = String.format("%.1f", Double.valueOf(str));
                                str=str.replace(",",".");
                            }
                            setViewByProcent(Double.valueOf(str));

                        }
                    }
                });
                AlertDialog alertDialog = alert.create();
                alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                alert.show();
        }
    }

    private void setViewByProcent(Double res){
        res=res>100?100:res;
        procent.setText(String.valueOf(res));
        seekBar.setProgress((int) (res*10));
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
