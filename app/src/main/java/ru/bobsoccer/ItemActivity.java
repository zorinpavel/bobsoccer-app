package ru.bobsoccer;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.ActionBar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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

    private CollapsingToolbarLayout collapsingToolbar;
    private AppBarLayout appBarLayout;

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
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem searchItem = menu.findItem(R.id.action_search);
        searchItem.setVisible(false);

        MenuItem settingsItem = menu.findItem(R.id.action_settings);
        settingsItem.setVisible(false);

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void setupActionBar() {
        super.setupActionBar();

        collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setStatusBarScrimResource(R.color.black_transparent_190);
        collapsingToolbar.setContentScrimResource(R.color.gray_transparent_150);
        collapsingToolbar.setExpandedTitleTextAppearance(R.style.Bobsoccer_Toolbar_Title_Expanded);

        appBarLayout = (AppBarLayout) findViewById(R.id.appbar);
        appBarLayout.setExpanded(false);
    }

    @Override
    public void setupDrawerLayout() {

        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null) {
            if(mDrawerToggle != null)
                mDrawerToggle.setDrawerIndicatorEnabled(false);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);

            mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ItemActivity.this.onBackPressed();
                }
            });
        }
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

        if(blogItem.Blo_Image != null) {
            ImageView header = (ImageView) findViewById(R.id.header);
            Picasso.with(this)
                    .load(API.DomainUrl + String.valueOf(blogItem.Blo_Image))
                    .resize(Utils.dpToPx(this, 480), Utils.dpToPx(this, 192))
                    .centerInside()
                    .into(header);

            appBarLayout.setExpanded(true, true);
        } else {
//            AppBarLayout.LayoutParams params = (AppBarLayout.LayoutParams) collapsingToolbar.getLayoutParams();
//            params.setScrollFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL | AppBarLayout.LayoutParams.SCROLL_FLAG_EXIT_UNTIL_COLLAPSED);
//            collapsingToolbar.setLayoutParams(params);

//            CollapsingToolbarLayout.LayoutParams mParams = new CollapsingToolbarLayout.LayoutParams(collapsingToolbar.getLayoutParams());
//            mParams.setCollapseMode(CollapsingToolbarLayout.LayoutParams.COLLAPSE_MODE_OFF);
//            mToolbar.setLayoutParams(mParams);
        }
    }

}
