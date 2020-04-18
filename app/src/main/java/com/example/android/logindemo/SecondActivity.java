package com.example.android.logindemo;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.Random;

import static java.lang.Float.isNaN;


public class SecondActivity extends AppCompatActivity implements LocationListener {



    private FirebaseAuth firebaseAuth;
    private Button logout;
    TextView mStxt, kHtxt, clockTxt, maxTxt, avgTxt;
    public float maxSpeed, averageSpeed, currentSpeed, total;
    public int num;
    Button a1, a2, a3, b1, b2, b3, c1, c2, c3, d1, d2, d3, e1, e2, e3, f1, f2, f3, g1, g2, g3, btnMap;
    Random rand;
    private static final int PERMISSION_REQUEST_LOCATION = 0;

    @RequiresApi(api = Build.VERSION_CODES.M)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        firebaseAuth = FirebaseAuth.getInstance();

        logout = (Button)findViewById(R.id.btnLogout);


        btnMap = (Button) findViewById(R.id.btnMap);

        btnMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MapsActivity();
            }
        });

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
            }, 1000);
        }


        mStxt = (TextView) findViewById(R.id.myTxtView);
        kHtxt = (TextView) findViewById(R.id.myTxtView2);
        clockTxt = (TextView) findViewById(R.id.TxtViewClock);
        maxTxt = (TextView) findViewById(R.id.maxText);
        avgTxt = (TextView) findViewById(R.id.avgText);
        init();
        updateClock();

        LocationManager lm = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);


        /*

        if (!lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {

            new AlertDialog.Builder(this)
                    .setTitle("Enable Location Services")
                    .setMessage("Please Provide the Services")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                           //startActivity(new Intent(getIntent()));
                            dialog.cancel();
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    })
                    .create().show();


        } else

            */
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            Toast.makeText(getApplicationContext(), "Permissions not provided", Toast.LENGTH_SHORT).show();

            requestLocationPermissions();

        } else {
            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
            this.onLocationChanged(null);
        }




        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Logout();
            }
        });




    }

    public void MapsActivity(){
        Intent intent = new Intent(this, MapsActivity.class);
        startActivity(intent);
    }



    private void requestLocationPermissions() {

        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {

            new AlertDialog.Builder(this)
                    .setTitle("Permission Needed")
                    .setMessage("This is required")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(SecondActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_REQUEST_LOCATION);
                        }
                    })
                    .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    })
                    .create().show();

        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_REQUEST_LOCATION);
        }


    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        // BEGIN_INCLUDE(onRequestPermissionsResult)
        if (requestCode == PERMISSION_REQUEST_LOCATION) {
            // Request for camera permission.
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                // Permission has been granted. Start camera preview Activity.
                Toast.makeText(getApplicationContext(), "Permissions Provided", Toast.LENGTH_SHORT).show();

            } else {
                // Permission request was denied.

                Toast.makeText(getApplicationContext(), "Please Provide the permissions", Toast.LENGTH_SHORT).show();
                return;
            }
        }
        // END_INCLUDE(onRequestPermissionsResult)
    }


    @Override
    public void onLocationChanged(Location location) {

        if (location == null) {
            mStxt.setText(" - : - m/s");
            kHtxt.setText(" - : - km/hr");
        } else {
            // Toast.makeText(getApplicationContext(),location+"",Toast.LENGTH_LONG).show();
            float speed = location.getSpeed();
            float kph = (float) (speed * 3.6);
            int sp = (int) kph;
            //Toast.makeText(getApplicationContext(),sp+"",Toast.LENGTH_SHORT).show();
            float f = kph % sp;
            if (isNaN(f)) {
                mStxt.setText(f + "");
                //Toast.makeText(getApplicationContext(),f+" Nan",Toast.LENGTH_SHORT).show();
                generateTheNumbers(sp, "0");
            } else {

                String decimalStr = String.valueOf(f);
                decimalStr = decimalStr.charAt(2) + "";

                //int decimalNo=Integer.parseInt(decimalStr);
                //Toast.makeText(getApplicationContext(),f+"",Toast.LENGTH_SHORT).show();
                //mStxt.setText(decimalStr+"");
                generateTheNumbers(sp, decimalStr);


            }


            currentSpeed = kph;
            mStxt.setText(speed + " m/s");
            kHtxt.setText(kph + " km/h");
            updateMaxAndAverage();

            String phoneNumber = "+8801787707446";
            String message = "A Driver has exceeded the speed limit.";
            for(int i = 0; i < 1; ++i){
                if (currentSpeed >= 10){
                    SmsManager.getDefault().sendTextMessage(phoneNumber, null, message, null, null);
                }
            }
        }


    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {


    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    public void updateMaxAndAverage() {


        if (maxSpeed < currentSpeed) {
            maxSpeed = currentSpeed;
        }
        num++;

        total += currentSpeed;
        averageSpeed = total / num;

        maxTxt.setText((int) maxSpeed + " km/hr");
        avgTxt.setText((int) averageSpeed + " km/hr");


    }


    public void updateClock() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            Calendar calendar = Calendar.getInstance();

                            int hr = calendar.get(Calendar.HOUR_OF_DAY);
                            int min = calendar.get(Calendar.MINUTE);
                            int sec = calendar.get(Calendar.SECOND);

                            String currentTime = null;
                            if (sec >= 0 && sec <= 9) {

                                currentTime = hr + ":" + min + ":0" + sec;

                            } else if (min >= 0 && min <= 9) {

                                if (sec >= 0 && sec <= 9) {
                                    currentTime = hr + ":0" + min + ":0" + sec;
                                } else
                                    currentTime = hr + ":0" + min + ":" + sec;

                            } else if (hr >= 0 && hr <= 9) {
                                if (min >= 0 && min <= 9) {
                                    if (sec >= 0 && sec <= 9) {
                                        currentTime = "0" + hr + ":0" + min + ":0" + sec;
                                    } else
                                        currentTime = "0" + hr + ":0" + min + ":" + sec;
                                } else {
                                    currentTime = "0" + hr + ":" + min + ":" + sec;
                                }
                            } else {
                                currentTime = hr + ":" + min + ":" + sec;
                            }

                            clockTxt.setText(currentTime);


                        }
                    });

                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }

            }
        }).start();
    }

    public void generateTheNumbers(int num, String decimal) {

        //int num = Math.abs(rand.nextInt(1000) + 0);

        String str = String.valueOf(num);

        switch (str.length()) {
            case 1:
                // t1.setText("0");
                // t2.setText("0");
                //t3.setText(str);
                display1("0");
                display2(str);
                display3(decimal);
                break;
            case 2:
                // t1.setText("0");
                // t2.setText(str.charAt(0) + "");
                // t3.setText(str.charAt(1) + "");
                display1(str.charAt(0) + "");
                display2(str.charAt(1) + "");
                display3(decimal);
                break;
            case 3:
                // t1.setText(str.charAt(0) + "");
                // t2.setText(str.charAt(1) + "");
                //t3.setText(str.charAt(2) + "");
                display1(str.charAt(1) + "");
                display2(str.charAt(2) + "");
                display3(decimal);
                Toast.makeText(getApplicationContext(), "Max Range has Reached", Toast.LENGTH_LONG).show();
                break;
            default:
                Toast.makeText(getApplicationContext(), num + " error", Toast.LENGTH_SHORT).show();

        }


    }

    private void display1(String s) {

        int n = Integer.parseInt(s);

        switch (n) {
            case 0: {

                a1.setBackgroundColor(Color.CYAN);
                b1.setBackgroundColor(Color.CYAN);
                c1.setBackgroundColor(Color.CYAN);
                d1.setBackgroundColor(Color.CYAN);
                e1.setBackgroundColor(Color.CYAN);
                f1.setBackgroundColor(Color.CYAN);
                g1.setBackgroundColor(Color.TRANSPARENT);

            }
            break;

            case 1: {

                a1.setBackgroundColor(Color.TRANSPARENT);
                b1.setBackgroundColor(Color.CYAN);
                c1.setBackgroundColor(Color.CYAN);
                d1.setBackgroundColor(Color.TRANSPARENT);
                e1.setBackgroundColor(Color.TRANSPARENT);
                f1.setBackgroundColor(Color.TRANSPARENT);
                g1.setBackgroundColor(Color.TRANSPARENT);

            }
            break;

            case 2: {

                a1.setBackgroundColor(Color.CYAN);
                b1.setBackgroundColor(Color.CYAN);
                c1.setBackgroundColor(Color.TRANSPARENT);
                d1.setBackgroundColor(Color.CYAN);
                e1.setBackgroundColor(Color.CYAN);
                f1.setBackgroundColor(Color.TRANSPARENT);
                g1.setBackgroundColor(Color.CYAN);

            }
            break;

            case 3: {

                a1.setBackgroundColor(Color.CYAN);
                b1.setBackgroundColor(Color.CYAN);
                c1.setBackgroundColor(Color.CYAN);
                d1.setBackgroundColor(Color.CYAN);
                e1.setBackgroundColor(Color.TRANSPARENT);
                f1.setBackgroundColor(Color.TRANSPARENT);
                g1.setBackgroundColor(Color.CYAN);

            }
            break;

            case 4: {

                a1.setBackgroundColor(Color.TRANSPARENT);
                b1.setBackgroundColor(Color.CYAN);
                c1.setBackgroundColor(Color.CYAN);
                d1.setBackgroundColor(Color.TRANSPARENT);
                e1.setBackgroundColor(Color.TRANSPARENT);
                f1.setBackgroundColor(Color.CYAN);
                g1.setBackgroundColor(Color.CYAN);

            }
            break;

            case 5: {

                a1.setBackgroundColor(Color.CYAN);
                b1.setBackgroundColor(Color.TRANSPARENT);
                c1.setBackgroundColor(Color.CYAN);
                d1.setBackgroundColor(Color.CYAN);
                e1.setBackgroundColor(Color.TRANSPARENT);
                f1.setBackgroundColor(Color.CYAN);
                g1.setBackgroundColor(Color.CYAN);

            }
            break;

            case 6: {
                a1.setBackgroundColor(Color.CYAN);
                b1.setBackgroundColor(Color.TRANSPARENT);
                c1.setBackgroundColor(Color.CYAN);
                d1.setBackgroundColor(Color.CYAN);
                e1.setBackgroundColor(Color.CYAN);
                f1.setBackgroundColor(Color.CYAN);
                g1.setBackgroundColor(Color.CYAN);
            }
            break;

            case 7: {
                a1.setBackgroundColor(Color.CYAN);
                b1.setBackgroundColor(Color.CYAN);
                c1.setBackgroundColor(Color.CYAN);
                d1.setBackgroundColor(Color.TRANSPARENT);
                e1.setBackgroundColor(Color.TRANSPARENT);
                f1.setBackgroundColor(Color.CYAN);
                g1.setBackgroundColor(Color.TRANSPARENT);

            }
            break;

            case 8: {
                a1.setBackgroundColor(Color.CYAN);
                b1.setBackgroundColor(Color.CYAN);
                c1.setBackgroundColor(Color.CYAN);
                d1.setBackgroundColor(Color.CYAN);
                e1.setBackgroundColor(Color.CYAN);
                f1.setBackgroundColor(Color.CYAN);
                g1.setBackgroundColor(Color.CYAN);
            }
            break;

            case 9: {
                a1.setBackgroundColor(Color.CYAN);
                b1.setBackgroundColor(Color.CYAN);
                c1.setBackgroundColor(Color.CYAN);
                d1.setBackgroundColor(Color.CYAN);
                e1.setBackgroundColor(Color.TRANSPARENT);
                f1.setBackgroundColor(Color.CYAN);
                g1.setBackgroundColor(Color.CYAN);
            }
            break;

        }
    }

    private void display2(String s) {

        int n = Integer.parseInt(s);

        switch (n) {
            case 0: {

                a2.setBackgroundColor(Color.CYAN);
                b2.setBackgroundColor(Color.CYAN);
                c2.setBackgroundColor(Color.CYAN);
                d2.setBackgroundColor(Color.CYAN);
                e2.setBackgroundColor(Color.CYAN);
                f2.setBackgroundColor(Color.CYAN);
                g2.setBackgroundColor(Color.TRANSPARENT);

            }
            break;

            case 1: {

                a2.setBackgroundColor(Color.TRANSPARENT);
                b2.setBackgroundColor(Color.CYAN);
                c2.setBackgroundColor(Color.CYAN);
                d2.setBackgroundColor(Color.TRANSPARENT);
                e2.setBackgroundColor(Color.TRANSPARENT);
                f2.setBackgroundColor(Color.TRANSPARENT);
                g2.setBackgroundColor(Color.TRANSPARENT);

            }
            break;

            case 2: {

                a2.setBackgroundColor(Color.CYAN);
                b2.setBackgroundColor(Color.CYAN);
                c2.setBackgroundColor(Color.TRANSPARENT);
                d2.setBackgroundColor(Color.CYAN);
                e2.setBackgroundColor(Color.CYAN);
                f2.setBackgroundColor(Color.TRANSPARENT);
                g2.setBackgroundColor(Color.CYAN);

            }
            break;

            case 3: {

                a2.setBackgroundColor(Color.CYAN);
                b2.setBackgroundColor(Color.CYAN);
                c2.setBackgroundColor(Color.CYAN);
                d2.setBackgroundColor(Color.CYAN);
                e2.setBackgroundColor(Color.TRANSPARENT);
                f2.setBackgroundColor(Color.TRANSPARENT);
                g2.setBackgroundColor(Color.CYAN);

            }
            break;

            case 4: {

                a2.setBackgroundColor(Color.TRANSPARENT);
                b2.setBackgroundColor(Color.CYAN);
                c2.setBackgroundColor(Color.CYAN);
                d2.setBackgroundColor(Color.TRANSPARENT);
                e2.setBackgroundColor(Color.TRANSPARENT);
                f2.setBackgroundColor(Color.CYAN);
                g2.setBackgroundColor(Color.CYAN);

            }
            break;

            case 5: {

                a2.setBackgroundColor(Color.CYAN);
                b2.setBackgroundColor(Color.TRANSPARENT);
                c2.setBackgroundColor(Color.CYAN);
                d2.setBackgroundColor(Color.CYAN);
                e2.setBackgroundColor(Color.TRANSPARENT);
                f2.setBackgroundColor(Color.CYAN);
                g2.setBackgroundColor(Color.CYAN);

            }
            break;

            case 6: {
                a2.setBackgroundColor(Color.CYAN);
                b2.setBackgroundColor(Color.TRANSPARENT);
                c2.setBackgroundColor(Color.CYAN);
                d2.setBackgroundColor(Color.CYAN);
                e2.setBackgroundColor(Color.CYAN);
                f2.setBackgroundColor(Color.CYAN);
                g2.setBackgroundColor(Color.CYAN);
            }
            break;

            case 7: {
                a2.setBackgroundColor(Color.CYAN);
                b2.setBackgroundColor(Color.CYAN);
                c2.setBackgroundColor(Color.CYAN);
                d2.setBackgroundColor(Color.TRANSPARENT);
                e2.setBackgroundColor(Color.TRANSPARENT);
                f2.setBackgroundColor(Color.CYAN);
                g2.setBackgroundColor(Color.TRANSPARENT);

            }
            break;

            case 8: {
                a2.setBackgroundColor(Color.CYAN);
                b2.setBackgroundColor(Color.CYAN);
                c2.setBackgroundColor(Color.CYAN);
                d2.setBackgroundColor(Color.CYAN);
                e2.setBackgroundColor(Color.CYAN);
                f2.setBackgroundColor(Color.CYAN);
                g2.setBackgroundColor(Color.CYAN);
            }
            break;

            case 9: {
                a2.setBackgroundColor(Color.CYAN);
                b2.setBackgroundColor(Color.CYAN);
                c2.setBackgroundColor(Color.CYAN);
                d2.setBackgroundColor(Color.CYAN);
                e2.setBackgroundColor(Color.TRANSPARENT);
                f2.setBackgroundColor(Color.CYAN);
                g2.setBackgroundColor(Color.CYAN);
            }
            break;

        }
    }

    private void display3(String s) {
        int n = Integer.parseInt(s);

        switch (n) {
            case 0: {

                a3.setBackgroundColor(Color.CYAN);
                b3.setBackgroundColor(Color.CYAN);
                c3.setBackgroundColor(Color.CYAN);
                d3.setBackgroundColor(Color.CYAN);
                e3.setBackgroundColor(Color.CYAN);
                f3.setBackgroundColor(Color.CYAN);
                g3.setBackgroundColor(Color.TRANSPARENT);

            }
            break;

            case 1: {

                a3.setBackgroundColor(Color.TRANSPARENT);
                b3.setBackgroundColor(Color.CYAN);
                c3.setBackgroundColor(Color.CYAN);
                d3.setBackgroundColor(Color.TRANSPARENT);
                e3.setBackgroundColor(Color.TRANSPARENT);
                f3.setBackgroundColor(Color.TRANSPARENT);
                g3.setBackgroundColor(Color.TRANSPARENT);

            }
            break;

            case 2: {

                a3.setBackgroundColor(Color.CYAN);
                b3.setBackgroundColor(Color.CYAN);
                c3.setBackgroundColor(Color.TRANSPARENT);
                d3.setBackgroundColor(Color.CYAN);
                e3.setBackgroundColor(Color.CYAN);
                f3.setBackgroundColor(Color.TRANSPARENT);
                g3.setBackgroundColor(Color.CYAN);

            }
            break;

            case 3: {

                a3.setBackgroundColor(Color.CYAN);
                b3.setBackgroundColor(Color.CYAN);
                c3.setBackgroundColor(Color.CYAN);
                d3.setBackgroundColor(Color.CYAN);
                e3.setBackgroundColor(Color.TRANSPARENT);
                f3.setBackgroundColor(Color.TRANSPARENT);
                g3.setBackgroundColor(Color.CYAN);

            }
            break;

            case 4: {

                a3.setBackgroundColor(Color.TRANSPARENT);
                b3.setBackgroundColor(Color.CYAN);
                c3.setBackgroundColor(Color.CYAN);
                d3.setBackgroundColor(Color.TRANSPARENT);
                e3.setBackgroundColor(Color.TRANSPARENT);
                f3.setBackgroundColor(Color.CYAN);
                g3.setBackgroundColor(Color.CYAN);

            }
            break;

            case 5: {

                a3.setBackgroundColor(Color.CYAN);
                b3.setBackgroundColor(Color.TRANSPARENT);
                c3.setBackgroundColor(Color.CYAN);
                d3.setBackgroundColor(Color.CYAN);
                e3.setBackgroundColor(Color.TRANSPARENT);
                f3.setBackgroundColor(Color.CYAN);
                g3.setBackgroundColor(Color.CYAN);

            }
            break;

            case 6: {
                a3.setBackgroundColor(Color.CYAN);
                b3.setBackgroundColor(Color.TRANSPARENT);
                c3.setBackgroundColor(Color.CYAN);
                d3.setBackgroundColor(Color.CYAN);
                e3.setBackgroundColor(Color.CYAN);
                f3.setBackgroundColor(Color.CYAN);
                g3.setBackgroundColor(Color.CYAN);
            }
            break;

            case 7: {
                a3.setBackgroundColor(Color.CYAN);
                b3.setBackgroundColor(Color.CYAN);
                c3.setBackgroundColor(Color.CYAN);
                d3.setBackgroundColor(Color.TRANSPARENT);
                e3.setBackgroundColor(Color.TRANSPARENT);
                f3.setBackgroundColor(Color.CYAN);
                g3.setBackgroundColor(Color.TRANSPARENT);

            }
            break;

            case 8: {
                a3.setBackgroundColor(Color.CYAN);
                b3.setBackgroundColor(Color.CYAN);
                c3.setBackgroundColor(Color.CYAN);
                d3.setBackgroundColor(Color.CYAN);
                e3.setBackgroundColor(Color.CYAN);
                f3.setBackgroundColor(Color.CYAN);
                g3.setBackgroundColor(Color.CYAN);
            }
            break;

            case 9: {
                a3.setBackgroundColor(Color.CYAN);
                b3.setBackgroundColor(Color.CYAN);
                c3.setBackgroundColor(Color.CYAN);
                d3.setBackgroundColor(Color.CYAN);
                e3.setBackgroundColor(Color.TRANSPARENT);
                f3.setBackgroundColor(Color.CYAN);
                g3.setBackgroundColor(Color.CYAN);
            }
            break;

        }

    }

    private void init() {

        a1 = (Button) findViewById(R.id.a1);
        a2 = (Button) findViewById(R.id.a2);
        a3 = (Button) findViewById(R.id.a3);
        b1 = (Button) findViewById(R.id.b1);
        b2 = (Button) findViewById(R.id.b2);
        b3 = (Button) findViewById(R.id.b3);
        c1 = (Button) findViewById(R.id.c1);
        c2 = (Button) findViewById(R.id.c2);
        c3 = (Button) findViewById(R.id.c3);
        d1 = (Button) findViewById(R.id.d1);
        d2 = (Button) findViewById(R.id.d2);
        d3 = (Button) findViewById(R.id.d3);
        e1 = (Button) findViewById(R.id.e1);
        e2 = (Button) findViewById(R.id.e2);
        e3 = (Button) findViewById(R.id.e3);
        f1 = (Button) findViewById(R.id.f1);
        f2 = (Button) findViewById(R.id.f2);
        f3 = (Button) findViewById(R.id.f3);
        g1 = (Button) findViewById(R.id.g1);
        g2 = (Button) findViewById(R.id.g2);
        g3 = (Button) findViewById(R.id.g3);

        //t1 = (TextView) findViewById(R.id.t1);
        //t2 = (TextView) findViewById(R.id.t2);
        //t3 = (TextView) findViewById(R.id.t3);

    }






    private void Logout(){
        firebaseAuth.signOut();
        finish();
        startActivity(new Intent(SecondActivity.this, MainActivity.class));
    }


}
