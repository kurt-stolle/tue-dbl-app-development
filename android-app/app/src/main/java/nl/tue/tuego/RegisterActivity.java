package nl.tue.tuego;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class RegisterActivity extends AppCompatActivity {
    private TextView TVToLogin;
    private EditText ETName, ETEmail, ETPassword, ETPasswordVerify;
    private Button BRegister;

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

        RegistrationModel reg = new RegistrationModel(nameText, emailText, passwordText);

        // Initialize api
//        WebAPI api = new WebAPI(); // Use Object for expected return; we aren't expecting a return value
//        String res;
//        try {
//            res = api.Call("POST", "/register", reg);
        new CallAPI("POST", "/register", reg).execute();

//        } catch (APIError e){
//            System.err.println("Can not register, error: " + e);
//            // TODO: Notify the user that they made some mistake
//        }

        // Registration is done - move to next view
        Intent intent = new Intent(this, InboxActivity.class); // should register
        startActivity(intent);
        finish();
    }

    // called when TVToLogin is clicked
    private void toLogin(View v) {
        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
        startActivity(intent);
    }
}

