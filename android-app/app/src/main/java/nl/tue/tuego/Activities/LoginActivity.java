package nl.tue.tuego.Activities;

import android.content.Context;
import android.content.Intent;
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

import nl.tue.tuego.WebAPI.APICall;
import nl.tue.tuego.WebAPI.APICallback;
import nl.tue.tuego.Models.LoginModel;
import nl.tue.tuego.R;
import nl.tue.tuego.Models.TokenModel;

public class LoginActivity extends AppCompatActivity {
    private TextView TVForgotPassword;
    private EditText ETEmail, ETPassword;
    private Button BLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // look up all needed views
        // TVForgotPassword = (TextView) findViewById(R.id.textViewLoginToForgotPassword);
        ETEmail = (EditText) findViewById(R.id.editTextLoginUsername);
        ETPassword = (EditText) findViewById(R.id.editTextLoginPassword);
        BLogin = (Button) findViewById(R.id.buttonLogin);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        // adds the toolbar to the activity
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        // set event listeners
//        TVForgotPassword.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                forgotPassword(v);
//            }
//        });

        BLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login(v);
            }
        });
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
                FileOutputStream fos = null;

                try {
                    fos = openFileOutput("token_file", Context.MODE_PRIVATE);
                    fos.write(tokenModel.Token.getBytes());

                    Log.d("LoginActivity", "Token saved");
                    Intent intent = new Intent(LoginActivity.this, InboxActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                } catch (IOException e) {
                    Toast.makeText(LoginActivity.this, "Error saving ID", Toast.LENGTH_SHORT).show();
                    Log.d("LoginActivity", "Error saving token");
                } finally {
                    closeStream(fos);
                }
            }

            @Override
            public void fail(String res) {
                Toast.makeText(LoginActivity.this, "Account not recognized", Toast.LENGTH_SHORT).show();
                Log.d("LoginActivity", "Logging in failed, check parameters");
            }
        };

        // Perform the API call
        new APICall("POST", "/login", log, callback).execute();
    }

//    // method that is called when TVForgotPassword is clicked
//    public void forgotPassword(View v) {
//        Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
//        startActivity(intent);
//    }

    // events that trigger when a certain button is pressed on the action bar
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // when the up button is pressed
            case android.R.id.home:
                onBackPressed();
                return true;

            // all other cases
            default:
                return super.onOptionsItemSelected(item);
        }
    }

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
