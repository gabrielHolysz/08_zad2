package com.example.inte2;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class SummaryActivity extends AppCompatActivity {

    public static final String EXTRA_CRUST = "extra_crust";
    public static final String EXTRA_SIZE = "extra_size";
    public static final String EXTRA_EXTRAS = "extra_extras";
    public static final String EXTRA_QUANTITY = "extra_quantity"; // typ int
    public static final String EXTRA_ADDRESS = "extra_address";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summary);

        TextView tvSummaryDetails = findViewById(R.id.tvSummaryDetails);
        Button btnShareSummary = findViewById(R.id.btnShareSummary);
        Button btnEdit = findViewById(R.id.btnEdit);
        Button btnConfirm = findViewById(R.id.btnConfirm);

        // Odbieranie danych przekazanych intencją jawną
        Intent intent = getIntent();
        String crust = intent.getStringExtra(EXTRA_CRUST);
        String size = intent.getStringExtra(EXTRA_SIZE);
        String extras = intent.getStringExtra(EXTRA_EXTRAS);
        int quantity = intent.getIntExtra(EXTRA_QUANTITY, 1); // Wymóg: typ int
        String address = intent.getStringExtra(EXTRA_ADDRESS);

        String summaryText = getString(R.string.summary_format, crust, size, extras, quantity, address);
        tvSummaryDetails.setText(summaryText);

        // Intencja niejawna – udostępnianie zabezpieczona blokiem try
        btnShareSummary.setOnClickListener(v -> {
            try {
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_TEXT, summaryText);
                Intent chooser = Intent.createChooser(shareIntent, getString(R.string.share_via));
                startActivity(chooser);
            } catch (Exception e) {
                Toast.makeText(this, R.string.error_no_app, Toast.LENGTH_SHORT).show();
            }
        });

        // Przycisk Potwierdź – zwrot RESULT_OK
        btnConfirm.setOnClickListener(v -> {
            setResult(RESULT_OK);
            finish();
        });

        // Przycisk Popraw – zwrot RESULT_CANCELED
        btnEdit.setOnClickListener(v -> {
            setResult(RESULT_CANCELED);
            finish();
        });
    }
}