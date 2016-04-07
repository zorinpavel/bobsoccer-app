package ru.bobsoccer;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.HeaderViewListAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

@SuppressWarnings("unchecked")
public abstract class BaseActivity extends AppCompatActivity implements
        SharedPreferences.OnSharedPreferenceChangeListener,
        NavigationView.OnNavigationItemSelectedListener {

    private final String TAG = "bobsoccer.BaseActivity";

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

    public Session activitySession;

    // list of navdrawer items that were actually added to the navdrawer, in order
    private ArrayList<Integer> mNavDrawerItems = new ArrayList<>();

    protected static final int NAVDRAWER_ITEM_INVALID = -1;
    protected static final int NAVDRAWER_ITEM_SEPARATOR = -2;
    protected static final int NAVDRAWER_ITEM_SEPARATOR_SPECIAL = -3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activitySession = new Session(this);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        setupNavDrawer();
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
                    setupAccauntBox();

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

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void setupNavDrawer() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setTitle(null);
        setSupportActionBar(mToolbar);

        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null)
            actionBar.setDisplayHomeAsUpEnabled(true);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (mDrawerLayout == null) {
            return;
        }
        ActionBarDrawerToggle mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar, R.string.open_drawer, R.string.close_drawer) {
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
        mNavigationView.setNavigationItemSelectedListener(this);

        createNavDrawerItems();
    }

    private void createNavDrawerItems() {
        mNavDrawerItems.clear();
        final Menu navigationMenu = mNavigationView.getMenu();
        navigationMenu.clear();

//        navigationMenu.setGroupVisible(R.id.group_1, true)

//        navigationMenu.inflateMenu(R.menu.second_menu);

        User currentUser = activitySession.GetObject("currentUser", User.class);
        Log.d(TAG, String.valueOf(currentUser));
        mNavDrawerItems.add(0);

        if (currentUser.us_id > 0)
            mNavDrawerItems.add(1);
        mNavDrawerItems.add(2);

        for (int itemId : mNavDrawerItems) {
            navigationMenu.add(NAVDRAWER_TITLE_RES_ID[itemId]);

//            MenuItem menuItem = navigationMenu.getItem(itemId);
//            menuItem.setIcon(NAVDRAWER_ICON_RES_ID[itemId]);
//            menuItem.setChecked(getSelfNavDrawerItem() == itemId);
        }

//        for (int itemId : mNavDrawerItems) {
//            navigationMenu.findItem(itemId).setIcon(NAVDRAWER_ICON_RES_ID[itemId]);
//            navigationMenu.findItem(itemId).setChecked(getSelfNavDrawerItem() == itemId);
//        }

        for (int i = 0, count = mNavigationView.getChildCount(); i < count; i++) {
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
        Log.d(TAG, key);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        item.setChecked(true);
        mDrawerLayout.closeDrawers();
        return true;
    }

    private void setupAccauntBox() {
        User currentUser = activitySession.GetObject("currentUser", User.class);

        if (currentUser.us_id > 0) {
            Target target = new Target() {
                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                    BitmapDrawable icon = new BitmapDrawable(mToolbar.getResources(), bitmap);
                    mToolbar.setNavigationIcon(icon);
                }

                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable) {
                }

                @Override
                public void onBitmapFailed(Drawable errorDrawable) {
                }
            };

            Picasso.with(mToolbar.getContext())
                    .load(API.DomainUrl + String.valueOf(currentUser.userpic))
                    .placeholder(R.drawable.ic_toolbar_account_circle)
                    .resize(80, 80)
                    .transform(new CircleTransform())
                    .into(target);

        TextView login = (TextView) findViewById(R.id.login);
        login.setText(currentUser.login);
        login.setTextColor(getResources().getColor(R.color.theme_red_dark));
//
        TextView mail = (TextView) findViewById(R.id.mail);
        mail.setText(currentUser.mail);

        TextView Lev_Points = (TextView) findViewById(R.id.Lev_Points);
        Lev_Points.setText(String.valueOf(currentUser.Lev_Points));

        TextView Prev_Points = (TextView) findViewById(R.id.Prev_Points);
        Prev_Points.setText((currentUser.Dif_Points >= 0 ? "+" : "-") + String.valueOf(currentUser.Dif_Points));
        Prev_Points.setTextColor(getResources().getColor(currentUser.Color_Points));

        if(currentUser.Level > 0) {
            ImageView Level = (ImageView) findViewById(R.id.Level);
            Picasso.with(getApplicationContext())
                    .load(API.DomainUrl + "/img/fishka/big/level_" + String.valueOf(currentUser.Level) + ".png")
                    .resize(30, 30)
                    .into(Level);
        }

        ImageView avatar = (ImageView) findViewById(R.id.avatar);
        Picasso.with(getApplicationContext())
                .load(API.DomainUrl + String.valueOf(currentUser.avatar))
                .placeholder(R.drawable.ic_toolbar_account_circle)
                .resize(120, 120)
                .transform(new CircleTransform())
                .into(avatar);

        }
    }

}
