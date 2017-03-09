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
}
