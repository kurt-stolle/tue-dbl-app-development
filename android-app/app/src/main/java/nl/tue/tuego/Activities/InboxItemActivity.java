package nl.tue.tuego.Activities;

import android.content.res.Resources;
import android.media.Image;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import nl.tue.tuego.R;

public class InboxItemActivity extends AppCompatActivity {
    // amount of time to guess a picture in days
    public static final int GUESS_TIME = 30;
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

        // look up all needed views
        Button BGuess = (Button) findViewById(R.id.buttonGuess);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        // adds the toolbar to the activity
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        // look up all needed views
        TVAuthor = (TextView) findViewById(R.id.itemAuthor);
        TVPoints = (TextView) findViewById(R.id.itemPoints);
        TVTimeTaken = (TextView) findViewById(R.id.itemTimeTaken);
        TVTimeRemaining = (TextView) findViewById(R.id.itemTimeRemaining);
        IVImage = (ImageView) findViewById(R.id.itemImage);

        // getting the UUID of the picture
        UUID = getIntent().getExtras().getString("UUID");
        Uploader = getIntent().getExtras().getString("Uploader");
        UploadTime = getIntent().getExtras().getString("UploadTime");
        Finder = getIntent().getExtras().getString("Finder");

        // Setting the text of the textviews
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
                guessLocation(v);
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
    public void guessLocation(View v) {
        // TODO: get location, send it to the client and react appropriately
    }

    // method that is called when the DELETE PICTURE button is pressed
    // only the poster of the picture can do this
    public void deletePic(View v) {
        // TODO: delete the picture
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
}
