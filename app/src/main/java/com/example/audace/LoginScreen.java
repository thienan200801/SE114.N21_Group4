package com.example.audace;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

public class LoginScreen extends AppCompatActivity {
    private LinearLayout loginLayout;
    private LinearLayout signupLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        loginLayout = findViewById(R.id.login_layout);
        signupLayout = findViewById(R.id.signup_layout);

    }
    @SuppressLint("NonConstantResourceId")
    public void onButtonClick(View view) {
        switch (view.getId()) {
            case R.id.btnLogin:
                loginLayout.setVisibility(View.VISIBLE);
                signupLayout.setVisibility(View.GONE);
                break;
            case R.id.btnSignUp:
                loginLayout.setVisibility(View.GONE);
                signupLayout.setVisibility(View.VISIBLE);
                break;
        }
    }


}