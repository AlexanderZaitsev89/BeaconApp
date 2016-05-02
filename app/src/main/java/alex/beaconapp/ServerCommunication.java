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

import org.json.JSONArray;
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

    protected void getCurentClassByBeacon(final String token,final VolleyCallbackReturnurrentClass callback) {

        Log.d(TAG, "the  getCurentClass request was sent");
        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_GET_CURRENT_CLASS_BY_BEACON, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG,"fuck response: "+response);
                try {
                    JSONObject jObj = new JSONObject(response);
                    success =  jObj.getBoolean("success");

                    if (success) {
                        JSONArray data = jObj.getJSONArray("data");
                        JSONObject dataClass=data.getJSONObject(0);

                        String choesnClass=dataClass.getString("courseName");
                        callback.onSuccess(choesnClass);
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

                params.put("beacons", "311");
                //params.put("beacons", "0");
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

    }

    protected void showMyEnrolledCourses(final String token, final VolleyCallback callback) {

        Log.d(TAG, "the  getCurentClass request was sent");
        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_SHOW_MY_ENROLLED_COURSES, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                //Log.d(TAG,"response: "+response);
                try {
                    JSONObject jObj = new JSONObject(response);
                    success =  jObj.getBoolean("success");

                    if (success) {
                        //JSONObject userCourses = jObj.getJSONObject("data");
                        JSONArray userCourses = jObj.getJSONArray("data");
                        String [] arrayOfUsercourses=new String[userCourses.length()];
                        for(int i=0;i<userCourses.length();i++){
                            JSONObject course = userCourses.getJSONObject(i);
                            arrayOfUsercourses[i]=course.getString("name");
                            Log.d(TAG, "course "+i + arrayOfUsercourses[i]);
                        }
                        callback.onSuccess(arrayOfUsercourses);
                        Log.d(TAG, "getMyClass  request was successful");
                    } else {
                        // Error occurred in registration.
                        Log.d(TAG, "getMyClass  request failed");

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.d(TAG, "PROBLEM OCCURED");
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "getMyClass  Error: " + error.getMessage());

            }
        }) {


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
        //Log.d(TAG, "AGain" + arrayOfUsercourses.toString());

    }

    protected void showMyAttendences(final String token, final VolleyCallbackAttendences callback) {

        Log.d(TAG, "the  getCurentClass request was sent");
        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_SHOW_MY_ATTENDECES, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                //Log.d(TAG,"response: "+response);
                try {
                    JSONObject jObj = new JSONObject(response);
                    success =  jObj.getBoolean("success");

                    if (success) {
                        //JSONObject userCourses = jObj.getJSONObject("data");
                        JSONArray userCourses = jObj.getJSONArray("data");
                        String [] arrayOfUsercourses=new String[userCourses.length()];
                        int [] arrayOfdurations=new int[userCourses.length()];
                        for(int i=0;i<userCourses.length();i++){
                            JSONObject course = userCourses.getJSONObject(i);
                            arrayOfUsercourses[i]=course.getString("courseName");

                            if(arrayOfUsercourses[i].length()>12){
                                arrayOfUsercourses[i]=arrayOfUsercourses[i].substring(0,12)+"...";
                            }
                            arrayOfdurations[i]=course.getInt("duration");
                            float convertToMin=(float)arrayOfdurations[i]/60000;

                            arrayOfUsercourses[i]=arrayOfUsercourses[i]+"     duration:"+String.format("%.2f", convertToMin)+" mins";
                            Log.d(TAG, "course "+i + arrayOfUsercourses[i]+arrayOfdurations[i]);
                        }
                        callback.onSuccess(arrayOfUsercourses,arrayOfdurations);
                        Log.d(TAG, "getMyClass  request was successful");
                    } else {
                        // Error occurred in registration.
                        Log.d(TAG, "getMyClass  request failed");

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.d(TAG, "PROBLEM OCCURED");
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "getMyClass  Error: " + error.getMessage());

            }
        }) {


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
        //Log.d(TAG, "AGain" + arrayOfUsercourses.toString());

    }

    public interface VolleyCallback{
        void onSuccess(String [] result);
    }
    public interface VolleyCallbackAttendences{
        void onSuccess(String [] resultNameofClasses,int[] resultDuration);
    }
    public interface VolleyCallbackReturnurrentClass{
        void onSuccess(String currntClass);
    }
}
