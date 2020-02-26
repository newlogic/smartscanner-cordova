package com.impactlabs.cordova.plugin;
// The native API

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
// Cordova-required packages
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
// Custom Activity paths
import com.impactlabs.mlkitdemo.MLKitBaseActivity;

public class MLKitPlugin extends CordovaPlugin {

    private final int OP_MLKIT = 1001;

    @Override
    public boolean execute(String action, JSONArray args,
                           final CallbackContext callbackContext) {
        // Verify that the user sent a 'show' action
        if (!action.equals("startMLActivity")) {
            callbackContext.error("\"" + action + "\" is not a recognized action.");
            return false;
        }
        // String message;
        // String duration;
        // try {
        //   JSONObject options = args.getJSONObject(0);
        //   message = options.getString("message");
        //   duration = options.getString("duration");
        // } catch (JSONException e) {
        //   callbackContext.error("Error encountered: " + e.getMessage());
        //   return false;
        // }
        // Start calling the new activity
        Activity activity = this.cordova.getActivity();
        //or Context context=cordova.getActivity().getApplicationContext();
        Intent intent = new Intent(activity, MLKitBaseActivity.class);
        activity.startActivityForResult(intent,OP_MLKIT);
        // Send a positive result to the callbackContext
        PluginResult pluginResult = new PluginResult(PluginResult.Status.OK);
        callbackContext.sendPluginResult(pluginResult);
        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
    }
}