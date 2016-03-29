package ru.bobsoccer;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("unchecked")
public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//
//        mNavigationView = (NavigationView) findViewById(R.id.user_navigation_view);
//        mNavigationView.setNavigationItemSelectedListener(
//                new NavigationView.OnNavigationItemSelectedListener() {
//                    @Override
//                    public boolean onNavigationItemSelected(MenuItem menuItem) {
//                        int id = menuItem.getItemId();
//                        switch(id) {
//                            case  R.id.nav_enter:
////                                Intent loginIntent = new Intent(getApplicationContext(), LoginActivity.class);
////                                startActivity(loginIntent);
//                                break;
//                        }
//                        mDrawerLayout.closeDrawers();
//                        return true;
//                    }
//                });
//
//
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
