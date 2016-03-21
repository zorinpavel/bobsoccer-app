package ru.bobsoccer;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
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

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class API extends AsyncTask<Map<String, String>, Void, JSONObject> {

    private static final String TAG = "API";
    private static final MediaType MEDIA_TYPE_JPG = MediaType.parse("image/jpeg");

    private ProgressDialog pDialog;

    private OkHttpClient client;
    private static final String ApiHostName = "api.bobsoccer.ru";
    private static final String ApiUrlBase = "http://" + ApiHostName;
    private String ApiUrlRequest = null;

    public interface ApiResponse {
        void onTaskCompleted(JSONObject output);
    }

    private Activity mActivity = null;
    public ApiResponse apiResponse = null;
    public String requestMethod = "GET";
    public String ClassName = null;
    public String MethodName = null;

    public API(Activity _mActivity, ApiResponse _apiResponse, String _requestMethod, String _ClassName, String _MethodName) {
        this.mActivity = _mActivity;
        this.apiResponse = _apiResponse;
        this.client = new OkHttpClient.Builder()
                .connectTimeout(15, TimeUnit.SECONDS)
                .readTimeout(15, TimeUnit.SECONDS)
                .writeTimeout(15, TimeUnit.SECONDS)
                .build();
        this.requestMethod = _requestMethod;
        this.ClassName = _ClassName;
        this.MethodName = _MethodName;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        pDialog = new ProgressDialog(this.mActivity);
        pDialog.setMessage("Загрузка ...");
        pDialog.setCancelable(false);
        pDialog.show();
    }

    @SafeVarargs
    @Override
    protected final JSONObject doInBackground(Map<String, String>... requestParams) {
        Map<String, String> params = new HashMap<>();
        if(requestParams.length > 0  ) {
            for (Map.Entry param : requestParams[0].entrySet()) {
                params.put(String.valueOf(param.getKey()), String.valueOf(param.getValue()));
            }
            return Get(this.ClassName, this.MethodName, params);
        } else
            return Get(this.ClassName, this.MethodName);
    }

    @Override
    protected void onPostExecute(JSONObject resultObj) {
        super.onPostExecute(resultObj);

        if (pDialog.isShowing())
            pDialog.dismiss();

        try {
            String isError = resultObj.getString("Error");
            if (isError.equals("1")) {
                String ErrorValue = "";
                JSONArray Errors = resultObj.getJSONArray("Errors");
                for (int i = 0; i < Errors.length(); i++) {
                    ErrorValue = ErrorValue + Errors.getJSONObject(i).getString("Error") + "\n";
                }
                showNotice(ErrorValue);
            } else {
                apiResponse.onTaskCompleted(resultObj);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public JSONObject Get(String ClassName, String MethodName) {
        Map<String, String> params = new HashMap<>();
        return Get(ClassName, MethodName, params);
    }

    public JSONObject Get(String ClassName, String MethodName, Map<String, String> params) {
        getApiUrlRequest(ClassName, MethodName, params);

        Request request = new Request.Builder()
                .header("User-Agent", GetUserAgent())
                .header("Authorization", "Bearer " + Session.Token)
                .url(ApiUrlRequest)
                .build();

        Response response;
        JSONObject jsonObj = null;
        try {
            response = this.client.newCall(request).execute();
            jsonObj = new JSONObject(response.body().string());
            ApiUrlRequest = null;
        } catch (IOException e) {
            this.mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (getStatus() == Status.RUNNING) {
                        cancel(true);
                        if (pDialog.isShowing())
                            pDialog.dismiss();
                    }

                    AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
                    builder.setTitle("Timeout error")
                            .setMessage("Sorry server doesn't response!\nCheck your internet connection or try again.")
                            .setCancelable(true)
                            .setPositiveButton("Try again", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int id) {
                                    Log.d(TAG, "load again");
                                }
                            }).setIcon(android.R.drawable.ic_dialog_alert);
                    AlertDialog alert = builder.create();
                    alert.show();
                }
            });
        } catch (JSONException e) {
            this.mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (getStatus() == Status.RUNNING) {
                        cancel(true);
                        if (pDialog.isShowing())
                            pDialog.dismiss();
                    }
                    showError("API.Get server error");
                }
            });
        }

        Log.d(TAG, String.valueOf(jsonObj));
        return jsonObj;
    }

    public JSONObject Post(String ClassName, String MethodName, Map<String, String> postParams, String postFileName) {
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

        Response response;
        JSONObject jsonObj = null;
        try {
            response = this.client.newCall(request).execute();
            jsonObj = new JSONObject(response.body().string());
        } catch (JSONException | IOException e) {
            e.printStackTrace();
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

    private String GetUserAgent() {
        PackageManager manager = this.mActivity.getPackageManager();
        PackageInfo info;
        String UserAgentName = "";
        try {
            info = manager.getPackageInfo(this.mActivity.getPackageName(), 0);
            UserAgentName = info.packageName + "/" + info.versionName + " (" + Build.MANUFACTURER + "/" + Build.MODEL + ")" + " Android/" + Build.VERSION.RELEASE + " " + Build.VERSION.CODENAME + " " + Build.VERSION.SDK_INT;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return UserAgentName;
    }


    // TODO use Handler
    public void showError(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this.mActivity);
        builder.setMessage(message)
                .setTitle("Ошибка")
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if(!mActivity.getLocalClassName().equals("MainActivity"))
                            mActivity.finish();
                    }
                }).setIcon(android.R.drawable.ic_dialog_alert);
        AlertDialog alert = builder.create();
        alert.show();
    }

    public void showNotice(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this.mActivity);
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
