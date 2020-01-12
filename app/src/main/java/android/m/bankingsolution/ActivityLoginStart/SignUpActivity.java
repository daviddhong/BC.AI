package android.m.bankingsolution.ActivityLoginStart;

import android.content.Intent;
import android.m.bankingsolution.R;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class SignUpActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private EditText email,pw, name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        initialize();
        createAccount();


    }

    private void initialize() {
        mAuth = FirebaseAuth.getInstance();
        name = findViewById(R.id. name_edittext);
        email = findViewById(R.id.email_edit_text);
        pw = findViewById(R.id.password_edit_text);
    }

    private void createAccount() {
        RelativeLayout button = findViewById(R.id.make_account_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignUpActivity.this, InitialStartingActivity.class);
                startActivity(intent);
            }
        });
    }

}
