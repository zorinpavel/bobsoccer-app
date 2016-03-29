package ru.bobsoccer;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("unchecked")
public class MainActivity extends BaseActivity {

    private static final String TAG = "MainActivity";
    private Session activitySession;

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

//    public void fillUserNavigation() {
//        User currentUser = activitySession.GetObject("currentUser", User.class);
//
//        TextView tView = (TextView) findViewById(R.id.textView);
//        tView.setText(currentUser.us_id + ":" + currentUser.login);
//
//        TextView login = (TextView) findViewById(R.id.login);
//        login.setText(currentUser.login);
//        login.setTextColor(getResources().getColor(R.color.theme_red_dark));
//
//        TextView mail = (TextView) findViewById(R.id.mail);
//        mail.setText(currentUser.mail);
//
//        TextView Lev_Points = (TextView) findViewById(R.id.Lev_Points);
//        Lev_Points.setText(String.valueOf(currentUser.Lev_Points));
//
//        TextView Prev_Points = (TextView) findViewById(R.id.Prev_Points);
//        Prev_Points.setText((currentUser.Dif_Points >= 0 ? "+" : "-") + String.valueOf(currentUser.Dif_Points));
//        Prev_Points.setTextColor(getResources().getColor(currentUser.Color_Points));
//
//        if(currentUser.Level > 0) {
//            ImageView Level = (ImageView) findViewById(R.id.Level);
//            Picasso.with(getApplicationContext())
//                    .load(API.DomainUrl + "/img/fishka/big/level_" + String.valueOf(currentUser.Level) + ".png")
//                    .resize(50, 50)
//                    .into(Level);
//        }
//
//        ImageView avatar = (ImageView) findViewById(R.id.avatar);
//        Picasso.with(getApplicationContext())
//                .load(API.DomainUrl + String.valueOf(currentUser.avatar))
//                .placeholder(R.drawable.ic_toolbar_account_circle)
//                .resize(120, 120)
//                .transform(new CircleTransform())
//                .into(avatar);
//
//        Target target = new Target() {
//            @Override
//            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
//                Log.d(TAG, "onBitmapLoaded");
//                BitmapDrawable icon = new BitmapDrawable(mToolbar.getResources(), bitmap);
//                mToolbar.setNavigationIcon(icon);
//            }
//            @Override
//            public void onPrepareLoad(Drawable placeHolderDrawable) {
//            }
//            @Override
//            public void onBitmapFailed(Drawable errorDrawable) {
//            }
//        };
//
//        Picasso.with(mToolbar.getContext())
//                .load(API.DomainUrl + String.valueOf(currentUser.userpic))
//                .placeholder(R.drawable.ic_toolbar_account_circle)
//                .resize(120, 120)
//                .transform(new CircleTransform())
//                .into(target);
//    }

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
