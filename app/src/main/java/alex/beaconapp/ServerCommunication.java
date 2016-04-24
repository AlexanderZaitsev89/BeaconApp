package alex.beaconapp;

import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Alex on 21.04.2016.
 */
public class ServerCommunication {
    // LogCat tag
    private static String TAG = ServerCommunication.class.getSimpleName();
    private Context context;
    boolean success;

    public ServerCommunication(Context context){
        this.context=context;
    }

    protected boolean attendClass(final int classId, final String token) {

        Log.d(TAG, "the  attend request was sent");
        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_ATTEND_CLASS, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jObj = new JSONObject(response);
                     success =  jObj.getBoolean("success");
                    if (success) {
                        Log.d(TAG, "attend request was successful");
                    } else {
                        // Error occurred in registration.
                        Log.d(TAG, "attend request failed");

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.d(TAG, "PROBLEM OCCURED");
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Registration Error: " + error.getMessage());

            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting params to register url
                Map<String, String> params = new HashMap<String, String>();
                params.put(AppConfig.CLASS_ID, String.valueOf(classId));
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<String, String>();
                Log.d(TAG, "TOKEN:"+token);
                headers.put("Authorization",token);
                return headers;
            }
        };

        // Adding request to request queue
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(strReq);
        return success;
    }

    protected boolean leaveClass(final int classId,final String token) {

        Log.d(TAG, "the  leave request was sent");
        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_LEAVE_CLASS, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                try {
                    JSONObject jObj = new JSONObject(response);
                    success =  jObj.getBoolean("success");
                    if (success) {
                        Log.d(TAG, "leave request was successful");
                    } else {
                        // Error occurred in registration.
                        Log.d(TAG, "leave request failed");

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.d(TAG, "PROBLEM OCCURED");
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Registration Error: " + error.getMessage());

            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting params to register url
                Map<String, String> params = new HashMap<String, String>();
                params.put(AppConfig.CLASS_ID, String.valueOf(classId));
                return params;
            }
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<String, String>();
                Log.d(TAG, "TOKEN:"+token);
                headers.put("Authorization",token);
                return headers;
            }

        };

        // Adding request to request queue
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(strReq);
        return success;
    }

    protected boolean getCurentClassByBeacon(final String token) {

        Log.d(TAG, "the  getCurentClass request was sent");
        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_GET_CURRENT_CLASS_BY_BEACON, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                try {
                    JSONObject jObj = new JSONObject(response);
                    success =  jObj.getBoolean("success");
                    if (success) {
                        Log.d(TAG, "getCurentClass request was successful");
                    } else {
                        // Error occurred in registration.
                        Log.d(TAG, "getCurentClass request failed");

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.d(TAG, "PROBLEM OCCURED");
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Registration Error: " + error.getMessage());

            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting params to register url
                Map<String, String> params = new HashMap<String, String>();
                params.put("beacons", String.valueOf(311));
                params.put("beacons", String.valueOf(0));
                return params;
            }
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<String, String>();
                Log.d(TAG, "TOKEN:"+token);
                headers.put("Authorization",token);
                return headers;
            }

        };

        // Adding request to request queue
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(strReq);
        return success;
    }
}
