package ru.bobsoccer;

import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("unchecked")
public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private Session activitySession;

    private Toolbar mToolbar;

    String TITLES[] = {"Home","Events","Mail","Shop","Travel"};
    int ICONS[] = {
            android.support.v7.appcompat.R.drawable.abc_ic_menu_copy_material,
            android.support.v7.appcompat.R.drawable.abc_ic_menu_cut_material,
            android.support.v7.appcompat.R.drawable.abc_ic_menu_share_material,
            android.support.v7.appcompat.R.drawable.abc_ic_menu_paste_material,
            android.support.v7.appcompat.R.drawable.abc_ic_menu_selectall_material
    };

    String NAME = "Akash Bangad";
    String EMAIL = "akash.bangad@android4devs.com";
    int PROFILE = R.drawable.aka;

    RecyclerView mRecyclerView;
    RecyclerView.Adapter userNavigationAdapter;
    RecyclerView.LayoutManager mLayoutManager;
    DrawerLayout mDrawer;

    ActionBarDrawerToggle mDrawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        mRecyclerView = (RecyclerView) findViewById(R.id.RecyclerView);
        mRecyclerView.setHasFixedSize(true);

        userNavigationAdapter = new UserNavigationAdapter(TITLES, ICONS, NAME, EMAIL, PROFILE);
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
//        mDrawer.setDrawerListener(mDrawerToggle);
//        mDrawerToggle.syncState();

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
        }, "GET", "Users", "anyFunc").execute(params);
    }

    public void setuserClick(View view) {
        Map<String, String> params = new HashMap<>();
        params.put("login", "admin");
        params.put("pass", "5awwor");
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
        }, "GET", "Users", "CheckValidEnter").execute(params);
    }

}
