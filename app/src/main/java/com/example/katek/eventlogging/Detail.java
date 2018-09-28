package com.example.katek.eventlogging;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class Detail extends AppCompatActivity {

    public JSONObject jo = null;
    public JSONArray ja = null;
    public int index;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Intent i = getIntent();
        String title = i.getStringExtra("title");
        String description = i.getStringExtra("des");
        String date = i.getStringExtra("date");
        String time = i.getStringExtra("time");
        String gps = i.getStringExtra("gps");
        index = i.getIntExtra("index",0);
        Button delete = (Button)findViewById(R.id.delete);

        TextView t = (TextView)findViewById(R.id.textView1);
        TextView time_1 = (TextView)findViewById(R.id.textView2);
        TextView date_1 = (TextView)findViewById(R.id.textView3);
        TextView gps_1 = (TextView)findViewById(R.id.textView4);
        TextView d = (TextView)findViewById(R.id.textView5);

        t.setText(title);
        time_1.setText(time);
        date_1.setText(date);
        gps_1.setText(gps);
        d.setText(description);

        delete.setOnClickListener(new Button.OnClickListener(){
            public void onClick(View view){
                try{
                    File f = new File(getFilesDir(), "file.ser");
                    FileInputStream fi = new FileInputStream(f);
                    ObjectInputStream o = new ObjectInputStream(fi);
                    // Notice here that we are de-serializing a String object (instead of
                    // a JSONObject object) and passing the String to the JSONObject’s
                    // constructor. That’s because String is serializable and
                    // JSONObject is not. To convert a JSONObject back to a String, simply
                    // call the JSONObject’s toString method.
                    String j = null;
                    try{
                        j = (String) o.readObject();
                    }
                    catch(ClassNotFoundException c){
                        c.printStackTrace();
                    }
                    try {
                        jo = new JSONObject(j);
                        ja = jo.getJSONArray("data");
                    }
                    catch(JSONException e){
                        e.printStackTrace();
                    }
                }
                catch(Exception e){
                    Log.d("Exception", e.toString());
                    return;
                }

                ja.remove(index);
                try{
                    File f = new File(getFilesDir(), "file.ser");
                    FileOutputStream fo = new FileOutputStream(f);
                    ObjectOutputStream o = new ObjectOutputStream(fo);
                    String j = jo.toString();
                    o.writeObject(j);
                    o.close();
                    fo.close();
                }
                catch(IOException e){

                }
                //pop the activity off the stack
                Intent i = new Intent(Detail.this, MainActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
            }});
    }
}

