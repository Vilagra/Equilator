package com.example.myequilator;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.LoaderManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.res.Configuration;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.myequilator.adapters.MyAdapter;
import com.example.myequilator.adapters.MyPositionAdapter;
import com.example.myequilator.adapters.StreetAdapter;
import com.example.myequilator.entity.DataFromIntent;
import com.example.myequilator.entity.IndexesDataWasChosen;
import com.example.myequilator.entity.Progress;

import java.util.Arrays;
import java.util.List;

import static android.content.res.Configuration.ORIENTATION_PORTRAIT;


public class MainFragment extends Fragment implements LoaderManager.LoaderCallbacks<double[]>, StreetAdapter.Calculation {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    MyPositionAdapter myPositionAdapter;
    StreetAdapter streetAdapter;

    RecyclerView recyclerViewPosition;
    Progress progress;

    Handler handler;

    boolean isResultDelivered = true;
    ProgressDialog progressDialog;

    View view = null;


    // TODO: Rename and change types of parameters
    private String stringIndicatorOfPlayersNumber;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public MainFragment() {
        // Required empty public constructor
    }

    public static MainFragment newInstance(String param1, String param2) {
        MainFragment fragment = new MainFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        //fragment.setRetainInstance(true);
        return fragment;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //outState.putString(Constants.CURRENT_TAG, );
        outState.putStringArray(Constants.STRNGS_FROM_STREET_ADAPTER, streetAdapter.getTextFromEditViewStreet());
        outState.putSerializable(Constants.INDEXES_DATA_WAS_CHOSEN_BY_STREET_ADAPTER, streetAdapter.getArrayIndexesDataWhichWasChoosen());
        outState.putStringArray(Constants.STRNGS_FROM_ADAPTER, myPositionAdapter.getTextFromTextView());
        outState.putDoubleArray(Constants.EQUITY, myPositionAdapter.getEquity());
        outState.putSerializable(Constants.INDEXES_DATA_WAS_CHOSEN_BY_POSITION_ADAPTER, myPositionAdapter.getArrayIndexesDataWhichWasChoosen());
        outState.putBoolean(Constants.IS_RESULT_DELIVERED, isResultDelivered);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(Constants.MY_LOG, "fragment create");
        if (getArguments() != null) {
            stringIndicatorOfPlayersNumber = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                Bundle bundle = msg.getData();
                progress = new Progress(bundle);
                sendResult(progress.result());
            }
        };

