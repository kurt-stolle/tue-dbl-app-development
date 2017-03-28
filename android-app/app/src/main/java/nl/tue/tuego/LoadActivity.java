package nl.tue.tuego;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

public class LoadActivity extends Activity {
    public static final String TOKEN_FILE_NAME = "token_file";
    public static final int TOKEN_LENGTH = 464;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load);
        load();
    }

    // method which checks several things before continuing
    private void load() {
        // check if the user is already logged in by checking if there is a token stored
        FileInputStream fis = null;
        BufferedReader bufferedReader = null;
        try {
            fis = openFileInput(TOKEN_FILE_NAME);
            InputStreamReader isr = new InputStreamReader(fis);
            bufferedReader = new BufferedReader(isr);

            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line);
            }

            // check if the length of the token is correct
            if (sb.length() != TOKEN_LENGTH) {
                // go to the register activity
                Log.d("LoadActivity", "Token has incorrect length");
                Intent intent = new Intent(this, RegisterActivity.class);
                startActivity(intent);
                finish();
            } else {
                // go to the inbox activity
                Log.d("LoadActivity", "Token is of correct length");
                Intent intent = new Intent(this, InboxActivity.class);
                startActivity(intent);
                finish();
            }
        } catch (FileNotFoundException e) {
            // go to the register activity
            Log.d("LoadActivity", "Token not found");
            Intent intent = new Intent(this, RegisterActivity.class);
            startActivity(intent);
            finish();
        } catch (IOException e) {
            // go to the register activity
            Log.d("LoadActivity", "Reading token failed");
            Intent intent = new Intent(this, RegisterActivity.class);
            startActivity(intent);
            finish();
        } finally {
            closeStream(fis);
            closeStream(bufferedReader);
        }
    }

    private void closeStream(Closeable stream) {
        try {
            if (stream != null) {
                stream.close();
            }
        } catch (IOException e) {
            Log.d("Stream", "Stream already closed");
        }
    }

//    // method that displays a warning if needed
//    private void showWarning() {
//        new GPSDialogFragment().show(getFragmentManager(), "dialog");
//    }
}
