package nl.tue.tuego.Activities;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.Closeable;
import java.io.FileOutputStream;
import java.io.IOException;

import nl.tue.tuego.Fragments.InternetDialogFragment;
import nl.tue.tuego.Models.UserModel;
import nl.tue.tuego.Models.UsernameModel;
import nl.tue.tuego.Storage.Storage;
import nl.tue.tuego.WebAPI.APICall;
import nl.tue.tuego.WebAPI.APICallback;
import nl.tue.tuego.Models.LoginModel;
import nl.tue.tuego.R;
import nl.tue.tuego.Models.TokenModel;

public class LoginActivity extends AppCompatActivity {
    private EditText ETEmail, ETPassword;
    private Button BLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Look up all needed views
        ETEmail = (EditText) findViewById(R.id.editTextLoginUsername);
        ETPassword = (EditText) findViewById(R.id.editTextLoginPassword);
        BLogin = (Button) findViewById(R.id.buttonLogin);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        // adds the toolbar to the activity
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        // set event listener
        BLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login(v);
            }
        });

        if (AppStatus.getInstance(this).isOnline()) {

            Log.v("Home", "#### Internet OK");

        } else {
            InternetDialogFragment newFragment = InternetDialogFragment.newInstance(
                    R.string.internetWarning);
            newFragment.show(getFragmentManager(), "dialog");
        }
    }

    // method that is called when BLogin is pressed
    public void login(View v) {
        // Debug print
        Log.d("LoginActivity","Starting logging in");

        // Fill out the model
        LoginModel log = new LoginModel();
        log.Email = ETEmail.getText().toString();
        log.Password = ETPassword.getText().toString();

        // Determine what happens when the call is done
        APICallback callback = new APICallback() {
            @Override
            public void done(String res) {
                Gson gson = new Gson();
                TokenModel tokenModel = gson.fromJson(res, TokenModel.class);
                Storage.setToken(tokenModel.Token);
                Storage.setUuid(tokenModel.UUID);

                // Write the token into local storage
                FileOutputStream fos = null;
                try {
                    fos = openFileOutput("Token", Context.MODE_PRIVATE);
                    fos.write(tokenModel.Token.getBytes());
                    Log.d("LoginActivity", "Token saved");
                    fos = openFileOutput("UUID", Context.MODE_PRIVATE);
                    fos.write(tokenModel.UUID.getBytes());
                    Log.d("LoginActivity", "UUID saved");

                } catch (IOException e) {
                    Toast.makeText(LoginActivity.this, "Error saving ID", Toast.LENGTH_SHORT).show();
                    Log.d("LoginActivity", "Error saving token");
                } finally {
                    closeStream(fos);
                }

                // Also get the username of the current user
                getUsername();
            }

            @Override
            public void fail(String res) {
                Toast.makeText(LoginActivity.this, "Invalid email address or password", Toast.LENGTH_SHORT).show();
                Log.d("LoginActivity", "Logging in failed, check parameters");
            }
        };

        // Perform the API call to login
        new APICall("POST", "/login", log, callback, this).execute();
    }

    // Gets the username of the current user and saves it
    private void getUsername() {
        // Determine what happens when the call is done
        APICallback callback = new APICallback() {
            @Override
            public void done(String res) {
                Gson gson = new Gson();
                UserModel userModel = gson.fromJson(res, UserModel.class);
                Storage.setUsername(userModel.Name);

                // Write the username into local storage
                FileOutputStream fos = null;
                try {
                    fos = openFileOutput("Username", Context.MODE_PRIVATE);
                    fos.write(userModel.Name.getBytes());
                    Log.d("LoginActivity", "Username saved");

                    Intent intent = new Intent(LoginActivity.this, InboxActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                } catch (IOException e) {
                    Toast.makeText(LoginActivity.this, "Error saving ID", Toast.LENGTH_SHORT).show();
                    Log.d("LoginActivity", "Error saving username");
                } finally {
                    closeStream(fos);
                }
            }

            @Override
            public void fail(String res) {
                Log.d("LoginActivity", "Failed to get username");
            }
        };

        // Perform the API Call to get the username
        String token = Storage.getToken(LoginActivity.this);
        Log.d("LoginActivity", "Token = " + token);
        new APICall("GET", "/users/" + Storage.getUuid(this), null, callback, LoginActivity.this).execute();
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

            // all other cases
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // Closes a stream correctly
    private void closeStream (Closeable stream) {
        try {
            if (stream != null) {
                stream.close();
            }
        } catch (IOException e) {
            Log.d("Stream", "Stream already closed");
        }
    }
}
