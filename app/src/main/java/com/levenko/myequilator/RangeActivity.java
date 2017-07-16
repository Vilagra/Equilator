package com.levenko.myequilator;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.levenko.myequilator.adapters.AdapterForRange;
import com.levenko.myequilator.entity.Combination;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class RangeActivity extends AppCompatActivity implements View.OnClickListener, SeekBar.OnSeekBarChangeListener {
    private AdapterForRange adapterForRange;
    private SeekBar seekBar;
    private EditText procent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.range_matrix);
        AdView mAdView;
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);

        LayoutInflater inflator = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        @SuppressLint("InflateParams") View v = inflator.inflate(R.layout.ads, null);

        mAdView = (AdView) v.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().addTestDevice("BC44035CB7EB870A409150BDE200B894").build();
        mAdView.loadAd(adRequest);
        actionBar.setCustomView(v);

        Button buttonOk = (Button) findViewById(R.id.ok);
        Button buttonCancel = (Button) findViewById(R.id.cancel);
        seekBar = (SeekBar) findViewById(R.id.sbWeight);
        seekBar.setMax(1000);
        procent = (EditText) findViewById(R.id.procent);
/*        TextWatcher textWatcher=new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(final Editable s) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String str = s.toString();
                        if(str.matches("[-+]?\\d*\\.?\\d*")){
                            if(str.matches("\\d*\\.\\d\\d+")){
                                str = String.format("%.1f", Double.valueOf(str));
                                str=str.replace(",",".");
                            }
                            setViewByProcent(Double.valueOf(str));

                        }
                    }
                });

            }
        };*/
        // procent.addTextChangedListener(textWatcher);
        procent.setOnEditorActionListener(new EditText.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    String str = procent.getText().toString();
                    if (str.matches("[-+]?\\d*\\.?\\d*")) {
                        if (str.matches("\\d*\\.\\d\\d+")) {
                            str = String.format("%.1f", Double.valueOf(str));
                            str = str.replace(",", ".");
                        }
                        setViewByProcent(Double.valueOf(str));
                        //return true;
                    }
                }
                return false;
            }
        });
        seekBar.setOnSeekBarChangeListener(this);
        procent.setOnClickListener(this);
        buttonOk.setOnClickListener(this);
        buttonCancel.setOnClickListener(this);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_range);
        GridLayoutManager manager = new GridLayoutManager(this, 13, GridLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(manager);
        @SuppressWarnings("unchecked") Set<Integer> idexesDataWasChoosen = (Set<Integer>) getIntent().getSerializableExtra(Constants.INDEXES_DATA_WAS_CHOSEN);
        adapterForRange = new AdapterForRange(this, new HashSet<Integer>());
        if (idexesDataWasChoosen != null) {
            adapterForRange.setChosen(idexesDataWasChoosen);
            @SuppressWarnings("unchecked") int lastIndex = AllCards.areAllHandsInRankingOrder(new HashSet(idexesDataWasChoosen));
            if (lastIndex != -1) {
                double currenProcent = AllCards.allCombinationsInRankingOrder.get(lastIndex).getRankingOfHand();
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
                resultIntent.putExtra(Constants.INDEXES_DATA_WAS_CHOSEN, (HashSet) adapterForRange.getChosen());
                setResult(RESULT_OK, resultIntent);
                finish();
                break;
            case R.id.cancel:
                setResult(RESULT_CANCELED);
                finish();
                break;
            case R.id.procent:
/*                final EditText input = new EditText(this);
                LinearLayout layout = new LinearLayout(this);
                layout.setOrientation(LinearLayout.VERTICAL);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        150, 130);
                params.gravity = Gravity.CENTER;
                params.setMargins(20, 0, 30, 0);
                input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                input.setHint("00.0");
                layout.addView(input, params);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    //noinspection deprecation
                    input.setBackground(getResources().getDrawable(R.drawable.blue_out_line));
                }
                else{
                    //noinspection deprecation
                    input.setBackgroundDrawable(getResources().getDrawable(R.drawable.blue_out_line));
                }
                input.setMaxWidth(40);
                AlertDialog.Builder alert = new AlertDialog.Builder(this);
                alert.setTitle(getString(R.string.enter_range)).
                        //setMessage(getString(R.string.enter_range)).
                        setView(layout).
                        setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @SuppressLint("DefaultLocale")
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String str = input.getText().toString();
                        if(str.matches("[-+]?\\d*\\.?\\d*")){
                            if(str.matches("\\d*\\.\\d\\d+")){
                                str = String.format("%.1f", Double.valueOf(str));
                                str=str.replace(",",".");
                            }
                            if(str.equals(""))
                                str="0";
                            setViewByProcent(Double.valueOf(str));

                        }
                    }
                });
                AlertDialog alertDialog = alert.create();
                alertDialog.getWindow().setBackgroundDrawableResource(R.drawable.blue_out_line);
                alert.show();*/
        }
    }

    private void setViewByProcent(Double res) {
        res = res > 100 ? 100 : res;
        procent.setText(String.valueOf(res));
        seekBar.setProgress((int) (res * 10));
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        double progress2 = progress / 10.0;
        procent.setText(String.valueOf(progress2));
        setInAdapterRangeByProcent(progress2);

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    private void setInAdapterRangeByProcent(double progress) {
        int index = Collections.binarySearch(AllCards.allCombinationsInRankingOrder, new Combination(null, -1, null, progress));
        index = index < 0 ? (-(index) - 2) : index + 1;
        adapterForRange.setChosen(AllCards.getIndexesByRecyclerBaseOnRanking(index));
        adapterForRange.notifyDataSetChanged();
    }
}
