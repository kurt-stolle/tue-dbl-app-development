package nl.tue.tuego;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class LoginActivity extends AppCompatActivity {
    private TextView TVForgotPassword;
    private EditText ETName, ETPassword;
    private Button BLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // look up all needed views
        TVForgotPassword = (TextView) findViewById(R.id.textViewLoginToForgotPassword);
        ETName = (EditText) findViewById(R.id.editTextLoginUsername);
        ETPassword = (EditText) findViewById(R.id.editTextLoginPassword);
        BLogin = (Button) findViewById(R.id.buttonLogin);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        // adds the toolbar to the activity
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        // set event listeners
        TVForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                forgotPassword(v);
            }
        });

        BLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login(v);
            }
        });
    }

    // method that is called when BLogin is pressed
    public void login(View v) {
        // TODO: make login check

        Intent intent = new Intent(this, InboxActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    // method that is called when TVForgotPassword is clicked
    public void forgotPassword(View v) {
        Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
        startActivity(intent);
    }

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
}
