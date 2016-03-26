package ru.bobsoccer.model;

import org.json.JSONException;
import org.json.JSONObject;

import ru.bobsoccer.R;

public class User {

    public Integer us_id;
    public String login;
    public String mail;
    public String avatar; // small
    public String userpic; // big
    public Integer Level;
    public Integer Lev_Points;
    public Integer Prev_Points;
    public Integer Dif_Points;
    public Integer Color_Points = R.color.colorPrimary;

    public User(JSONObject User) {
        try {

            us_id = Integer.parseInt(User.getString("us_id"));
            login = User.getString("login");
            mail = User.getString("mail");
            avatar = User.getString("avatar");
            userpic = User.getString("userpic");
            Level = Integer.parseInt(User.getString("Level"));
            Lev_Points = Integer.parseInt(User.getString("Lev_Points"));
            Prev_Points = Integer.parseInt(User.getString("Prev_Points"));
            Dif_Points = Prev_Points - Lev_Points;
            Color_Points = (Dif_Points >= 0) ? R.color.darkGreen : R.color.darkRed;

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
