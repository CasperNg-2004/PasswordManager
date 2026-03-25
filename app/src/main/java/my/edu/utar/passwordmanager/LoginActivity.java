package my.edu.utar.passwordmanager;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {
    private static final String PREFS = "pm_prefs";
    private static final String KEY_PIN = "pin";
    private static final String KEY_FAILED_ATTEMPTS = "failed_attempts";
    private static final String KEY_LOCK_UNTIL = "lock_until_ms";
    private static final int MAX_ATTEMPTS = 5;
    private static final long LOCKOUT_MS = 30_000; // 30 seconds

    private EditText edtPin;
    private EditText edtConfirmPin;
    private boolean isFirstSetup = false;
    private SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.activity_login);
        edtPin = findViewById(R.id.edtPin);
        edtConfirmPin = findViewById(R.id.edtConfirmPin);
        TextView txtMessage = findViewById(R.id.txtMessage);
        Button btnAction = findViewById(R.id.btnAction);
        sp = getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        String savedPin = sp.getString(KEY_PIN, null);
        if (savedPin == null) {
            // First time: user must create PIN
            isFirstSetup = true;
            txtMessage.setText("Create a 4-digit PIN to protect your password manager.");
            edtConfirmPin.setVisibility(View.VISIBLE);
            btnAction.setText("Save PIN");
        } else {
            // Existing user: login
            isFirstSetup = false;
            txtMessage.setText("Enter your 4-digit PIN to unlock.");
            edtConfirmPin.setVisibility(View.GONE);
            btnAction.setText("Unlock");
        }
        btnAction.setOnClickListener(v -> {
            if (isFirstSetup) {
                setupPin();
            } else {
                checkPin(savedPin);
            }
        });
    }

    private void setupPin() {
        String pin = edtPin.getText().toString();
        String confirm = edtConfirmPin.getText().toString();
        if (pin.length() != 4 || !pin.matches("\\d{4}")) {
            Toast.makeText(this, "PIN must be exactly 4 digits.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!pin.equals(confirm)) {
            Toast.makeText(this, "PINs do not match.", Toast.LENGTH_SHORT).show();
            return;
        }
        SharedPreferences sp = getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        sp.edit().putString(KEY_PIN, pin).apply();
        openMain();
    }

    private void checkPin(String savedPin) {
        long now = System.currentTimeMillis();
        long lockUntil = sp.getLong(KEY_LOCK_UNTIL, 0);
        if (now < lockUntil) {
            long remainingSec = Math.max(1, (lockUntil - now + 999) / 1000);
            Toast.makeText(this, "Too many attempts. Try again in " + remainingSec + "s.", Toast.LENGTH_SHORT).show();
            return;
        }

        String input = edtPin.getText().toString();
        if (TextUtils.isEmpty(input)) {
            Toast.makeText(this, "Enter your PIN.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (input.equals(savedPin)) {
            sp.edit().putInt(KEY_FAILED_ATTEMPTS, 0).putLong(KEY_LOCK_UNTIL, 0).apply();
            openMain();
        } else {
            int attempts = sp.getInt(KEY_FAILED_ATTEMPTS, 0) + 1;
            if (attempts >= MAX_ATTEMPTS) {
                sp.edit()
                        .putInt(KEY_FAILED_ATTEMPTS, 0)
                        .putLong(KEY_LOCK_UNTIL, now + LOCKOUT_MS)
                        .apply();
                Toast.makeText(this, "Too many attempts. Locked for 30 seconds.", Toast.LENGTH_SHORT).show();
            } else {
                sp.edit().putInt(KEY_FAILED_ATTEMPTS, attempts).apply();
                int left = MAX_ATTEMPTS - attempts;
                Toast.makeText(this, "Incorrect PIN. " + left + " tries left.", Toast.LENGTH_SHORT).show();
            }
        }
    }
    private void openMain() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
