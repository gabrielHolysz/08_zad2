package com.example.inte2;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private Spinner spCrust;
    private RadioGroup rgSize;
    private CheckBox cbCheese, cbMushroom, cbHam;
    private SeekBar sbQuantity;
    private TextView tvQuantityValue;
    private EditText etAddress;
    private Button btnSubmit;

    private final ActivityResultLauncher<Intent> summaryLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    // Obsługa przypadku 1: Potwierdzenie zamówienia
                    Snackbar.make(findViewById(R.id.main), R.string.sent, Snackbar.LENGTH_LONG).show();
                    clearForm();
                } else if (result.getResultCode() == RESULT_CANCELED) {
                    // Obsługa przypadku 2: Rezygnacja / Poprawa danych
                    Snackbar.make(findViewById(R.id.main), R.string.returned_to_edit, Snackbar.LENGTH_LONG).show();
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        setUpCrustSpinner();
        setUpListeners();

        tvQuantityValue.setText(String.valueOf(sbQuantity.getProgress()));
    }

    private void initViews() {
        spCrust = findViewById(R.id.spCrust);
        rgSize = findViewById(R.id.rgSize);
        cbCheese = findViewById(R.id.cbCheese);
        cbMushroom = findViewById(R.id.cbMushroom);
        cbHam = findViewById(R.id.cbHam);
        sbQuantity = findViewById(R.id.sbQuantity);
        tvQuantityValue = findViewById(R.id.tvQuantityValue);
        etAddress = findViewById(R.id.etAddress);
        btnSubmit = findViewById(R.id.btnSubmit);
    }

    private void setUpCrustSpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.crusts, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spCrust.setAdapter(adapter);
    }

    private void setUpListeners() {
        sbQuantity.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int currentVal = progress < 1 ? 1 : progress;
                tvQuantityValue.setText(String.valueOf(currentVal));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) { }
        });

        btnSubmit.setOnClickListener(v -> {
            if (validateForm()) {
                openSummaryActivity();
            }
        });
    }

    private boolean validateForm() {
        etAddress.setError(null);

        if (spCrust.getSelectedItemPosition() == 0) {
            Toast.makeText(this, R.string.error_crust, Toast.LENGTH_SHORT).show();
            return false;
        }

        if (rgSize.getCheckedRadioButtonId() == -1) {
            Toast.makeText(this, R.string.error_size, Toast.LENGTH_SHORT).show();
            return false;
        }

        String address = etAddress.getText().toString().trim();
        if (address.isEmpty()) {
            etAddress.setError(getString(R.string.error_address));
            etAddress.requestFocus();
            return false;
        }

        return true;
    }

    private void openSummaryActivity() {
        String crust = spCrust.getSelectedItem().toString();

        String size = "";
        int selectedId = rgSize.getCheckedRadioButtonId();
        if (selectedId == R.id.rbSmall) size = getString(R.string.size_small);
        else if (selectedId == R.id.rbMedium) size = getString(R.string.size_medium);
        else if (selectedId == R.id.rbLarge) size = getString(R.string.size_large);

        List<String> extrasList = new ArrayList<>();
        if (cbCheese.isChecked()) extrasList.add(getString(R.string.extra_cheese));
        if (cbMushroom.isChecked()) extrasList.add(getString(R.string.extra_mushroom));
        if (cbHam.isChecked()) extrasList.add(getString(R.string.extra_ham));

        String extras = extrasList.isEmpty() ? "brak" : String.join(", ", extrasList);
        int quantity = Integer.parseInt(tvQuantityValue.getText().toString());
        String address = etAddress.getText().toString().trim();

        Intent intent = new Intent(this, SummaryActivity.class);
        intent.putExtra(SummaryActivity.EXTRA_CRUST, crust);
        intent.putExtra(SummaryActivity.EXTRA_SIZE, size);
        intent.putExtra(SummaryActivity.EXTRA_EXTRAS, extras);
        intent.putExtra(SummaryActivity.EXTRA_QUANTITY, quantity); // Przekazanie int
        intent.putExtra(SummaryActivity.EXTRA_ADDRESS, address);

        summaryLauncher.launch(intent);
    }

    private void clearForm() {
        spCrust.setSelection(0);
        rgSize.clearCheck();
        cbCheese.setChecked(false);
        cbMushroom.setChecked(false);
        cbHam.setChecked(false);
        sbQuantity.setProgress(1);
        etAddress.setText("");
        etAddress.setError(null);
    }
}