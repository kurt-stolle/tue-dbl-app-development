package com.example.app.tuego;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

public class LoadActivity extends AppCompatActivity {

    ImageView logo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load);

        logo = (ImageView) findViewById(R.id.Logo);
        // TODO: make stuff load
        // For now make the logo clickable to continue to next slide

        assert logo != null;
        logo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoadActivity.this, RegisterActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
