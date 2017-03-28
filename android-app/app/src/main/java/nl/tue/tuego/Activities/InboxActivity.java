package nl.tue.tuego.Activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
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

import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import nl.tue.tuego.Models.ImageModel;
import nl.tue.tuego.Adapters.InboxAdapter;
import nl.tue.tuego.R;

public class InboxActivity extends AppCompatActivity {
    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int REQUEST_TAKE_PHOTO = 1;
    static final int REQUEST_PERMISSIONS = 1;
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
                onCameraButtonClick();
            }
        });

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();

        refresh();
    }

    // method that is called when the camera button is pressed
    public void onCameraButtonClick() {
        List<String> permissionsNeeded = new ArrayList<>();

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            permissionsNeeded.add(Manifest.permission.CAMERA);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            permissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }

        for (int i = 0; i < permissionsNeeded.size(); i++) {
            // check if permissions denied earlier
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    permissionsNeeded.get(i))) {
                // TODO: add an explanation on different thread
            }
        }

        if (permissionsNeeded.size() > 0) {
        // ask for permissions
        ActivityCompat.requestPermissions(this,
                permissionsNeeded.toArray(new String[permissionsNeeded.size()]),
                REQUEST_PERMISSIONS);

        } else {
            // all permissions granted so go to camera
            toCamera();
        }
    }

    // go to the camera
    private void toCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // create the file where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
                Log.d("File", "Path created (" + mCurrentPhotoPath + ")");
            } catch (IOException e) {
                // error occurred while creating the file
                // TODO: handle error
                Log.d("File", "Failed to create path. Error: " + e);
            }

            // continue only if the file was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(getApplicationContext(),
                        "nl.tue.tuego.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                Log.d("Camera", "Starting camera");
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
                imageFileName,  // prefix
                ".jpg",        // suffix
                storageDir      // directory
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    // called when returning from permission pop-up
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PERMISSIONS:
                // if request is canceled
                if (grantResults.length == 0) {
                    Log.d("Permissions", "Canceled");
                } else {
                    boolean permissionsGranted = true;

                    for (int i = 0; i < permissions.length; i++) {
                        if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                            Log.d("Permissions", permissions[i] + " granted");
                        } else {
                            permissionsGranted = false;
                            Log.d("Permissions", permissions[i] + " denied");
                        }
                    }

                    if (permissionsGranted) {
                        toCamera();
                        Log.d("Permissions", "All permissions granted");
                    } else {
                        Toast.makeText(this, "Some permissions denied", Toast.LENGTH_SHORT).show();
                        Log.d("Permissions", "Some permissions denied");
                    }
                }
                break;

            // other permission cases should go here

            default: {
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
                Log.d("Permissions", "DEFAULT");
            }
        }
    }

    // called when returning from the camera API
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("Camera", "Finished");
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Log.d("Camera", "Picture OK");
            Intent intent = new Intent(this, PostPictureActivity.class);
            intent.putExtra("Path", mCurrentPhotoPath);
            startActivity(intent);
        } else {
            Log.d("Camera", "Picture not OK");
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
                Intent intent1 = new Intent(this, LeaderboardActivity.class);
                startActivity(intent1);
                return true;

            case R.id.actionLogout:
                FileOutputStream fos = null;
                try {
                    String token = "";
                    fos = openFileOutput(LoginActivity.TOKEN_FILE_NAME, Context.MODE_PRIVATE);
                    fos.write(token.getBytes());
                    Log.d("Log out", "Token deleted");
                } catch (IOException e) {
                    Log.d("Log out", "No token found");
                } finally {
                    closeStream(fos);
                }

                Intent intent2 = new Intent(this, LoginActivity.class);
                startActivity(intent2);
                finish();
                return true;

            case android.R.id.home:
                onBackPressed();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void closeStream (Closeable stream) {
        try {
            if (stream != null) {
                stream.close();
            }
        } catch (IOException e) {
            Log.d("Stream", "Stream already closed");
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
