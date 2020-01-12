package android.m.bankingsolution.Camera;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.m.bankingsolution.MainActivity;
import android.m.bankingsolution.R;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.ml.common.FirebaseMLException;
import com.google.firebase.ml.common.modeldownload.FirebaseModelDownloadConditions;
import com.google.firebase.ml.common.modeldownload.FirebaseModelManager;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.automl.FirebaseAutoMLLocalModel;
import com.google.firebase.ml.vision.automl.FirebaseAutoMLRemoteModel;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.label.FirebaseVisionImageLabel;
import com.google.firebase.ml.vision.label.FirebaseVisionImageLabeler;
import com.google.firebase.ml.vision.label.FirebaseVisionOnDeviceAutoMLImageLabelerOptions;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ImageActivity extends AppCompatActivity {

    private ImageView imageView;
    private EditText name_of_picture;
    private Button upload_button, upload_others_button;

    private ArrayList<String> pathArray;
    private int arrayposition;

    private StorageReference spaceRef;
    private Bitmap image;
    private FirebaseAuth auth;

    private String currentUID;


    private Bitmap bitmap;

    private TextView textView;

    private DatabaseReference myCardRef, OtherCardRef, RootRef;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);

        imageView = (ImageView) findViewById(R.id.image_view);
        textView = (TextView) findViewById(R.id.text_view_ml);

        FirebaseStorage storage = FirebaseStorage.getInstance();
        auth = FirebaseAuth.getInstance();
        currentUID = auth.getUid();
        OtherCardRef = FirebaseDatabase.getInstance().getReference();
        myCardRef =FirebaseDatabase.getInstance().getReference().child("Users");
        OtherCardRef = FirebaseDatabase.getInstance().getReference().child("OthersBusinessCards");

        upload_others_button = findViewById(R.id.upload_others_image_button);
        upload_button = findViewById(R.id.upload_image_button);

//        imageView = (ImageView) findViewById(R.id.image_view);
//        Intent intent = getIntent();
//        String imageString = intent.getStringExtra("Image");
//        Uri uri = Uri.parse(imageString);
////        imageView.setImageURI(uri);
//        try {
//            bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
//            imageView.setImageBitmap(bitmap);
//        } catch (IOException e) {
//            // handles exception
//        }


        upload_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadMyCard();
            }
        });

        upload_others_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadOthersCard();
            }
        });

        ImageButton button = (ImageButton) findViewById(R.id.button_select);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pick_image();
            }
        });
    }

    private void detect(View v) {

        FirebaseVisionImage firebaseVisionImage = FirebaseVisionImage.fromBitmap(bitmap);
        FirebaseVisionTextRecognizer firebaseVisionTextRecognizer = FirebaseVision.getInstance().getOnDeviceTextRecognizer();
        firebaseVisionTextRecognizer.processImage(firebaseVisionImage)
                .addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {
                    @Override
                    public void onSuccess(FirebaseVisionText firebaseVisionText) {
                        process_text(firebaseVisionText);
                    }
                }).addOnFailureListener(
                new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        e.printStackTrace();
                    }
                });
    }

    private void process_text(FirebaseVisionText firebaseVisionText) {
        List<FirebaseVisionText.TextBlock> blocks = firebaseVisionText.getTextBlocks();
        if (blocks.size() == 0) {
            Toast.makeText(getApplicationContext(), "No text detected", Toast.LENGTH_SHORT).show();
            return;
        }
        else {
            String str = "";
            for (FirebaseVisionText.TextBlock block : firebaseVisionText.getTextBlocks()) {
                String text = block.getText();
                for (FirebaseVisionText.Line line : block.getLines()) {
                    String lineText = line.getText();
                    for (FirebaseVisionText.Element element : line.getElements()) {
                        String elementText = element.getText();
                        str += elementText + " ";
                        textView.setText(str);
                    }
                }
            }
        }
    }

    public void pick_image() {
        Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(i, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            Uri uri = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver()
                        , uri);
                imageView.setImageBitmap(bitmap);

                detect(imageView);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void uploadOthersCard() {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        StorageReference othersCardImageRef = storageRef.child("images").child("othersBusinessCard").child(currentUID + ".jpg");
        imageView.setDrawingCacheEnabled(true);
        imageView.buildDrawingCache();
        Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();
        UploadTask uploadTask = othersCardImageRef.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Toast.makeText(ImageActivity.this, "FAIL", Toast.LENGTH_LONG).show();
                goToMain();
            }

        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(ImageActivity.this, "Successful", Toast.LENGTH_LONG).show();
                String uniqueKEY = OtherCardRef.push().getKey();
                HashMap<String, Object> riderTicketKey = new HashMap<>();
                OtherCardRef.updateChildren(riderTicketKey);
                OtherCardRef = OtherCardRef.child(uniqueKEY);
                String currentUserID = auth.getCurrentUser().getUid();
                HashMap<String, Object> profileMap = new HashMap<>();
//                profileMap.put("name", name2);
//                profileMap.put("phone", phone2);
//                profileMap.put("email", email2);
                profileMap.put("uniqueKEY", uniqueKEY);
                profileMap.put("myUID", currentUID);
                OtherCardRef.updateChildren(profileMap);
                goToMain();
            }
        });
    }
    private void uploadMyCard() {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        StorageReference myCardImageRef = storageRef.child("images").child("myBusinessCard").child(currentUID + ".jpg");
        imageView.setDrawingCacheEnabled(true);
        imageView.buildDrawingCache();
        Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();
        UploadTask uploadTask = myCardImageRef.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Toast.makeText(ImageActivity.this, "FAIL", Toast.LENGTH_LONG).show();
                goToMain();
            }

        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(ImageActivity.this, "Successful", Toast.LENGTH_LONG).show();
                HashMap<String, String> profileMap = new HashMap<>();
