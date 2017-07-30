package com.levenko.myequilator;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;


import android.support.design.widget.TabLayout;
import android.support.v13.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.levenko.myequilator.entity.DataFromIntent;
import com.levenko.myequilator.entity.IndexesDataWasChosen;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;

import android.app.FragmentManager;
import android.view.ViewGroup;

public class MainActivity2 extends AppCompatActivity implements CardsDialogFragment.CardDialogFragmentListener,AdShower {
    private AdView mAdView;
    private InterstitialAd mInterstitialAd;


    private static boolean RUN_ONCE = true;


    private ViewPager viewPager;

    private int tryToShowAd;
    private int mCurrentPagerPosition;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);

        if (savedInstanceState!=null){
           tryToShowAd = savedInstanceState.getInt("counter");
           mCurrentPagerPosition = savedInstanceState.getInt("currentPager");
        }

        LayoutInflater inflator = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        @SuppressLint("InflateParams") View v = inflator.inflate(R.layout.ads, null);

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
            AllCards.initializeData();
            RUN_ONCE = false;
        }

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);
        //viewPager.setCurrentItem(0);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }
            @Override
            public void onPageSelected(int position) {
                if(position!=mCurrentPagerPosition) {
                    AllCards.resetWasChosen();
                    getFragment().cleanFragment();
                }
                mCurrentPagerPosition = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab);
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        tabLayout.setupWithViewPager(viewPager);



    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getFragmentManager());
        viewPager.setAdapter(adapter);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        menu.findItem(R.id.speed).setIntent(new Intent(this, SettingsActivity.class));
        menu.findItem(R.id.credit).setIntent(new Intent(this, DescriptionActivity.class));
        menu.findItem(R.id.help).setIntent(new Intent(this, HelpActivity.class));
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.quit:
                finish();
                break;
            case R.id.clear_all:
                getFragment().cleanFragment();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (outState.isEmpty()) {
            outState.putBoolean("bug:fix", true);
        }
        outState.putInt("counter", tryToShowAd);
        outState.putInt("currentPager", mCurrentPagerPosition);
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
    public void onDialogOkClick(Intent data) {
        onActivityResult(Constants.REQUEST_CODE_CARD, RESULT_OK, data);

    }
    private MainFragment getFragment(){
        //return (MainFragment) adapter.getRegisteredFragment(viewPager.getCurrentItem());
        return (MainFragment) viewPager.getAdapter().instantiateItem(viewPager, viewPager.getCurrentItem());
    }

    @Override
    public void onDialogCancelClick(int positionOfAdapter, String kindOfAdapter) {
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
        private final String[] tabTitles = new String[] { getString(R.string.for6),getString(R.string.for10) };

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return MainFragment.newInstance(tabTitles[position]);
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
            return super.instantiateItem(container, position);
        }

    }

}
