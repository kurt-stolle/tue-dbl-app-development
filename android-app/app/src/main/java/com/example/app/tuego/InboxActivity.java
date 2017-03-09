package com.example.app.tuego;

import android.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.app.tuego.R;

public class InboxActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inbox);

        // setActionBar();
        // or
        // Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        // setSupportActionBar(myToolbar);
    }

    // method that is called when the camera button is pressed
    public void openCamera() {
        // TODO: go to a screen to create a picture
    }

    // method that is called when the leaderboard button is pressed
    public void openLeaderboard() {
        // TODO: go to the leaderboard activity
    }

    // method that is called when the screen is pulled down to refresh items
    public void refresh() {
        // TODO: refresh the items on the screen
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

    // This is just a draft
    private void setActionBar() {
        ActionBar actionbar = getActionBar();
        actionbar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionbar.setCustomView(R.layout.actionbar);

        ImageView leaderboard = (ImageView) findViewById(R.id.actionBarLeaderboard);
        ImageView logout = (ImageView) findViewById(R.id.actionBarLogout);

        leaderboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Leaderboard", Toast.LENGTH_SHORT).show();
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Logout", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
