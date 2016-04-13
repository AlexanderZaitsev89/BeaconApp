package alex.beaconapp;



import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
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

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    TextView txt;
    TextView txtName;
    private BeaconManager beaconManager;
    Region region;
    Button attendButton;
    ListView listView ;
    SharedPreferences sPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        txt=(TextView)findViewById(R.id.txtMain);
        txtName=(TextView)findViewById(R.id.txtStudentName);

        attendButton=(Button)findViewById(R.id.buttonAttend);
        listView = (ListView) findViewById(R.id.list);
        sPref = getSharedPreferences("MyPref", MODE_PRIVATE);

        if(sPref.contains(AppConfig.SHARED_PREFERENCES_PASSWORD)!=true){  //in the final version here should be id for user as a check
            // User is not registered. Take him to main register activity
            Intent intent = new Intent(MainActivity.this,RegisterActivity.class );
            startActivity(intent);
        }
        String nameText = sPref.getString(AppConfig.SHARED_PREFERENCES_NAME, "No value");
        txtName.setText("Student's name: "+nameText);


        beaconManager = new BeaconManager(getApplicationContext());
        region = new Region("rid", null, 88, null);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                attendButton.setVisibility(View.VISIBLE); //To set visible
            }
        });

        // Default values are 5s of scanning and 25s of waiting time to save CPU cycles.
        // In order for this demo to be more responsive and immediate we lower down those values.
        beaconManager.setBackgroundScanPeriod(TimeUnit.SECONDS.toMillis(1), 0);
        beaconManager.setMonitoringListener(new BeaconManager.MonitoringListener() {
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
        });
    }

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

    @Override
    public void onClick(View v) {

    }
}
