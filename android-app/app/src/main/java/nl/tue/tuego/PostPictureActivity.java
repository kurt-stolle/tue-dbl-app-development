package nl.tue.tuego;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class PostPictureActivity extends AppCompatActivity {
    Button BPost, BDiscard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_picture);

        // look up all needed views
        BPost = (Button) findViewById(R.id.buttonPost);
        BDiscard = (Button) findViewById(R.id.buttonDiscard);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        // adds the toolbar to the activity
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        // set event listeners
        BPost.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                postPic(v);
            }
        });

        BDiscard.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                discardPic(v);
            }
        });
    }

    // method that is called when DISCARD button is pressed
    public void discardPic(View v) {
        // TODO: discard the picture
    }

    // method that is called when POST button is pressed
    public void postPic(View v) {
        // TODO: post the picture
    }
}
