package android.m.bankingsolution.ActivityLoginStart;


import android.content.Intent;
import android.m.bankingsolution.MainActivity;
import android.m.bankingsolution.R;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;

public class InitialStartingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_initial);


        RelativeLayout button = findViewById(R.id.button_login);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(InitialStartingActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

}
