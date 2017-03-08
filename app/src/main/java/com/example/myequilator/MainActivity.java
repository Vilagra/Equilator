package com.example.myequilator;

import android.app.Dialog;
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
import android.util.Log;
import android.view.View;
import android.widget.TabHost;

import com.example.myequilator.adapters.MyAdapter;
import com.example.myequilator.adapters.StreetAdapter;
import com.example.myequilator.adapters.MyAdapterForCard;
import com.example.myequilator.entity.IndexesDataWasChosen;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;

import mi.poker.calculation.EquityCalculation;
import mi.poker.calculation.HandInfo;
import mi.poker.calculation.Result;

public class MainActivity extends AppCompatActivity implements CardsDialogFragment.SetterPositionOfAdapter {
    MyAdapter myAdapter;
    TabHost tabHost;
    RecyclerView recyclerView;
    StreetAdapter streetAdapter;
    Handler handler;

    int positionOfAdapterBeforeRotate = -1;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(Constants.MY_LOG, "createAct");

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
        String[] textFomEditTextStreet = null;

        if (savedInstanceState != null) {
            String currentTag = savedInstanceState.getString(Constants.CURRENT_TAG);
            String[] textFomEditText = savedInstanceState.getStringArray(Constants.STRNGS_FROM_ADAPTER);
            textFomEditTextStreet = savedInstanceState.getStringArray(Constants.STRNGS_FROM_STREET_ADAPTER);
            IndexesDataWasChosen[] indexesDataWasChosen = (IndexesDataWasChosen[]) savedInstanceState.getSerializable(Constants.INDEXES_DATA_WAS_CHOSEN);
            tabHost.setCurrentTabByTag(currentTag);
            setRecycler(currentTag, textFomEditText,indexesDataWasChosen);
        } else {
            tabHost.setCurrentTabByTag("tag1");
            setRecycler("tag1", null,null);
        }
        tabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            public void onTabChanged(String tabId) {
                setRecycler(tabId, null,null);
                AllCards.resetWasChosen();
            }
        });
        String[] dataForRecycler = getResources().getStringArray(R.array.streets);
        RecyclerView recyclerViewPosition = (RecyclerView) findViewById(R.id.recycler_street);
        LinearLayoutManager manager =new LinearLayoutManager(this,OrientationHelper.HORIZONTAL,false);
        recyclerViewPosition.setLayoutManager(manager);
        streetAdapter = new StreetAdapter(MainActivity.this,dataForRecycler);
        if(textFomEditTextStreet!=null){
            //Log.d(MY_LOG,Arrays.toString(textFomEditTextStreet));
            streetAdapter.setTextFromEditViewStreet(textFomEditTextStreet);
        }
        recyclerViewPosition.setAdapter(streetAdapter);

        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                //double[] equity =msg.getData().getDoubleArray(EQUITY);
                //myAdapter.setResult(equity);
                myAdapter.notifyDataSetChanged();
            }
        };

    }

    private void setRecycler(String tag, String[] textFomEditText,IndexesDataWasChosen[] indexesDataWasChosen) {
        String[] dataForRecycler = getResources().getStringArray(R.array.positions);
        tabHost.findViewById(R.id.tab1).findViewById(R.id.recycler);
        switch (tag) {
            case "tag1":
                recyclerView = (RecyclerView) tabHost.findViewById(R.id.tab1).findViewById(R.id.recycler);
                myAdapter = new MyAdapter(this, Arrays.copyOfRange(dataForRecycler, 4, dataForRecycler.length));
                break;
            case "tag2":
                recyclerView = (RecyclerView) tabHost.findViewById(R.id.tab2).findViewById(R.id.recycler);
                myAdapter = new MyAdapter(this, dataForRecycler);
                break;
            default:
                throw new IllegalArgumentException();
        }
        if (textFomEditText != null) {
            myAdapter.setTextFromTextView(textFomEditText);
            myAdapter.setArrayIndexesDataWhichWasChoosen(indexesDataWasChosen);
        }
        LinearLayoutManager manager = new LinearLayoutManager(this, OrientationHelper.VERTICAL, false);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(myAdapter);
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putStringArray(Constants.STRNGS_FROM_ADAPTER, myAdapter.getTextFromTextView());
        outState.putString(Constants.CURRENT_TAG, tabHost.getCurrentTabTag());
        outState.putStringArray(Constants.STRNGS_FROM_STREET_ADAPTER,streetAdapter.getTextFromEditViewStreet());
        outState.putSerializable(Constants.INDEXES_DATA_WAS_CHOSEN,myAdapter.getArrayIndexesDataWhichWasChoosen());
    }
    public void onClick(View v){
        switch (v.getId()){
            case R.id.calculate:
                final ProgressDialog progressDialog =new ProgressDialog(this);
                Thread t=new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String[] hand = myAdapter.getTextFromTextView();
                        double[] equity =new double[hand.length];
                        Arrays.fill(equity,-1.0);
                        String hands="";
                        for (String s1 : hand) {
                            if(!s1.equals("")){
                                if(hands.equals("")){
                                    hands+=s1;
                                }
                                else {
                                    hands+=","+s1;
                                }
                            }
                        }
                        String board="";
                        for (String s : streetAdapter.getTextFromEditViewStreet()) {
                            board+=s;
                        }
                        Result result=EquityCalculation.calculateExhaustiveEnumration(hands,board,"");
                        Map<Integer,HandInfo> mapResult=result.getMap();
                        int positionInResult=0;
                        for (int i = 0; i < hand.length; i++) {
                            if(!hand[i].equals("")){
                                equity[i]=mapResult.get(positionInResult++).getEquity();
                            }
                        }
                        Message msg= handler.obtainMessage();
                        Bundle bundle= new Bundle();
                        bundle.putDoubleArray(Constants.EQUITY,equity);
                        msg.setData(bundle);
                        myAdapter.setResult(equity);
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
        if (positionOfAdapterBeforeRotate != -1) {
            recyclerView.scrollToPosition(positionOfAdapterBeforeRotate);
            positionOfAdapterBeforeRotate = -1;
        }

    }

    @Override
    public void setPosition(int i) {
        positionOfAdapterBeforeRotate = i;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_OK){
            if(requestCode==Constants.REQUEST_CODE_RANGE){
                int positionOfAdapter= data.getIntExtra(Constants.POSITION_OF_ADAPTER,-1);
                Set<Integer> indexesData= (Set<Integer>) data.getSerializableExtra(Constants.INDEXES_DATA_WAS_CHOSEN);
                String string = AllCards.getStringFromRange(indexesData);
                myAdapter.addToIndexesDataWasChosen(positionOfAdapter,indexesData);
                myAdapter.addToTextFromTextView(positionOfAdapter,string);
                myAdapter.notifyDataSetChanged();
            }
        }
    }
}



