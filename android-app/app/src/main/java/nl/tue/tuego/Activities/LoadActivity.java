package nl.tue.tuego.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

import nl.tue.tuego.R;
import nl.tue.tuego.Storage.Storage;
import nl.tue.tuego.WebAPI.APICall;

public class LoadActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load);
        load();
    }

    // Method which checks several things before continuing
    private void load() {
        boolean readingIsOk = true;

        String token = Storage.getToken(this);
        if (token.equals("")) {
            Log.d("LoadActivity", "Token does not exist");
            readingIsOk = false;
        } else {
            Log.d("LoadActivity", "Token found:" + token);
        }

        String uuid = Storage.getUuid(this);
        if (uuid.equals("")) {
            Log.d("LoadActivity", "UUID does not exist");
            readingIsOk = false;
        } else {
            Log.d("LoadActivity", "UUID found:" + uuid);
        }

        String username = Storage.getUsername(this);
        if (username.equals("")) {
            Log.d("LoadActivity", "Username does not exist");
            readingIsOk = false;
        } else {
            Log.d("LoadActivity", "Username found:" + username);
        }

        // All data has been found
        if (readingIsOk) {
            Log.d("LoadActivity", "All data loaded");
            // Go to the inbox activity
            Intent intent = new Intent(this, InboxActivity.class);
            startActivity(intent);
            finish();
        } else { // Some data could not be found
            Log.d("LoadActivity", "Some data not found");
            // Go to the register activity
            Intent intent = new Intent(this, RegisterActivity.class);
            startActivity(intent);
            finish();
        }
    }
}
