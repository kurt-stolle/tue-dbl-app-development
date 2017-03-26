package nl.tue.tuego;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

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
            System.err.println("Password not the same!");

            // TODO: Display an error message
            return;
        }

        // Debug print
        Log.d("RegisterActivity","Strarting registration");

        // Fill out the model
        RegistrationModel reg = new RegistrationModel(nameText, emailText, passwordText);

        // Determine what happens when the call is done
        APICallback callback = new APICallback() {
            @Override
            public void done(String res) {
                Intent intent = new Intent(RegisterActivity.this, InboxActivity.class); // should register
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }

            @Override
            public void fail(String res) {
                System.err.println("Registration failed. Check parameters");
            }
        };

        // Perform the API call
        new APICall("POST", "/register", reg, callback).execute();

        // Initialize api
//        WebAPI api = new WebAPI(); // Use Object for expected return; we aren't expecting a return value
//        String res;
//        try {
//            res = api.Call("POST", "/register", reg);
        //String res = new CallAPI("POST", "/register", reg).execute();
        // Log.d(res);
//        } catch (APIError e){
//            System.err.println("Can not register, error: " + e);
//            // TODO: Notify the user that they made some mistake
//        }

        // Registration is done - move to next view

    }

    // called when TVToLogin is clicked
    private void toLogin(View v) {
        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
        startActivity(intent);
    }

    // Not sure who has created this, what is its purpose?
    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Register Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://nl.tue.tuego/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Register Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://nl.tue.tuego/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }
}