//                profileMap.put("name", name2);
//                profileMap.put("phone", phone2);
//                profileMap.put("email", email2);
                myCardRef.child(currentUID).setValue(profileMap);
                goToMain();
            }


        });
    }

    private void goToMain() {
        Intent intent = new Intent(ImageActivity.this, MainActivity.class);
        startActivity(intent);
    }


    private void specifyFirebase() {
        final FirebaseAutoMLRemoteModel remoteModel =
                new FirebaseAutoMLRemoteModel.Builder("Business_202011213450").build();

        FirebaseModelDownloadConditions conditions = new FirebaseModelDownloadConditions.Builder()
                .requireWifi()
                .build();

        FirebaseModelManager.getInstance().download(remoteModel, conditions)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // Success.
                    }
                });

        final FirebaseAutoMLLocalModel localModel = new FirebaseAutoMLLocalModel.Builder()
                .setAssetFilePath("manifest.json")
                .build();

        FirebaseModelManager.getInstance().isModelDownloaded(remoteModel)
                .addOnSuccessListener(new OnSuccessListener<Boolean>() {
                    @Override
                    public void onSuccess(Boolean isDownloaded) {
                        FirebaseVisionOnDeviceAutoMLImageLabelerOptions.Builder optionsBuilder;
                        if (isDownloaded) {
                            optionsBuilder = new FirebaseVisionOnDeviceAutoMLImageLabelerOptions.Builder(remoteModel);
                        } else {
                            optionsBuilder = new FirebaseVisionOnDeviceAutoMLImageLabelerOptions.Builder(localModel);
                        }

                        FirebaseVisionOnDeviceAutoMLImageLabelerOptions options = optionsBuilder
                                .setConfidenceThreshold(0.5f).build();

                        FirebaseVisionImageLabeler labeler = null;
                        try {
                            labeler = FirebaseVision.getInstance().getOnDeviceAutoMLImageLabeler(options);
                        } catch (FirebaseMLException e) {
                            // Error. 
                        }

                        Bitmap bitmapML = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
                        FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(bitmapML);

                        labeler.processImage(image)
                                .addOnSuccessListener(new OnSuccessListener<List<FirebaseVisionImageLabel>>() {
                                    @Override
                                    public void onSuccess(List<FirebaseVisionImageLabel> labels) {
                                        // Task completed successfully
                                        // ...
                                        for (FirebaseVisionImageLabel label : labels) {
                                            String text = label.getText();
                                            float confidence = label.getConfidence();
                                        }
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        // Task failed with an exception
                                        // ...
                                    }
                                });
                    }
                });

    // Comment
    }

}
