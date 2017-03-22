package nl.tue.tuego;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class InboxActivity extends AppCompatActivity {
    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int REQUEST_TAKE_PHOTO = 1;
    static final int REQUEST_WRITE_EXTERNAL_STORAGE = 3;
    private String mCurrentPhotoPath;
    private LinearLayout BCamera;
    private ListView LVFeed;
    private List<ImageModel> images;
    private InboxAdapter adapter;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inbox);

        // initialize images
        images = new ArrayList<>();

        // look up all needed views
        BCamera = (LinearLayout) findViewById(R.id.inboxButton);
        LVFeed = (ListView) findViewById(R.id.inboxListViewFeed);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        // adds the toolbar to the activity
        setSupportActionBar(toolbar);

        // set event listeners
        BCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCamera();
            }
        });

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();

        refresh();
    }

    // method that is called when the camera button is pressed
    public void openCamera() {
        // if there is permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {
            makePicture();
        } else {
            // check if permissions denied earlier
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                // TODO: add an explanation on different thread

            } else {
                // ask for permissions
//                ActivityCompat.requestPermissions(this,
//                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
//                        REQUEST_WRITE_EXTERNAL_STORAGE);
            }

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_WRITE_EXTERNAL_STORAGE);
        }
    }

    // go to the camera
    private void makePicture() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // create the file where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException e) {
                // error occurred while creating the file
                // TODO: handle error
                Log.d("File", "Failed to create path. Error: " + e);
            }

            // continue only if the file was successfully created
            if (photoFile != null) {
                Log.d("File", "Path recognized");
                Uri photoURI = FileProvider.getUriForFile(getApplicationContext(),
                        "nl.tue.tuego.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    // returns a unique file name for a new picture
    private File createImageFile() throws IOException {
        // Create an image file name
        Log.d("File", "Creating unique file path");
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".png",        /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        Log.d("File", "Path created (" + mCurrentPhotoPath + ")");
        return image;
    }

    // called when returning from permission pop-up
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_WRITE_EXTERNAL_STORAGE: {
                Log.d("Permissions", Integer.toString(REQUEST_WRITE_EXTERNAL_STORAGE));
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! we now go to the camera
                    makePicture();
                } else {
                    // permission denied, boo! TODO: handle this "error".
                }
                return;
            }

            default: {
                Log.d("Permissions", "DEFAULT");
                return;
            }
            // other 'case' lines should go here if used
        }
    }

    // called when returning from the camera API
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("Camera", "RETURNED");
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Log.d("Camera", "PICTURE OK");
            Intent intent = new Intent(this, PostPictureActivity.class);
            intent.putExtra("Path", mCurrentPhotoPath);
            startActivity(intent);
        }
    }

    // method that is called when the screen is pulled down to refresh items
    public void refresh() {
        // TODO: refresh the items on the screen
        images.add(new ImageModel("a", "b", "c", "d"));
        images.add(new ImageModel("a", "b", "c", "d"));
        images.add(new ImageModel("a", "b", "c", "d"));
        images.add(new ImageModel("a", "b", "c", "d"));
        images.add(new ImageModel("a", "b", "c", "d"));
        images.add(new ImageModel("a", "b", "c", "d"));
        images.add(new ImageModel("a", "b", "c", "d"));
        images.add(new ImageModel("a", "b", "c", "d"));
        images.add(new ImageModel("a", "b", "c", "d"));
        images.add(new ImageModel("a", "b", "c", "d"));
        images.add(new ImageModel("a", "b", "c", "d"));
        images.add(new ImageModel("a", "b", "c", "d"));

        adapter = new InboxAdapter(this, images);
        LVFeed.setAdapter(adapter);
    }

    // method that is called when an item is clicked, also gives an item as argument
    public void onItemClick(ImageModel model) {
        // TODO: view the item that is pressed
    }

    // creates the options in the menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.action_bar, menu);
        return true;
    }

    // events that trigger when a certain button is pressed on the action bar
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.actionLeaderboard:
                Toast.makeText(getApplicationContext(), "Hello!", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(this, LeaderboardActivity.class);
                startActivity(intent);
                return true;

            case R.id.actionLogout:
                // TODO: add code to make logout work
                return true;

            case android.R.id.home:
                onBackPressed();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Inbox Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }
}
