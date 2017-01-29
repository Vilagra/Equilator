package com.example.myequilator;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
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

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Map;

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


    public final static String MY_LOG = "my_logs";
    public final static String STRNGS_FROM_ADAPTER = "strings_from_adapter";
    public final static String STRNGS_FROM_STREET_ADAPTER = "strings_from_street_adapter";
    public final static String CURRENT_TAG = "curent_tag";
    public final static String EQUITY = "equity";


    MyAdapterForCard myAdapterForCard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(MY_LOG, "createAct");

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
            String currentTag = savedInstanceState.getString(CURRENT_TAG);
            String[] textFomEditText = savedInstanceState.getStringArray(STRNGS_FROM_ADAPTER);
            textFomEditTextStreet = savedInstanceState.getStringArray(STRNGS_FROM_STREET_ADAPTER);
            Log.d(MY_LOG,Arrays.toString(textFomEditTextStreet));
            tabHost.setCurrentTabByTag(currentTag);
            setRecycler(currentTag, textFomEditText);
        } else {
            tabHost.setCurrentTabByTag("tag1");
            setRecycler("tag1", null);
        }
        tabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            public void onTabChanged(String tabId) {
                setRecycler(tabId, null);
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

    private void setRecycler(String tag, String[] textFomEditText) {
        String[] dataForRecycler = getResources().getStringArray(R.array.positions);
        tabHost.findViewById(R.id.tab1).findViewById(R.id.recycler);
        switch (tag) {
            case "tag1":
                recyclerView = (RecyclerView) tabHost.findViewById(R.id.tab1).findViewById(R.id.recycler);
                myAdapter = new MyAdapter(this, Arrays.copyOfRange(dataForRecycler, 4, dataForRecycler.length));
                break;
            case "tag2":
                recyclerView = (RecyclerView) tabHost.findViewById(R.id.tab2).findViewById(R.id.recycler);
                myAdapter = new MyAdapter(MainActivity.this, dataForRecycler);
                break;
            default:
                throw new IllegalArgumentException();
        }
        if (textFomEditText != null) {
            myAdapter.setTextFromEditView(textFomEditText);
        }
        LinearLayoutManager manager = new LinearLayoutManager(this, OrientationHelper.VERTICAL, false);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(myAdapter);
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putStringArray(STRNGS_FROM_ADAPTER, myAdapter.getTextFromEditView());
        outState.putString(CURRENT_TAG, tabHost.getCurrentTabTag());
        outState.putStringArray(STRNGS_FROM_STREET_ADAPTER,streetAdapter.getTextFromEditViewStreet());
    }
    public void onClick(View v){
        switch (v.getId()){
            case R.id.calculate:
                final ProgressDialog progressDialog =new ProgressDialog(this);
                Thread t=new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String[] hand = myAdapter.getTextFromEditView();
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
                        Log.d(MY_LOG,result.toString());
                        Map<Integer,HandInfo> mapResult=result.getMap();
                        int positionInResult=0;
                        for (int i = 0; i < hand.length; i++) {
                            if(!hand[i].equals("")){
                                Log.d(MY_LOG, String.valueOf(i));
                                equity[i]=mapResult.get(positionInResult++).getEquity();
                            }
                        }
                        Message msg= handler.obtainMessage();
                        Bundle bundle= new Bundle();
                        Log.d(MY_LOG,Arrays.toString(equity));
                        bundle.putDoubleArray(EQUITY,equity);
                        msg.setData(bundle);
                        myAdapter.setResult(equity);
                        //myAdapter.notifyDataSetChanged();
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
        //Log.d(MY_LOG, "destroy");
        super.onDestroy();
    }


    @Override
    protected void onStart() {
        //Log.d(MY_LOG, "start");
        super.onStart();

    }

    @Override
    protected void onResume() {
        //Log.d(MY_LOG, "resume");
        super.onResume();
        if (positionOfAdapterBeforeRotate != -1) {
            //((LinearLayoutManager)recyclerView.getLayoutManager()).scrollToPositionWithOffset(positionOfAdapterBeforeRotate,4);
            recyclerView.scrollToPosition(positionOfAdapterBeforeRotate);
            Log.d(MainActivity.MY_LOG, "scroll" + positionOfAdapterBeforeRotate);
            positionOfAdapterBeforeRotate = -1;
        }

    }

    @Override
    public void setPosition(int i) {
        positionOfAdapterBeforeRotate = i;
    }
}



