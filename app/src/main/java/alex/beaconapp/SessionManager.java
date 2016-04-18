package alex.beaconapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

/**
 * Created by Alex on 18.04.2016.
 */
public class SessionManager {
    // LogCat tag
    private static String TAG = SessionManager.class.getSimpleName();

    // Shared Preferences
    SharedPreferences pref;

    SharedPreferences.Editor editor;
    Context _context;

    // Shared pref mode
    int PRIVATE_MODE = 0;



    private static final String KEY_IS_LOGGEDIN = "isLoggedIn";

    public SessionManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(AppConfig.PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void setLogin(boolean isLoggedIn) {

        editor.putBoolean(KEY_IS_LOGGEDIN, isLoggedIn);

        // commit changes
        editor.commit();

        Log.d(TAG, "User login session modified!");
    }

    public void setUserParameters(int id,String name,String lname, String email,String token,String type) {

        editor.putInt(AppConfig.USER_ID,id);
        editor.putString(AppConfig.USER_NAME, name);
        editor.putString(AppConfig.USER_LAST_NAME, lname);
        editor.putString(AppConfig.USER_EMAIL, email);
        editor.putString(AppConfig.USER_TOKEN, token);
        editor.putString(AppConfig.USER_TYPE, type);
        // commit changes
        editor.commit();

        Log.d(TAG, "User login session modified!");
    }

    public boolean isLoggedIn(){
        return pref.getBoolean(KEY_IS_LOGGEDIN, false);
    }
}
