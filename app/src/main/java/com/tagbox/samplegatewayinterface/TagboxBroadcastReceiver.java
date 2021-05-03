package com.tagbox.samplegatewayinterface;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import static com.tagbox.samplegatewayinterface.Constants.ERROR_DATA;
import static com.tagbox.samplegatewayinterface.Constants.INTENT_LOCATION_DATA;
import static com.tagbox.samplegatewayinterface.Constants.INTENT_PERMISSION_CHECK;
import static com.tagbox.samplegatewayinterface.Constants.INTENT_RECEIVED_FETCH_DATA;
import static com.tagbox.samplegatewayinterface.Constants.INTENT_RECEIVED_START_SCAN;
import static com.tagbox.samplegatewayinterface.Constants.INTENT_SENSOR_DATA;
import static com.tagbox.samplegatewayinterface.Constants.INTENT_TAGSYNC_ERROR;
import static com.tagbox.samplegatewayinterface.Constants.INTENT_TAGSYNC_RUNNING_STATUS;
import static com.tagbox.samplegatewayinterface.Constants.LOCATION_DATA;
import static com.tagbox.samplegatewayinterface.Constants.PERMISSION_CHECK;
import static com.tagbox.samplegatewayinterface.Constants.SENSOR_DATA;
import static com.tagbox.samplegatewayinterface.Constants.STATUS;


/**   The data packet that comes in the INTENT_SENSOR_DATA will have the data field in the following format:
//        {
//        “sensorId” : “1901010000095”,
//        “temperature” : 25.6,	// temperature data in Celcius
//        “utcTimestamp” : 1567596493	// timestamp in unix epoch format
//        }
//
//        The error data that comes will have the following format”
//        {
//        “errorCode” : 301,
//        “errorMessage” : “Location is not enabled”
//        }
//
//        Following are the possible error codes:
//
//        301 : location not enabled
//        Ii.   302 :  bluetooth adapter not working
//        Iii.  303: sensor not found

//
//        The permission check message will have the following format”
//        {
//        “permission_flag”: {
 //            “location_permission” : false,
//             “storage_permission” : “true”
//          }
//        }
*/

public class TagboxBroadcastReceiver extends BroadcastReceiver{

    @Override
    public void onReceive(final Context context, final Intent intent) {

        if (intent.getAction().equals(INTENT_SENSOR_DATA)){

            String temperatureData = intent.getStringExtra(SENSOR_DATA);
            Toast.makeText(context,temperatureData,Toast.LENGTH_LONG).show();
        }
        if (intent.getAction().equals(INTENT_LOCATION_DATA)){

            String locationData = intent.getStringExtra(LOCATION_DATA);
            Toast.makeText(context,locationData,Toast.LENGTH_LONG).show();
        }
        if (intent.getAction().equals(INTENT_TAGSYNC_ERROR)){

            String errorData = intent.getStringExtra(ERROR_DATA);
            Toast.makeText(context,errorData,
                    Toast.LENGTH_LONG).show();
        }
        if (intent.getAction().equals(INTENT_TAGSYNC_RUNNING_STATUS)){

            boolean status = intent.getBooleanExtra(STATUS,false);
            Toast.makeText(context,"is TagSync service Running : "+status,
                    Toast.LENGTH_LONG).show();
        }
        if (intent.getAction().equals(INTENT_PERMISSION_CHECK)){
            String permissionCheck = intent.getStringExtra(PERMISSION_CHECK);
        }
        if (intent.getAction().equals(INTENT_RECEIVED_FETCH_DATA)){
            Toast.makeText(context,"Fetch data request received by TagSync app",
                    Toast.LENGTH_LONG).show();
        }
        if (intent.getAction().equals(INTENT_RECEIVED_START_SCAN)){
            Toast.makeText(context,"Start scan request received by TagSync app",
                    Toast.LENGTH_LONG).show();
        }
    }
}


