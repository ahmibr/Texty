package com.example.texty.Utilities;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class Authenticator {

    static private final String PREF_USERNAME = "username";
    static private final String PREF_TOKEN = "token";
    static private final String PREF_MYNAME = "myname";

    static private SharedPreferences getSharedPreferences(Context ctx) {
        return PreferenceManager.getDefaultSharedPreferences(ctx);
    }

    public static void setUsername(Context context, String username)
    {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putString(PREF_USERNAME, username);
        editor.commit();
    }

    public static String getUsername(Context context)
    {
        return getSharedPreferences(context).getString(PREF_USERNAME, "");
    }

    public static void setToken(Context context, String token)
    {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putString(PREF_TOKEN, token);
        editor.commit();
    }

    public static String getToken(Context context)
    {
        return getSharedPreferences(context).getString(PREF_TOKEN, "");
    }

    public static boolean isLoggedIn(Context context){
       return getUsername(context).length() != 0;
    }

    public static void logOut(Context context){
        setToken(context,"");
        setUsername(context,"");
    }

}
