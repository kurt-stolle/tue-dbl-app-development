package nl.tue.tuego.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

import nl.tue.tuego.Adapters.InboxAdapter;
import nl.tue.tuego.Adapters.LeaderboardAdapter;
import nl.tue.tuego.Models.LeaderboardEntry;
import nl.tue.tuego.Models.ManifestEntry;
import nl.tue.tuego.R;
import nl.tue.tuego.Models.UserModel;
import nl.tue.tuego.WebAPI.APICall;
import nl.tue.tuego.WebAPI.APICallback;

public class LeaderboardActivity extends AppCompatActivity {
    private final int AMOUNT_BEST_USERS = 4; // set amount of users in the leaderboard
    private List<LeaderboardEntry> entries;
    ListView LVLeaderboard;
    LeaderboardAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);

        entries = new ArrayList<>();

        // look up all needed views
        LVLeaderboard = (ListView) findViewById(R.id.leaderboardList);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        // adds the toolbar to the activity
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        refresh();
        displayLeaderboard();
    }

    // method that will refresh the best users
    public void refresh() {
        APICallback callback = new APICallback() {
            public void done(String data) {
                Log.d("LeaderboardCallback", data);
                // Parse JSON
                Gson gson = new Gson();
                JsonParser parser = new JsonParser();
                JsonArray resData = (JsonArray) parser.parse(data);

                // Iterate over array
                for (int i = 0; i < resData.size(); i++) {
                    // Load image from JSON
                    LeaderboardEntry entry = gson.fromJson(resData.get(i), LeaderboardEntry.class);

                    entry.position = i + 1;

                    // Add image to list
                    entries.add(entry);

                    // Debug print
                    Log.d("LeaderboardCallback", "Added new entry to list, Name: " + entry.Name);
                }

                LVLeaderboard.setAdapter(new LeaderboardAdapter(LeaderboardActivity.this, entries));
            }

            public void fail(String data) {
                Toast.makeText(LeaderboardActivity.this, "Failed to load data", Toast.LENGTH_LONG).show();
            }
        };

        (new APICall("GET","/leaderboard", null, callback)).execute();
    }

    // method that will display the leaderboard
    private void displayLeaderboard() {
        // TODO: delete existing leaderboard and create a new one to display
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
}
