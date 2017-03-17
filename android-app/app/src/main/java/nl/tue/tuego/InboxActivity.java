package nl.tue.tuego;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
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

import java.util.ArrayList;
import java.util.List;

public class InboxActivity extends AppCompatActivity {
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
        // TODO: go to a screen to create a picture which uses the camera API
    }

    // method that is called when the leaderboard button is pressed
    public void openLeaderboard() {
        // TODO: go to the leaderboard activity
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

    // method that will be called several times to add items to the feed
    public void addItem() {
        // TODO: add a single item to the layout
    }

    // method that is called when the logout button is pressed
    public void logout() {
        // TODO: make the user logout
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
