package ru.bobsoccer;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.common.api.CommonStatusCodes;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("unchecked")
public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";

    public Session activitySession;

    private TextView _emailText;
    private TextView _passwordText;
    private TextView _errorText;
    private Button _signupButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activitySession = new Session(this);

        setContentView(R.layout.activity_login);

        _emailText = (TextView) findViewById(R.id.input_email);
        _passwordText = (TextView) findViewById(R.id.input_password);
        _errorText = (TextView) findViewById(R.id.error_text);
        _signupButton = (Button) findViewById(R.id.button_signup);
    }

    public void onSignupClick(View view) {
        if (!validate()) {
            _signupButton.setEnabled(true);
            return;
        }
        _signupButton.setEnabled(false);

        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

        Map<String, String> params = new HashMap<>();
        params.put("login", email);
        params.put("pass", password);
        new API(this, new API.ApiResponse() {
            @Override
            public void onTaskCompleted(JSONObject resultObj) {
                try {
                    String isError = resultObj.getString("Error");
                    if (!isError.equals("1")) {
                        String apiToken = resultObj.getString("Token");
                        activitySession.SetToken(apiToken);

                        User rUser = new User(resultObj.getJSONObject("User"));
                        activitySession.SetObject("currentUser", rUser);

                        setResult(CommonStatusCodes.SUCCESS);
                        finish();
                    } else {
                        _signupButton.setEnabled(true);
                        String ErrorValue = "";
                        JSONArray Errors = resultObj.getJSONArray("Errors");
                        for (int i = 0; i < Errors.length(); i++) {
                            ErrorValue = ErrorValue + Errors.getJSONObject(i).getString("Error") + "\n";
                        }
                        _errorText.setText(ErrorValue);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, "GET", "Users", "CheckValidEnter")
                .requestParams(params)
                .setLoadingDialogDisabled()
                .setErrorDialogDisabled()
                .execute();
    }

    public boolean validate() {
        boolean valid = true;

        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _emailText.setError(getResources().getString(R.string.enter_valid_email));
            valid = false;
        } else {
            _emailText.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            _passwordText.setError(getResources().getString(R.string.enter_valid_password));
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        return valid;
    }

}
