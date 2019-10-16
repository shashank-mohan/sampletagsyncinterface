**TagSync Integration Documentation**

**Software requirements** :

Android Version: 5.0+

Apps:

1. Host app (communicates and interacts with background TagSync apk)
2. TagSync background app (BLE sensor data collection)

Sample Host Application source code : [https://github.com/shashank-mohan/sampletagsyncinterface](https://github.com/shashank-mohan/sampletagsyncinterface)

**Getting Started:**

1. A sample Github project explains how an application can interact with the downloaded TagSync
2. Before calling TagSync&#39;s methods, the host application needs to ensure that the package is downloaded and installed with all permissions granted. The sample application explains how a user can do the above checks
3. To install TagSync, the host application needs to download the apk from the given url and install the apk from the downloaded file
4. After installing, the TagSync will request for runtime location and storage permissions which must be provided for sensor data collection to work properly
5. The application will have the following two methods that needs to be instantiated from the host application (described in Developer Guide):
  1. startScan – for starting Bluetooth scanner and collecting temperature data
  2. fetchData- getting the recent sensor data from TagSync
6. For the Bluetooth scanner to work properly, the location needs to be turned on in high accuracy mode from phone settings. Hence, whenever &quot;Start Scanner&quot; method is being called, the host application needs to check location status and prompt the user to turn it on in high accuracy mode
7. The host application can subscribe to events from TagSync APK by registering to a broadcast receiver (described in Developer Guide)
8. There can be different scenarios in which the app fails to get recent sensor data. The error codes (which can be received through broadcast receiver) can be due to:

1. sensor not being in the vicinity of the phone
2. location turned off
3. Bluetooth scanner on the android device is not working properly

**Developer Guide:**

 **1.** **Installing TagSync application:**

The host application can download and setup TagSync in following steps:

- Download TagSync apk from the given url (as declared in GitHub sample application)

    
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


- Setup a file provider to access the file uri for the downloaded file in the host application&#39;s manifest
- Install the app in the following way (the resulting intent takes user response for installing the file. Hence, it needs to be a user event)

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


- After installation (and also recommended at every startup of the host application) it can be checked whether TagSync is installed properly in the following manner

        private boolean isPackageInstalled(){
            try{
               getPackageManager().getPackageInfo("com.tagbox.tag_sync",0);
               return true;
            }
            catch (Exception e){
                return false;
            }
        }

- Recommended: at every host application startup, the host application should compare the latest TagSync version (to be fetched using an API request as provided in sample application) against the version of the installed application The API returns a string body with the version. In case of mismatch, the host application can download the apk from the same download and install flow to update apk.

        private String getPackageVersion(){
            try{
                return getPackageManager().getPackageInfo("com.tagbox.tag_sync",0).versionName;
            }
            catch (Exception e){
                return "";
            }
        }

 **2.**  **Calling startScan method from the host app:**

This method will invoke the scanner on the TagSync and start collecting the data from relevant sensor in background. After some time interval (typically a few minutes) this activity is stopped, shutting off the Bluetooth Scanner to optimize battery life. Hence, this method should be called before each fetchData call with some delay (recommended delay is 2mins) to ensure that telemetry data received from fetchData is not stale.

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
    
    private void launchTagSyncApp(){
        Intent launchIntent = getPackageManager().getLaunchIntentForPackage("com.tagbox.tag_sync);
        if (launchIntent != null) {
            startActivity(launchIntent);//null pointer check in case package name was not found
        }
    }

Android&#39;s Bluetooth adapter requires the device&#39;s location to be ON with high accuracy to function properly. Hence, the host application should check the status of location every time startScan method is being called and prompt the user if it is found to be not enabled.

Recommended: In latest Android versions, a background application cannot be invoked reliably through broadcast events if the background application is inactive. To make sure that a method like startScan is received by TagSync application, it is recommended that before every startScan calls the launch activity

    private void launchTagSyncApp(){
        Intent launchIntent = getPackageManager().getLaunchIntentForPackage("com.tagbox.tag_sync);
        if (launchIntent != null) {
            startActivity(launchIntent);//null pointer check in case package name was not found
        }
    }

 **3.** **Calling fetchData method from the host app:**

Calling this method will lead to TagSync APK responding with a broadcast that contains data or an error message that is illustrated in the following section. The communication of the host application with TagSync APK is through Android broadcasts received on the registered broadcast receiver

The sensor data in response to this method is in JSON format and has a data field and a timestamp field in unix epoch format. The application picks up the latest telemetry packet of the relevant sensor device at each call. To ensure that the date is not stale, startScan method should be called at least 2 minutes before an attempt to fetch data.

public class FetchSensorData extends AsyncTask\&lt;Void, Void, Void\&gt; {

    public FetchSensorData() {
    }

    @Override
    protected Void doInBackground(Void... params) {
        Intent fetchIntent = new Intent(INTENT_FETCH_SENSOR_DATA);
        //sensor id should look like the below format always
        // here a dummy sensor is entered
        fetchIntent.putExtra(SENSOR_ID, "MX34FE");
        fetchIntent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
        sendBroadcast(fetchIntent);
        return null;
    }
}

Since, this method is always called after startScan method, it can be assumed that TagSync is already up and running and launch activity need not be called.

**4.** **Receiving data through broadcast receiver**

This code piece demonstrates how to receive the data via a broadcast receiver on the host application. This broadcast receiver needs to be explicitly registered in the activity or service in the host app.

public class TagboxBroadcastReceiver extends BroadcastReceiver{

    @Override
    public void onReceive(final Context context, final Intent intent) {

        if (intent.getAction().equals(INTENT_SENSOR_DATA)){
            String temperatureData = intent.getStringExtra(SENSOR_DATA);
        }
        if (intent.getAction().equals(INTENT_TAGSYNC_ERROR)){
            String errorData = intent.getStringExtra(ERROR_DATA);
        }
        if (intent.getAction().equals(INTENT_PERMISSION_CHECK)){
            String permissionCheck = intent.getStringExtra(PERMISSION_CHECK);
        }
        if (intent.getAction().equals(INTENT_RECEIVED_FETCH_DATA)){
            Toast.makeText(context,"Fetch data request received by TagSync" ,
                    Toast.LENGTH_LONG).show();
        }
        if (intent.getAction().equals(INTENT_RECEIVED_START_SCAN)){
            Toast.makeText(context,"Start scan request received by TagSync",
                    Toast.LENGTH_LONG).show();
        }
    }
    }

The data packet that comes in the INTENT_SENSOR_DATA will have the data field in the following format:

    { 
       "sensorId":"MX56BE", 
       "temperature":25.6, // temperature in celsius
       "utcTimestamp": "2019-09-14 05:29:19" // timestamp in utc
    }


The error data that comes will have the following format&quot;

    { 
       "errorCode":301,
       "errorMessage":"Location is not enabled"
    }


Following are the possible error codes:

- 301 : location not enabled
- 302 :  bluetooth adapter not working
- 303: sensor data not found
- 306: Sensor mapping not found. Need network to fetch sensor mapping

The permission check message will have the following format&quot;

       { 
       "permission_flag":{ 
              "location_permission":false,
               "storage_permission":true
          }
        }


 **5.** **Receiving data through broadcast receiver**

In case of missing permissions, the host application needs to provide the missing permissions to the TagSync app. The host application can call the launch activity in this case which will ask for runtime permissions and then the user can provide the missing permission.

    private void launchTagSyncApp(){
        Intent launchIntent = getPackageManager().getLaunchIntentForPackage("com.tagbox.tag_sync");
        if (launchIntent != null) {
            startActivity(launchIntent); //null pointer check in case package name was not //found
        }
    }
