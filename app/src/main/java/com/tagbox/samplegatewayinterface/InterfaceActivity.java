package com.tagbox.samplegatewayinterface;

import android.app.ActivityManager;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PermissionInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.ArrayMap;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.Permissions;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import static com.tagbox.samplegatewayinterface.Constants.API_HEADER_NAME;
import static com.tagbox.samplegatewayinterface.Constants.API_HEADER_VALUE;
import static com.tagbox.samplegatewayinterface.Constants.APK_DOWNLOAD_URL;
import static com.tagbox.samplegatewayinterface.Constants.APK_FILE_NAME;
import static com.tagbox.samplegatewayinterface.Constants.APK_VERSION_URL;
import static com.tagbox.samplegatewayinterface.Constants.INTENT_FETCH_SENSOR_DATA;
import static com.tagbox.samplegatewayinterface.Constants.INTENT_PERMISSION_CHECK;
import static com.tagbox.samplegatewayinterface.Constants.INTENT_RECEIVED_FETCH_DATA;
import static com.tagbox.samplegatewayinterface.Constants.INTENT_RECEIVED_START_SCAN;
import static com.tagbox.samplegatewayinterface.Constants.INTENT_SENSOR_DATA;
import static com.tagbox.samplegatewayinterface.Constants.INTENT_START_SCANNER;
import static com.tagbox.samplegatewayinterface.Constants.INTENT_TAGSYNC_ERROR;
import static com.tagbox.samplegatewayinterface.Constants.SENSOR_ID;

public class InterfaceActivity extends AppCompatActivity {

    private Button startButton;
    private Button fetchButton;
    private Button installButton;
    private Button updateButton;

