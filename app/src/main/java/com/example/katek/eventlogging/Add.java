package com.example.katek.eventlogging;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.location.Location;
import android.location.LocationManager;
import android.content.Context;
import java.util.List;

public class Add extends AppCompatActivity {

    private final String TAG = "EventLogging";
    public JSONObject jo = null;
    public JSONArray ja = null;

    // Used for debugging. Below method is extraneous
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        final EditText title = findViewById(R.id.title_entry);
        final EditText des = findViewById(R.id.des_entry);
        Button enter = findViewById(R.id.enter);

        try {
            File f = new File(getFilesDir(), "file.ser");
            FileInputStream fi = new FileInputStream(f);
            ObjectInputStream o = new ObjectInputStream(fi);
            // Notice here that we are de-serializing a String object (instead of
            // a JSONObject object) and passing the String to the JSONObject’s
            // constructor. That’s because String is serializable and
            // JSONObject is not. To convert a JSONObject back to a String, simply
            // call the JSONObject’s toString method.
            String j = null;
            try {
                j = (String) o.readObject();
            } catch (ClassNotFoundException c) {
                c.printStackTrace();
            }
            try {
                jo = new JSONObject(j);
                ja = jo.getJSONArray("data");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            // Here, initialize a new JSONObject
            jo = new JSONObject();
            ja = new JSONArray();
            try {
                jo.put("data", ja);
            } catch (JSONException j) {
                j.printStackTrace();
            }
        }
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                99);

        enter.setOnClickListener(new Button.OnClickListener() {
            @SuppressLint("MissingPermission")
            public void onClick(View v) {
                String firstText = title.getText().toString();
                String secondText = des.getText().toString();

                LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                List<String> providers = lm.getProviders(true);
                Location l;

                for (int i = providers.size() - 1; i >= 0; i--) {
                    l = lm.getLastKnownLocation(providers.get(i));

                    if (l != null) {
                        Double longtitude = l.getLongitude();
                        Double latitude = l.getLatitude();
                        String longtitude_re = String.format("%.2f",longtitude);
                        String latitude_re = String.format("%.2f",latitude);

                        Date date_i = Calendar.getInstance().getTime();
                        Date time_i = Calendar.getInstance().getTime();

                        DateFormat formatter = new SimpleDateFormat("MM-dd-yyyy");
                        DateFormat formatter_time = new SimpleDateFormat("hh:mm:ss a");
                        String time = formatter_time.format(time_i);
                        String date = formatter.format(date_i);

                        JSONObject temp = new JSONObject();
                        try {
                            temp.put("title", firstText);
                            temp.put("des", secondText);
                            temp.put("date", date);
                            temp.put("time", time);
                            temp.put("gps",latitude_re+", "+longtitude_re);
                        } catch (JSONException j) {
                            j.printStackTrace();
                        }

                        ja.put(temp);

                        // write the file
                        try {
                            File f = new File(getFilesDir(), "file.ser");
                            FileOutputStream fo = new FileOutputStream(f);
                            ObjectOutputStream o = new ObjectOutputStream(fo);
                            String j = jo.toString();
                            o.writeObject(j);
                            o.close();
                            fo.close();
                        } catch (IOException e) {
                        }
                        //pop the activity off the stack
                        Intent intent = new Intent(Add.this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        break;
                    }
                }

                }

        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        Log.d(TAG, "callback");
        switch (requestCode) {
            case 99:
                // If the permissions aren't set, then return. Otherwise, proceed.
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                        PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                                        Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.INTERNET}
                                , 10);
                    }
                    Log.d(TAG, "returning program");
                    return;
                }
                else{
                    // Create Intent to reference MyService, start the Service.
                    Log.d(TAG, "starting service");
                    Intent i = new Intent(this, MyService.class);
                    if(i==null)
                        Log.d(TAG, "intent null");
                    else{
                        startService(i);
                    }

                }
                break;
            default:
                break;
        }
    }
}
