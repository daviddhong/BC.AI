package android.m.bankingsolution.ActivityLoginStart;

import android.content.Intent;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


import java.util.HashMap;

public class SignUpActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private DatabaseReference RootRef;
    private RelativeLayout backbutton;

    private EditText email, pw, name;
    private String UserEmail, UserPw, UserName,currentuid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        initialize();
        backbuttonpressed();
        createAccount();
    }

    private void initialize() {
        mAuth = FirebaseAuth.getInstance();
        RootRef = FirebaseDatabase.getInstance().getReference();
        currentUser = mAuth.getCurrentUser();

        backbutton = findViewById(R.id.back_button_signuppage);
        name = findViewById(R.id.name_edittext);
        email = findViewById(R.id.email_edit_text);
        pw = findViewById(R.id.password_edit_text);
    }

    private void backbuttonpressed(){
        backbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignUpActivity.this, InitialStartingActivity.class);
                startActivity(intent);
            }
        });
    }

    private void createAccount() {
        RelativeLayout button = findViewById(R.id.make_account_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserEmail = email.getText().toString();
                UserName = name.getText().toString();
                UserPw = pw.getText().toString();

                if(UserEmail.isEmpty() || UserName.isEmpty() || UserPw.isEmpty()){
                    Toast.makeText(SignUpActivity.this, "Please fill in all the information", Toast.LENGTH_LONG).show();
                } else {
                    createAccount_createUser_SendVerificationEmail();
                }
            }
        });
    }

    private void createAccount_createUser_SendVerificationEmail() {
        mAuth.createUserWithEmailAndPassword(UserEmail, UserPw).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    currentuid = currentUser.getUid();
                    CollectFirstLastNameEmailIntoRealTimeDatabase(UserName, UserEmail);
                    SendVerificationEmail();
                } else {
                    // if account is not made
                    Toast.makeText(SignUpActivity.this, task.getException().toString(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }


    private void CollectFirstLastNameEmailIntoRealTimeDatabase(String firstN, String userEmail) {
//        String currentUserID = mAuth.getCurrentUser().getUid();
        HashMap<String, String> profileMap = new HashMap<>();
        profileMap.put("uid", currentuid);
        profileMap.put("name", firstN);
        profileMap.put("email", userEmail);
        RootRef.child("Users").child(currentuid).setValue(profileMap);
    }

    private void SendVerificationEmail() {
        currentUser.sendEmailVerification()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Intent intent = new Intent(SignUpActivity.this, InitialStartingActivity.class);
                            startActivity(intent);
                            Toast.makeText(SignUpActivity.this,
                                    "Verification email sent to \n" + UserEmail, Toast.LENGTH_LONG).show();
                        } else {
                            Intent intent = new Intent(SignUpActivity.this, InitialStartingActivity.class);
                            startActivity(intent);
                            Toast.makeText(SignUpActivity.this,
                                    "Email not sent!" + task.getException().toString(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

}
