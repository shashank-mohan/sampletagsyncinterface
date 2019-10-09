package com.tagbox.samplegatewayinterface;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import static com.tagbox.samplegatewayinterface.Constants.HUMIDITY_BREACH;
import static com.tagbox.samplegatewayinterface.Constants.INTENT_GATEWAY_BREACH;
import static com.tagbox.samplegatewayinterface.Constants.INTENT_SYNC_DONE;
import static com.tagbox.samplegatewayinterface.Constants.LAST_SYNC_TIME;
import static com.tagbox.samplegatewayinterface.Constants.RECENT_PARAMS;
import static com.tagbox.samplegatewayinterface.Constants.SHOCK_BREACH;
import static com.tagbox.samplegatewayinterface.Constants.TEMP_BREACH;


/**
 * Created by Suhas on 1/25/2017.
 */

public class TagboxBroadcastReceiver extends BroadcastReceiver {


    @Override
    public void onReceive(final Context context, final Intent intent) {


        if (intent.getAction().equals(INTENT_GATEWAY_BREACH)) {

            // persist following breach data in sharedprefs/ file to display data
            // or whatever you want to by starting IntentService maybe
            String temperatureBreach = intent.getStringExtra(TEMP_BREACH);
            String shockBreach = intent.getStringExtra(SHOCK_BREACH);
            String humidityBreach = intent.getStringExtra(HUMIDITY_BREACH);
            String currentParams = intent.getStringExtra(RECENT_PARAMS);
            Log.d("breach",  humidityBreach+" \n"+
                    currentParams);
            //this data is passed to disPlay data // you can check the values of array in interface activity code
            Intent intentBreach = new Intent(context, InterfaceActivity.class);
            if (temperatureBreach != null && shockBreach != null && humidityBreach != null) {
                intentBreach.putExtra(TEMP_BREACH, temperatureBreach);
                intentBreach.putExtra(SHOCK_BREACH, shockBreach);
                intentBreach.putExtra(HUMIDITY_BREACH, humidityBreach);
                intentBreach.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intentBreach);
            }

        } else if (intent.getAction().equals(INTENT_SYNC_DONE)) {

            // pass last sync time of sensor to the interface activity to be displayed
            Intent lastSyncTimeIntent = new Intent(context, InterfaceActivity.class);
            lastSyncTimeIntent.putExtra(LAST_SYNC_TIME, intent.getStringExtra(LAST_SYNC_TIME));
            lastSyncTimeIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(lastSyncTimeIntent);
            Toast.makeText(context, "Sensor synced till " + intent.getStringExtra("time"), Toast.LENGTH_LONG).show();
        }
    }
}