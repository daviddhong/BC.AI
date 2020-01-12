package android.m.bankingsolution.Fragments;

import android.content.Intent;
import android.m.bankingsolution.ActivityLoginStart.InitialStartingActivity;
import android.m.bankingsolution.R;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;

public class ContactsFragment extends Fragment {
    private View myCardView;
    private Button SignOut;
    private FirebaseAuth auth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        myCardView = inflater.inflate(R.layout.fragment_contacts, container, false);

        initiateFields();
        signout();
        return myCardView;

    }

    private void initiateFields() {
        SignOut = myCardView.findViewById(R.id.SignOutButton);
        auth = FirebaseAuth.getInstance();
    }

    private void signout() {
        SignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                auth.signOut();
                Intent intent = new Intent(getActivity(), InitialStartingActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });
    }


}
