package com.example.salesrecordapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class MainActivity extends AppCompatActivity {

    TextInputLayout email_TIL,password_TIL;
    TextInputEditText email_TIET,password_TIET;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        email_TIET = findViewById(R.id.email_TIET);
        password_TIET = findViewById(R.id.password_TIET);
        email_TIL = findViewById(R.id.email_TIL);
        password_TIL = findViewById(R.id.password_TIL);

        findViewById(R.id.signup_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i =  new Intent(MainActivity.this,SignUpActivity.class);
                startActivity(i);
            }
        });

        findViewById(R.id.sign_in_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i =  new Intent(MainActivity.this,TabLayoutActivity.class);
                startActivity(i);
                if(CheckIfEmailAndPasswordAreEmpty()){
                    String loginText = email_TIET.getText().toString().trim();
                    String passwordText = password_TIET.getText().toString().trim();

                }
            }
        });

    }

    private boolean CheckIfEmailAndPasswordAreEmpty() {
        if(email_TIET.getText().toString().equals("")){
            email_TIL.setError("Cannot be empty");
            return false;
        }else{
            email_TIL.setError("");
        }
        if(password_TIET.getText().toString().equals("")){
            password_TIL.setError("Cannot be empty");
            return false;
        }else{
            password_TIL.setError("");
        }
        return true;
    }
}