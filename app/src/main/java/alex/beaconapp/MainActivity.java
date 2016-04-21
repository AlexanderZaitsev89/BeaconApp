package alex.beaconapp;



import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.estimote.sdk.Beacon;
import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.Region;
import com.estimote.sdk.eddystone.Eddystone;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity implements View.OnClickListener,
                                                                BeaconManager.MonitoringListener,
                                                                    AdapterView.OnItemClickListener{
    private static final String TAG = MainActivity.class.getSimpleName();
    TextView txt;
    TextView txtName;
    private BeaconManager beaconManager;
    Region region;
    Button attendButton;
    ListView listView ;
    SharedPreferences sPref;
    private SessionManager session;
    String token;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        prepareLayout();
        checkLoginState();

        beaconManager.setMonitoringListener(this);
        attendButton.setOnClickListener(this);
        listView.setOnItemClickListener(this);
    }

////////////////////////////////INITIAL PREPARE METHODS///////////////////////////////////////////
    public void prepareLayout(){
        txt=(TextView)findViewById(R.id.txtMain);
        Typeface face= Typeface.createFromAsset(getAssets(), "fonts/goodfisb.ttf");
        txt.setTypeface(face);
        txtName=(TextView)findViewById(R.id.txtStudentName);
        attendButton=(Button)findViewById(R.id.buttonAttend);
        listView = (ListView) findViewById(R.id.list);
        session = new SessionManager(getApplicationContext());

        beaconManager = new BeaconManager(getApplicationContext());
        // Default values are 5s of scanning and 25s of waiting time to save CPU cycles.
        // In order for this demo to be more responsive and immediate we lower down those values.
        beaconManager.setBackgroundScanPeriod(TimeUnit.SECONDS.toMillis(1), 0);
        region = new Region("rid", null, 88, null);
    }
    public void checkLoginState(){
        if (!session.isLoggedIn()) {
            // User is already logged in. Take him to main activity
            Log.d(TAG, "not logged in");
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }else {
            sPref=getSharedPreferences(AppConfig.PREF_NAME, MODE_PRIVATE);
            String name = sPref.getString(AppConfig.USER_NAME, "no name");
            String lname = sPref.getString(AppConfig.USER_LAST_NAME, "no lname");
            token=sPref.getString(AppConfig.USER_TOKEN,"no token");
            txtName.setText("Student: "+name+" "+lname);
            Log.d(TAG, "info"+ name+lname+ "token"+token);
        }
    }
///////////////////////////////CLICK  METHODS/////////////////////////////////////////////////////
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        attendButton.setVisibility(View.VISIBLE); //To set visible
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.buttonAttend:
                Log.d("testattend", "button was pushed");
                ServerCommunication attendRequest=new ServerCommunication(this);
                //attendRequest.attendClass(2,token);
                attendRequest.attendClass(2,token);
                break;


            default:
                break;
        }
    }
///////////////////////////////MENU ///////////////////////////////////////////////////////////////
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.actionbar_menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Take appropriate action for each action item click
        switch (item.getItemId()) {
            case R.id.menu_logout:
                session.setLogin(false);
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
//////////////////////////////LIFECYLCE METHODS///////////////////////////////////////////////////
    @Override
    protected void onStart() {
        super.onStart();
        // Should be invoked in #onStart.
        beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
            @Override
            public void onServiceReady() {
                // Beacons ranging.
                beaconManager.startMonitoring(region);
                Log.d("testbeacon", "Start Monitoring");
            }
        });
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        // When no longer needed. Should be invoked in #onDestroy.
        beaconManager.disconnect();
    }
//////////////////////////////BEACON //////////////////////////////////////////////////////////////
    @Override
    public void onEnteredRegion(Region region, List<Beacon> list) {
        String[] subjects = new String[list.size()];
        subjects[0] = "minor: " + list.get(0).getMinor();
        Log.d("testbeacon", "enter region");
        //  Log.d("testbeacon", "Nearby eddystones: " + list);
        //  Log.d("testbeacon", "mac: " + list.get(0).getMacAddress());
        //  Log.d("testbeacon", "uuid: " + list.get(0).getProximityUUID());
        //  Log.d("testbeacon", "minor: " + list.get(0).getMinor());
        // Log.d("testbeacon", "major " + list.get(0).getMajor());
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(),
                android.R.layout.simple_list_item_1, android.R.id.text1, subjects);
        listView.setAdapter(adapter);
    }
    @Override
    public void onExitedRegion(Region region) {
        Log.d("testbeacon", "exit region");
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
        alertDialogBuilder.setMessage("Are you leaving the classroom?");

        alertDialogBuilder.setPositiveButton("yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                Toast.makeText(MainActivity.this,"You clicked yes button", Toast.LENGTH_LONG).show();
                ServerCommunication leave=new ServerCommunication(getApplicationContext());
                //leave.leaveClass(2);
                finish();
            }
        });

        alertDialogBuilder.setNegativeButton("No",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }


}
