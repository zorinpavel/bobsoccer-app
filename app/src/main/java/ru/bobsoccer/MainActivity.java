package ru.bobsoccer;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import org.json.JSONObject;

public class MainActivity extends Activity implements API.ApiResponse {

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        new API(this, new API.ApiResponse() {
            @Override
            public void onTaskCompleted(JSONObject jsonObj){
                Log.d(TAG, "Api.jsonObj:" + String.valueOf(jsonObj));
            }
        }).Get("Users", "GetUser");
    }

    public void buttonClick(View view) {
    }

    @Override
    public void onTaskCompleted(JSONObject output) {

    }
}
