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
        inputPassword = (EditText) findViewById(R.id.textFieldPassword);
        inputPasswordVerify = (EditText) findViewById(R.id.textFieldPassword2);

        // Verify results
        assert login != null;

        // Events
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Perform the registration
                RegistrationModel reg = new RegistrationModel();
                reg.Email = inputEmail.getText().toString();
                reg.Email = inputName.getText().toString();
                reg.Password = inputPassword.getText().toString();

                if (reg.Password != inputPasswordVerify.getText().toString()) {
                    // TODO: Display an error message

                    return;
                }

                // Initialize api
                WebAPI api = new WebAPI(); // Use Object for expected return; we aren't expecting a return value
                String res;
                try {
                    res = api.Call("POST", "/register", reg);
                } catch (APIError e){
                    // TODO: Notify the user that they made some mistake
                }

                // Registration is done - move to next view
                // TODO: Auto login
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
    }
}

