package com.example.app.tuego;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.app.tuego.R;

import static com.example.app.tuego.R.string.login;

public class LoginActivity extends AppCompatActivity {
    TextView TVForgotPassword;
    EditText ETName, ETPassword;
    Button BLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // look up all needed views
        TVForgotPassword = (TextView) findViewById(R.id.textViewLoginToForgotPassword);
        ETName = (EditText) findViewById(R.id.editTextLoginUsername);
        ETPassword = (EditText) findViewById(R.id.editTextLoginPassword);
        BLogin = (Button) findViewById(R.id.buttonLogin);

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

        Intent intent = new Intent(this, InboxActivity.class); // should register
        startActivity(intent);
        finish();
    }

    // method that is called when the TVForgotPassword is clicked
    public void forgotPassword(View v) {
        Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
        startActivity(intent);
    }
}
