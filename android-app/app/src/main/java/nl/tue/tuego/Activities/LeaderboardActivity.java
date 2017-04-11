package nl.tue.tuego.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ListView;

import nl.tue.tuego.Adapters.LeaderboardAdapter;
import nl.tue.tuego.R;
import nl.tue.tuego.Models.UserModel;

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
        getBestUsers();
        for (int i=0; i<4; i++) {
            UserModel user = new UserModel();
            user.Email = "test@example.com";
            user.Name = "BGWD69";
            user.Points = 15;
            user.UUID = "uuid-123021931-wadjowaw";

            bestUsers[i] = user;
        }
            adapter = new LeaderboardAdapter(this, bestUsers);
            LVLeaderboard.setAdapter(adapter);
    }

    // method that will display the leaderboard
    private void displayLeaderboard() {
        // TODO: delete existing leaderboard and create a new one to display
    }

    // method that will place the four users with the most Points in bestUsers
    public void getBestUsers() {
        // TODO: sort all users by Points
        // TODO: Place the four users with the most points in array

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
