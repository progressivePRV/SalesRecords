package com.example.salesrecordapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class SignUpActivity extends AppCompatActivity {

    TextInputLayout firstName_TIL,lastName_TIL,age_TIL,email_TIL,password_TIL;
    TextInputEditText firstName_TIET,lastName_TIET,age_TIET,email_TIET,password_TIET;
    RadioGroup genderRadioGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        firstName_TIET = findViewById(R.id.firstName_TIET);
        lastName_TIET = findViewById(R.id.lastName_TIET);
        age_TIET = findViewById(R.id.age_TIET);
        email_TIET = findViewById(R.id.email_TIET);
        password_TIET = findViewById(R.id.password_TIET);
        firstName_TIL = findViewById(R.id.firstName_TIL);
        lastName_TIL = findViewById(R.id.lastName_TIL);
        age_TIL = findViewById(R.id.age_TIL);
        email_TIL = findViewById(R.id.email_TIL);
        password_TIL = findViewById(R.id.password_TIL);
        genderRadioGroup = findViewById(R.id.radio_group_male_female);



        findViewById(R.id.sign_in_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(CheckIfFieldAreEmpty()){
                    String fnameValue = firstName_TIET.getText().toString().trim();
                    String lnameValue = lastName_TIET.getText().toString().trim();
                    String passwordValue = password_TIET.getText().toString().trim();
                    //String repeatPasswordValue = repeatPassword.getText().toString().trim();
                    String emailValue = email_TIET.getText().toString().trim();
                    String ageValue = age_TIET.getText().toString().trim();
                    String genderValue;
                    if(R.id.female == genderRadioGroup.getCheckedRadioButtonId()){
                        genderValue = "Female";
                    }else{
                        genderValue = "Male";
                    }

                }
            }
        });

    }

    public boolean CheckIfFieldAreEmpty(){
        if(firstName_TIET.getText().toString().equals("")){
            firstName_TIL.setError("Cannot be empty");
            return false;
        }else{
            firstName_TIL.setError("");
        }
        if(lastName_TIET.getText().toString().equals("")) {
            lastName_TIL.setError("Cannot be empty");
            return false;
        }else{
            lastName_TIL.setError("");
        }
        if(genderRadioGroup.getCheckedRadioButtonId() == -1){
            Toast.makeText(this, "Please select a gender", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(age_TIET.getText().toString().equals("")){
            age_TIL.setError("Cannot be empty");
            return false;
        }else{
            age_TIL.setError("");
        }
        if(email_TIET.getText().toString().equals("")){
            email_TIL.setError("Cannot be empty");
            return false;
        }else{
            email_TIL.setError("");
        }
        String x = password_TIET.getText().toString();
        if(x.equals("")){
            password_TIL.setError("Cannot be empty");
            return false;
        }
//        }else if(x.length()<6 || x.length()>20){
//            password_TIL.setError("should be of length more than 6 and less than 20");
//            return false;
//        }
        else{
            password_TIL.setError("");
        }
        return true;
    }

}