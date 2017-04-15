package com.example.myequilator;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.View;
import android.widget.TabHost;

import com.example.myequilator.adapters.MyAdapter;
import com.example.myequilator.adapters.MyPositionAdapter;
import com.example.myequilator.adapters.StreetAdapter;
import com.example.myequilator.entity.DataFromIntent;
import com.example.myequilator.entity.IndexesDataWasChosen;
import com.stevebrecher.showdown.Showdown;

import java.util.Arrays;
import java.util.Map;


import mi.poker.calculation.EquityCalculation;
import mi.poker.calculation.HandInfo;
import mi.poker.calculation.Result;

import static android.content.res.Configuration.ORIENTATION_PORTRAIT;

public class MainActivity extends AppCompatActivity implements CardsDialogFragment.CardDialogFragmentListener {

    MyPositionAdapter myPositionAdapter;
    StreetAdapter streetAdapter;

    TabHost tabHost;
    RecyclerView recyclerViewPosition;

    Handler handler;
    GestureDetector mGestureDetector;

    private static boolean RUN_ONCE = true;

    int positionOfAdapterBeforeRotate = -1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (RUN_ONCE) {
            AllCards.initializeData(getApplicationContext());
            RUN_ONCE = false;
        }

        setTab();

        setRecycler(savedInstanceState);


        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                myPositionAdapter.notifyDataSetChanged();
            }
        };


    }

    private void setTab(){
        tabHost = (TabHost) findViewById(R.id.tabhost);
        tabHost.setup();
        TabHost.TabSpec tabSpec;
        tabSpec = tabHost.newTabSpec("tag1");
        tabSpec.setIndicator(getString(R.string.for6));
        tabSpec.setContent(R.id.tab1);
        tabHost.addTab(tabSpec);
        tabSpec = tabHost.newTabSpec("tag2");
        tabSpec.setIndicator(getString(R.string.for10));
        tabSpec.setContent(R.id.tab2);
        tabHost.addTab(tabSpec);
        tabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            public void onTabChanged(String tabId) {
                setRecycler(tabId);
                AllCards.resetWasChosen();
            }
        });
    }

    private void setRecycler(String tag) {
        String[] dataForRecyclerPosition = getResources().getStringArray(R.array.positions);
        String[] dataForRecyclerStreet = getResources().getStringArray(R.array.streets);
        tabHost.findViewById(R.id.tab1).findViewById(R.id.recycler);
        switch (tag) {
            case "tag1":
                recyclerViewPosition = (RecyclerView) tabHost.findViewById(R.id.tab1).findViewById(R.id.recycler);
                myPositionAdapter = new MyPositionAdapter(this, Arrays.copyOfRange(dataForRecyclerPosition, 4, dataForRecyclerPosition.length));
                break;
            case "tag2":
                recyclerViewPosition = (RecyclerView) tabHost.findViewById(R.id.tab2).findViewById(R.id.recycler);
                myPositionAdapter = new MyPositionAdapter(this, dataForRecyclerPosition);
                break;
            default:
                throw new IllegalArgumentException();
        }
        RecyclerView recyclerViewStreet = (RecyclerView) findViewById(R.id.recycler_street);
        RecyclerView.LayoutManager managerStreet;
        if ((getResources().getConfiguration().screenLayout &
                Configuration.SCREENLAYOUT_SIZE_MASK) ==
                Configuration.SCREENLAYOUT_SIZE_NORMAL&&getResources().getConfiguration().orientation== ORIENTATION_PORTRAIT) {
            managerStreet = new GridLayoutManager(this, 2,OrientationHelper.HORIZONTAL, false);
        }
        else {
            managerStreet = new LinearLayoutManager(this, OrientationHelper.HORIZONTAL, false);
        }

        LinearLayoutManager managerPosition = new LinearLayoutManager(this, OrientationHelper.VERTICAL, false);
        recyclerViewPosition.setLayoutManager(managerPosition);
        recyclerViewStreet.setLayoutManager(managerStreet);
        streetAdapter = new StreetAdapter(MainActivity.this, dataForRecyclerStreet);
        recyclerViewPosition.setAdapter(myPositionAdapter);
        recyclerViewStreet.setAdapter(streetAdapter);
    }

    private void setRecycler(Bundle savedInstanceState){
        if(savedInstanceState==null) {
            setRecycler("tag1");
        }
        else{
            String currentTag = savedInstanceState.getString(Constants.CURRENT_TAG);
            tabHost.setCurrentTabByTag(currentTag);
            setRecycler(currentTag);
            double[] equity = savedInstanceState.getDoubleArray(Constants.EQUITY);
            String[] textFomEditTextPosition = savedInstanceState.getStringArray(Constants.STRNGS_FROM_ADAPTER);
            String[]textFomEditTextStreet = savedInstanceState.getStringArray(Constants.STRNGS_FROM_STREET_ADAPTER);
            IndexesDataWasChosen[] indexesFromPositionAdapter = (IndexesDataWasChosen[]) savedInstanceState.getSerializable(Constants.INDEXES_DATA_WAS_CHOSEN_BY_POSITION_ADAPTER);
            IndexesDataWasChosen[] indexesFromStreetAdapter = (IndexesDataWasChosen[]) savedInstanceState.getSerializable(Constants.INDEXES_DATA_WAS_CHOSEN_BY_STREET_ADAPTER);

            myPositionAdapter.setEquity(equity);
            myPositionAdapter.setTextFromTextView(textFomEditTextPosition);
            myPositionAdapter.setArrayIndexesDataWhichWasChoosen(indexesFromPositionAdapter);
            streetAdapter.setTextFromEditViewStreet(textFomEditTextStreet);
            streetAdapter.setArrayIndexesDataWhichWasChoosen(indexesFromStreetAdapter);
        }
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(Constants.CURRENT_TAG, tabHost.getCurrentTabTag());
        outState.putStringArray(Constants.STRNGS_FROM_STREET_ADAPTER, streetAdapter.getTextFromEditViewStreet());
        outState.putStringArray(Constants.STRNGS_FROM_ADAPTER, myPositionAdapter.getTextFromTextView());
        outState.putDoubleArray(Constants.EQUITY,myPositionAdapter.getEquity());
        outState.putSerializable(Constants.INDEXES_DATA_WAS_CHOSEN_BY_STREET_ADAPTER, streetAdapter.getArrayIndexesDataWhichWasChoosen());
        outState.putSerializable(Constants.INDEXES_DATA_WAS_CHOSEN_BY_POSITION_ADAPTER, myPositionAdapter.getArrayIndexesDataWhichWasChoosen());
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.calculate:
               calculation();
        }
    }

    public void calculation(){
        final ProgressDialog progressDialog = new ProgressDialog(this);
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                String[] hand = myPositionAdapter.getTextFromTextView();
                double[] equity = new double[hand.length];
                Arrays.fill(equity, -1.0);
                String hands = "";
                String board = "";
                for (String s : hand) {
                    if (!s.equals("")) {
                        if(s.length()>4){
                            s=s.replace(",","|");
                        }
                        if (hands.equals("")) {
                            hands += s;
                        } else {
                            hands += "," + s;
                        }
                    }
                }
                for (String s : streetAdapter.getTextFromEditViewStreet()) {
                    board += s;
                }
                long start = System.currentTimeMillis();
                Result result=EquityCalculation.calculateMonteCarlo(hands,board,"");

                long end = System.currentTimeMillis()-start;
                Log.d("seconds", String.valueOf(end));
                double[] res = Showdown.calculate(hands,board);
                sendResult(hand,equity,new double[2],result);
                progressDialog.dismiss();
            }
        });
        progressDialog.setTitle(getString(R.string.calculate));
        progressDialog.setMessage("Calculating in progress...");
        progressDialog.setButton(Dialog.BUTTON_NEGATIVE, getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                progressDialog.dismiss();
            }
        });
        progressDialog.show();
        t.start();
    }

    public void sendResult(String[] hand, double[] equity, double[] result, Result result1){
        Map<Integer, HandInfo> mapResult = result1.getMap();
        int positionInResult = 0;
        Log.d("equity",Arrays.toString(result));
        for (int i = 0; i < hand.length; i++) {
            if (!hand[i].equals("")) {
                //equity[i] = result[positionInResult++];
                equity[i]=mapResult.get(positionInResult++).getEquity();
            }
        }
        myPositionAdapter.setEquity(equity);
        handler.sendEmptyMessage(1);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onResume() {
        super.onResume();
/*
        if (positionOfAdapterBeforeRotate != -1) {
            recyclerViewPosition.scrollToPosition(positionOfAdapterBeforeRotate);
            positionOfAdapterBeforeRotate = -1;
        }
*/

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);
        DataFromIntent dataFromIntent;
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case Constants.REQUEST_CODE_RANGE:
                    dataFromIntent = new DataFromIntent(data, IndexesDataWasChosen.Type.RANGE);
                    updateMyPositionAdapter(dataFromIntent, myPositionAdapter);
                    break;
                case Constants.REQUEST_CODE_CARD:
                    dataFromIntent = new DataFromIntent(data, IndexesDataWasChosen.Type.CARD);
                    String type_of_adapter = data.getStringExtra(Constants.KIND_OF_ADAPTER);
                    switch (type_of_adapter) {
                        case Constants.POSITION_ADAPTER:
                            updateMyPositionAdapter(dataFromIntent, myPositionAdapter);
                            break;
                        case Constants.STREET_ADAPTER:
                            updateMyPositionAdapter(dataFromIntent, streetAdapter);
                            break;
                    }
                    break;
            }
        }
    }

    @Override
    public void onDialogOkClick(DialogFragment dialog, Intent data) {
        onActivityResult(Constants.REQUEST_CODE_CARD, RESULT_OK, data);

    }

    @Override
    public void onDialogCancelClick(DialogFragment dialog, int positionOfAdapter, String kindOfAdapter) {
        IndexesDataWasChosen indexes = null;
        switch (kindOfAdapter) {
            case (Constants.POSITION_ADAPTER):
                indexes = myPositionAdapter.getArrayIndexesDataWhichWasChoosen()[positionOfAdapter];
                break;
            case (Constants.STREET_ADAPTER):
                indexes = streetAdapter.getArrayIndexesDataWhichWasChoosen()[positionOfAdapter];
                break;
        }
        if (indexes != null && indexes.getType() == IndexesDataWasChosen.Type.CARD) {
            AllCards.checkFlags(indexes.getIndexesDataWasChosen());
        }
    }

    public void updateMyPositionAdapter(DataFromIntent dataFromIntent, MyAdapter adapter) {
        adapter.replacedIndexesDataWasChosen(dataFromIntent);
        adapter.replacedToTextFromTextView(dataFromIntent);
        adapter.notifyDataSetChanged();
    }

}



