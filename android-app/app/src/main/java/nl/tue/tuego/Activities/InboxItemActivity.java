package nl.tue.tuego.Activities;

import android.media.Image;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import nl.tue.tuego.R;

public class InboxItemActivity extends AppCompatActivity {
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

        // setting the text of the textviews
        TVAuthor.setText(Uploader);
        TVPoints.setText(String.format("Points: %s", R.string.tenPoints));
        TVTimeTaken.setText(UploadTime);
        Timer timer = new Timer();
        Calendar c = Calendar.getInstance();
        timer.scheduleAtFixedRate(new TimerTask() {
            int i = 0;

            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        updateTime(String.valueOf(i));
                    }
                });

                i++;
            }
        }, 0, 1000);

        // set event listeners
        BGuess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                guessLocation(v);
            }
        });

        refresh();
    }

    private void updateTime(String time) {
        TVTimeRemaining.setText(String.valueOf(time));
    }

    // get the image of the picture
    private void refresh() {
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
