package nl.tue.tuego;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class InboxItemActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inbox_item);
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
}
