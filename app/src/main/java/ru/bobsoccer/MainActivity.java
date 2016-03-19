package ru.bobsoccer;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import org.json.JSONObject;

import java.io.IOException;

public class MainActivity extends Activity {

    private API Api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Api = new API(this);
        Api.Get("Users", "GetUser");
//        Log.d(this.getPackageName(), "jsonObj:" + String.valueOf(jsonObj));
    }

    public void buttonClick(View view) {
        Api.Get("Blog", "GetUser");
    }
}
