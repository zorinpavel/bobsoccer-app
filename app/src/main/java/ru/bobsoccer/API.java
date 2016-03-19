package ru.bobsoccer;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

class API {

    private static final MediaType MEDIA_TYPE_JPG = MediaType.parse("image/jpeg");

    private final OkHttpClient client;
    private static final String ApiHostName = "api.bobsoccer.ru";
    private static final String ApiUrlBase = "http://" + ApiHostName;
    private String ApiUrlRequest = null;
    public Activity mActivity;
    public static Session Session;
    public static String UserToken;
    public Callback callback;

    private static final String TAG = "API";

    public API(Activity _mActivity) {
        mActivity = _mActivity;

        client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();

        Session = new Session(mActivity);
        UserToken = Session.Get("UserToken");
        callback = new Callback() {

            private JSONObject jsonObj;

            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                try {
                    jsonObj = new JSONObject(response.body().string());
                    Log.d(TAG, String.valueOf(jsonObj));

                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                String isError = jsonObj.getString("Error");
                                if (isError.equals("1")) {
                                    String ErrorValue = "";
                                    JSONArray Errors = jsonObj.getJSONArray("Errors");
                                    for (int i = 0; i < Errors.length(); i++) {
                                        ErrorValue = ErrorValue + Errors.getJSONObject(i).getString("Error") + "\n";
                                    }
                                    showNotice(ErrorValue);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });

                } catch (JSONException e) {
                    Log.e(mActivity.getPackageName(), "API jsonObj is NULL");
                    e.printStackTrace();
                }
            }

        };
    }

    private String GetUserAgent() {
        PackageManager manager = mActivity.getPackageManager();
        PackageInfo info;
        String UserAgentName = "";
        try {
            info = manager.getPackageInfo(mActivity.getPackageName(), 0);
            UserAgentName = info.packageName + " " + info.versionName + " Android: " + Build.VERSION.RELEASE + " " + Build.VERSION.CODENAME + " " + Build.VERSION.SDK_INT + " (" + Build.MANUFACTURER + " " + Build.MODEL + ")";
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return UserAgentName;
    }

    public JSONObject Get(String ClassName, String MethodName) {
        Map<String, String> params = new HashMap<>();
        return Get(ClassName, MethodName, params);
    }

    public JSONObject Get(String ClassName, String MethodName, Map<String, String> params) {
        getApiUrlRequest(ClassName, MethodName, params);

        Request request = new Request.Builder()
                .header("User-Agent", GetUserAgent())
                .url(ApiUrlRequest)
                .build();
        ApiUrlRequest = null;

        client.newCall(request).enqueue(callback);

        return null;
    }

    public JSONObject Post(String ClassName, String MethodName, Map<String, String> postParams, String postFileName) throws IOException {
        JSONObject jsonObj = null;
        getApiUrlRequest(ClassName, MethodName, new HashMap<String, String>());

        MultipartBody.Builder builder = new MultipartBody.Builder();
        builder.setType(MultipartBody.FORM);
        for (Map.Entry param : postParams.entrySet()) {
            builder.addFormDataPart(String.valueOf(param.getKey()), String.valueOf(param.getValue()));
        }
        if(postFileName != null && !postFileName.isEmpty()) {
            builder.addFormDataPart("image", postFileName, RequestBody.create(MEDIA_TYPE_JPG, new File(postFileName)));
        }
        RequestBody requestBody = builder.build();

        Request request = new Request.Builder()
                .url(this.ApiUrlRequest)
                .post(requestBody)
                .build();
        this.ApiUrlRequest = null;

        // TODO: Catch SocketTimeoutException
        Response response = client.newCall(request).execute();
        if (!response.isSuccessful())
            throw new IOException("Unexpected code " + response);

        try {
            jsonObj = new JSONObject(response.body().string());
        } catch (JSONException e) {
            showError("API.Post server error");
        }

        Log.d(TAG, String.valueOf(jsonObj));
        return jsonObj;
    }

    public void getApiUrlRequest(String ClassName, String MethodName, Map<String, String> params) {
        this.ApiUrlRequest = ApiUrlBase + "/" + ClassName + "." + MethodName;

        for (Map.Entry entry : params.entrySet()) {
            this.ApiUrlRequest = this.ApiUrlRequest + "&" + entry.getKey() + "=" + entry.getValue();
        }
        Log.d(TAG, this.ApiUrlRequest);

    }

    public void showError(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
        builder.setMessage(message)
                .setTitle("Ошибка")
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        mActivity.finish();
                    }
                }).setIcon(android.R.drawable.ic_dialog_alert);
        AlertDialog alert = builder.create();
        alert.show();
    }

    public void showNotice(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
        builder.setMessage(message)
                .setTitle("Предупреждение")
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // do nothing
                    }
                }).setIcon(android.R.drawable.ic_dialog_info);
        AlertDialog alert = builder.create();
        alert.show();
    }

}
