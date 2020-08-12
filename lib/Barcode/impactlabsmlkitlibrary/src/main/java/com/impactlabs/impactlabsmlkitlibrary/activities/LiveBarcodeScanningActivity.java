/*
 * Copyright 2019 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.impactlabs.impactlabsmlkitlibrary.activities;

import android.Manifest;
import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.gms.common.internal.Objects;
import com.google.android.material.chip.Chip;
import com.google.android.material.snackbar.Snackbar;
import com.impactlabs.impactlabsmlkitlibrary.R;
import com.impactlabs.impactlabsmlkitlibrary.barcodedetection.BarcodeProcessor;
import com.impactlabs.impactlabsmlkitlibrary.barcodedetection.BarcodeResultFragment;
import com.impactlabs.impactlabsmlkitlibrary.camera.CameraSource;
import com.impactlabs.impactlabsmlkitlibrary.camera.CameraSourcePreview;
import com.impactlabs.impactlabsmlkitlibrary.camera.GraphicOverlay;
import com.impactlabs.impactlabsmlkitlibrary.camera.WorkflowModel;
import com.impactlabs.impactlabsmlkitlibrary.settings.SettingsActivity;
import com.impactlabs.impactlabsmlkitlibrary.utils.Utils;

import java.io.IOException;
import java.util.List;

/**
 * Demonstrates the barcode scanning workflow using camera preview.
 */
public class LiveBarcodeScanningActivity extends AppCompatActivity implements OnClickListener {

    public static String KEY_BARCODE_RESULT = "LiveBarcodeScanningActivity.RESULT";

    private static final String TAG = "LiveBarcodeActivity";

    private CameraSource cameraSource;
    private CameraSourcePreview preview;
    private GraphicOverlay graphicOverlay;
    private View settingsButton;
    private View flashButton;
    private Chip promptChip;
    private AnimatorSet promptChipAnimator;
    private WorkflowModel workflowModel;
    private WorkflowModel.WorkflowState currentWorkflowState;

    private boolean hasRequiredPermissions = false;

