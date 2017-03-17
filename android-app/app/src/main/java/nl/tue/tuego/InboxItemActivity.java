package nl.tue.tuego;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

public class InboxItemActivity extends AppCompatActivity {
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

        // set event listeners
        BGuess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                guessLocation(v);
            }
        });
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
