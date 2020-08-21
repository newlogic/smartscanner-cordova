package com.impactlabs.cordova.plugin;
// The native API

import android.app.Activity;
import android.content.Intent;
// Cordova-required packages
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;

import timber.log.Timber;

// Custom Activity paths
import com.newlogic.mlkitlibrary.MLKitActivity;

public class MLKitPlugin extends CordovaPlugin {

    private final int OP_MLKIT = 1001;
    public static final int RESULT_SCAN_FAILED = 2;

    private CallbackContext callbackContext = null;

    @Override
    public boolean execute(String action, JSONArray args,
                           final CallbackContext callbackContext) {
        
        String mode = "mrz";

        this.callbackContext = callbackContext;
        if (action.equals("startMLActivity")) {
            Activity activity = this.cordova.getActivity();
            Intent intent = new Intent(activity, MLKitActivity.class);
            if (args.length() > 0) {
                try {
                    String value = args.getJSONObject(0).getString("mode");   
                    if (value != null) mode = value;  
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            intent.putExtra("mode", mode);
            cordova.setActivityResultCallback (this);
            activity.startActivityForResult(intent,OP_MLKIT);
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

        if (requestCode == OP_MLKIT) {
            Timber.d("Plugin post MLKit Activity resultCode %d", resultCode);
        
            if (resultCode == Activity.RESULT_OK) {
                String returnedResult = intent.getStringExtra(MLKitActivity.MLKIT_RESULT);
                Timber.d("Plugin post MLKit Activity result %s", returnedResult);
                pluginResult = new PluginResult(PluginResult.Status.OK, returnedResult);
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Timber.d("Plugin post MLKit Activity. RESULT CANCELLED");
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