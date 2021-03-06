package nl.tue.tuego.Activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NavUtils;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.Calendar;

import nl.tue.tuego.Models.CoordinateModel;
import nl.tue.tuego.R;
import nl.tue.tuego.Storage.Storage;
import nl.tue.tuego.WebAPI.APICall;
import nl.tue.tuego.WebAPI.APICallback;

public class InboxItemActivity extends AppCompatActivity implements LocationListener {
    // amount of time to guess a picture in days
    public static final int GUESS_TIME = 13;
    static final int REQUEST_GPS_PERMISSION = 1;

    LocationManager mLocationManager;
    private String UUID, UploaderUUID, UploaderName, UploadTime;
    private TextView TVAuthor, TVTimeTaken;
    ImageView IVImage;
    Button BGuess;
    boolean isGuessing;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inbox_item);

        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        // Look up all needed views
        TVAuthor = (TextView) findViewById(R.id.itemAuthor);
        TVTimeTaken = (TextView) findViewById(R.id.itemTimeTaken);
//        TVTimeRemaining = (TextView) findViewById(R.id.itemTimeRemaining);
        IVImage = (ImageView) findViewById(R.id.itemImage);
        BGuess = (Button) findViewById(R.id.buttonGuess);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        // Adds the toolbar to the activity
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        // Getting all the ImageModel data
        UUID = getIntent().getExtras().getString("UUID");
        UploaderName = getIntent().getExtras().getString("UploaderName");
        UploaderUUID = getIntent().getExtras().getString("UploaderUUID");
        UploadTime = getIntent().getExtras().getString("UploadTime");

        // Setting the text of the TextViews
        final Resources res = getResources();
        TVAuthor.setText(res.getString(R.string.dataAuthor, UploaderName));
        TVTimeTaken.setText(res.getString(R.string.dataTimeTaken, UploadTime));

        // Setting text of TVTimeRemaining
//        setTimeRemaining(res);

        // Remove button if the user is the poster
        Log.d("InboxItemActivity", "Comparing " + Storage.getUuid(this) + " to " + UploaderUUID);
        if (Storage.getUuid(this).equals(UploaderUUID)) {
            ViewGroup viewGroup = (ViewGroup) BGuess.getParent();
            if (viewGroup != null) // For safety only as you are doing onClick
                viewGroup.removeView(BGuess);
        }

        // Load the image using the Picasso library
        Log.d("InboxItemActivity", "Loading image file at /images/" + UUID + "/image.jpg");
        Picasso.with(this).load(APICall.URL + "/images/" + UUID + "/image.jpg").into(IVImage);

        // Set event listeners
        BGuess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onGuessButtonClick(v);
            }
        });
    }

    // method that is called when the DELETE PICTURE button is pressed
    // only the poster of the picture can do this
    public void deletePic(View v) {
        // TODO: delete the picture
    }

    // method that is called when the GUESS LOCATION button is pressed
    // only other users than the poster can do this
    public void onGuessButtonClick(View v) {
        if (!isGuessing) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.ACCESS_FINE_LOCATION)) {
                    // TODO: give explanation
                } else {
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            REQUEST_GPS_PERMISSION);
                }
            } else {
                getLocation();
            }
        }
    }

    private void getLocation() {
        isGuessing = true;
        // TODO: make guessing noticable
        Log.d("InboxItemActivity", "Getting location");
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            Location location = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

            if (location != null && location.getTime() > Calendar.getInstance().getTimeInMillis() - 2000) {
                guessLocation(location);
            } else {
                mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
                mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
                Log.d("InboxItemActivity", "Requesting location updates");
            }
        } else {
            Log.wtf("InboxItemActivity", "Permission changed abruptly?");
        }
    }

    private void guessLocation(Location location) {
        Log.d("InboxItemActivity", "Guessing location: latitude: " + location.getLatitude()
                + ", and longitude: " + location.getLongitude());
        CoordinateModel coords = new CoordinateModel();
        coords.Latitude = location.getLatitude();
        coords.Longitude = location.getLongitude();

        // Determine what happens when the call is done
        APICallback callback = new APICallback() {
            @Override
            public void done(String res) {
                // TODO: Show a correct pop-up
                Log.d("InboxItemActivity", "Correct guess!");
                Toast.makeText(InboxItemActivity.this, "You guessed correctly and earned 15 studypoints!", Toast.LENGTH_LONG).show();
                onBackPressed();
                isGuessing = false;
            }

            @Override
            public void fail(String res) {
                // TODO: Show an incorrect pop-up
                Log.d("InboxItemActivity", "Incorrect guess!");
                Toast.makeText(InboxItemActivity.this, "Incorrect guess, try again.", Toast.LENGTH_LONG).show();
                isGuessing = false;
            }
        };

        // Perform the API call
        APICall call = new APICall("POST", "/images/" + UUID, coords, callback, this);
        call.execute();
    }

    // called when returning from permission pop-up
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_GPS_PERMISSION:
                // if request is canceled
                if (grantResults.length == 0) {
                    Log.d("Permissions", "Canceled");
                } else {
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        getLocation();
                        Log.d("Permissions", "GPS permission granted");
                    } else {
                        Toast.makeText(this, "GPS permission denied", Toast.LENGTH_SHORT).show();
                        Log.d("Permissions", "GPS permission denied");
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

    // Events that trigger when a certain button is pressed on the action bar
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
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

    public void onLocationChanged(Location location) {
        if (location != null) {
            Log.d("Location Changed", location.getLatitude() + " and " + location.getLongitude());
            mLocationManager.removeUpdates(this);
            guessLocation(location);
        }
    }

    // Required functions of LocationListener
    public void onStatusChanged(String provider, int status, Bundle extras) {}
    public void onProviderEnabled(String provider) {}
    public void onProviderDisabled(String provider) {}
}
