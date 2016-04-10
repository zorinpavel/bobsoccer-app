package ru.bobsoccer;

import android.text.Html;

import org.json.JSONException;
import org.json.JSONObject;

class Blog {

    Integer Blo_Code;
    String  Blo_Header;
    String  Blo_Anounce;
    String  Blo_Avatar;
    String  Blo_Login;
    String  Blo_Date;

    Blog(JSONObject Blog) {
        try {

            Blo_Code    = Integer.parseInt(Blog.getString("Blo_Code"));
            Blo_Header  = String.valueOf(Html.fromHtml(Blog.getString("Blo_Header")));
            Blo_Anounce = String.valueOf(Html.fromHtml(Blog.getString("Blo_Anounce")));
            Blo_Avatar  = Blog.getString("avatar");
            Blo_Login   = Blog.getString("login");
            Blo_Date    = Blog.getString("Blo_DateToday");

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}