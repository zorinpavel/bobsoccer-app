package ru.bobsoccer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("unchecked")
public class ItemActivity extends BaseActivity {

    private final String TAG = "ItemActivity";
    private int Blo_Code;
    private Blog blogItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item);

        Intent intent = getIntent();
        Blo_Code = intent.getIntExtra("Blo_Code", 0);

        if(Blo_Code > 0)
            GetItem();
        else
            finish();

    }

    @Override
    public void setupActionBar() {
        super.setupActionBar();
    }

    @Override
    public void setupDrawerLayout() {
        super.setupDrawerLayout();

        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null) {
            mDrawerToggle.setDrawerIndicatorEnabled(false);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);

//            actionBar.setDisplayHomeAsUpEnabled(false);
//            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d(TAG, "onOptionsItemSelected:" + item.getItemId() + " = " + R.id.home + " (" + android.R.id.home + ")");
        return super.onOptionsItemSelected(item);
    }

    private void GetItem() {
        Map<String, String> params = new HashMap<>();
        params.put("Blo_Code", String.valueOf(Blo_Code));

        new API(this, new API.ApiResponse() {
            @Override
            public void onTaskCompleted(JSONObject resultObj) {
                try {
                    blogItem = new Blog(resultObj.getJSONObject(String.valueOf(Blo_Code)));
                    setItemView();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, "GET", "Blog", "GetItem")
                .requestParams(params)
                .execute();
    }
    
    private void setItemView() {
        ((TextView) findViewById(R.id.Blo_Header)).setText(blogItem.Blo_Header);
        mToolbar.setTitle(blogItem.Blo_Header);

        ((TextView) findViewById(R.id.Blo_Login)).setText(blogItem.Blo_Login);
        ((TextView) findViewById(R.id.Blo_Date)).setText(blogItem.Blo_Date);

        ImageView Blo_AvatarView = (ImageView) findViewById(R.id.Blo_Avatar);
        Picasso.with(this)
                .load(API.DomainUrl + String.valueOf(blogItem.Blo_Avatar))
                .placeholder(R.drawable.ic_account_circle_dark_48dp)
                .resize(Utils.dpToPx(this, 48), Utils.dpToPx(this, 48))
                .transform(new CircleTransform())
                .into(Blo_AvatarView);

        ((TextView) findViewById(R.id.Blo_Anounce)).setText(blogItem.Blo_Anounce);
        ((TextView) findViewById(R.id.Blo_Text)).setText(blogItem.Blo_Text);
    }

}
