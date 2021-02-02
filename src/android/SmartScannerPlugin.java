package org.idpass.cordova.plugin;
// The native API

import android.app.Activity;
import android.content.Intent;
// Cordova-required packages
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import com.google.gson.Gson;

import timber.log.Timber;

// ID PASS SmartScanner paths
import org.idpass.smartscanner.lib.SmartScannerActivity;
import org.idpass.smartscanner.lib.scanner.config.ScannerOptions;

public class SmartScannerPlugin extends CordovaPlugin {

    private final int REQUEST_OP_SCANNER = 1001;
    public static final int RESULT_SCAN_FAILED = 2;

    private CallbackContext callbackContext = null;

    @Override
    public boolean execute(String action, JSONArray args,
                           final CallbackContext callbackContext) {

        this.callbackContext = callbackContext;
        if (action.equals("START_SCANNER")) {
            Activity activity = this.cordova.getActivity();
            Intent intent = new Intent(activity, SmartScannerActivity.class);
            if (args.length() > 0) {
                try {
                    Gson gson = new Gson();
                    ScannerOptions scannerOptions = gson.fromJson(args.getJSONObject(0).toString(), ScannerOptions.class);
                    intent.putExtra("scanner_options", scannerOptions);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            cordova.setActivityResultCallback (this);
            activity.startActivityForResult(intent,REQUEST_OP_SCANNER);
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

        if (requestCode == REQUEST_OP_SCANNER) {
            Timber.d("Plugin post SmartScannerActivity resultCode %d", resultCode);

            if (resultCode == Activity.RESULT_OK) {
                String returnedResult = intent.getStringExtra(SmartScannerActivity.SCANNER_RESULT);
                Timber.d("Plugin post SmartScannerActivity result %s", returnedResult);
                pluginResult = new PluginResult(PluginResult.Status.OK, returnedResult);
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Timber.d("Plugin post SmartScannerActivity RESULT CANCELLED");
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