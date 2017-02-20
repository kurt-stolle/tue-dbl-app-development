package com.example.app.tuego;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.app.tuego.R;

import static com.example.app.tuego.R.string.login;

public class LoginActivity extends AppCompatActivity {
    TextView forgotPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        forgotPassword = (TextView) findViewById(R.id.textViewForgotPassword);

        assert forgotPassword != null;
        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
                startActivity(intent);
            }
        });
    }

    public void login(View v) {
        // TODO: make login check

        Intent intent = new Intent(this, InboxActivity.class); // should register
        startActivity(intent);
        finish();
    }
}
