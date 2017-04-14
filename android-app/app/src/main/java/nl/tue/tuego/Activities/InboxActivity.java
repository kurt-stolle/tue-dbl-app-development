package nl.tue.tuego.Activities;

import android.Manifest;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;
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
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import nl.tue.tuego.Models.ManifestEntry;
import nl.tue.tuego.Models.PaginatedResponseModel;
import nl.tue.tuego.WebAPI.APICallback;
import nl.tue.tuego.Models.ImageModel;
import nl.tue.tuego.Adapters.InboxAdapter;
import nl.tue.tuego.R;
import nl.tue.tuego.WebAPI.APICall;

public class InboxActivity extends AppCompatActivity implements ListView.OnItemClickListener {
    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int REQUEST_TAKE_PHOTO = 1;
    static final int REQUEST_PERMISSIONS = 1;
    private String mCurrentPhotoPath;
    private FloatingActionButton BCamera;
    private ListView LVFeed;
    private List<ManifestEntry> images;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inbox);

        // initialize images
        images = new ArrayList<>();

        // look up all needed views
        BCamera = (FloatingActionButton) findViewById(R.id.inboxButton);
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
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            permissionsNeeded.add(Manifest.permission.ACCESS_FINE_LOCATION);
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
        Log.d("InboxActivity", "Refreshing...");
        // Determine what happens when the call is done
        APICallback callback = new APICallback() {
            public void done(String data) {
                // Parse JSON
                Gson gson = new Gson();
                JsonParser parser = new JsonParser();
                JsonObject resObject = (JsonObject) parser.parse(data);

                // If the resObject is a JSON array (i.e. it has entries)
                // then parse
                if (resObject.get("Data").isJsonArray()){
                    JsonArray resData = resObject.getAsJsonArray("Data");

                    // Iterate over array
                    for (int i = 0; i < resData.size(); i++) {
                        // Load image from JSON
                        ManifestEntry entry = gson.fromJson(resData.get(i), ManifestEntry.class);

                        // Add image to list
                        images.add(entry);

                        // Debug print
                        Log.d("InboxCallback", "Added new image to list, UUID: " + entry.Image.UUID);
                    }
                }


                InboxAdapter adapter = new InboxAdapter(InboxActivity.this, images);
                LVFeed.setAdapter(adapter);
            }

            public void fail(String data) {
                Toast.makeText(InboxActivity.this, "Failed to load data", Toast.LENGTH_LONG).show();
                // TODO: This log causes an error
                // Log.e("InboxCallback",data);
            }
        };

        // Perform the API call
        APICall call = new APICall("GET", "/images", null, callback);
        call.setAPIKey(APICall.ReadToken(InboxActivity.this));
        call.execute();
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

    // Events that trigger when a certain button is pressed on the action bar
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.actionLeaderboard:
                Intent intent1 = new Intent(this, LeaderboardActivity.class);
                startActivity(intent1);
                return true;

            // When the leaderboard button is pressed
            case R.id.actionLogout:
                FileOutputStream fos = null;
                try {
                    String token = "";
                    fos = openFileOutput("token_file", Context.MODE_PRIVATE);
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

            // When the up button is pressed
            case android.R.id.home:
                Intent parentIntent = NavUtils.getParentActivityIntent(this);
                parentIntent.setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(parentIntent);
                finish();
                return true;

            // All other cases
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

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
}
