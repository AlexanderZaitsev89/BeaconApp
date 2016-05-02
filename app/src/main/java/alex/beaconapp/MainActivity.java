package alex.beaconapp;



import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;

import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.estimote.sdk.Beacon;
import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.Region;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity implements View.OnClickListener,
                                                                BeaconManager.MonitoringListener{
    private static final String TAG ="mainptest";
    TextView txt,txtName,txtDescription;
    String[] classroms,subjects;
    String token;
    int chosenCourse,chosenBeaconCode;
    Button attendButton;
    ListView listViewClassrooms, listViewCourses,userEnrolledCourses;
    SharedPreferences sPref;
    LinearLayout header;

    private BeaconManager beaconManager;
    Region region;

    private SessionManager session;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        prepareLayout();
        checkLoginState();
        settingListenersOnListViews();

    }

////////////////////////////////INITIAL PREPARE METHODS///////////////////////////////////////////
    public void prepareLayout(){

        header=(LinearLayout)findViewById(R.id.header);
        //to make design more responsive: calculte header height for each screen
        header.getLayoutParams().height= calculateHeaderHeight();

        Typeface customFont= Typeface.createFromAsset(getAssets(), "fonts/goodfisb.ttf");

        txt=(TextView)findViewById(R.id.txtMain);
        txtName=(TextView)findViewById(R.id.txtStudentName);
        txtDescription=(TextView)findViewById(R.id.txtSubject);
        txt.setTypeface(customFont);
        txtName.setTypeface(customFont);
        txtDescription.setTypeface(customFont);

        attendButton=(Button)findViewById(R.id.buttonAttend);
        listViewClassrooms = (ListView) findViewById(R.id.list);
        listViewCourses = (ListView) findViewById(R.id.list2);
        userEnrolledCourses = (ListView) findViewById(R.id.list);
        session = new SessionManager(getApplicationContext());

        beaconManager = new BeaconManager(getApplicationContext());
        // Default values are 5s of scanning and 25s of waiting time to save CPU cycles.
        // In order for this demo to be more responsive and immediate we lower down those values.
        beaconManager.setBackgroundScanPeriod(TimeUnit.SECONDS.toMillis(1), 0);
        region = new Region("rid", null, 1808, null);
    }
    public int calculateHeaderHeight(){
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        Log.d("testmetrics", "metricks " + " h= " + metrics.heightPixels + " w= " + metrics.widthPixels);
        float height=(metrics.heightPixels/100)*30;
        Log.d("testmetrics", "new height " +height);
        return Math.round(height);
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
            txtName.setText("Dear Sir/Madam, "+name+" "+lname+". We are pleased to welcome you " +
                    "in our humble alma mater of wisdom and possibilities.");
            Log.d(TAG, "info"+ name+lname+ "token"+token);
        }
    }
    public void settingListenersOnListViews(){
        beaconManager.setMonitoringListener(this);
        attendButton.setOnClickListener(this);

        listViewClassrooms.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                attendButton.setVisibility(View.VISIBLE); //To set visible
            }
        });

        listViewCourses.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                txtDescription.setText("You have selected: " + subjects[position] + "course. Click attend button to start.");
                chosenCourse = position;
            }
        });
    }

