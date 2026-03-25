package my.edu.utar.passwordmanager;

import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.textfield.TextInputLayout;

public class ViewPasswordActivity extends AppCompatActivity {

    private TextView txtSite, txtUsername, txtPassword;
    private ImageButton btnShowPassword;
    private String passwordValue = "";
    private static final String MASK = "••••••••";
    private Button btnExtra;
    private View layoutExtra;
    private TextView txtExtraPin, txtChallengeQuestion, txtChallengeAnswer;
    private String extraPinValue = "";
    private String challengeAnswerValue = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.activity_view_password);

        txtSite = findViewById(R.id.txtSite);
        txtUsername = findViewById(R.id.txtUsername);
        txtPassword = findViewById(R.id.txtPassword);
        btnShowPassword = findViewById(R.id.btnShowPassword);
        Button btnEdit = findViewById(R.id.btnEdit);
        btnExtra = findViewById(R.id.btnExtra);
        layoutExtra = findViewById(R.id.layoutExtra);
        txtExtraPin = findViewById(R.id.txtExtraPin);
        txtChallengeQuestion = findViewById(R.id.txtChallengeQuestion);
        txtChallengeAnswer = findViewById(R.id.txtChallengeAnswer);

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("site")) {
            String site = intent.getStringExtra("site");
            String username = intent.getStringExtra("username");
            passwordValue = intent.getStringExtra("password");
            if (passwordValue == null) passwordValue = "";
            String extraPin = intent.getStringExtra("extraPin");
            String challengeQuestion = intent.getStringExtra("challengeQuestion");
            challengeAnswerValue = intent.getStringExtra("challengeAnswer");
            if (challengeAnswerValue == null) challengeAnswerValue = "";
            extraPinValue = extraPin == null ? "" : extraPin;

            txtSite.setText(site);
            txtUsername.setText(username);
            txtPassword.setText(MASK);

            txtExtraPin.setText(extraPinValue.isEmpty() ? "(none)" : MASK);
            txtChallengeQuestion.setText((challengeQuestion == null || challengeQuestion.isEmpty()) ? "(none)" : challengeQuestion);
            txtChallengeAnswer.setText(challengeAnswerValue.isEmpty() ? "(none)" : MASK);

            boolean hasExtra = (extraPin != null && !extraPin.isEmpty())
                    || (challengeQuestion != null && !challengeQuestion.isEmpty())
                    || (challengeAnswerValue != null && !challengeAnswerValue.isEmpty());
            btnExtra.setVisibility(hasExtra ? View.VISIBLE : View.GONE);
            layoutExtra.setVisibility(View.GONE);
        }

        btnShowPassword.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                txtPassword.setText(passwordValue);
                return true;
            } else if (event.getAction() == MotionEvent.ACTION_UP
                    || event.getAction() == MotionEvent.ACTION_CANCEL) {
                txtPassword.setText(MASK);
                return true;
            }
            return false;
        });

        TextInputLayout tilPasswordView = findViewById(R.id.tilPasswordView);
        View passwordEndIcon = tilPasswordView.findViewById(com.google.android.material.R.id.text_input_end_icon);
        if (passwordEndIcon != null) {
            passwordEndIcon.setOnTouchListener((v, event) -> {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    txtPassword.setText(passwordValue);
                    return true;
                } else if (event.getAction() == MotionEvent.ACTION_UP
                        || event.getAction() == MotionEvent.ACTION_CANCEL) {
                    txtPassword.setText(MASK);
                    return true;
                }
                return false;
            });
        }

        TextInputLayout tilExtraPinView = findViewById(R.id.tilExtraPinView);
        tilExtraPinView.setEndIconMode(TextInputLayout.END_ICON_CUSTOM);
        tilExtraPinView.setEndIconDrawable(android.R.drawable.ic_menu_view);
        tilExtraPinView.setEndIconContentDescription("Hold to show extra PIN");
        View extraPinEndIcon = tilExtraPinView.findViewById(com.google.android.material.R.id.text_input_end_icon);
        if (extraPinEndIcon != null) {
            extraPinEndIcon.setOnTouchListener((v, event) -> {
                if (extraPinValue.isEmpty() || "(none)".contentEquals(txtExtraPin.getText())) return false;
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    txtExtraPin.setText(extraPinValue);
                    return true;
                } else if (event.getAction() == MotionEvent.ACTION_UP
                        || event.getAction() == MotionEvent.ACTION_CANCEL) {
                    txtExtraPin.setText(MASK);
                    return true;
                }
                return false;
            });
        }

        TextInputLayout tilChallengeAnswerView = findViewById(R.id.tilChallengeAnswerView);
        tilChallengeAnswerView.setEndIconMode(TextInputLayout.END_ICON_CUSTOM);
        tilChallengeAnswerView.setEndIconDrawable(android.R.drawable.ic_menu_view);
        tilChallengeAnswerView.setEndIconContentDescription("Hold to show challenge answer");
        View challengeAnswerEndIcon = tilChallengeAnswerView.findViewById(com.google.android.material.R.id.text_input_end_icon);
        if (challengeAnswerEndIcon != null) {
            challengeAnswerEndIcon.setOnTouchListener((v, event) -> {
                if (challengeAnswerValue.isEmpty() || "(none)".contentEquals(txtChallengeAnswer.getText())) return false;
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    txtChallengeAnswer.setText(challengeAnswerValue);
                    return true;
                } else if (event.getAction() == MotionEvent.ACTION_UP
                        || event.getAction() == MotionEvent.ACTION_CANCEL) {
                    txtChallengeAnswer.setText(MASK);
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

        // Reveal on hold is now handled via the Material end icons.

        btnEdit.setOnClickListener(v -> {
            Intent editIntent = new Intent(ViewPasswordActivity.this, EditPasswordActivity.class);
            editIntent.putExtra("id", getIntent().getLongExtra("id", -1));
            editIntent.putExtra("site", getIntent().getStringExtra("site"));
            editIntent.putExtra("username", getIntent().getStringExtra("username"));
            editIntent.putExtra("password", getIntent().getStringExtra("password"));
            editIntent.putExtra("extraPin", getIntent().getStringExtra("extraPin"));
            editIntent.putExtra("challengeQuestion", getIntent().getStringExtra("challengeQuestion"));
            editIntent.putExtra("challengeAnswer", getIntent().getStringExtra("challengeAnswer"));
            startActivity(editIntent);
            finish();
        });
    }
}
