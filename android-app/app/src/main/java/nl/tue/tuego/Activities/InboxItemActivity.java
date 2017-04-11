package nl.tue.tuego.Activities;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import nl.tue.tuego.Models.CoordinateModel;
import nl.tue.tuego.R;
import nl.tue.tuego.WebAPI.APICall;
import nl.tue.tuego.WebAPI.APICallback;

public class InboxItemActivity extends AppCompatActivity implements LocationListener {
    // amount of time to guess a picture in days
    public static final int GUESS_TIME = 13;
    static final int REQUEST_GPS_PERMISSION = 1;
    LocationManager mLocationManager;
    private String UUID;
    private String Uploader;
    private String UploadTime;
    private String Finder;
    TextView TVAuthor;
    TextView TVPoints;
    TextView TVTimeTaken;
    TextView TVTimeRemaining;
    ImageView IVImage;
    Button BGuess;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inbox_item);

        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        // look up all needed views
        TVAuthor = (TextView) findViewById(R.id.itemAuthor);
        TVPoints = (TextView) findViewById(R.id.itemPoints);
        TVTimeTaken = (TextView) findViewById(R.id.itemTimeTaken);
        TVTimeRemaining = (TextView) findViewById(R.id.itemTimeRemaining);
        IVImage = (ImageView) findViewById(R.id.itemImage);
        BGuess = (Button) findViewById(R.id.buttonGuess);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        // adds the toolbar to the activity
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        // getting all the ImageModel data
        UUID = getIntent().getExtras().getString("UUID");
        Uploader = getIntent().getExtras().getString("Uploader");
        UploadTime = getIntent().getExtras().getString("UploadTime");
        Finder = getIntent().getExtras().getString("Finder");

        // Setting the text of the TextViews
        final Resources res = getResources();
        TVAuthor.setText(res.getString(R.string.dataAuthor, Uploader));
        TVPoints.setText(res.getString(R.string.dataPoints, "10"));
        TVTimeTaken.setText(res.getString(R.string.dataTimeTaken, UploadTime));

        // Setting text of time remaining
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.ENGLISH);
        Date uploadDate = null;
        try {
            uploadDate = format.parse(UploadTime);
        } catch (ParseException e) {
            Log.e("InboxItemActivity", "Date cannot be parsed");
            e.printStackTrace();
        }

        if (uploadDate != null) {
            Timer timer = new Timer();
            final Date finalUploadDate = uploadDate;
            timer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    Date currentDate = new Date();
                    long diffDate = finalUploadDate.getTime() + TimeUnit.DAYS.toMillis(GUESS_TIME) - currentDate.getTime();
                    final long diffSeconds = diffDate / 1000 % 60;
                    final long diffMinutes = diffDate / (60 * 1000) % 60;
                    final long diffHours = diffDate / (60 * 60 * 1000) % 24;
                    final long diffDays = diffDate / (1000 * 60 * 60 * 24);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            TVTimeRemaining.setText(res.getString(R.string.dataTimeRemaining, diffDays, diffHours, diffMinutes, diffSeconds));
                        }
                    });
                }
            }, 0, 1000);
        } else {
            TVTimeRemaining.setText("Error getting date");
        }

        // set event listeners
        BGuess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onGuessButtonClick(v);
            }
        });

        loadImage();
    }

    // get the image of the picture
    private void loadImage() {
        // TODO: get the image from the client
    }

    // method that is called when the GUESS LOCATION button is pressed
    // only other users than the poster can do this
    public void onGuessButtonClick(View v) {
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

    private void getLocation() {
        Log.d("InboxItemActivity", "Getting location");
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            Location location = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (location != null && location.getTime() > Calendar.getInstance().getTimeInMillis() - 2000) {
                guessLocation(location);
            }
            else {
                mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
            }
        } else {
            Log.wtf("InboxItemActivity", "Permission changed abruptly?");
        }
    }

    private void guessLocation(Location location) {
        Log.d("InboxItemActivity", "Guessing location: " + location.getLatitude()
                + " and " + location.getLongitude());

        CoordinateModel coords = new CoordinateModel();
        coords.Latitude = String.valueOf(location.getLatitude());
        coords.Longitude = String.valueOf(location.getLongitude());

        // Determine what happens when the call is done
        APICallback callback = new APICallback() {
            @Override
            public void done(String res) {
                Log.d("InboxItemActivity", "Correct guess!");
                // TODO: Show a correct pop-up

                onBackPressed();
            }

            @Override
            public void fail(String res) {
                Log.d("InboxItemActivity", "Incorrect guess!");
                // TODO: Show an incorrect pop-up
            }
        };

        // Perform the API call
        APICall call = new APICall("POST", "/images/" + UUID, coords, callback);
        call.setAPIKey(APICall.ReadToken(this));
        call.execute();
    }

    // method that is called when the DELETE PICTURE button is pressed
    // only the poster of the picture can do this
    public void deletePic(View v) {
        // TODO: delete the picture
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

    // events that trigger when a certain button is pressed on the action bar
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // when the up button is pressed
            case android.R.id.home:
                onBackPressed();
                return true;

            // all other cases
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
