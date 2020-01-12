package android.m.bankingsolution.ActivityLoginStart;

import android.content.Intent;
import android.m.bankingsolution.R;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;

public class SignUpActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

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
