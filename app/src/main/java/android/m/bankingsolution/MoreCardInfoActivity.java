package android.m.bankingsolution;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

public class MoreCardInfoActivity extends AppCompatActivity {
    private RelativeLayout detailedcardbackbutton,emailcardbutton;
    private Button messagecardbutton;
    private String phonenumber;

    private TelephonyManager mTelephonyManager;
    private MyPhoneCallListener mListener;
    private static final int MY_PERMISSIONS_REQUEST_CALL_PHONE = 1;
    public static final String TAG = MoreCardInfoActivity.class.getSimpleName();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more_card_info);
        createATelephonyManager();

        detailedcardbackbutton = findViewById(R.id.detailedcard_back_button);
        emailcardbutton = findViewById(R.id.emailthecardd);
        phonenumber = "7788737920";

        detailedcardbackbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MoreCardInfoActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        emailcardbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MoreCardInfoActivity.this, MainActivity.class);
                startActivity(intent);
                Toast.makeText(MoreCardInfoActivity.this, "Send Email", Toast.LENGTH_LONG).show();
            }
        });

    }







    // TELEPHONE
    private void createATelephonyManager() {
        mTelephonyManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        if (isTelephonyEnabled()) {
//            Log.d(TAG, "Telephony is enabled");
            // Check for phone permission.
            checkForPhonePermission();
            // Register the PhoneStateListener to monitor phone activity.
            mListener = new MyPhoneCallListener();
            mTelephonyManager.listen(mListener, PhoneStateListener.LISTEN_CALL_STATE);
        } else {
//            Toast.makeText(this, "TELEPHONY NOT ENABLED!", Toast.LENGTH_LONG).show();
//            Log.d(TAG, "TELEPHONY NOT ENABLED!");
            // Disable the call button.
            disableCallButton();
        }
    }

    public void callNumber(View view) {
        // Use format with "tel:" and phone number to create phoneNumber.
        String phoneNumber = String.format("tel: %s", phonenumber);
        // Log the concatenated phone number for dialing.
        Log.d(TAG, "Phone Status: DIALING: " + phoneNumber);
        Toast.makeText(this, "Dialing: " + phoneNumber, Toast.LENGTH_LONG).show();
        // Create the intent.
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        // Set the data for the intent as the phone number.
        callIntent.setData(Uri.parse(phoneNumber));
        // If package resolves to an app, send intent.
        if (callIntent.resolveActivity(getPackageManager()) != null) {
            checkForPhonePermission();
            startActivity(callIntent);
        } else {
            Log.e(TAG, "Can't resolve app for ACTION_CALL Intent.");
        }
    }

    private boolean isTelephonyEnabled() {
        if (mTelephonyManager != null) {
            if (mTelephonyManager.getSimState() == TelephonyManager.SIM_STATE_READY) {
                return true;
            }
        }
        return false;
    }

    private void disableCallButton() {
        Toast.makeText(this, "Phone calling disabled", Toast.LENGTH_LONG).show();
    }

    private void checkForPhonePermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE)
                != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "PERMISSION NOT GRANTED!");
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, MY_PERMISSIONS_REQUEST_CALL_PHONE);
        } else {
            // Permission already granted. Enable the call button.
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_CALL_PHONE: {
                if (permissions[0].equalsIgnoreCase(Manifest.permission.CALL_PHONE)
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission was granted
                } else {
                    // Permission denied.
                    Log.d(TAG, "Failure to obtain permission!");
//                    Toast.makeText(this, "Failure to obtain permission!",
//                            Toast.LENGTH_LONG).show();
                    // Disable the call button.
                    disableCallButton();
                }
            }
        }
    }

    // Inner class
    private class MyPhoneCallListener extends PhoneStateListener {
        private boolean returningFromOffHook = false;

        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            // Define a string for the message to use in a toast.
            String message = "Phone Status: ";
            switch (state) {
                case TelephonyManager.CALL_STATE_RINGING:
                    // Incoming call is ringing (not used for outgoing call).
                    message = message + "RINGING, number: " + incomingNumber;
//                    Toast.makeText(MoreCardInfoActivity.this, message, Toast.LENGTH_SHORT).show();
                    Log.i(TAG, message);
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK:
                    // Phone call is active -- off the hook.
                    message = message + "OFFHOOK";
//                    Toast.makeText(MoreCardInfoActivity.this, message, Toast.LENGTH_SHORT).show();
                    Log.i(TAG, message);
                    returningFromOffHook = true;
                    break;
                case TelephonyManager.CALL_STATE_IDLE:
                    // Phone is idle before and after phone call.
                    // If running on version older than 19 (KitKat), restart activity when phone
                    // call ends.
                    message = message + "IDLE";
//                    Toast.makeText(MoreCardInfoActivity.this, message, Toast.LENGTH_SHORT).show();
                    Log.i(TAG, message);
                    if (returningFromOffHook) {
                        // No need to do anything if >= version KitKat.
                        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
                            Log.i(TAG, "Restarting app");
                            // Restart the app.
                            Intent intent = getPackageManager()
                                    .getLaunchIntentForPackage(getApplicationContext().getPackageName());
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                        }
                    }
                    break;
                default:
                    // Must be an error. Raise an exception or just log it.
                    message = message + "Phone off";
//                    Toast.makeText(MoreCardInfoActivity.this, message,
//                            Toast.LENGTH_SHORT).show();
                    Log.i(TAG, message);
                    break;
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (isTelephonyEnabled()) {
            mTelephonyManager.listen(mListener,
                    PhoneStateListener.LISTEN_NONE);
        }
    }

    public void smsSendMessage(View view) {
//        TextView RiderPhoneNumber = (TextView) findViewById(R.id.phone_number_data);

        // Use format with "smsto:" and phone number to create smsNumber.
        String smsNumber = String.format("smsto: %s", phonenumber);

        // Find the sms_message view.
//        EditText smsEditText = (EditText) findViewById(R.id.text_message);
        // Get the text of the SMS message.
//        String sms = smsEditText.getText().toString();

        // Create the intent.
        Intent smsIntent = new Intent(Intent.ACTION_SENDTO);
        // Set the data for the intent as the phone number.
        smsIntent.setData(Uri.parse(smsNumber));
        // Add the message (sms) with the key ("sms_body")
//        smsIntent.putExtra("sms_body", sms);

        // If package resolves (target app installed), send intent.
        if (smsIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(smsIntent);
        } else {
            Log.e(TAG, "Can't resolve app for ACTION_SENDTO Intent");
        }
    }











}
