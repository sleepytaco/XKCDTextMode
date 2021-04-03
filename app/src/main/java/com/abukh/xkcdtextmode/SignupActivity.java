package com.abukh.xkcdtextmode;

import android.content.Intent;
import android.os.Bundle;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class SignupActivity extends AppCompatActivity {

    public static final String TAG = "SignupActivity";

    private EditText etUsername;
    private EditText etPassword;
    private Button btnSignUp;
    private EditText etEmail;
    private Button btBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        etUsername = findViewById(R.id.etSIUsername);
        etPassword = findViewById(R.id.etSIPassword);
        btnSignUp = findViewById(R.id.btSignUp);
        etEmail = findViewById(R.id.etSIEmail);
        btBack = findViewById(R.id.btBack);

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signUserUp();
            }
        });

        btBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    private void signUserUp() {
        // Create the ParseUser
        ParseUser user = new ParseUser();

        // Set core properties
        user.setUsername(etUsername.getText().toString());
        user.setPassword(etPassword.getText().toString());
        user.setEmail(etEmail.getText().toString());

        // Invoke signUpInBackground
        user.signUpInBackground(new SignUpCallback() {
            public void done(ParseException e) {
                if (e == null) {
                    Toast.makeText(SignupActivity.this, "Account created!", Toast.LENGTH_SHORT).show();
                    //logUserIn();
                } else {
                    // Sign up didn't succeed. Look at the ParseException
                    // to figure out what went wrong
                    Log.e(TAG, "Sign in failed", e);
                    Toast.makeText(SignupActivity.this, "Uh-oh! Had trouble signing up!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void logUserIn() {
        ParseUser.logInInBackground(etUsername.getText().toString(), etPassword.getText().toString(), new LogInCallback() {
            public void done(ParseUser user, ParseException e) {
                if (user != null) {
                    // Hooray! The user is logged in.
                    Intent i = new Intent(SignupActivity.this, MainActivity.class);
                    startActivity(i);
                    Toast.makeText(SignupActivity.this, "Welcome, " + etUsername.getText().toString(), Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    // Signup failed. Look at the ParseException to see what happened.
                    Log.e(TAG, "Log in failed :(", e);
                }
            }
        });
    }}