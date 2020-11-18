package com.example.salesrecordapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "sale_record";
    TextInputLayout email_TIL,password_TIL;
    TextInputEditText email_TIET,password_TIET;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    Gson gson =  new Gson();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        email_TIET = findViewById(R.id.email_TIET);
        password_TIET = findViewById(R.id.password_TIET);
        email_TIL = findViewById(R.id.email_TIL);
        password_TIL = findViewById(R.id.password_TIL);

        checkIfUserIsLogedIN();

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
                if(CheckIfEmailAndPasswordAreEmpty()){
                    String loginText = email_TIET.getText().toString().trim();
                    String passwordText = password_TIET.getText().toString().trim();
                    Log.d("demo",loginText+" "+passwordText);
                    Log.d(TAG, "onClick: calling async");
                    new getTokeyAsync(loginText, passwordText).execute();
                }
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 100 && resultCode == 200){
            finish();
        }
    }

    private void checkIfUserIsLogedIN() {
        preferences = getApplicationContext().getSharedPreferences("TokeyKey",0);
        String pastTokenKey = preferences.getString("TOKEN_KEY", null);
        if(pastTokenKey!=null && !pastTokenKey.equals("")){
            //starting animation and async to check user
            new getUser().execute();
        }
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

    public class getTokeyAsync extends AsyncTask<String, Void, String> {

        String username, password;
        boolean isStatus =true;

        public getTokeyAsync(String username, String password) {
            this.username = username;
            this.password = password;
        }

        @Override
        protected String doInBackground(String... strings) {
            final OkHttpClient client = new OkHttpClient();
            String decodedValue = username+":"+password;

            Log.d(TAG, "doInBackground: async called for login");

            byte[] encodedValue = new byte[0];
            try {
                encodedValue = decodedValue.getBytes("UTF-8");
                String encodedString = Base64.encodeToString(encodedValue, Base64.NO_WRAP);

                Request request = new Request.Builder()
                        .url(getResources().getString(R.string.endPointUrl)+"login")
                        .header("Authorization", "Basic " + encodedString)
                        .build();
                try (Response response = client.newCall(request).execute()) {
                    String result = response.body().string();
                    Log.d(TAG, "doInBackground: login response=>"+result);
                    if (response.isSuccessful()){
                        isStatus = true;
                    }else{
                        isStatus = false;
                    }
                    return result;
                } catch (IOException e) {
                    e.printStackTrace();
                }

            } catch (UnsupportedEncodingException e) {
                Toast.makeText(MainActivity.this, "Some problem occured with the password", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }

            return "";
        }

        @Override
        protected void onPostExecute(String result1) {
            super.onPostExecute(result1);
            JSONObject root = null;
            Log.d("demo",result1);
            try {
                root = new JSONObject(result1);
                if(isStatus){
                    Log.d("demo",root.toString());
                    User user = new User();
                    user._id = root.getString("_id");
                    user.firstName = root.getString("firstName");
                    user.lastName = root.getString("lastName");
                    user.gender = root.getString("gender");
                    user.email = root.getString("email");
                    preferences = getApplicationContext().getSharedPreferences("TokeyKey",0);
                    editor = preferences.edit();
                    editor.putString("TOKEN_KEY",root.getString("token"));
                    editor.putString("ID",user._id);
                    editor.putString("USER",gson.toJson(user));
                    editor.commit();
                    Toast.makeText(MainActivity.this, "Logged in Successfully", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(MainActivity.this, TabLayoutActivity.class);
                    startActivity(intent);
                    finish();
                }else{
                    //It means that they are some error while signing up.
                    Toast.makeText(MainActivity.this, root.getString("error"), Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public class getUser extends AsyncTask<String, Void, String> {
        boolean isStatus = true;
        @Override
        protected String doInBackground(String... strings) {
            final OkHttpClient client = new OkHttpClient();
            SharedPreferences preferences = getApplicationContext().getSharedPreferences("TokeyKey", 0);

            try {
                Request request = new Request.Builder()
                        .url(getResources().getString(R.string.endPointUrl)+"profile")
                        .header("Authorization", "Bearer "+ preferences.getString("TOKEN_KEY", null))
                        .build();
                try (Response response = client.newCall(request).execute()) {
                    if (response.isSuccessful()){
                        isStatus = true;
                    }else{
                        isStatus = false;
                    }
                    return response.body().string();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }catch (Exception e){

            }
            return "";
        }

        @Override
        protected void onPostExecute(String result1) {
            super.onPostExecute(result1);
            JSONObject root = null;
            Log.d(TAG,result1);
            try {
                root = new JSONObject(result1);
                if(isStatus){
                    Log.d("demo","Token is valid");
                    Toast.makeText(MainActivity.this, "Logged in Successfully", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(MainActivity.this, TabLayoutActivity.class);
                    startActivity(intent);
                    finish();
                }else{
                    //It means that they are some error while signing up.
                    Toast.makeText(MainActivity.this, "Session has expired. Please login again!", Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}