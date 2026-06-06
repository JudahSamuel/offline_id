package com.example.suprahighwaynative;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private Button btnScan;
    private TextView tvShiftStatus;
    private TextView tvLogs;

    private final ActivityResultLauncher<Intent> scannerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    tvShiftStatus.setText("SECURED");
                    tvShiftStatus.setTextColor(Color.parseColor("#4CAF50"));
                    btnScan.setText("✓ Biometrics Verified");
                    btnScan.setBackgroundColor(Color.parseColor("#4CAF50"));
                    btnScan.setEnabled(false);

                    String pastLogs = tvLogs.getText().toString();
                    tvLogs.setText("⚠️ Today, 09:15 AM - NHAI Stretch 42 (Offline)\n" + pastLogs);
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnScan = findViewById(R.id.btnScan);
        tvShiftStatus = findViewById(R.id.tvShiftStatus);
        tvLogs = findViewById(R.id.tvLogs);

        btnScan.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ScannerActivity.class);
            scannerLauncher.launch(intent);
        });
    }
}