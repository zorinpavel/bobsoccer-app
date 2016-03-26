package ru.bobsoccer;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import ru.bobsoccer.model.User;

@SuppressWarnings("unchecked")
public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private Session activitySession;

    private Toolbar mToolbar;
    public RecyclerView mRecyclerView;
    public RecyclerView.Adapter userNavigationAdapter;
    public RecyclerView.LayoutManager mLayoutManager;
    public DrawerLayout mDrawer;

    ActionBarDrawerToggle mDrawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setTitle(null);
        setSupportActionBar(mToolbar);

        mRecyclerView = (RecyclerView) findViewById(R.id.RecyclerView);
        mRecyclerView.setHasFixedSize(true);

        userNavigationAdapter = new UserNavigationAdapter(this);
        mRecyclerView.setAdapter(userNavigationAdapter);

        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mDrawer = (DrawerLayout) findViewById(R.id.DrawerLayout);
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawer, mToolbar, R.string.openDrawer, R.string.closeDrawer) {

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
        mDrawerToggle.setDrawerIndicatorEnabled(false);
        mToolbar.setNavigationIcon(R.drawable.ic_toolbar_account_circle);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDrawer.openDrawer(Gravity.LEFT);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        activitySession = new Session(this);
        new API(this, new API.ApiResponse() {
                @Override
                public void onTaskCompleted(JSONObject resultObj) {
                    TextView tView = (TextView) findViewById(R.id.textView);
                    try {
                        JSONObject rUser = resultObj.getJSONObject("User");
                        activitySession.SetObject("currentUser", new User(rUser));

                        if(Integer.parseInt(rUser.getString("us_id")) > 0) {

                            User currentUser = activitySession.GetObject("currentUser", User.class);
                            tView.setText(currentUser.us_id + ":" + currentUser.login);

                            Target target = new Target() {
                                @Override
                                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                                    Log.d(TAG, "onBitmapLoaded");
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
                                    .resize(120, 120)
                                    .transform(new CircleTransform())
                                    .into(target);

                        }
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

    public void buttonClick(View view) {
        Map<String, String> params = new HashMap<>();
        params.put("paramKey", "paramValue");
        new API(this, new API.ApiResponse() {
            @Override
            public void onTaskCompleted(JSONObject resultObj) {
                TextView tView = (TextView) findViewById(R.id.textView);
                JSONObject User;
                try {
                    User = resultObj.getJSONObject("User");
                    tView.setText(String.valueOf(User));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, "GET", "Users", "GetUser")
                .requestParams(params)
                .execute();
    }

    public void setuserClick(View view) {
        Map<String, String> params = new HashMap<>();
        params.put("login", "Pashtet");
        params.put("pass", "paafoos");
        new API(this, new API.ApiResponse() {
            @Override
            public void onTaskCompleted(JSONObject resultObj) {
                TextView tView = (TextView) findViewById(R.id.textView);
                JSONObject User;
                try {
                    String apiToken = resultObj.getString("Token");
                    activitySession.SetToken(apiToken);
                    TextView tokenView = (TextView) findViewById(R.id.tokenView);
                    tokenView.setText(String.valueOf(Session.Token));

                    User = resultObj.getJSONObject("User");
                    tView.setText(String.valueOf(User));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, "GET", "Users", "CheckValidEnter")
                .requestParams(params)
                .execute();
    }

}
