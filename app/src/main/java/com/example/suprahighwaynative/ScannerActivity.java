package com.example.suprahighwaynative;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.google.common.util.concurrent.ListenableFuture;
import java.nio.ByteBuffer;
import java.util.concurrent.ExecutionException;

public class ScannerActivity extends AppCompatActivity {

    private PreviewView viewFinder;
    private TextView tvStatus;
    private TextView tvPulse;
    private View targetBox;
    private boolean isScanning = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanner);

        viewFinder = findViewById(R.id.viewFinder);
        tvStatus = findViewById(R.id.tvStatus);
        tvPulse = findViewById(R.id.tvPulse);
        targetBox = findViewById(R.id.targetBox);

        targetBox.setBackground(ContextCompat.getDrawable(this, android.R.drawable.dialog_frame));
        targetBox.setBackgroundColor(Color.TRANSPARENT);

        if (allPermissionsGranted()) {
            startCamera();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 10);
        }
    }

    private void startCamera() {
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(this);
        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                Preview preview = new Preview.Builder().build();
                preview.setSurfaceProvider(viewFinder.getSurfaceProvider());

                ImageAnalysis imageAnalysis = new ImageAnalysis.Builder()
                        .setTargetResolution(new android.util.Size(640, 480))
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        .build();

                imageAnalysis.setAnalyzer(ContextCompat.getMainExecutor(this), image -> {
                    if (isScanning) {
                        image.close();
                        return;
                    }

                    // ACTUAL LIVE LIGHT DETECTION MATH
                    ByteBuffer buffer = image.getPlanes()[0].getBuffer();
                    byte[] data = new byte[buffer.capacity()];
                    buffer.get(data);

                    long totalBrightness = 0;
                    // Sample pixels to calculate ambient light (0 = Pitch Black, 255 = Bright White)
                    for (int i = 0; i < data.length; i += 10) {
                        totalBrightness += (data[i] & 0xFF);
                    }
                    int averageLuminosity = (int) (totalBrightness / (data.length / 10));

                    isScanning = true; // Lock the scanner so it only runs the sequence once

                    // Trigger the sequence based on the real light level
                    triggerSmartDemoSequence(averageLuminosity);

                    image.close();
                });

                CameraSelector cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA;
                cameraProvider.unbindAll();
                cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageAnalysis);

            } catch (ExecutionException | InterruptedException e) {
                tvStatus.setText("Camera error.");
            }
        }, ContextCompat.getMainExecutor(this));
    }

    private void triggerSmartDemoSequence(int luminosity) {
        Handler handler = new Handler(Looper.getMainLooper());

        // IF IT IS DARK (< 80 luminosity), SHOW THE ZERO-DCE LOW LIGHT INTERVENTION
        if (luminosity < 80) {
            tvStatus.setText("Low Light Detected! Engaging Zero-DCE Matrix...");
            tvStatus.setTextColor(Color.parseColor("#FFEB3B")); // Yellow Warning
            tvPulse.setText("🌙 Enhancing Exposure...");
            tvPulse.setTextColor(Color.parseColor("#FFEB3B"));

            handler.postDelayed(this::runFaceExtraction, 2000); // Add 2 seconds for the "enhancement" delay
        } else {
            // IF IT IS BRIGHT, SKIP DIRECTLY TO FACE EXTRACTION
            runFaceExtraction();
        }
    }

    private void runFaceExtraction() {
        Handler handler = new Handler(Looper.getMainLooper());

        tvStatus.setText("Scanning EdgeFace Vector...");
        tvStatus.setTextColor(Color.WHITE);
        tvPulse.setText("👤 Aligning Face...");
        tvPulse.setTextColor(Color.WHITE);

        handler.postDelayed(() -> {
            tvStatus.setText("Face Detected! Analyzing TransRPPG Liveness...");
            tvStatus.setTextColor(Color.parseColor("#FF9800")); // Orange
            tvPulse.setText("❤️ Reading Blood Volume Pulse...");
            tvPulse.setTextColor(Color.parseColor("#FF9800"));

            handler.postDelayed(() -> {
                tvStatus.setText("Match Found. Liveness Confirmed!");
                tvStatus.setTextColor(Color.parseColor("#00FF00")); // Green
                tvPulse.setText("❤️ Pulse: 74 BPM (Biological Match)");
                tvPulse.setTextColor(Color.parseColor("#FF5252")); // Red

                handler.postDelayed(() -> {
                    setResult(RESULT_OK);
                    finish();
                }, 2000);

            }, 2500);
        }, 1500);
    }

    private boolean allPermissionsGranted() {
        return ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 10 && allPermissionsGranted()) {
            startCamera();
        }
    }
}