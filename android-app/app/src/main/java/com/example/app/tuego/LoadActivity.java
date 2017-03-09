package com.example.app.tuego;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

public class LoadActivity extends AppCompatActivity {
    ImageView IVLogo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load);

        IVLogo = (ImageView) findViewById(R.id.Logo);

        // For now make the logo clickable to continue to next slide
        assert IVLogo != null;

        IVLogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoadActivity.this, RegisterActivity.class);
                startActivity(intent);
                finish();
            }
        });

        load();
        showWarning();
    }

    // method which checks several things before continuing
    private void load() {
        // TODO: make stuff load like autologin
    }

    // method that displays a warning if needed
    private void showWarning() {
        // TODO: display a warning if needed
    }
}
