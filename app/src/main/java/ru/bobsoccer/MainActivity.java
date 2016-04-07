package ru.bobsoccer;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("unchecked")
public class MainActivity extends BaseActivity {

    private final String TAG = "bobsoccer.MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            Log.d(TAG, "savedInstanceState == null");
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            TabsFragment tabsFragment = new TabsFragment();
            transaction.replace(R.id.tabs_frame, tabsFragment);
            transaction.commit();
        } else
            Log.d(TAG, "savedInstanceState != null");

    }

    @Override
    protected int getSelfNavDrawerItem() {
        return 0;//NAVDRAWER_ITEM_MAIN;
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
