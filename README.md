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

1. **1.**** Installing TagSync application:**

The host application can download and setup TagSync in following steps:

- Download TagSync apk from the given url (as declared in GitHub sample application)

public class DownloadApk extends AsyncTask\&lt;Void, Void, Boolean\&gt; {

    public DownloadApk() {
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        boolean result = false;
        InputStream is = null;
        FileOutputStream fos = null;
        HttpURLConnection connection = null;
        try {
            String getRequestUrl = _APK\_DOWNLOAD\_URL_;
            URL url = new URL(getRequestUrl);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod(&quot;GET&quot;);
            connection.setRequestProperty(_API\_HEADER\_NAME_,_API\_HEADER\_VALUE_);
            connection.setChunkedStreamingMode(1000000);
            connection.setConnectTimeout(240000);
            connection.connect();

            // expect HTTP 200 OK for successful download
            // instead of the file
            if (connection.getResponseCode() != HttpURLConnection._HTTP\_OK_) {
                return false;
            }

            File sdcard = Environment._getExternalStorageDirectory_();
            File inputFile = new File(sdcard, _APK\_FILE\_NAME_);
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
            Toast._makeText_(getApplicationContext(),&quot;Error while downloading&quot; +
                    &quot; apk :&quot;+e,Toast._LENGTH\_LONG_).show();
            Log._e_(&quot;error&quot;, e.toString());
            result = false;
        }

        return result;
        // startTime and endTime in some time format or pass unix timestamp in UTC(pass long in that case)
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        super.onPostExecute(aBoolean);
        Log._d_(&quot;downloadStatus&quot;,aBoolean+&quot;&quot;);
        if(aBoolean){
            installApk();
        }
    }
}

- Setup a file provider to access the file uri for the downloaded file in the host application&#39;s manifest
- Install the app in the following way (the resulting intent takes user response for installing the file. Hence, it needs to be a user event)

public void installApk(){
    try {
        File directory = Environment._getExternalStorageDirectory_();
        File file = new File(directory, _APK\_FILE\_NAME_);
        Uri fileUri = Uri._fromFile_(file);
        if (Build.VERSION._SDK\_INT_ \&gt;= 24) {
            fileUri = FileProvider._getUriForFile_(this, &quot;com.tagbox.tag\_sync.fileprovider&quot;,
                    file);
        }
        Log._d_(&quot;uri&quot;,fileUri.getPath());


        Intent intent = new Intent(Intent._ACTION\_VIEW_, fileUri);
        intent.putExtra(Intent._EXTRA\_NOT\_UNKNOWN\_SOURCE_, true);
        intent.setDataAndType(fileUri, &quot;application/vnd.android&quot;  +  &quot;.package-archive&quot;);
        intent.setFlags(Intent._FLAG\_ACTIVITY\_CLEAR\_TASK_ |  Intent._FLAG\_ACTIVITY\_NEW\_TASK_);
        intent.addFlags(Intent._FLAG\_GRANT\_READ\_URI\_PERMISSION_);
        startActivity(intent);
    }
    catch (Exception e){
        Log._d_(&quot;AppInstall Error&quot;,e.getLocalizedMessage());
    }
}

- After installation (and also recommended at every startup of the host application) it can be checked whether TagSync is installed properly in the following manner

private boolean isPackageInstalled(){
    try{
       getPackageManager().getPackageInfo(&quot;com.tagbox.tag\_sync&quot;,0);
       return true;
    }
    catch (Exception e){
        return false;
    }
}

- Recommended: at every host application startup, the host application should compare the latest TagSync version (to be fetched using an API request as provided in sample application) against the version of the installed application The API returns a string body with the version. In case of mismatch, the host application can download the apk from the same download and install flow to update apk.

private String getPackageVersion(){
    try{
        return getPackageManager().getPackageInfo(&quot;com.tagbox.tag\_sync&quot;,0).versionName;
    }
    catch (Exception e){
        return &quot;&quot;;
    }
}

1. **2.**** Calling startScan method from the host app:**

This method will invoke the scanner on the TagSync and start collecting the data from relevant sensor in background. After some time interval (typically a few minutes) this activity is stopped, shutting off the Bluetooth Scanner to optimize battery life. Hence, this method should be called before each fetchData call with some delay (recommended delay is 2mins) to ensure that telemetry data received from fetchData is not stale.

public class StartScanner extends AsyncTask\&lt;Void, Void, Void\&gt; {

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
        startIntent.addFlags(Intent._FLAG\_INCLUDE\_STOPPED\_PACKAGES_);
        startIntent.setAction(_INTENT\_START\_SCANNER_);
        getApplicationContext().sendBroadcast(startIntent);
        super.onPostExecute(aVoid);
    }
}

