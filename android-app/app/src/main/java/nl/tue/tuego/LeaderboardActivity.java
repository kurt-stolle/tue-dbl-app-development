package nl.tue.tuego;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

public class LeaderboardActivity extends AppCompatActivity {
    private final int AMOUNT_BEST_USERS = 4; // set amount of users in the leaderboard
    UserModel[] bestUsers = new UserModel[AMOUNT_BEST_USERS];
    ListView LVLeaderboard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);

        LVLeaderboard = (ListView) findViewById(R.id.leaderboardList);

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
}
