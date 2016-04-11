package ru.bobsoccer;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.Nullable;
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

class API extends AsyncTask<Map<String, String>, Void, JSONObject> {

    private static final String TAG = "API";
    private static final MediaType MEDIA_TYPE_JPG = MediaType.parse("image/jpeg");

    private ProgressDialog pDialog;

    private OkHttpClient client;
    public static final String DomainUrl = "http://bobsoccer.ru";
    public static final String ApiHostName = "api.bobsoccer.ru";
    public static final String ApiUrlBase = "http://" + ApiHostName;
    private String ApiUrlRequest = null;

    private Boolean hiddenDialog = false;
    private Map<String, String> requestParams = new HashMap<>();

    public interface ApiResponse {
        void onTaskCompleted(JSONObject output);
    }

    private Activity mActivity = null;
    private ApiResponse apiResponse = null;
    private String requestMethod = "GET";
    private String ClassName = null;
    private String MethodName = null;

    public API(Activity _mActivity, ApiResponse _apiResponse, String _requestMethod, String _ClassName, String _MethodName) {
        mActivity = _mActivity;
        apiResponse = _apiResponse;
        client = new OkHttpClient.Builder()
                .connectTimeout(15, TimeUnit.SECONDS)
                .readTimeout(15, TimeUnit.SECONDS)
                .writeTimeout(15, TimeUnit.SECONDS)
                .build();
        requestMethod = _requestMethod;
        ClassName = _ClassName;
        MethodName = _MethodName;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if (!hiddenDialog) {
            pDialog = new ProgressDialog(mActivity);
            pDialog.setMessage("Загрузка ...");
            pDialog.setCancelable(false);
            pDialog.show();
        }
    }

    @SafeVarargs
    @Override
    protected final JSONObject doInBackground(@Nullable Map<String, String>... params) {
        if (requestParams.size() > 0) {
            return Get(ClassName, MethodName, requestParams);
        }
        return Get(ClassName, MethodName);
    }

    @Override
    protected void onPostExecute(JSONObject resultObj) {
        super.onPostExecute(resultObj);

        if (!hiddenDialog) {
            if (pDialog.isShowing())
                pDialog.dismiss();
        }

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
            response = client.newCall(request).execute();
            jsonObj = new JSONObject(response.body().string());
            ApiUrlRequest = null;
        } catch (IOException e) {
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (getStatus() == Status.RUNNING) {
                        cancel(true);
                        if(!hiddenDialog) {
                            if (pDialog.isShowing())
                                pDialog.dismiss();
                        }
                    }

                    AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
                    builder.setTitle("Timeout error")
                            .setMessage("Sorry server doesn't response!\nCheck your internet connection and try again.")
                            .setCancelable(true)
                            .setPositiveButton("Try again", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int id) {
                                    Log.d(TAG, "load again");
                                }
                            }).setIcon(R.drawable.ic_dialog_alert_dark);
                    AlertDialog alert = builder.create();
                    alert.show();
                }
            });
        } catch (JSONException e) {
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (getStatus() == Status.RUNNING) {
                        cancel(true);
                        if (!hiddenDialog) {
                            if (pDialog.isShowing())
                                pDialog.dismiss();
                        }
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
                .url(ApiUrlRequest)
                .post(requestBody)
                .build();
        ApiUrlRequest = null;

        Response response;
        JSONObject jsonObj = null;
        try {
            response = client.newCall(request).execute();
            jsonObj = new JSONObject(response.body().string());
        } catch (JSONException | IOException e) {
            e.printStackTrace();
            showError("API.Post server error");
        }

        Log.d(TAG, String.valueOf(jsonObj));
        return jsonObj;
    }

    public void getApiUrlRequest(String ClassName, String MethodName, Map<String, String> params) {
        ApiUrlRequest = ApiUrlBase + "/" + ClassName + "." + MethodName;

        for (Map.Entry entry : params.entrySet()) {
            ApiUrlRequest = ApiUrlRequest + "&" + entry.getKey() + "=" + entry.getValue();
        }
        Log.d(TAG, ApiUrlRequest);

    }

    private String GetUserAgent() {
        PackageManager manager = mActivity.getPackageManager();
        PackageInfo info;
        String UserAgentName = "";
        try {
            info = manager.getPackageInfo(mActivity.getPackageName(), 0);
            UserAgentName = info.packageName + "/" + info.versionName + " (" + Build.MANUFACTURER + "/" + Build.MODEL + ")" + " Android/" + Build.VERSION.RELEASE + " " + Build.VERSION.CODENAME + " " + Build.VERSION.SDK_INT;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return UserAgentName;
    }


    // TODO use Handler
    public void showError(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
        builder.setMessage(message)
                .setTitle("Ошибка")
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if(!mActivity.getLocalClassName().equals("MainActivity"))
                            mActivity.finish();
                    }
                }).setIcon(R.drawable.ic_dialog_alert_dark);
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
                }).setIcon(R.drawable.ic_dialog_info_dark);
        AlertDialog alert = builder.create();
        alert.show();
    }

    public API setHiddenDialog() {
        hiddenDialog = true;
        return this;
    }

    public API requestParams(Map<String, String> params) {
        for (Map.Entry param : params.entrySet()) {
            requestParams.put(String.valueOf(param.getKey()), String.valueOf(param.getValue()));
        }
        return this;
    }

}
