package nl.tue.tuego.Activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.ExifInterface;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import nl.tue.tuego.Storage.Storage;
import nl.tue.tuego.WebAPI.APICallback;
import nl.tue.tuego.WebAPI.APIPostPicture;
import nl.tue.tuego.R;

public class PostPictureActivity extends AppCompatActivity implements LocationListener {
    static final int REQUEST_GPS_PERMISSION = 1;

    LocationManager mLocationManager;
    private TextView TVTimeTaken, TVPoints;
    private ImageView IVImage;
    private Button BPost, BDiscard;
    Bitmap picture;
    String filePath;
    Location location;
    private boolean isPosting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_picture);

        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        // Look up all needed views
        TVTimeTaken = (TextView) findViewById(R.id.postPictureTimeTaken);
        TVPoints = (TextView) findViewById(R.id.postPicturePoints);
        BPost = (Button) findViewById(R.id.buttonPost);
        BDiscard = (Button) findViewById(R.id.buttonDiscard);
        IVImage = (ImageView) findViewById(R.id.postPictureImage);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        // Adds the toolbar to the activity
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        // Set text of TVPoints
        final Resources res = getResources();
        TVPoints.setText(res.getString(R.string.dataPoints, "15"));

        // Set text of TVTimeTaken
        Date currentDate = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.ENGLISH);
        String currentDateString = format.format(currentDate);
        TVTimeTaken.setText(res.getString(R.string.dataTimeTaken, currentDateString));

        // Getting the picture taken from the camera
        filePath = getIntent().getExtras().getString("Path");
        picture = null;
        try {
            picture = rotateBitmapOrientation(filePath);
            IVImage.setImageBitmap(picture);
        } catch (IOException e) {
            Log.d("PostPictureActivity", "Picture not found");
        }

        isPosting = false;

        // Getting the location
        location = null;
        getLocation();

        // Set event listeners
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

    // Rotates the picture correctly
    private Bitmap rotateBitmapOrientation(String filePath) throws IOException {
        ExifInterface ei = new ExifInterface(filePath);
        Bitmap bitmap = (BitmapFactory.decodeFile(filePath));
        int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_UNDEFINED);

        switch (orientation) {

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

    // Rotates image by angle
    private Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(),
                matrix, true);
    }

    // Method that is called when DISCARD button is pressed
    public void discardPic(View v) {
        onBackPressed();
    }

    // Method that is called when POST button is pressed
    public void postPic(View v) {
        // Check if the location has been determined
        if (!isPosting) {
            if (location != null) {
                isPosting = true;

                // Determine what happens when the call is done
                APICallback callback = new APICallback() {
                    @Override
                    public void done(String res) {
                        Toast.makeText(PostPictureActivity.this, "Picture posted", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(PostPictureActivity.this, InboxActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                        isPosting = false;
                    }

                    @Override
                    public void fail(String res) {
                        Toast.makeText(PostPictureActivity.this, "You cannot upload more than five pictures", Toast.LENGTH_SHORT).show();
                        Log.d("PostPictureActivity", "Picture failed to upload");
                        isPosting = false;
                    }
                };

                // Setup params
                Log.d("Picture", "Picture width:" + picture.getWidth());
                Log.d("Picture", "Picture height" + picture.getHeight());
                Map<String, String> params = new HashMap<>(0);
                params.put("Latitude", String.valueOf(location.getLatitude()));
                params.put("Longitude", String.valueOf(location.getLongitude()));

                // Perform the API call
                new APIPostPicture(filePath, picture, Storage.getToken(this), params, callback).execute();
            } else {
                Toast.makeText(this, "Retry posting in a moment", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Retrieves the location by using the location manager
    private void getLocation() {
        Log.d("PostPictureActivity", "Getting location");
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            Log.d("PostPictureActivity", "GPS permission was granted");
            Location newLocation = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (newLocation != null && newLocation.getTime() > Calendar.getInstance().getTimeInMillis() - 2000) {
                this.location = newLocation;
                Log.d("PostPictureActivity", "Set location");
            } else {
                mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
                mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
                Log.d("PostPictureActivity", "Requesting location updates");
            }
        } else {
            Log.wtf("PostPictureActivity", "Permission changed abruptly?");
        }
    }

    // Events that trigger when a certain button is pressed on the action bar
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // When the up button is pressed
            case android.R.id.home:
                onBackPressed();
                return true;

            // All other cases
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // Method that is called by the location manager whenever the location has changed
    public void onLocationChanged(Location newLocation) {
        Log.d("PostPictureActivity", "New location received");
        if (newLocation != null) {
            Log.d("PostPictureActivity", "New location: latitude " + newLocation.getLatitude() + ", longitude:" + newLocation.getLongitude());
            mLocationManager.removeUpdates(this);
            this.location = newLocation;
        } else {
            Log.e("PostPictureActivity", "New location is null");
        }
    }

    // Required functions of LocationListener
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    public void onProviderEnabled(String provider) {
    }

    public void onProviderDisabled(String provider) {
    }
}
