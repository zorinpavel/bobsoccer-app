package ru.bobsoccer;

import org.json.JSONException;
import org.json.JSONObject;

class User {

    Integer us_id;
    String login;
    String mail;
    String avatar; // small
    String userpic; // big
    Integer Level;
    Integer Lev_Points;
    Integer Prev_Points;
    Integer Dif_Points;
    Integer Color_Points = R.color.app_primary;

    User(JSONObject User) {
        try {

            us_id = Integer.parseInt(User.getString("us_id"));

            if(us_id > 0) {

                login = User.getString("login");
                mail = User.getString("mail");
                avatar = User.getString("avatar");
                userpic = User.getString("userpic");
                Level = Integer.parseInt(User.getString("Level"));
                Lev_Points = Integer.parseInt(User.getString("Lev_Points"));
                Prev_Points = Integer.parseInt(User.getString("Prev_Points"));
                Dif_Points = Prev_Points - Lev_Points;
                Color_Points = (Dif_Points >= 0) ? R.color.theme_green_dark : R.color.theme_red_dark;

            }

        } catch (JSONException ignored) {
        }
    }

}
