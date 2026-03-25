package my.edu.utar.passwordmanager;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import android.text.method.PasswordTransformationMethod;
import android.text.method.TransformationMethod;
import androidx.appcompat.app.AppCompatActivity;
import android.view.WindowManager;
import com.google.android.material.textfield.TextInputLayout;
public class EditPasswordActivity extends AppCompatActivity {
    private EditText edtSite, edtUsername, edtPassword;
    private Button btnSave;
    private ImageButton btnShowPassword;
    private Button btnExtra;
    private View layoutExtra;
    private EditText edtExtraPin, edtChallengeQuestion, edtChallengeAnswer;
    private PasswordDbHelper dbHelper;
    private long entryId = -1; // -1 = new
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.activity_edit_password);
        dbHelper = new PasswordDbHelper(this);
        edtSite = findViewById(R.id.edtSite);
        edtUsername = findViewById(R.id.edtUsername);
        edtPassword = findViewById(R.id.edtPassword);
        btnSave = findViewById(R.id.btnSave);
        btnShowPassword = findViewById(R.id.btnShowPassword);
        btnExtra = findViewById(R.id.btnExtra);
        layoutExtra = findViewById(R.id.layoutExtra);
        edtExtraPin = findViewById(R.id.edtExtraPin);
        edtChallengeQuestion = findViewById(R.id.edtChallengeQuestion);
        edtChallengeAnswer = findViewById(R.id.edtChallengeAnswer);

        TransformationMethod hidden = PasswordTransformationMethod.getInstance();
        edtPassword.setTransformationMethod(hidden);

        btnShowPassword.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                // show while pressed
                edtPassword.setTransformationMethod(null);
                edtPassword.setSelection(edtPassword.getText().length());
                return true;
            } else if (event.getAction() == MotionEvent.ACTION_UP
                    || event.getAction() == MotionEvent.ACTION_CANCEL) {
                // hide when released
                edtPassword.setTransformationMethod(hidden);
                edtPassword.setSelection(edtPassword.getText().length());
                return true;
            }
            return false;
        });

        TextInputLayout tilPassword = findViewById(R.id.tilPassword);
        View passwordEndIcon = tilPassword.findViewById(com.google.android.material.R.id.text_input_end_icon);
        if (passwordEndIcon != null) {
            passwordEndIcon.setOnTouchListener((v, event) -> {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    edtPassword.setTransformationMethod(null);
                    edtPassword.setSelection(edtPassword.getText().length());
                    return true;
                } else if (event.getAction() == MotionEvent.ACTION_UP
                        || event.getAction() == MotionEvent.ACTION_CANCEL) {
                    edtPassword.setTransformationMethod(hidden);
                    edtPassword.setSelection(edtPassword.getText().length());
                    return true;
                }
                return false;
            });
        }

        btnExtra.setOnClickListener(v -> {
            if (layoutExtra.getVisibility() == View.VISIBLE) {
                layoutExtra.setVisibility(View.GONE);
            } else {
                layoutExtra.setVisibility(View.VISIBLE);
            }
        });
        // Check if editing existing
        if (getIntent() != null && getIntent().hasExtra("id")) {
            entryId = getIntent().getLongExtra("id", -1);
            String site = getIntent().getStringExtra("site");
            String username = getIntent().getStringExtra("username");
            String password = getIntent().getStringExtra("password");
            String extraPin = getIntent().getStringExtra("extraPin");
            String challengeQuestion = getIntent().getStringExtra("challengeQuestion");
            String challengeAnswer = getIntent().getStringExtra("challengeAnswer");
            edtSite.setText(site);
            edtUsername.setText(username);
            edtPassword.setText(password);
            edtExtraPin.setText(extraPin);
            edtChallengeQuestion.setText(challengeQuestion);
            edtChallengeAnswer.setText(challengeAnswer);

            boolean hasExtra = !TextUtils.isEmpty(extraPin)
                    || !TextUtils.isEmpty(challengeQuestion)
                    || !TextUtils.isEmpty(challengeAnswer);
            layoutExtra.setVisibility(hasExtra ? View.VISIBLE : View.GONE);
            setTitle("Edit Password");
        } else {
            setTitle("New Password");
        }
        btnSave.setOnClickListener(v -> save());
    }
    private void save() {
        String site = edtSite.getText().toString().trim();
        String username = edtUsername.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();
        String extraPin = edtExtraPin.getText().toString().trim();
        String challengeQuestion = edtChallengeQuestion.getText().toString().trim();
        String challengeAnswer = edtChallengeAnswer.getText().toString().trim();
        if (TextUtils.isEmpty(site) || TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "All fields are required.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (entryId == -1) {
            dbHelper.insertEntry(site, username, password, extraPin, challengeQuestion, challengeAnswer);
            Toast.makeText(this, "Password added.", Toast.LENGTH_SHORT).show();
        } else {
            dbHelper.updateEntry(entryId, site, username, password, extraPin, challengeQuestion, challengeAnswer);
            Toast.makeText(this, "Password updated.", Toast.LENGTH_SHORT).show();
        }
        finish();
    }
}
