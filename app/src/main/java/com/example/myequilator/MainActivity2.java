package com.example.myequilator;

import android.app.DialogFragment;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;


import android.support.design.widget.TabLayout;
import android.support.v13.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;

import com.example.myequilator.adapters.MyPositionAdapter;
import com.example.myequilator.entity.DataFromIntent;
import com.example.myequilator.entity.IndexesDataWasChosen;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;

import java.util.ArrayList;
import java.util.List;

import android.app.FragmentManager;
import android.view.ViewGroup;
import android.widget.Toast;

public class MainActivity2 extends AppCompatActivity implements CardsDialogFragment.CardDialogFragmentListener,AdShower {
    private AdView mAdView;
    private InterstitialAd mInterstitialAd;


    private static boolean RUN_ONCE = true;


    ViewPagerAdapter adapter;
    private TabLayout tabLayout;
    private ViewPager viewPager;

    private int tryToShowAd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        Log.d(Constants.MY_LOG, "activity create");
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);

        if (savedInstanceState!=null){
           tryToShowAd = savedInstanceState.getInt("counter");
        }

        LayoutInflater inflator = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflator.inflate(R.layout.ads, null);

        PreferenceManager.setDefaultValues(this.getApplicationContext(), R.xml.speed_accuracy, true);
        mAdView = (AdView) v.findViewById(R.id.adView);
        final AdRequest adRequest = new AdRequest.Builder().addTestDevice("BC44035CB7EB870A409150BDE200B894").build();
        mAdView.loadAd(adRequest);
        actionBar.setCustomView(v);

        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-7055288022092797/1744327063");
        mInterstitialAd.loadAd(adRequest);
        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                // Load the next interstitial.
                mInterstitialAd.loadAd(adRequest);
            }

        });




        Toolbar parent = (Toolbar) v.getParent();
        parent.setPadding(0, 0, 0, 0);//for tab otherwise give space in tab
        parent.setContentInsetsAbsolute(0, 0);

        if (RUN_ONCE) {
            AllCards.initializeData(getApplicationContext());
            RUN_ONCE = false;
        }

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);
        viewPager.setCurrentItem(0);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                AllCards.resetWasChosen();
                getFragment().cleanFragment();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        tabLayout = (TabLayout) findViewById(R.id.tab);
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        tabLayout.setupWithViewPager(viewPager);



    }

    private void setupViewPager(ViewPager viewPager) {
        adapter = new ViewPagerAdapter(getFragmentManager());
        viewPager.setAdapter(adapter);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        menu.findItem(R.id.speed).setIntent(new Intent(this, SettingsActivity.class));
        menu.findItem(R.id.credit).setIntent(new Intent(this, DescriptionActivity.class));
        return true;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (outState.isEmpty()) {
            outState.putBoolean("bug:fix", true);
        }
        outState.putInt("counter", tryToShowAd);
    }

    @Override
    protected void onPause() {
        mAdView.pause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mAdView.resume();
    }

    @Override
    protected void onDestroy() {
        mAdView.destroy();
        super.onDestroy();
    }

    @Override
    public void onDialogOkClick(DialogFragment dialog, Intent data) {
        onActivityResult(Constants.REQUEST_CODE_CARD, RESULT_OK, data);

    }
    private MainFragment getFragment(){
        //return (MainFragment) adapter.getRegisteredFragment(viewPager.getCurrentItem());
        return (MainFragment) viewPager.getAdapter().instantiateItem(viewPager, viewPager.getCurrentItem());
    }

    @Override
    public void onDialogCancelClick(DialogFragment dialog, int positionOfAdapter, String kindOfAdapter) {
        getFragment().noteCardsChoosenAfterCancelDialog(kindOfAdapter,positionOfAdapter);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        DataFromIntent dataFromIntent;
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case Constants.REQUEST_CODE_RANGE:
                    dataFromIntent = new DataFromIntent(data, IndexesDataWasChosen.Type.RANGE);
                    getFragment().updateMyPositionAdapter(dataFromIntent,Constants.POSITION_ADAPTER);
                    break;
                case Constants.REQUEST_CODE_CARD:
                    dataFromIntent = new DataFromIntent(data, IndexesDataWasChosen.Type.HAND);
                    String type_of_adapter = data.getStringExtra(Constants.KIND_OF_ADAPTER);
                    switch (type_of_adapter) {
                        case Constants.POSITION_ADAPTER:
                            getFragment().updateMyPositionAdapter(dataFromIntent,Constants.POSITION_ADAPTER);
                            break;
                        case Constants.STREET_ADAPTER:
                            getFragment().updateMyPositionAdapter(dataFromIntent,Constants.STREET_ADAPTER);
                            break;
                    }
                    break;
            }
        }
    }

    @Override
    public void adShow() {
        tryToShowAd++;
        if (tryToShowAd%5==0&&mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
        }
    }


    class ViewPagerAdapter extends FragmentStatePagerAdapter {
        private String tabTitles[] = new String[] { getString(R.string.for6),getString(R.string.for10) };
        SparseArray<Fragment> registeredFragments = new SparseArray<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return MainFragment.newInstance(tabTitles[position],"");
        }

        @Override
        public int getCount() {
            return tabTitles.length;
        }



        @Override
        public CharSequence getPageTitle(int position) {
            return tabTitles[position];
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            Fragment fragment = (Fragment) super.instantiateItem(container, position);
            registeredFragments.put(position, fragment);
            return fragment;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            registeredFragments.remove(position);
            super.destroyItem(container, position, object);
        }

        public Fragment getRegisteredFragment(int position) {
            return registeredFragments.get(position);
        }
    }

}