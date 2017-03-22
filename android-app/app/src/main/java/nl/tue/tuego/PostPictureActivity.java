package nl.tue.tuego;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;

import java.io.IOException;

public class PostPictureActivity extends AppCompatActivity {
    Button BPost, BDiscard;
    ImageView IVImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_picture);

        // look up all needed views
        BPost = (Button) findViewById(R.id.buttonPost);
        BDiscard = (Button) findViewById(R.id.buttonDiscard);
        IVImage = (ImageView) findViewById(R.id.postPostPictureImage);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        // adds the toolbar to the activity
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        // getting the picture taken from the camera
        String filePath = getIntent().getExtras().getString("Path");
        try {
            IVImage.setImageBitmap(rotateBitmapOrientation(filePath));
        } catch (IOException e) {
            e.printStackTrace();
        }

        // set event listeners
        BPost.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                postPic(v);
            }
        });

        BDiscard.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                discardPic(v);
            }
        });
    }

    // rotates the picture correctly
    private Bitmap rotateBitmapOrientation(String filePath) throws IOException {
        ExifInterface ei = new ExifInterface(filePath);
        Bitmap bitmap = (BitmapFactory.decodeFile(filePath));
        int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_UNDEFINED);

        switch(orientation) {

            case ExifInterface.ORIENTATION_ROTATE_90:
                return rotateImage(bitmap, 90);

            case ExifInterface.ORIENTATION_ROTATE_180:
                return rotateImage(bitmap, 180);

            case ExifInterface.ORIENTATION_ROTATE_270:
                return rotateImage(bitmap, 270);

            case ExifInterface.ORIENTATION_NORMAL:

            default:
                return bitmap;
        }
    }

    // rotates image by angle
    private Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(),
                matrix, true);
    }

    // method that is called when DISCARD button is pressed
    public void discardPic(View v) {
        onBackPressed();
    }

    // method that is called when POST button is pressed
    public void postPic(View v) {
        // TODO: post the picture
    }

    // events that trigger when a certain button is pressed on the action bar
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
