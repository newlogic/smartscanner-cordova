package com.impactlabs.mlkitdemo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.impactlabs.impactlabsmlkitlibrary.activities.LiveBarcodeScanningActivity;
import com.impactlabs.impactlabsmlkitlibrary.barcodedetection.BarcodeField;
import com.impactlabs.impactlabsmlkitlibrary.barcodedetection.BarcodeResultFragment;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private final int OP_MLKIT = 1001;

    private static final String TAG = "LiveBarcodeActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        if (requestCode == OP_MLKIT) {
            Log.d(TAG, "Plugin post ML Activity resultCode " + resultCode);

            if (resultCode == Activity.RESULT_OK) {
                String returnedResult = intent.getStringExtra(LiveBarcodeScanningActivity.KEY_BARCODE_RESULT);
                ArrayList<BarcodeField> barcodeFieldList = new ArrayList<>();
                barcodeFieldList.add(new BarcodeField("Raw Value", returnedResult));
                BarcodeResultFragment.show(getSupportFragmentManager(), barcodeFieldList);
            }
        }
    }

    public void startScanningActivity(@NonNull View view){
        Intent intent = new Intent(this, LiveBarcodeScanningActivity.class);
        startActivityForResult(intent, OP_MLKIT);
    }
}