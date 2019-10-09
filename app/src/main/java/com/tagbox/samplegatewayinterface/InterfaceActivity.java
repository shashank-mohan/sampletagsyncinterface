package com.tagbox.samplegatewayinterface;

import android.content.Intent;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import static com.tagbox.samplegatewayinterface.Constants.APP_END_TIME;
import static com.tagbox.samplegatewayinterface.Constants.APP_START_TIME;
import static com.tagbox.samplegatewayinterface.Constants.BREACH_HUM;
import static com.tagbox.samplegatewayinterface.Constants.BREACH_JERK;
import static com.tagbox.samplegatewayinterface.Constants.BREACH_LIMIT;
import static com.tagbox.samplegatewayinterface.Constants.BREACH_LOWER_LIMIT;
import static com.tagbox.samplegatewayinterface.Constants.BREACH_PARAMETERS;
import static com.tagbox.samplegatewayinterface.Constants.BREACH_TEMP;
import static com.tagbox.samplegatewayinterface.Constants.BREACH_UPPER_LIMIT;
import static com.tagbox.samplegatewayinterface.Constants.HUMIDITY_BREACH;
import static com.tagbox.samplegatewayinterface.Constants.INTENT_FETCH_BREACH_DATA;
import static com.tagbox.samplegatewayinterface.Constants.INTENT_START_GATEWAY_APP;
import static com.tagbox.samplegatewayinterface.Constants.LAST_SYNC_TIME;
import static com.tagbox.samplegatewayinterface.Constants.SENSOR_ID;
import static com.tagbox.samplegatewayinterface.Constants.SHOCK_BREACH;
import static com.tagbox.samplegatewayinterface.Constants.TEMP_BREACH;

public class InterfaceActivity extends AppCompatActivity {

    public static final String DATEFORMAT = "yyyy-MM-dd HH:mm:ss";
    private Button startButton;
    private Button fetchButton;
    private TextView textView;

    public static String getUtcDatetimeAsString() {
        final SimpleDateFormat sdf = new SimpleDateFormat(DATEFORMAT);
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date date = new Date();
        if (date.getTime() / 1000 < 1506322751) {
            return "";
        }
        final String utcTime = sdf.format(date);
        return utcTime;
    }

    public static String getOneWeekBackDatetimeAsString() {
        final SimpleDateFormat sdf = new SimpleDateFormat(DATEFORMAT);
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -7);
        Date dateBefore7Days = cal.getTime();
        final String utcTime = sdf.format(dateBefore7Days);
        return utcTime;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interface);


        startButton = (Button) findViewById(R.id.start_button);
        fetchButton = (Button) findViewById(R.id.fetch_button);
        textView = (TextView) findViewById(R.id.text_view);

        // IF activity has been opened from broadcast receiver to display data, then display data
        Intent intent = getIntent();

        if (intent.hasExtra(LAST_SYNC_TIME)) {
            textView.append("last sync time" + intent.getStringExtra(LAST_SYNC_TIME));
        }

        //emulates starting of gateway app through Bristlecone app
        startButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                new StartGatewayApp().execute();
            }
        });

        fetchButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                new FetchBreachData().execute();

            }
        });
    }

    //emulates fetching of breach parmaters from gateway app
    public class StartGatewayApp extends AsyncTask<Void, Void, Void> {

        public StartGatewayApp() {
        }

        @Override
        protected Void doInBackground(Void... params) {

            //pass a JSON object with upper and lower limit for temperature, shock and humidity
            String breach_params = "passJSONObject as string";

            //pass sensor id of the associated sensor
            // sensor id
            String sensorID = "D7:B9:26:05:61:B6";
            Log.d("startg", "aaaa");
            Intent startIntent = new Intent();
            startIntent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
            startIntent.setAction(INTENT_START_GATEWAY_APP);
            startIntent.putExtra(SENSOR_ID, sensorID);

            getApplicationContext().sendBroadcast(startIntent);
            return null;

        }

    }

    public class FetchBreachData extends AsyncTask<Void, Void, Void> {

        public FetchBreachData() {
        }

        @Override
        protected Void doInBackground(Void... params) {

            // startTime and endTime in some time format or pass unix timestamp in UTC(pass long in that case)
            long start_time = System.currentTimeMillis() / 1000 - 10*86400;
            long end_time = System.currentTimeMillis() / 1000;

            // Corresponding to this broadcast, static(for now) breach data will be fetched in TagboxBroadcastReceiver
            //check TagboxBroadcastReceiver
            JSONObject breachObject = new JSONObject();
            try {
                JSONObject tempBreachParam = new JSONObject();
                tempBreachParam.put(BREACH_UPPER_LIMIT, 25.9);
                tempBreachParam.put(BREACH_LOWER_LIMIT, 25.7);
                JSONObject humBreachParam = new JSONObject();
                humBreachParam.put(BREACH_UPPER_LIMIT, 45);
                humBreachParam.put(BREACH_LOWER_LIMIT, 40);
                JSONObject jerkBreachParam = new JSONObject();
                jerkBreachParam.put(BREACH_LIMIT, 0);
                breachObject.put(BREACH_TEMP, tempBreachParam);
                breachObject.put(BREACH_HUM, humBreachParam);
                breachObject.put(BREACH_JERK, jerkBreachParam);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Log.d("breach", breachObject + "");

            Intent fetchIntent = new Intent(INTENT_FETCH_BREACH_DATA);
            fetchIntent.putExtra(APP_START_TIME, start_time);
            fetchIntent.putExtra(APP_END_TIME, end_time);
            fetchIntent.putExtra(BREACH_PARAMETERS, breachObject + "");

            //sensor id should look like the below format always
            fetchIntent.putExtra(SENSOR_ID, "D7:B9:26:05:61:B6");
            fetchIntent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
            sendBroadcast(fetchIntent);
            return null;
        }
    }
}
