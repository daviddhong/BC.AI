package android.m.bankingsolution.Camera;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.m.bankingsolution.MainActivity;
import android.m.bankingsolution.R;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

public class ImageActivity extends AppCompatActivity {

    private ImageView imageView;
    private EditText name_of_picture;
    private Button upload_button;

    private ArrayList<String> pathArray;
    private int arrayposition;

    private FirebaseAuth auth;
    private StorageReference spaceRef;
    private Bitmap image;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);

        FirebaseStorage storage = FirebaseStorage.getInstance();

        name_of_picture = findViewById(R.id.name_of_b_card);
        upload_button = findViewById(R.id.upload_image_button);
        imageView = (ImageView) findViewById(R.id.image_view);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            image = (Bitmap) bundle.get("image");
            if (image != null) {
                imageView.setImageBitmap(image);
            }
        }


        upload_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadImage();
            }
        });


    }


    private void uploadImage() {
        FirebaseStorage storage = FirebaseStorage.getInstance();

        // [START upload_create_reference]
        // Create a storage reference from our app
        StorageReference storageRef = storage.getReference();

        // Create a reference to "mountains.jpg"
        StorageReference mountainsRef = storageRef.child("mountains.jpg");

        // Create a reference to 'images/mountains.jpg'
        StorageReference mountainImagesRef = storageRef.child("images/mountains.jpg");

        // While the file names are the same, the references point to different files
//        mountainsRef.getName().equals(mountainImagesRef.getName());    // true
//        mountainsRef.getPath().equals(mountainImagesRef.getPath());    // false
        // [END upload_create_reference]

        // [START upload_memory]
        // Get the data from an ImageView as bytes
        imageView.setDrawingCacheEnabled(true);
        imageView.buildDrawingCache();

        Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = mountainImagesRef.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
                Toast.makeText(ImageActivity.this, "FAIL", Toast.LENGTH_LONG).show();
                goToMain();
            }

        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                // ...
                Toast.makeText(ImageActivity.this, "Success", Toast.LENGTH_LONG).show();
                goToMain();
            }


        });
    }

    private void goToMain() {
        Intent intent = new Intent(ImageActivity.this, MainActivity.class);
        startActivity(intent);

    }

}
