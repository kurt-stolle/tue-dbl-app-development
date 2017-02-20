package com.example.app.tuego;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.example.app.tuego.R;

import static com.example.app.tuego.R.string.login;

public class ForgotPasswordActivity extends AppCompatActivity {
    EditText ETCode;
    TextView TVResendCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        TVResendCode = (TextView) findViewById(R.id.textViewResendCode);
        ETCode = (EditText) findViewById(R.id.editTextCode);

        assert TVResendCode != null;
        TVResendCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: write code to resend code
            }
        });
    }

    public void next(View v) {
        // TODO: write code to check if the code is correct

        Intent intent = new Intent(this, ChangePasswordActivity.class);
        startActivity(intent);
        finish();
    }
}