        progressDialog = new ProgressDialog(getActivity(), R.style.MyProgress);
        progressDialog.setTitle(getString(R.string.calculate));
        progressDialog.setMessage("Calculating in progress...");
        progressDialog.setButton(Dialog.BUTTON_NEGATIVE, getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Loader<double[]> loader = getLoaderManager().getLoader(Constants.LOADER_ID);
                ((CalculationLoader) loader).finishLoad();
                progressDialog.dismiss();
            }
        });
        progressDialog.setCancelable(false);
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(Constants.MY_LOG, "fragment createview");
        if (view == null) {
            view = inflater.inflate(R.layout.main_fragment, container, false);
            setPositionRecycler(savedInstanceState);
            setStreetRecycler(savedInstanceState);
        }
        Loader loader = getLoaderManager().initLoader(Constants.LOADER_ID, null, this);
        if(savedInstanceState!=null)
        isResultDelivered = savedInstanceState.getBoolean(Constants.IS_RESULT_DELIVERED);
        if (!isResultDelivered) {
            progressDialog.show();
            ((CalculationLoader) loader).setHandler(handler);
        }

        return view;
    }

    private void setPositionRecycler(Bundle savedInstanceState) {
        String[] dataForRecyclerPosition = getResources().getStringArray(R.array.positions);
        recyclerViewPosition = (RecyclerView) view.findViewById(R.id.recycler);
        if (stringIndicatorOfPlayersNumber.equals(getString(R.string.for6))) {
            myPositionAdapter = new MyPositionAdapter(getActivity(), Arrays.copyOfRange(dataForRecyclerPosition, 4, dataForRecyclerPosition.length), this);
        } else if (stringIndicatorOfPlayersNumber.equals(getString(R.string.for10))) {
            myPositionAdapter = new MyPositionAdapter(getActivity(), dataForRecyclerPosition, this);
        } else {
            throw new IllegalArgumentException();
        }
        if (savedInstanceState != null) {
            double[] equity = savedInstanceState.getDoubleArray(Constants.EQUITY);
            IndexesDataWasChosen[] indexesFromPositionAdapter = (IndexesDataWasChosen[]) savedInstanceState.getSerializable(Constants.INDEXES_DATA_WAS_CHOSEN_BY_POSITION_ADAPTER);
            String[] textFomEditTextPosition = savedInstanceState.getStringArray(Constants.STRNGS_FROM_ADAPTER);
            myPositionAdapter.setEquity(equity);
            myPositionAdapter.setTextFromTextView(textFomEditTextPosition);
            myPositionAdapter.setArrayIndexesDataWhichWasChoosen(indexesFromPositionAdapter);
        }

        LinearLayoutManager managerPosition = new LinearLayoutManager(getActivity(), OrientationHelper.VERTICAL, false);
        recyclerViewPosition.setLayoutManager(managerPosition);
        recyclerViewPosition.setAdapter(myPositionAdapter);

    }

    private void setStreetRecycler(Bundle savedInstanceState) {
        RecyclerView recyclerViewStreet = (RecyclerView) view.findViewById(R.id.recycler_street);
        String[] dataForRecyclerStreet = getResources().getStringArray(R.array.streets);
        RecyclerView.LayoutManager managerStreet;
        if ((getResources().getConfiguration().screenLayout &
                Configuration.SCREENLAYOUT_SIZE_MASK) ==
                Configuration.SCREENLAYOUT_SIZE_NORMAL && getResources().getConfiguration().orientation == ORIENTATION_PORTRAIT) {
            managerStreet = new GridLayoutManager(getActivity(), 2, OrientationHelper.HORIZONTAL, false);
        } else {
            managerStreet = new LinearLayoutManager(getActivity(), OrientationHelper.HORIZONTAL, false);
        }
        streetAdapter = new StreetAdapter(getActivity(), dataForRecyclerStreet, this);
        if (savedInstanceState != null) {
            String[] textFomEditTextStreet = savedInstanceState.getStringArray(Constants.STRNGS_FROM_STREET_ADAPTER);
            IndexesDataWasChosen[] indexesFromStreetAdapter = (IndexesDataWasChosen[]) savedInstanceState.getSerializable(Constants.INDEXES_DATA_WAS_CHOSEN_BY_STREET_ADAPTER);
            streetAdapter.setTextFromEditViewStreet(textFomEditTextStreet);
            streetAdapter.setArrayIndexesDataWhichWasChoosen(indexesFromStreetAdapter);
        }
        recyclerViewStreet.setLayoutManager(managerStreet);
        recyclerViewStreet.setAdapter(streetAdapter);
    }

    public void calculation() {
        if (myPositionAdapter.amountPlayers() < 2) {
            Toast.makeText(getActivity(), R.string.not_enough, Toast.LENGTH_SHORT).show();
        } else {
            ((AdShower)getActivity()).adShow();
            Loader loader = getLoaderManager().restartLoader(Constants.LOADER_ID, null, this);
            //Loader loader = getLoaderManager().initLoader(Constants.LOADER_ID, null, this);
            progressDialog.show();
            isResultDelivered = false;
            loader.forceLoad();
        }
    }

    public void sendResult(double[] result) {
        IndexesDataWasChosen[] indexes = myPositionAdapter.getArrayIndexesDataWhichWasChoosen();
        double[] equity = new double[indexes.length];
        Arrays.fill(equity, -1.0);
        int positionInResult = 0;
        for (int i = 0; i < indexes.length; i++) {
            if (indexes[i] != null && indexes[i].getType() == IndexesDataWasChosen.Type.HAND) {
                equity[i] = result[positionInResult++];
            }
        }
        for (int i = 0; i < indexes.length; i++) {
            if (indexes[i] != null && indexes[i].getType() == IndexesDataWasChosen.Type.RANGE) {
                equity[i] = result[positionInResult++];
            }
        }
        myPositionAdapter.setEquity(equity);
        myPositionAdapter.notifyDataSetChanged();
    }


    public void noteCardsChoosenAfterCancelDialog(String kindOfAdapter, int positionOfAdapter) {
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


    public void updateMyPositionAdapter(DataFromIntent dataFromIntent, String kindOfAdapter) {
        MyAdapter adapter = null;
        switch (kindOfAdapter) {
            case Constants.POSITION_ADAPTER:
                adapter = myPositionAdapter;
                break;
            case Constants.STREET_ADAPTER:
                adapter = streetAdapter;
                break;
        }
        adapter.replacedIndexesDataWasChosen(dataFromIntent);
        adapter.replacedToTextFromTextView(dataFromIntent);
        adapter.notifyDataSetChanged();
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
/*            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");*/
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onDestroy() {
        progressDialog.dismiss();
        super.onDestroy();
    }

    @Override
    public Loader<double[]> onCreateLoader(int id, Bundle args) {
        if (id == Constants.LOADER_ID) {
            return new CalculationLoader(getActivity(), myPositionAdapter.getArrayIndexesDataWhichWasChoosen(), streetAdapter.getTextFromEditViewStreet(), handler);
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<double[]> loader, double[] data) {
        if (!isResultDelivered) {
            sendResult(data);
            progressDialog.dismiss();
        }
        isResultDelivered = true;
    }

    @Override
    public void onLoaderReset(Loader<double[]> loader) {

    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    public void cleanFragment() {
        myPositionAdapter.clean();
        streetAdapter.clean();
    }
}