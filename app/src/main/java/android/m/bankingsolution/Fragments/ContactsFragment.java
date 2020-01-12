package android.m.bankingsolution.Fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.m.bankingsolution.ActivityLoginStart.InitialStartingActivity;
import android.m.bankingsolution.Camera.ImageActivity;
import android.m.bankingsolution.R;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class ContactsFragment extends Fragment {
    private View myCardView;
    private Button SignOut;
    private FirebaseAuth auth;
    private ImageView mybcimage;
    private String currentUID;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        myCardView = inflater.inflate(R.layout.fragment_contacts, container, false);

        initiateFields();
        retrieveMyBusinessCard();
        signout();
        return myCardView;

    }


    private void initiateFields() {
        SignOut = myCardView.findViewById(R.id.SignOutButton);
        auth = FirebaseAuth.getInstance();
        currentUID = auth.getUid();
        mybcimage = myCardView.findViewById(R.id.mybusinesscardimageview);
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

    private void retrieveMyBusinessCard() {
        FirebaseStorage storage = FirebaseStorage.getInstance();

        // [START upload_create_reference]
        // Create a storage reference from our app
        StorageReference storageRef = storage.getReference();

        StorageReference islandRef = storageRef.child("images/myBusinessCard" + currentUID + ".jpg");

        final long ONE_MEGABYTE = 1024 * 1024;
        islandRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                // Data for "images/island.jpg" is returns, use this as needed
//                Toast.makeText(getContext(), "Here is your Business Card", Toast.LENGTH_LONG).show();

                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0 , bytes.length);
                mybcimage.setImageBitmap(bitmap);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
                Toast.makeText(getActivity(), "Upload Your Business Card", Toast.LENGTH_LONG).show();

            }
        });
    }

}
