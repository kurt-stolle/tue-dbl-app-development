package nl.tue.tuego.Activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import nl.tue.tuego.WebAPI.APICall;
import nl.tue.tuego.WebAPI.APICallback;
import nl.tue.tuego.R;
import nl.tue.tuego.Models.RegistrationModel;

public class RegisterActivity extends AppCompatActivity {
    private TextView TVToLogin;
    private EditText ETName, ETEmail, ETPassword, ETPasswordVerify;
    private Button BRegister;

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // look up all needed views
        TVToLogin = (TextView) findViewById(R.id.textViewRegisterToLogin);
        ETName = (EditText) findViewById(R.id.editTextRegisterUsername);
        ETEmail = (EditText) findViewById(R.id.editTextRegisterEmail);
        ETPassword = (EditText) findViewById(R.id.editTextRegisterPassword);
        ETPasswordVerify = (EditText) findViewById(R.id.editTextRegisterPasswordVerify);
        BRegister = (Button) findViewById(R.id.buttonRegister);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        // adds the toolbar to the activity
        setSupportActionBar(toolbar);

        // set event listeners
        TVToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toLogin(v);
            }
        });
        BRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register(v);
            }
        });

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    // method called when bRegister is pressed
    private void register(View v) {
        // Perform the registration
        String nameText = ETName.getText().toString();
        String emailText = ETEmail.getText().toString();
        String passwordText = ETPassword.getText().toString();
        String passwordVerifyText = ETPasswordVerify.getText().toString();

        if (!passwordText.equals(passwordVerifyText)) {
            Toast.makeText(this, "Passwords are not the same", Toast.LENGTH_SHORT).show();
            Log.d("RegisterActivity", "Passwords not the same!");

            return;
        }

        // Debug print
        Log.d("RegisterActivity","Starting registration");

        // Fill out the model
        RegistrationModel reg = new RegistrationModel();
        reg.Name = nameText;
        reg.Email = emailText;
        reg.Password = passwordText;

        // Determine what happens when the call is done
        APICallback callback = new APICallback() {
            @Override
            public void done(String res) {
                Toast.makeText(RegisterActivity.this, "Account created", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }

            @Override
            public void fail(String res) {
                // TODO: make error more precise
                Toast.makeText(RegisterActivity.this, "Some data is incorrect, try again", Toast.LENGTH_SHORT).show();
                Log.d("RegisterActivity", "Registration failed. Check parameters");
            }
        };

        // Perform the API call
        new APICall("POST", "/register", reg, callback).execute();
    }

    // called when TVToLogin is clicked
    private void toLogin(View v) {
        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
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

            // All other cases
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}

