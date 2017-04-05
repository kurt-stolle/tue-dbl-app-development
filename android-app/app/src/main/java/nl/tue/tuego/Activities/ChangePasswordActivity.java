package nl.tue.tuego.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import nl.tue.tuego.R;

public class ChangePasswordActivity extends AppCompatActivity {
    EditText ETPassword, ETConfirmPassword;
    Button BNext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        // look up all needed views
        ETPassword = (EditText) findViewById(R.id.editTextChangePasswordPassword);
        ETConfirmPassword = (EditText) findViewById(R.id.editTextChangePasswordVerifyPassword);
        BNext = (Button) findViewById(R.id.buttonChangePasswordNext);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        // adds the toolbar to the activity
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        // set event listeners
        BNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changePassword(v);
            }
        });
    }

    // method which is called when BNext is pressed
    public void changePassword(View view) {
        // TODO: write code to check password and change password

        String s = getResources().getText(R.string.passwordChanged).toString();
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
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
