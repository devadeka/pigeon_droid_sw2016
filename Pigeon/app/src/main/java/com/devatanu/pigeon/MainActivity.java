package com.devatanu.pigeon;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TimePicker;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.text.DateFormat;
import java.util.Calendar;



public class MainActivity extends AppCompatActivity {


    TimePickerDialog.OnTimeSetListener timePickerDialog =new TimePickerDialog.OnTimeSetListener() {
        public void onTimeSet(TimePicker view, int hourOfDay,
                              int minute) {
            dateAndTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
            dateAndTime.set(Calendar.MINUTE, minute);
            updateLabel();
        }
    };

    DateFormat fmtDateAndTime=DateFormat.getTimeInstance();
    Calendar dateAndTime=Calendar.getInstance();
    Button btn_Time;
    Button btn_Start;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });



        btn_Time =(Button)findViewById(R.id.btn_time_picker);
        btn_Start =(Button)findViewById(R.id.btn_start_journey);

        btn_Time.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                new TimePickerDialog(MainActivity.this,
                        timePickerDialog,
                        dateAndTime.get(Calendar.HOUR_OF_DAY),
                        dateAndTime.get(Calendar.MINUTE),
                        true).show();
            }
        });

        btn_Start.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try {
                    Long timestamp = dateAndTime.getTimeInMillis()/1000L;
                    Long currTimestamp = Calendar.getInstance().getTimeInMillis() / 1000L;
                    if (timestamp-currTimestamp > 300) {
                        Log.i(LOGGER_TAG, "TimeFormat: " + timestamp.toString());
//                    requestUrl("http://pigeonapp.net/?notify", "uid=1&lat=1&lng=12&dest=mall&by=1461977776");
                    }
                    else{
                        Toast.makeText(getApplicationContext(), "Please set time 5 minutes from ", Toast.LENGTH_SHORT);
                    }

                    btn_Start.setEnabled(false);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }



    private void updateLabel() {

        btn_Time.setText(fmtDateAndTime.format(dateAndTime.getTime()));
        btn_Start.setEnabled(true);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }


        return super.onOptionsItemSelected(item);
    }

    private static String LOGGER_TAG = "PigeonLog";
    private static int CONNECTION_TIMEOUT = 2000;
    private static int DATARETRIEVAL_TIMEOUT = 2000;

    public static String requestUrl(String url, String postParameters)
            throws Exception {
        if (Log.isLoggable(LOGGER_TAG, Log.INFO)) {
            Log.i(LOGGER_TAG, "Requesting service: " + url);
        }

//        disableConnectionReuseIfNecessary();

        HttpURLConnection urlConnection = null;
        try {
            // create connection
            URL urlToRequest = new URL(url);
            urlConnection = (HttpURLConnection) urlToRequest.openConnection();
            urlConnection.setConnectTimeout(CONNECTION_TIMEOUT);
            urlConnection.setReadTimeout(DATARETRIEVAL_TIMEOUT);

            // handle POST parameters
            if (postParameters != null) {

                if (Log.isLoggable(LOGGER_TAG, Log.INFO)) {
                    Log.i(LOGGER_TAG, "POST parameters: " + postParameters);
                }

                urlConnection.setDoOutput(true);
                urlConnection.setRequestMethod("POST");
                urlConnection.setFixedLengthStreamingMode(
                        postParameters.getBytes().length);
                urlConnection.setRequestProperty("Content-Type",
                        "application/x-www-form-urlencoded");

                //send the POST out
                PrintWriter out = new PrintWriter(urlConnection.getOutputStream());
                out.print(postParameters);
                out.close();
            }

            // handle issues
            int statusCode = urlConnection.getResponseCode();
            if (statusCode != HttpURLConnection.HTTP_OK) {
                // throw some exception
            }

            // read output (only for GET)
            if (postParameters != null) {
                return null;
            } else {
                InputStream in =
                        new BufferedInputStream(urlConnection.getInputStream());
//                return getResponseText(in);
            }


        } catch (MalformedURLException e) {
            Log.i(LOGGER_TAG, "MalformedURL: " + e.toString());
        } catch (SocketTimeoutException e) {
            Log.i(LOGGER_TAG, "SocketTimeout: " + e.toString());
        } catch (IOException e) {
            Log.i(LOGGER_TAG, "IOEception: " + e.toString());
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }

        return null;
    }
}
