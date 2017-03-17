package nl.tue.tuego;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ListView;

public class LeaderboardActivity extends AppCompatActivity {
    private final int AMOUNT_BEST_USERS = 4; // set amount of users in the leaderboard
    UserModel[] bestUsers = new UserModel[AMOUNT_BEST_USERS];
    ListView LVLeaderboard;
    LeaderboardAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);

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
        // TODO: it will update the list of users
    }

    // method that will display the leaderboard
    private void displayLeaderboard() {
        // TODO: delete existing leaderboard and create a new one to display
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
