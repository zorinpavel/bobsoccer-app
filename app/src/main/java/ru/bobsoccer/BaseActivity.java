package ru.bobsoccer;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.HeaderViewListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

@SuppressWarnings("unchecked")
public abstract class BaseActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    private final String TAG = "bobsoccer";

    // titles for navdrawer items (indices must correspond to the above)
    private static final int[] NAVDRAWER_TITLE_RES_ID = new int[]{
            R.string.navdrawer_item_sign_in,
            R.string.navdrawer_item_settings,
            R.string.navdrawer_item_log_out,
    };

    // icons for navdrawer items (indices must correspond to above array)
    private static final int[] NAVDRAWER_ICON_RES_ID = new int[]{
            R.drawable.ic_navview_off, // Sign in
            R.drawable.ic_navview_settings, // Settings.
            R.drawable.ic_navview_off, // Settings.
    };

    private Toolbar mToolbar;
    private NavigationView mNavigationView;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private Session activitySession;

    // list of navdrawer items that were actually added to the navdrawer, in order
    private ArrayList<Integer> mNavDrawerItems = new ArrayList<>();

    protected static final int NAVDRAWER_ITEM_INVALID = -1;
    protected static final int NAVDRAWER_ITEM_SEPARATOR = -2;
    protected static final int NAVDRAWER_ITEM_SEPARATOR_SPECIAL = -3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activitySession = new Session(this);

        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
        }

    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        Log.d(TAG, "onPostCreate");
//        setupNavDrawer();
    }

    @Override
    protected void onResume() {
        super.onResume();

        new API(this, new API.ApiResponse() {
            @Override
            public void onTaskCompleted(JSONObject resultObj) {
                try {
                    User rUser = new User(resultObj.getJSONObject("User"));
                    activitySession.SetObject("currentUser", rUser);

                    setupNavDrawer();
//                    if(Integer.parseInt(rUser.getString("us_id")) > 0) {
//
//                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, "GET", "Users", "GetUser")
                .setHiddenDialog()
                .execute();

        TextView tokenView = (TextView) findViewById(R.id.tokenView);
        tokenView.setText(String.valueOf(Session.Token));
    }

    private void setupNavDrawer() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setTitle(null);
        setSupportActionBar(mToolbar);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (mDrawerLayout == null) {
            return;
        }
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar, R.string.open_drawer, R.string.close_drawer) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                // code here will execute once the drawer is opened
            }
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                // Code here will execute once drawer is closed
            }
        };
        if (mToolbar != null) {
            mToolbar.setNavigationIcon(R.drawable.ic_toolbar_account_circle);
            mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mDrawerLayout.openDrawer(GravityCompat.START);
                }
            });
        }

        mNavigationView = (NavigationView) findViewById(R.id.user_navigation_view);

        createNavDrawerItems();

    }

    private void createNavDrawerItems() {
        mNavDrawerItems.clear();

        User currentUser = activitySession.GetObject("currentUser", User.class);
        Log.d(TAG, String.valueOf(currentUser));
        mNavDrawerItems.add(0);

        if(currentUser.us_id > 0)
            mNavDrawerItems.add(1);
        mNavDrawerItems.add(2);

        final Menu navigationMenu = mNavigationView.getMenu();
        for (int itemId : mNavDrawerItems) {
            navigationMenu.add(NAVDRAWER_TITLE_RES_ID[itemId]);
        }

        for (int i = 0, count = mNavigationView.getChildCount(); i < count; i++) {

            navigationMenu.findItem(i).setIcon(NAVDRAWER_ICON_RES_ID[i]);
            navigationMenu.findItem(i).setChecked(getSelfNavDrawerItem() == i);

            Log.d(TAG, getSelfNavDrawerItem() + " == " + i + " (" + count + ")");

            final View child = mNavigationView.getChildAt(i);
            if (child != null && child instanceof ListView) {
                final ListView menuView = (ListView) child;
                final HeaderViewListAdapter adapter = (HeaderViewListAdapter) menuView.getAdapter();
                final BaseAdapter wrapped = (BaseAdapter) adapter.getWrappedAdapter();
                wrapped.notifyDataSetChanged();
            }
        }
    }

    protected int getSelfNavDrawerItem() {
        return NAVDRAWER_ITEM_INVALID;
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

    }

}
