package ru.bobsoccer;

import android.app.Activity;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

class Session {

    private SharedPreferences Preferences;

    public Session(Activity context) {
        Preferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public void Set(String Key, String Value) {
        Preferences.edit().putString(Key, Value).apply();
    }

    public String Get(String Key) {
        return Preferences.getString(Key, "");
    }

}