package com.ashishgangaramani.dashboardapp;

import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.walshfernandes.dashboardapp.R;

public class LoginActivity extends AppCompatActivity {

    TextInputLayout usernameWrapper, passwordWrapper;
    TextInputEditText username, password;
    Button loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

    }
    public void initializeComponents() {
        usernameWrapper = findViewById(R.id.username_wrapper);
        passwordWrapper = findViewById(R.id.password_wrapper);
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        loginButton = findViewById(R.id.login_button);
        loginButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        handleLogin();
                    }
                }
        );
    }

    private void handleLogin() {

    }

}