///////////////////////////////CLICK  METHODS/////////////////////////////////////////////////////
    @Override
    public void onClick(View v) {
        Button b = (Button)v;
        String buttonText = b.getText().toString();
        switch (buttonText) {

            case "ENTER":
                subjects=new String[1];
                subjects[0]="No classes available";
                //when enter is pressed a list of subjects that connected with this class is showed

                txtDescription.setText("Choose subject your want to attend");
                ((Button) v).setText("ATTEND");
                ServerCommunication getClassByBeacon=new ServerCommunication(this);
                getClassByBeacon.getCurentClassByBeacon(token, new ServerCommunication.VolleyCallbackReturnurrentClass() {
                    @Override
                    public void onSuccess(String currntClass) {
                        Log.d("servercomm", currntClass);
                        if (currntClass.length() > 0) {
                            subjects[0] = currntClass;
                        }
                        showListView(subjects, 2);
                    }
                });

                break;
            case "ATTEND":
                //when attend is pressed an attend request is send to server
                ((Button) v).setText("LEAVE");
                ServerCommunication attendRequest=new ServerCommunication(this);
                attendRequest.attendClass(4, token);
                txtDescription.setText("You are attending course: " + subjects[chosenCourse]);
                listViewCourses.setVisibility(View.GONE);
                break;
            case "LEAVE":
                //when leave is pressed a leave request is send to server
                ServerCommunication leaveRequest=new ServerCommunication(this);
                leaveRequest.leaveClass(4, token);
                ((Button) v).setText("ENTER");
                txtDescription.setText("Choose the classroom that your are currently in: ");
                showListView(classroms, 1);
                break;
            case "BACK":
                //when leave is pressed a leave request is send to server
                ((Button) v).setText("ENTER");
                txtDescription.setText("Choose the classroom that your are currently in: ");
                userEnrolledCourses.setVisibility(View.GONE);
                if(classroms!=null) {
                    showListView(classroms, 1);
                }

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
            case R.id.menu_my_enrolled_courses:
                ServerCommunication myCourses=new ServerCommunication(this);
               myCourses.showMyEnrolledCourses(token, new ServerCommunication.VolleyCallback() {
                   @Override
                   public void onSuccess(String[] result) {
                       if(result!=null) {
                           Log.d("servercomm", String.valueOf(result.length));
                           showListView(result, 3);
                           attendButton.setVisibility(View.VISIBLE);
                           attendButton.setText("BACK");
                       }
                   }
               });
                return true;
            case R.id.menu_user_attendance:
                ServerCommunication attendance=new ServerCommunication(this);
               attendance.showMyAttendences(token, new ServerCommunication.VolleyCallbackAttendences() {
                   @Override
                   public void onSuccess(String[] resultNameofClasses, int[] resultDuration) {
                       if(resultNameofClasses!=null) {
                           Log.d("servercomm", String.valueOf(resultNameofClasses.length));
                           showListView(resultNameofClasses, 3);
                           attendButton.setVisibility(View.VISIBLE);
                           attendButton.setText("BACK");
                       }
                   }
               });

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
        classroms= new String[list.size()];
        for(int i=0;i<list.size();i++){
            classroms[i] = "Classrom #: " + list.get(i).getMinor();
        }

        Log.d("enter region", "enter region");
        //  Log.d("testUUID", "Nearby eddystones: " + list.get(0).getProximityUUID());
        //  Log.d("testbeacon", "mac: " + list.get(0).getMacAddress());
        //  Log.d("testbeacon", "uuid: " + list.get(0).getProximityUUID());
        //  Log.d("testbeacon", "minor: " + list.get(0).getMinor());
        // Log.d("testbeacon", "major " + list.get(0).getMajor());
        showListView(classroms, 1);
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
///////////////////////////////////OTHER STUFF //////////////////////////////////////////////////////
    public void showListView(String [] dataToShow,int listViewNumber){
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(),
                android.R.layout.simple_list_item_1, android.R.id.text1, dataToShow);
        listViewCourses.setVisibility(View.GONE);
        listViewClassrooms.setVisibility(View.GONE);
        if(listViewNumber==1) {

            listViewClassrooms.setAdapter(adapter);
            listViewClassrooms.setVisibility(View.VISIBLE);
        }
        if(listViewNumber==2) {

            listViewCourses.setAdapter(adapter);
            listViewCourses.setVisibility(View.VISIBLE);
        }
        if(listViewNumber==3) {

           userEnrolledCourses.setAdapter(adapter);
            userEnrolledCourses.setVisibility(View.VISIBLE);
        }
    }
}
