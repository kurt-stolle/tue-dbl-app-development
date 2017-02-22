package com.example.app.tuego;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.app.tuego.R;
import com.google.gson.Gson;


public class RegisterActivity extends AppCompatActivity {
    TextView login;
    EditText inputName;
    EditText inputEmail;
    EditText inputPassword;
    EditText inputPasswordVerify;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Fetch elements from view
        login = (TextView) findViewById(R.id.textViewtoLogin);
        inputName = (EditText) findViewById(R.id.textFieldName);
        inputEmail = (EditText) findViewById(R.id.textFieldEmail);
        inputPassword = (EditText) findViewById(R.id.editTextPasswordLogin);
        inputPasswordVerify = (EditText) findViewById(R.id.editTextPasswordVerifyLogin);

        // Verify results
        assert login != null;

        // Events
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    // method called when the REGISTER button is pressed
    public void register(View v) {
        // Perform the registration
        String nameText = inputName.getText().toString();
        String emailText = inputEmail.getText().toString();
        String passwordText = inputPassword.getText().toString();
        String passwordVerifyText = inputPasswordVerify.getText().toString();

        if (!passwordText.equals(passwordVerifyText)) {
            System.err.println("Password not the same!");

            // TODO: Display an error message
            return;
        }

        RegistrationModel reg = new RegistrationModel(nameText, emailText, passwordText);

        // Initialize api
        WebAPI api = new WebAPI(); // Use Object for expected return; we aren't expecting a return value
        String res;
        try {
            res = api.Call("POST", "/register", reg);
        } catch (APIError e){
            System.err.println("Can not register, error: " + e);
            // TODO: Notify the user that they made some mistake
        }

        // Registration is done - move to next view
        // TODO: Auto login
        Intent intent = new Intent(this, InboxActivity.class); // should register
        startActivity(intent);
        finish();
    }
}