Android&#39;s Bluetooth adapter requires the device&#39;s location to be ON with high accuracy to function properly. Hence, the host application should check the status of location every time startScan method is being called and prompt the user if it is found to be not enabled.

Recommended: In latest Android versions, a background application cannot be invoked reliably through broadcast events if the background application is inactive. To make sure that a method like startScan is received by TagSync application, it is recommended that before every startScan calls the launch activity

private void launchTagSyncApp(){
    Intent launchIntent = getPackageManager().getLaunchIntentForPackage(&quot;com.tagbox.tag\_sync&quot;);
    if (launchIntent != null) {
        startActivity(launchIntent);//null pointer check in case package name was not found
    }

}

1. **3.**** Calling fetchData method from the host app:**

Calling this method will lead to TagSync APK responding with a broadcast that contains data or an error message that is illustrated in the following section. The communication of the host application with TagSync APK is through Android broadcasts received on the registered broadcast receiver

The sensor data in response to this method is in JSON format and has a data field and a timestamp field in unix epoch format. The application picks up the latest telemetry packet of the relevant sensor device at each call. To ensure that the date is not stale, startScan method should be called at least 2 minutes before an attempt to fetch data.

public class FetchSensorData extends AsyncTask\&lt;Void, Void, Void\&gt; {

    public FetchSensorData() {
    }

    @Override
    protected Void doInBackground(Void... params) {


        Intent fetchIntent = new Intent(_INTENT\_FETCH\_SENSOR\_DATA_);
        //sensor id should look like the below format always
        // here a dummy sensor is entered
        fetchIntent.putExtra(_SENSOR\_ID_, &quot;D7:B9:26:05:61:B6&quot;);
        fetchIntent.addFlags(Intent._FLAG\_INCLUDE\_STOPPED\_PACKAGES_);
        sendBroadcast(fetchIntent);
        return null;
    }
}

Since, this method is always called after startScan method, it can be assumed that TagSync is already up and running and launch activity need not be called.

1. **4.**** Receiving data through broadcast receiver**

This code piece demonstrates how to receive the data via a broadcast receiver on the host application. This broadcast receiver needs to be explicitly registered in the activity or service in the host app.

public class TagboxBroadcastReceiver extends BroadcastReceiver{

    @Override
    public void onReceive(final Context context, final Intent intent) {

        if (intent.getAction().equals(_INTENT\_SENSOR\_DATA_)){
            String temperatureData = intent.getStringExtra(_SENSOR\_DATA_);
        }
        if (intent.getAction().equals(_INTENT\_TAGSYNC\_ERROR_)){
            String errorData = intent.getStringExtra(_ERROR\_DATA_);
        }
        if (intent.getAction().equals(_INTENT\_PERMISSION\_CHECK_)){
            String permissionCheck = intent.getStringExtra(_PERMISSION\_CHECK_);
        }
        if (intent.getAction().equals(_INTENT\_RECEIVED\_FETCH\_DATA_)){
            Toast._makeText_(context,&quot;Fetch data request received by TagSync app&quot;,
                    Toast._LENGTH\_LONG_).show();
        }
        if (intent.getAction().equals(_INTENT\_RECEIVED\_START\_SCAN_)){
            Toast._makeText_(context,&quot;Start scan request received by TagSync app&quot;,
                    Toast._LENGTH\_LONG_).show();
        }
    }
}

The data packet that comes in the INTENT\_SENSOR\_DATA will have the data field in the following format:

{
    **&quot;sensorId&quot;** :&quot;1901010000095&quot;,
    **&quot;temperature&quot;** :25.6, // temperature in celsius
    **&quot;utcTimestamp&quot;** :1567596493. // timestamp in unix epoch format
}

The error data that comes will have the following format&quot;

{
    **&quot;errorCode&quot;** :301,
    **&quot;errorMessage&quot;** :&quot;Location is not enabled&quot;
}

Following are the possible error codes:

- 301 : location not enabled
- 302 :  bluetooth adapter not working
- 303: sensor not found

The permission check message will have the following format&quot;

{
    **&quot;permission\_flag&quot;** :{
       **&quot;location\_permission&quot;** :false,
       **&quot;storage\_permission&quot;** :true
   }
}

1. **5.**** Receiving data through broadcast receiver**

In case of missing permissions, the host application needs to provide the missing permissions to the TagSync app. The host application can call the launch activity in this case which will ask for runtime permissions and then the user can provide the missing permission.

private void launchTagSyncApp(){
    Intent launchIntent = getPackageManager().getLaunchIntentForPackage(&quot;com.tagbox.tag\_sync&quot;);
    if (launchIntent != null) {
        startActivity(launchIntent);//null pointer check in case package name was not found
    }

}
