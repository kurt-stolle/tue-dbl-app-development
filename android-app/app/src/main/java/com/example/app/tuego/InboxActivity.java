package com.example.app.tuego;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.app.tuego.R;

public class InboxActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inbox);
    }

    // method that is called when the camera button is pressed
    public void makePicture() {
        // TODO: go to a screen to create a picture
    }

    // method that is called when the leaderboard button is pressed
    public void displayLeaderboard() {
        // TODO: go to the leaderboard activity
    }

    // method that is called when the screen is pulled down to refresh items
    public void refresh() {
        // TODO: refresh the items on the screen
    }

    // method that is called when an item is clicked, also gives an item as argument
    public void itemClick(ImageModel model) {
        // TODO: view the item that is pressed
    }

    // method that will be called several times to add items to the feed
    public void addItem() {
        // TODO: add a single item to the layout
    }
}
