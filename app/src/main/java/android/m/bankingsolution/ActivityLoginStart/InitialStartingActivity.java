package android.m.bankingsolution.ActivityLoginStart;


import android.content.Intent;
import android.m.bankingsolution.MainActivity;
import android.m.bankingsolution.R;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class InitialStartingActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private EditText loginEmail, loginPassword;
    private RelativeLayout logginButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_initial);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        loginEmail = findViewById(R.id.email_edit_text);
        loginPassword = findViewById(R.id.password_edit_text);
        logginButton = findViewById(R.id.button_login);

        logginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logInWithEmailAndPassword();
            }
        });

        RelativeLayout signupbutton = findViewById(R.id.signup_button);
        signupbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(InitialStartingActivity.this, SignUpActivity.class);
                startActivity(intent);
            }
        });
    }


    // Log In when login button is pressed
    private void logInWithEmailAndPassword() {
        logginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get strings from email and password editText fields
                String email = loginEmail.getText().toString();
                String password = loginPassword.getText().toString();
                if (email.isEmpty()) {
                    Toast.makeText(InitialStartingActivity.this, "Please enter Your Email", Toast.LENGTH_LONG).show();
                } else if (password.isEmpty()) {
                    Toast.makeText(InitialStartingActivity.this, "Please enter Your Password", Toast.LENGTH_LONG).show();
                } else {
                    mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                if (mAuth.getCurrentUser().isEmailVerified()) {
                                    SendToMainActivity();
                                } else {
                                    Toast.makeText(InitialStartingActivity.this, "please verify email", Toast.LENGTH_LONG).show();
                                }
                            } else {
                                Toast.makeText(InitialStartingActivity.this, task.getException().toString(), Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
            }
        });
    }

    private void SendToMainActivity() {
        Intent intent = new Intent(InitialStartingActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);

    }

    //checks if user is already logged in when app is started
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        AutomaticLoginFunctionIfUserIsAlreadySignedIn();
    }

    // Must check if email is also verified && is logged in already.
    private void AutomaticLoginFunctionIfUserIsAlreadySignedIn() {
        if (currentUser != null) {
            boolean emailVerified = currentUser.isEmailVerified();
            if (emailVerified) {
                SendToMainActivity();
            }
        }
    }


}