    private View coordinatorLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_live_barcode);
        coordinatorLayout = findViewById(R.id.coordinator_layout);
        preview = findViewById(R.id.camera_preview);
        graphicOverlay = findViewById(R.id.camera_preview_graphic_overlay);
        graphicOverlay.setOnClickListener(this);
        cameraSource = new CameraSource(graphicOverlay);

        promptChip = findViewById(R.id.bottom_prompt_chip);
        promptChipAnimator =
                (AnimatorSet) AnimatorInflater.loadAnimator(this, R.animator.bottom_prompt_chip_enter);
        promptChipAnimator.setTarget(promptChip);

        findViewById(R.id.close_button).setOnClickListener(this);
        flashButton = findViewById(R.id.flash_button);
        flashButton.setOnClickListener(this);
        settingsButton = findViewById(R.id.settings_button);
        settingsButton.setOnClickListener(this);

        setUpWorkflowModel();
    }

    @Override
    protected void onStart() {
        super.onStart();
        hasRequiredPermissions = Utils.allPermissionsGranted(this);
        Log.d(TAG, "onStart() hasRequiredPermissions " + hasRequiredPermissions);
        if (!hasRequiredPermissions) {
            Utils.requestRuntimePermissions(this);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (hasRequiredPermissions) {
            workflowModel.markCameraFrozen();
            settingsButton.setEnabled(true);
            currentWorkflowState = WorkflowModel.WorkflowState.NOT_STARTED;
            cameraSource.setFrameProcessor(new BarcodeProcessor(graphicOverlay, workflowModel));
            workflowModel.setWorkflowState(WorkflowModel.WorkflowState.DETECTING);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.d(TAG, "onRequestPermissionsResult : " + requestCode);
        if (requestCode == Utils.PERMISSION_REQUEST_CODE) {

            hasRequiredPermissions = Utils.allPermissionsGranted(this);
            if (!hasRequiredPermissions) {
                List<String> permissionsWithRationale = Utils.getPermissionsWithOutRationale(this);
                if (permissionsWithRationale.size() > 0) {
                    for (String permission : permissionsWithRationale) {
                        if (permission.compareTo(Manifest.permission.CAMERA) == 0) {
                            //Build Alert dialog
                            new AlertDialog.Builder(this)
                                    .setTitle(R.string.camera_permission_required)
                                    .setMessage(R.string.camera_permission_rationale)
                                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            // Continue with delete operation
                                            dialog.dismiss();
                                            openSettingsApp();

                                        }
                                    })
                                    // A null listener allows the button to dismiss the dialog and take no further action.
                                    .setNegativeButton(android.R.string.cancel, null)
                                    .setIcon(android.R.drawable.ic_dialog_alert)
                                    .show();
                            return;
                        }
                    }
                }

                Snackbar snackbar = Snackbar.make(coordinatorLayout,
                        R.string.required_perms_not_given, Snackbar.LENGTH_INDEFINITE);
                snackbar.setAction(R.string.request, new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (!hasRequiredPermissions) {
                            Utils.requestRuntimePermissions(LiveBarcodeScanningActivity.this);
                        }
                    }
                });
                snackbar.show();
            }

        }
    }

    private void openSettingsApp() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivity(intent);
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        BarcodeResultFragment.dismiss(getSupportFragmentManager());
    }

    @Override
    protected void onPause() {
        super.onPause();
        currentWorkflowState = WorkflowModel.WorkflowState.NOT_STARTED;
        stopCameraPreview();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (cameraSource != null) {
            cameraSource.release();
            cameraSource = null;
        }
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.close_button) {
            onBackPressed();

        } else if (id == R.id.flash_button) {
            if (flashButton.isSelected()) {
                flashButton.setSelected(false);
                cameraSource.updateFlashMode(Camera.Parameters.FLASH_MODE_OFF);
            } else {
                flashButton.setSelected(true);
                cameraSource.updateFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
            }

        } else if (id == R.id.settings_button) {
            // Sets as disabled to prevent the user from clicking on it too fast.
            settingsButton.setEnabled(false);
            startActivity(new Intent(this, SettingsActivity.class));
        }
    }

    private void startCameraPreview() {
        if (!workflowModel.isCameraLive() && cameraSource != null) {
            try {
                workflowModel.markCameraLive();
                preview.start(cameraSource);
            } catch (IOException e) {
                Log.e(TAG, "Failed to start camera preview!", e);
                cameraSource.release();
                cameraSource = null;
            }
        }
    }

    private void stopCameraPreview() {
        if (workflowModel.isCameraLive()) {
            workflowModel.markCameraFrozen();
            flashButton.setSelected(false);
            preview.stop();
        }
    }

    private void setUpWorkflowModel() {
        workflowModel = ViewModelProviders.of(this).get(WorkflowModel.class);

        // Observes the workflow state changes, if happens, update the overlay view indicators and
        // camera preview state.
        workflowModel.workflowState.observe(
                this,
                workflowState -> {
                    if (workflowState == null || Objects.equal(currentWorkflowState, workflowState)) {
                        return;
                    }

                    currentWorkflowState = workflowState;
                    Log.d(TAG, "Current workflow state: " + currentWorkflowState.name());

                    boolean wasPromptChipGone = (promptChip.getVisibility() == View.GONE);

                    switch (workflowState) {
                        case DETECTING:
                            promptChip.setVisibility(View.VISIBLE);
                            promptChip.setText(R.string.prompt_point_at_a_barcode);
                            startCameraPreview();
                            break;
                        case CONFIRMING:
                            promptChip.setVisibility(View.VISIBLE);
                            promptChip.setText(R.string.prompt_move_camera_closer);
                            startCameraPreview();
                            break;
                        case SEARCHING:
                            promptChip.setVisibility(View.VISIBLE);
                            promptChip.setText(R.string.prompt_searching);
                            stopCameraPreview();
                            break;
                        case DETECTED:
                        case SEARCHED:
                            promptChip.setVisibility(View.GONE);
                            stopCameraPreview();
                            break;
                        default:
                            promptChip.setVisibility(View.GONE);
                            break;
                    }

                    boolean shouldPlayPromptChipEnteringAnimation =
                            wasPromptChipGone && (promptChip.getVisibility() == View.VISIBLE);
                    if (shouldPlayPromptChipEnteringAnimation && !promptChipAnimator.isRunning()) {
                        promptChipAnimator.start();
                    }
                });

        workflowModel.detectedBarcode.observe(
                this,
                barcode -> {
                    if (barcode != null) {
                        //Return the scanned value
                        Intent data = new Intent();
                        String text = barcode.getRawValue();
                        //---set the data to pass back---
                        data.putExtra(KEY_BARCODE_RESULT, text);
                        setResult(RESULT_OK, data);
                        //---close the activity---
                        finish();
                    }
                });
    }
}
