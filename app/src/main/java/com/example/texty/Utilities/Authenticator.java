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

    /**
     * Sets username into SharedPreferences
     *
     * @author Ahmed Ibrahim
     * @version 1.0
     */
    public static void setUsername(Context context, String username)
    {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putString(PREF_USERNAME, username);
        editor.commit();
    }

    /**
     * gets username from SharedPreferences
     *
     * @author Ahmed Ibrahim
     * @version 1.0
     */
    public static String getUsername(Context context)
    {
        return getSharedPreferences(context).getString(PREF_USERNAME, "");
    }

    /**
     * Sets token into SharedPreferences
     *
     * @author Ahmed Ibrahim
     * @version 1.0
     */
    public static void setToken(Context context, String token)
    {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putString(PREF_TOKEN, token);
        editor.commit();
    }

    /**
     * Gets token into SharedPreferences
     *
     * @author Ahmed Ibrahim
     * @version 1.0
     */
    public static String getToken(Context context)
    {
        return getSharedPreferences(context).getString(PREF_TOKEN, "");
    }

    /**
     * Checks whether user is loggged in or not
     *
     * @author Ahmed Ibrahim
     * @version 1.0
     */
    public static boolean isLoggedIn(Context context){
       return getUsername(context).length() != 0;
    }

    /**
     * Logs user out from application
     *
     * @author Ahmed Ibrahim
     * @version 1.0
     */
    public static void logOut(Context context){
        setToken(context,"");
        setUsername(context,"");
    }

}
