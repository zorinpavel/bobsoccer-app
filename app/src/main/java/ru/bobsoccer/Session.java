package ru.bobsoccer;

import android.app.Activity;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.gson.Gson;


class Session {

    private SharedPreferences Preferences;
    private Gson gson = new Gson();

    public static String Token;

    public Session(Activity context) {
        Preferences = PreferenceManager.getDefaultSharedPreferences(context);
        Token = Get("Token");
    }

    public void Remove(String Key) {
        Preferences.edit().remove(Key).apply();
    }

    public void Set(String Key, String Value) {
        Preferences.edit().putString(Key, Value).apply();
    }

    public String Get(String Key) {
        return Preferences.getString(Key, null);
    }

    public void SetObject(String Key, Object object) {
        String jsonValue = gson.toJson(object);
        Preferences.edit().putString(Key, jsonValue).apply();
    }

    public <T> T GetObject(String Key, Class<T> a) {
        String jsonString = Preferences.getString(Key, null);
        if (gson == null) {
            return null;
        } else {
            try {
                return gson.fromJson(jsonString, a);
            } catch (Exception e) {
                throw new IllegalArgumentException("Object stored with key " + Key + " is instance of other class");
            }
        }
    }

    public void SetToken(String apiToken) {
        Set("Token", apiToken);
        Token = apiToken;
    }
}