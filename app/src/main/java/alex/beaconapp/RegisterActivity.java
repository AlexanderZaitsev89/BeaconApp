package alex.beaconapp;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

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

public class RegisterActivity extends AppCompatActivity {
    private Button btnRegister;
    private EditText inputName;
    private EditText inputLastName;
    private EditText inputEmail;
    private EditText inputPassword;
    private String type="student";
    Spinner spinner;
    SharedPreferences sPref;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        inputName = (EditText) findViewById(R.id.name);
        inputLastName = (EditText) findViewById(R.id.lastName);
        inputEmail = (EditText) findViewById(R.id.email);
        inputPassword = (EditText) findViewById(R.id.password);
        btnRegister = (Button) findViewById(R.id.btnRegister);
        spinner = (Spinner) findViewById(R.id.type_spinner);

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.types_spinner_array, R.layout.spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);

        // Register Button Click event
        btnRegister.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                String name = inputName.getText().toString().trim();
                String lastname = inputLastName.getText().toString().trim();
                String email = inputEmail.getText().toString().trim();
                String password = inputPassword.getText().toString().trim();
                String typeofuser=type;

                if (!name.isEmpty() && !email.isEmpty() && !password.isEmpty()&&!lastname.isEmpty()) {
                    registerUser(name,lastname, email, password,typeofuser);
                } else {
                    Toast.makeText(getApplicationContext(),
                            "Please enter your details!", Toast.LENGTH_LONG)
                            .show();
                }
            }
        });
    }



    private void registerUser(final String name,final String lastname, final String email,
                              final String password, final String typeofuser) {
        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_REGISTER, new Response.Listener<String>() {

            @Override

            public void onResponse(String response) {
                Log.d("testresponse1", "Register Response: " + response.toString());
                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean success =  jObj.getBoolean("success");

                    if (success) {
                        Log.d("testresponse", "WAS HERE");
                        // User successfully stored in database
                        JSONObject user = jObj.getJSONObject("data");
                        int id = user.getInt(AppConfig.USER_ID);
                        String name = user.getString(AppConfig.USER_NAME);
                        String lname = user.getString(AppConfig.USER_LAST_NAME);
                        String email = user.getString(AppConfig.USER_EMAIL);
                        String token = user.getString(AppConfig.USER_TOKEN);
                        // Inserting row in users table
                        Log.d("testresponse2", "Register info: "+ String.valueOf(id)+name+lname+email+token);
                        Toast.makeText(getApplicationContext(), "User successfully registered. Try login now!", Toast.LENGTH_LONG).show();

                    } else {
                        // Error occurred in registration.
                        Log.d("testresponse", "Register failed");
                        Toast.makeText(getApplicationContext(),
                                "Failed to register! Try again", Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.d("testresponse", "PROBLEM OCCURED");
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("testresponse", "Registration Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();

            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting params to register url
                Map<String, String> params = new HashMap<String, String>();
                params.put(AppConfig.USER_NAME, name);
                params.put(AppConfig.USER_LAST_NAME, lastname);
                params.put(AppConfig.USER_EMAIL, email);
                params.put(AppConfig.USER_PASSWORD, password);
                params.put(AppConfig.USER_TYPE, typeofuser);
                return params;
            }

        };

        // Adding request to request queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(strReq);


        sPref = getSharedPreferences("MyPref", MODE_PRIVATE);
        SharedPreferences.Editor ed = sPref.edit();
        ed.putString(AppConfig.SHARED_PREFERENCES_NAME, name);
        ed.putString(AppConfig.SHARED_PREFERENCES_EMAIL, email);
        //ed.putString(AppConfig.SHARED_PREFERENCES_PASSWORD, password);
        ed.commit();
        finish();



    }


}
