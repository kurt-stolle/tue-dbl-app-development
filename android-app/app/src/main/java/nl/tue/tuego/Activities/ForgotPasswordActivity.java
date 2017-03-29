package nl.tue.tuego.Activities;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import nl.tue.tuego.R;

public class ForgotPasswordActivity extends AppCompatActivity {
    TextView TVResendCode;
    EditText ETCode;
    Button BNext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        // look up all needed views
        TVResendCode = (TextView) findViewById(R.id.textViewForgotPasswordResendCode);
        ETCode = (EditText) findViewById(R.id.editTextForgotPasswordCode);
        BNext = (Button) findViewById(R.id.buttonForgotPasswordNext);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        // adds the toolbar to the activity
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        // set event listeners
        TVResendCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resendCode(v);
            }
        });

        BNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmCode(v);
            }
        });
    }

    // method called when bNext is pressed
    public void confirmCode(View v) {
        // TODO: write code to check if the code is correct

        Intent intent = new Intent(this, ChangePasswordActivity.class);
        startActivity(intent);
        finish();
    }

    // method called when TVResendCode is pressed
    public void resendCode(View v) {
        // TODO: write code to resend code
    }

    // events that trigger when a certain button is pressed on the action bar
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
