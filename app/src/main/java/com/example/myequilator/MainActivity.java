package com.example.myequilator;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TabHost;

import com.example.myequilator.adapters.MyAdapter;
import com.example.myequilator.adapters.MyPositionAdapter;
import com.example.myequilator.adapters.StreetAdapter;
import com.example.myequilator.entity.DataFromIntent;
import com.example.myequilator.entity.IndexesDataWasChosen;

import java.util.Arrays;
import java.util.Map;

import mi.poker.calculation.EquityCalculation;
import mi.poker.calculation.HandInfo;
import mi.poker.calculation.Result;

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
        mGestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                if(velocityY<2200&&velocityY>-2200) {
                    if (velocityX < -3000) {
                        if (tabHost.getCurrentTabTag().equals("tag1")) {
                            tabHost.setCurrentTabByTag("tag2");
                        }
                    }
                    if (velocityX > 3000) {
                        if (tabHost.getCurrentTabTag().equals("tag2")) {
                            tabHost.setCurrentTabByTag("tag1");
                        }
                    }
                }
                return true;
            }
        });

    }

    private void setTab(){
        tabHost = (TabHost) findViewById(android.R.id.tabhost);
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
        LinearLayoutManager managerStreet = new LinearLayoutManager(this, OrientationHelper.HORIZONTAL, false);
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
            String[] textFomEditTextPosition = savedInstanceState.getStringArray(Constants.STRNGS_FROM_ADAPTER);
            String[]textFomEditTextStreet = savedInstanceState.getStringArray(Constants.STRNGS_FROM_STREET_ADAPTER);
            IndexesDataWasChosen[] indexesFromPositionAdapter = (IndexesDataWasChosen[]) savedInstanceState.getSerializable(Constants.INDEXES_DATA_WAS_CHOSEN_BY_POSITION_ADAPTER);
            IndexesDataWasChosen[] indexesFromStreetAdapter = (IndexesDataWasChosen[]) savedInstanceState.getSerializable(Constants.INDEXES_DATA_WAS_CHOSEN_BY_STREET_ADAPTER);

            myPositionAdapter.setTextFromTextView(textFomEditTextPosition);
            myPositionAdapter.setArrayIndexesDataWhichWasChoosen(indexesFromPositionAdapter);
            streetAdapter.setTextFromEditViewStreet(textFomEditTextStreet);
            streetAdapter.setArrayIndexesDataWhichWasChoosen(indexesFromStreetAdapter);
        }
    }
    

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        mGestureDetector.onTouchEvent(ev);
        return super.dispatchTouchEvent(ev);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(Constants.CURRENT_TAG, tabHost.getCurrentTabTag());
        outState.putStringArray(Constants.STRNGS_FROM_STREET_ADAPTER, streetAdapter.getTextFromEditViewStreet());
        outState.putStringArray(Constants.STRNGS_FROM_ADAPTER, myPositionAdapter.getTextFromTextView());
        outState.putSerializable(Constants.INDEXES_DATA_WAS_CHOSEN_BY_STREET_ADAPTER, streetAdapter.getArrayIndexesDataWhichWasChoosen());
        outState.putSerializable(Constants.INDEXES_DATA_WAS_CHOSEN_BY_POSITION_ADAPTER, myPositionAdapter.getArrayIndexesDataWhichWasChoosen());
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.calculate:
                final ProgressDialog progressDialog = new ProgressDialog(this);
                Thread t = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String[] hand = myPositionAdapter.getTextFromTextView();
                        double[] equity = new double[hand.length];
                        Arrays.fill(equity, -1.0);
                        String hands = "";
                        for (String s1 : hand) {
                            if (!s1.equals("")) {
                                if (hands.equals("")) {
                                    hands += s1;
                                } else {
                                    hands += "," + s1;
                                }
                            }
                        }
                        String board = "";
                        for (String s : streetAdapter.getTextFromEditViewStreet()) {
                            board += s;
                        }
                        Result result = EquityCalculation.calculateExhaustiveEnumration(hands, board, "");
                        Map<Integer, HandInfo> mapResult = result.getMap();
                        int positionInResult = 0;
                        for (int i = 0; i < hand.length; i++) {
                            if (!hand[i].equals("")) {
                                equity[i] = mapResult.get(positionInResult++).getEquity();
                            }
                        }
                        Message msg = handler.obtainMessage();
                        Bundle bundle = new Bundle();
                        bundle.putDoubleArray(Constants.EQUITY, equity);
                        msg.setData(bundle);
                        myPositionAdapter.setResult(equity);
                        handler.sendEmptyMessage(1);
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



