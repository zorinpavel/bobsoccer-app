package ru.bobsoccer;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.common.api.CommonStatusCodes;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.json.JSONException;
import org.json.JSONObject;

@SuppressWarnings("unchecked")
public abstract class BaseActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener {

    private final String TAG = "BaseActivity";

    private static final int REQUEST_SIGNIN = 1;

    public Toolbar mToolbar;
    public NavigationView mNavigationView;
    public DrawerLayout mDrawerLayout;
    public ActionBarDrawerToggle mDrawerToggle;

    public Session activitySession;
    public User currentUser;

    protected static final int NAVDRAWER_ITEM_INVALID = -1;
    protected static final int NAVDRAWER_ITEM_SEPARATOR = -2;
    protected static final int NAVDRAWER_ITEM_SEPARATOR_SPECIAL = -3;
    protected static final String TAB_POSITION = "TAB_POSITION";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activitySession = new Session(this);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        setupActionBar();
        setupDrawerLayout();
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

                    setupAccaunt();
                    setupDrawerLayout();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, "GET", "Users", "GetCurrentUser")
                .setLoadingDialogDisabled()
                .setErrorDialogDisabled()
                .execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);

        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                return true;
            case R.id.action_settings:
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        item.setChecked(true);
        mDrawerLayout.closeDrawers();
        switch (item.getItemId()) {
            case R.id.navigation_signin:
                Intent intent = new Intent(this, LoginActivity.class);
                startActivityForResult(intent, REQUEST_SIGNIN);
                break;
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch(requestCode) {
            case REQUEST_SIGNIN:
                if(resultCode == CommonStatusCodes.SUCCESS) {
                    setupAccaunt();
                    createNavDrawerItems();
                }
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
                break;
        }
    }

    protected int getSelfNavDrawerItem() {
        return NAVDRAWER_ITEM_INVALID;
    }


    public void setupActionBar() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        mToolbar.setTitle(null);

//        ActionBar actionBar = getSupportActionBar();
//        if(actionBar != null) {
//            actionBar.setDisplayHomeAsUpEnabled(false);
//            actionBar.setHomeButtonEnabled(false);
//        }

    }

    public void setupDrawerLayout() {
        mToolbar.setNavigationIcon(R.drawable.ic_account_circle_light_36dp);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (mDrawerLayout == null) {
            return;
        }
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, BaseActivity.this.mToolbar, R.string.open_drawer, R.string.close_drawer) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }
        };

        mNavigationView = (NavigationView) findViewById(R.id.user_navigation_view);
        mNavigationView.setNavigationItemSelectedListener(this);

        createNavDrawerItems();
    }

    public void createNavDrawerItems() {
        Menu navigationMenu = mNavigationView.getMenu();

        if (currentUser != null && currentUser.us_id > 0) {
            navigationMenu.setGroupVisible(R.id.group_signin, true);
        } else {
            navigationMenu.setGroupVisible(R.id.group_signout, true);
        }
    }

    public void setupAccaunt() {
        currentUser = activitySession.GetObject("currentUser", User.class);

        if(currentUser != null && currentUser.us_id > 0) {
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
                    .placeholder(R.drawable.ic_account_circle_light_36dp)
                    .resize(Utils.dpToPx(this, 36), Utils.dpToPx(this, 36))
                    .transform(new CircleTransform())
                    .into(target);

            if(mDrawerLayout != null) {
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

                if (currentUser.Level > 0) {
                    ImageView Level = (ImageView) findViewById(R.id.Level);
                    Picasso.with(getApplicationContext())
                            .load(API.DomainUrl + "/img/fishka/big/level_" + String.valueOf(currentUser.Level) + ".png")
                            .resize(Utils.dpToPx(this, 12), Utils.dpToPx(this, 12))
                            .into(Level);
                }

                ImageView avatar = (ImageView) findViewById(R.id.avatar);
                Picasso.with(getApplicationContext())
                        .load(API.DomainUrl + String.valueOf(currentUser.avatar))
                        .placeholder(R.drawable.ic_account_circle_dark_48dp)
                        .resize(Utils.dpToPx(this, 48), Utils.dpToPx(this, 48))
                        .transform(new CircleTransform())
                        .into(avatar);
            }
        }
    }

}
