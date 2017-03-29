package nl.tue.tuego.Activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import nl.tue.tuego.Models.APICallback;
import nl.tue.tuego.WebAPI.APIPostPicture;
import nl.tue.tuego.R;

public class PostPictureActivity extends AppCompatActivity {
    Button BPost, BDiscard;
    ImageView IVImage;
    Bitmap picture;
    String filePath;

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
        filePath = getIntent().getExtras().getString("Path");
        picture = null;
        try {
            picture = rotateBitmapOrientation(filePath);
            IVImage.setImageBitmap(picture);
        } catch (IOException e) {
            Log.d("PostPictureActivity", "Picture not found");
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
        // Determine what happens when the call is done
        APICallback callback = new APICallback() {
            @Override
            public void done(String res) {
                Toast.makeText(PostPictureActivity.this, "Picture posted", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(PostPictureActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }

            @Override
            public void fail(String res) {
                Toast.makeText(PostPictureActivity.this, "Picture failed to upload", Toast.LENGTH_SHORT).show();
                Log.d("PostPictureActivity", "Picture failed to upload");
            }
        };

        // Perform the API call
        // Setup params
        Log.d("Picture", "Picture width:" + picture.getWidth());
        Log.d("Picture", "Picture height" + picture.getHeight());
        Map<String, String> params = new HashMap<>(0);
//        params.put("file", picture);

        // load the token to give to the post call
        FileInputStream fis = null;
        BufferedReader bufferedReader = null;
        try {
            fis = openFileInput(LoginActivity.TOKEN_FILE_NAME);
            InputStreamReader isr = new InputStreamReader(fis);
            bufferedReader = new BufferedReader(isr);

            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line);
            }
            String token = sb.toString();

            new APIPostPicture(filePath, picture, token, params, callback).execute();

        } catch (FileNotFoundException e) {
            // go to the register activity
            Toast.makeText(this, "Session expired", Toast.LENGTH_SHORT).show();
            Log.wtf("PostPictureActivity", "Token not found");
            Intent intent = new Intent(this, InboxActivity.class);
            startActivity(intent);
            finish();
        } catch (IOException e) {
            // go to the register activity
            Toast.makeText(this, "Session expired", Toast.LENGTH_SHORT).show();
            Log.wtf("LoadActivity", "Reading token failed");
            Intent intent = new Intent(this, RegisterActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        } finally {
            closeStream(fis);
            closeStream(bufferedReader);
        }
    }

    private void closeStream(Closeable stream) {
        try {
            if (stream != null) {
                stream.close();
            }
        } catch (IOException e) {
            Log.d("Stream", "Stream already closed");
        }
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