    private TagboxBroadcastReceiver tagboxBroadcastReceiver;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interface);

        startButton = (Button) findViewById(R.id.start_button);
        fetchButton = (Button) findViewById(R.id.fetch_button);
        installButton = (Button) findViewById(R.id.install_button);
        updateButton = (Button) findViewById(R.id.update_button);

        //start TagSync scanner
        startButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                new StartScanner().execute();
            }
        });

        // fetching sensor data for a sensor
        fetchButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                new FetchSensorData().execute();
            }
        });


        installButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                if(isPackageInstalled()){
                    Toast.makeText(getApplicationContext(),
                            "Apk is already installed",Toast.LENGTH_LONG)
                            .show();
                }
                else{

                    // apk is not installed. download and install apk
                    new DownloadApk().execute();
                }
            }
        });

        updateButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                if(isPackageInstalled()){
                    new FetchApkVersion().execute();
                }
                else{

                    // apk is not installed. download and install apk
                    new DownloadApk().execute();
                }
            }
        });

        tagboxBroadcastReceiver = new TagboxBroadcastReceiver();
        registerReceiver(tagboxBroadcastReceiver, makeTBoxUpdateIntentFilter());

    }


    private static IntentFilter makeTBoxUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(INTENT_SENSOR_DATA);
        intentFilter.addAction(INTENT_TAGSYNC_ERROR);
        intentFilter.addAction(INTENT_PERMISSION_CHECK);
        intentFilter.addAction(INTENT_RECEIVED_FETCH_DATA);
        intentFilter.addAction(INTENT_RECEIVED_START_SCAN);
        return intentFilter;
    }

    //emulates fetching of breach parmaters from gateway app
    public class StartScanner extends AsyncTask<Void, Void, Void> {

        public StartScanner() {
            launchTagSyncApp();
        }

        @Override
        protected Void doInBackground(Void... params) {


            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            Intent startIntent = new Intent();
            startIntent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
            startIntent.setAction(INTENT_START_SCANNER);
            getApplicationContext().sendBroadcast(startIntent);
            super.onPostExecute(aVoid);
        }
    }

    public class FetchSensorData extends AsyncTask<Void, Void, Void> {

        public FetchSensorData() {
        }

        @Override
        protected Void doInBackground(Void... params) {


            Intent fetchIntent = new Intent(INTENT_FETCH_SENSOR_DATA);
            //sensor id should look like the below format always
            // here a dummy sensor is entered
            fetchIntent.putExtra(SENSOR_ID, "D7:B9:26:05:61:B6");
            fetchIntent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
            sendBroadcast(fetchIntent);
            return null;
        }
    }

    public class FetchApkVersion extends AsyncTask<Void, Void, String> {

        public FetchApkVersion() {
        }

        @Override
        protected String doInBackground(Void... params) {
            final String[] result = {""};
            RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
            String url =APK_VERSION_URL;
            StringRequest getRequest = new StringRequest(Request.Method.GET, url,
                    new Response.Listener<String>()
                    {
                        @Override
                        public void onResponse(String response) {
                            // response
                            Log.d("Response", response);
                            result[0] = response;
                        }
                    },
                    new Response.ErrorListener()
                    {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            // TODO Auto-generated method stub
                            Log.d("ERROR","error => "+error.toString());
                            Toast.makeText(getApplicationContext(),"Error while fetching" +
                                    " apk version "+error,Toast.LENGTH_LONG).show();
                            result[0] = "";
                        }
                    }
            ) {
                @Override
                public Map<String, String> getHeaders()  {
                    Map<String, String>  params = new HashMap<String, String>();
                    params.put(API_HEADER_NAME, API_HEADER_VALUE);
                    return params;
                }
            };
            queue.add(getRequest);
            Log.d("resultVersion",result[0]+" version");
            return result[0];
            // startTime and endTime in some time format or pass unix timestamp in UTC(pass long in that case)
        }

        @Override
        protected void onPostExecute(String version) {
            super.onPostExecute(version);

            if(!version.equals("")){
                String currentVersion = getPackageVersion();
                if(version.equals(currentVersion)){

                }
            }
        }
    }




    public class DownloadApk extends AsyncTask<Void, Void, Boolean> {

        public DownloadApk() {
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            boolean result = false;
            InputStream is = null;
            FileOutputStream fos = null;
            HttpURLConnection connection = null;
            try {
                String getRequestUrl = APK_DOWNLOAD_URL;
                URL url = new URL(getRequestUrl);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setRequestProperty(API_HEADER_NAME,API_HEADER_VALUE);
                connection.setChunkedStreamingMode(1000000);
                connection.setConnectTimeout(240000);
                connection.connect();

                // expect HTTP 200 OK for successful download
                // instead of the file
                if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    return false;
                }

                File sdcard = Environment.getExternalStorageDirectory();
                File inputFile = new File(sdcard, APK_FILE_NAME);
                fos = new FileOutputStream(inputFile);
                is = connection.getInputStream();
                byte[] buffer = new byte[1024];
                int len1 = 0;
                while ((len1 = is.read(buffer)) != -1) {

                    fos.write(buffer, 0, len1);
                }
                connection.disconnect();
                fos.flush();
                result = true;
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(),"Error while downloading" +
                        " apk :"+e,Toast.LENGTH_LONG).show();
                Log.e("error", e.toString());
                result = false;
            }

            return result;
            // startTime and endTime in some time format or pass unix timestamp in UTC(pass long in that case)
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            Log.d("downloadStatus",aBoolean+"");
            if(aBoolean){
                installApk();
            }
        }
    }



    public void installApk(){
        try {
            File directory = Environment.getExternalStorageDirectory();
            File file = new File(directory, APK_FILE_NAME);
            Uri fileUri = Uri.fromFile(file);
            if (Build.VERSION.SDK_INT >= 24) {
                fileUri = FileProvider.getUriForFile(this, "com.tagbox.tag_sync.fileprovider",
                        file);
            }
            Log.d("uri",fileUri.getPath());


            Intent intent = new Intent(Intent.ACTION_VIEW, fileUri);
            intent.putExtra(Intent.EXTRA_NOT_UNKNOWN_SOURCE, true);
            intent.setDataAndType(fileUri, "application/vnd.android"  +  ".package-archive");
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK |  Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(intent);
        }
        catch (Exception e){
            Log.d("AppInstall Error",e.getLocalizedMessage());
        }
    }


    private boolean isPackageInstalled(){
        try{
           getPackageManager().getPackageInfo("com.tagbox.tag_sync",0);
           return true;
        }
        catch (Exception e){
            return false;
        }
    }

    private String getPackageVersion(){
        try{
            return getPackageManager().getPackageInfo("com.tagbox.tag_sync",0).versionName;
        }
        catch (Exception e){
            return "";
        }
    }

    private void launchTagSyncApp(){
        Intent launchIntent = getPackageManager().getLaunchIntentForPackage("com.tagbox.tag_sync");
        if (launchIntent != null) {
            startActivity(launchIntent);//null pointer check in case package name was not found
        }

    }

}
