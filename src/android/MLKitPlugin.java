package com.impactlabs.cordova.plugin;
// The native API

import android.app.Activity;
import android.content.Intent;
// Cordova-required packages
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;

import timber.log.Timber;

// Custom Activity paths
import com.impactlabs.impactlabsmlkitlibrary.activities.LiveBarcodeScanningActivity;
import com.newlogic.mrzlibrary.MRZActivity;

public class MLKitPlugin extends CordovaPlugin {

    private final int OP_PDF417 = 1001;
    private final int OP_MRZ = 1002;
    public static final int RESULT_SCAN_FAILED = 2;

    private CallbackContext callbackContext = null;

    @Override
    public boolean execute(String action, JSONArray args,
                           final CallbackContext callbackContext) {

        this.callbackContext = callbackContext;
        if (action.equals("startMLActivity")) {
            Activity activity = this.cordova.getActivity();
            Intent intent = new Intent(activity, LiveBarcodeScanningActivity.class);
            cordova.setActivityResultCallback (this);
            activity.startActivityForResult(intent,OP_PDF417);
            return true;
        }
        else if (action.equals("startMRZActivity")) {
            Activity activity = this.cordova.getActivity();
            Intent intent = new Intent(activity, MRZActivity.class);
            cordova.setActivityResultCallback (this);
            activity.startActivityForResult(intent,OP_MRZ);
            return true;
        } else {
            callbackContext.error("\"" + action + "\" is not a recognized action.");
            return false;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        PluginResult pluginResult;

        if (requestCode == OP_PDF417) {
            Timber.d("Plugin post ML Activity resultCode %d", resultCode);
        
            if (resultCode == Activity.RESULT_OK) {
                String returnedResult = intent.getStringExtra(LiveBarcodeScanningActivity.KEY_BARCODE_RESULT);
                Timber.d("Plugin post ML Activity result %s", returnedResult);
                pluginResult = new PluginResult(PluginResult.Status.OK, returnedResult);
            } else if (resultCode == Activity.RESULT_CANCELED) {
				Timber.d("Plugin post ML Activity. RESULT CANCELLED");
                pluginResult = new PluginResult(PluginResult.Status.NO_RESULT, "Scanning Cancelled.");
            } else {
                callbackContext.error("Scanning Failed.");
                pluginResult = new PluginResult(PluginResult.Status.ERROR, "Scanning Failed.");
            }
        } else if (requestCode == OP_MRZ) {
            Timber.d("Plugin post MRZ Activity resultCode %d", resultCode);
        
            if (resultCode == Activity.RESULT_OK) {
                String returnedResult = intent.getStringExtra(MRZActivity.MRZ_RESULT);
                Timber.d("Plugin post MRZ Activity result %s", returnedResult);
                pluginResult = new PluginResult(PluginResult.Status.OK, returnedResult);
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Timber.d("Plugin post MRZ Activity. RESULT CANCELLED");
                pluginResult = new PluginResult(PluginResult.Status.NO_RESULT, "Scanning Cancelled.");
            } else {
                callbackContext.error("Scanning Failed.");
                pluginResult = new PluginResult(PluginResult.Status.ERROR, "Scanning Failed.");
            }
        } else {
            callbackContext.error("Unknown Request Code!");
            pluginResult = new PluginResult(PluginResult.Status.ERROR, "Unknown Request Code!");
        }


        pluginResult.setKeepCallback(true);
        callbackContext.sendPluginResult(pluginResult);
    }
}