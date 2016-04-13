package ru.bobsoccer;

import android.text.Html;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

class Blog {

    private final String imagePreviewContentPath = "/imagepreview/content";

    Integer Blo_Code;
    String  Blo_Header;
    String  Blo_Login;
    String  Blo_Avatar;
    String  Blo_Date;
    String  Blo_Anounce;
    String  Blo_Text;
    String  Blo_Image;

    Blog(JSONObject Blog) {
        try {

            Blo_Code    = Integer.parseInt(Blog.getString("Blo_Code"));
            Blo_Header  = String.valueOf(Html.fromHtml(Blog.getString("Blo_Header")));
            Blo_Anounce = String.valueOf(Html.fromHtml(Blog.getString("Blo_Anounce")));
            Blo_Avatar  = Blog.getString("avatar");
            Blo_Login   = Blog.getString("login");
            Blo_Date    = Blog.getString("Blo_DateToday");
            Blo_Text    = String.valueOf(Html.fromHtml(Blog.getString("Blo_Text")));
            Blo_Image   = Blog.getString("Blo_Image");
            if(!Objects.equals(Blo_Image, ""))
                Blo_Image = imagePreviewContentPath + Blo_Image;

        } catch (JSONException ignored) {
        }
    }

}