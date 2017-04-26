package com.example.myequilator;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.LoaderManager;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.res.Configuration;
import android.graphics.drawable.ColorDrawable;
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
import com.example.myequilator.entity.Progress;
import com.stevebrecher.showdown.Showdown;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;


import static android.content.res.Configuration.ORIENTATION_PORTRAIT;

public class MainActivity extends AppCompatActivity implements CardsDialogFragment.CardDialogFragmentListener, LoaderManager.LoaderCallbacks<double[]> {

    MyPositionAdapter myPositionAdapter;
    StreetAdapter streetAdapter;

    TabHost tabHost;
    RecyclerView recyclerViewPosition;
    Progress progress;

    Handler handler;

    boolean isResultDelivered= true;
    ProgressDialog progressDialog;

    private static boolean RUN_ONCE = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (RUN_ONCE) {
            AllCards.initializeData(getApplicationContext());
            RUN_ONCE = false;
        }

        setTab();
        if (savedInstanceState != null) {
            setRecycler(savedInstanceState);
            isResultDelivered = savedInstanceState.getBoolean(Constants.IS_RESULT_DELIVERED);
        }
        else{
            setRecycler("tag1");
        }

        getLoaderManager().initLoader(Constants.LOADER_ID, null, this);
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                Bundle bundle = msg.getData();
                progress=new Progress(bundle);
                sendResult(progress.result());
            }
        };
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle(getString(R.string.calculate));
        progressDialog.setMessage("Calculating in progress...");
        progressDialog.setButton(Dialog.BUTTON_NEGATIVE, getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                progressDialog.dismiss();
            }
        });
        progressDialog.setCancelable(false);
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        if(!isResultDelivered){
            progressDialog.show();
        }
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


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(Constants.CURRENT_TAG, tabHost.getCurrentTabTag());
        outState.putStringArray(Constants.STRNGS_FROM_STREET_ADAPTER, streetAdapter.getTextFromEditViewStreet());
        outState.putStringArray(Constants.STRNGS_FROM_ADAPTER, myPositionAdapter.getTextFromTextView());
        outState.putDoubleArray(Constants.EQUITY,myPositionAdapter.getEquity());
        outState.putSerializable(Constants.INDEXES_DATA_WAS_CHOSEN_BY_STREET_ADAPTER, streetAdapter.getArrayIndexesDataWhichWasChoosen());
        outState.putSerializable(Constants.INDEXES_DATA_WAS_CHOSEN_BY_POSITION_ADAPTER, myPositionAdapter.getArrayIndexesDataWhichWasChoosen());
        outState.putBoolean(Constants.IS_RESULT_DELIVERED,isResultDelivered);
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.calculate:
               calculation();
        }
    }

    public void calculation(){
        getLoaderManager().restartLoader(Constants.LOADER_ID,null,this).forceLoad();
        progressDialog.show();
        isResultDelivered=false;
    }

    public void sendResult(double[] result){
        IndexesDataWasChosen[] indexes = myPositionAdapter.getArrayIndexesDataWhichWasChoosen();
        double[] equity = new double[indexes.length];
        Arrays.fill(equity, -1.0);
        int positionInResult = 0;
        for (int i = 0; i < indexes.length; i++) {
            if(indexes[i]!=null&&indexes[i].getType()== IndexesDataWasChosen.Type.HAND){
                equity[i]=result[positionInResult++];
            }
        }
        for (int i = 0; i < indexes.length; i++) {
            if(indexes[i]!=null&&indexes[i].getType()== IndexesDataWasChosen.Type.RANGE){
                equity[i]=result[positionInResult++];
            }
        }
        myPositionAdapter.setEquity(equity);
        myPositionAdapter.notifyDataSetChanged();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        DataFromIntent dataFromIntent;
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case Constants.REQUEST_CODE_RANGE:
                    dataFromIntent = new DataFromIntent(data, IndexesDataWasChosen.Type.RANGE);
                    updateMyPositionAdapter(dataFromIntent, myPositionAdapter);
                    break;
                case Constants.REQUEST_CODE_CARD:
                    dataFromIntent = new DataFromIntent(data, IndexesDataWasChosen.Type.HAND);
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
        if (indexes != null && indexes.getType() == IndexesDataWasChosen.Type.HAND) {
            AllCards.checkFlags(indexes.getIndexesDataWasChosen());
        }
    }

    public void updateMyPositionAdapter(DataFromIntent dataFromIntent, MyAdapter adapter) {
        adapter.replacedIndexesDataWasChosen(dataFromIntent);
        adapter.replacedToTextFromTextView(dataFromIntent);
        adapter.notifyDataSetChanged();
    }

    @Override
    public Loader<double[]> onCreateLoader(int id, Bundle args) {
        if(id==Constants.LOADER_ID){
            return new CalculationLoader(this,myPositionAdapter.getArrayIndexesDataWhichWasChoosen(),streetAdapter.getTextFromEditViewStreet(),handler);
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<double[]> loader, double[] data) {
        if(!isResultDelivered) {
            sendResult(data);
            progressDialog.dismiss();
        }
        isResultDelivered=true;
    }

    @Override
    public void onLoaderReset(Loader<double[]> loader) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        progressDialog.dismiss();
    }
}



