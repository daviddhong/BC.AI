package android.m.bankingsolution.Camera;

import android.graphics.Bitmap;
import android.m.bankingsolution.R;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class ImageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);

        ImageView imageView = (ImageView) findViewById(R.id.image_view);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            Bitmap image = (Bitmap) bundle.get("image");
            if (image != null) {
                imageView.setImageBitmap(image);
            }
        }
    }
}
