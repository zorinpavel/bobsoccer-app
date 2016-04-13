package ru.bobsoccer;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.util.Log;


@SuppressWarnings("unchecked")
public class MainActivity extends BaseActivity {

    private final String TAG = "MainActivity";

    private TabLayout mTabLayout;
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mViewPager = (ViewPager) findViewById(R.id.tabs_viewpager);
        mViewPager.setAdapter(new TabFragmentAdapter(getSupportFragmentManager()));

        mTabLayout = (TabLayout) findViewById(R.id.tabs_layout);
        mTabLayout.setupWithViewPager(mViewPager);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mViewPager.setCurrentItem(2);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(TAB_POSITION, mTabLayout.getSelectedTabPosition());
        Log.d(TAG, "onSaveInstanceState:" + outState.getInt(TAB_POSITION));
    }


    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mViewPager.setCurrentItem(savedInstanceState.getInt(TAB_POSITION));
        Log.d(TAG, "onRestoreInstanceState:" + savedInstanceState.getInt(TAB_POSITION));
    }

    @Override
    protected int getSelfNavDrawerItem() {
        return 0; //NAVDRAWER_ITEM_MAIN;
    }

}
