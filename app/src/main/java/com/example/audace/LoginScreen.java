package com.example.audace;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LoginScreen extends AppCompatActivity {
    private LinearLayout loginLayout;
    private LinearLayout signupLayout;
    EditText loginUser, loginPass, signupUser, signupPass,confirmPass;
    Handler handler;
    ImageButton btnLogin, btnSignUp;
    ImageButton getBtnLogin, getBtnSignUp;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        loginLayout = findViewById(R.id.login_layout);
        signupLayout = findViewById(R.id.signup_layout);

        Log.i("message", "start on create");
        handler = new Handler();

        loginUser = (EditText) findViewById(R.id.loginUsername);
        loginPass = (EditText) findViewById(R.id.loginPass);
        signupUser = (EditText) findViewById(R.id.signupUsername);
        signupPass = (EditText) findViewById(R.id.signupPass);
        confirmPass = (EditText) findViewById(R.id.signupConfirmPass);

        btnLogin = (ImageButton) findViewById(R.id.btnLogin);
        btnSignUp = (ImageButton) findViewById(R.id.btnSignUp);
        getBtnLogin = (ImageButton)  findViewById(R.id.btnLogin);

        getBtnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkAuthorization();
            }
        });
        getBtnSignUp = (ImageButton) findViewById(R.id.imageButton34);
        getBtnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                checkConfirmPass();
            }
        });

    }

    private void checkConfirmPass() {
        String password = signupPass.getText().toString();
        String confirmPassword = confirmPass.getText().toString();

        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Password and Confirm Password do not match", Toast.LENGTH_SHORT).show();
            confirmPass.clearComposingText();// You can perform further actions or validations here, such as clearing the password fields or displaying an error message
        } else {
            postUser();
        }
    }

    private void postUser() {
        try {

            OkHttpClient client = new OkHttpClient().newBuilder()
                    .build();
            MediaType mediaType = MediaType.parse("application/json");
            // Get the values from the EditText fields
            String username = signupUser.getText().toString();
            String password = signupPass.getText().toString();

            new Thread(new Runnable() {
                @Override
                public void run() {
                    RequestBody body = new FormBody.Builder()
                            .add("usernameOrEmail",username)
                            .add("password",password)
                            .build();

                    Request request = new Request.Builder()
                            .url("https://audace-ecomerce.herokuapp.com/users")
                            .method("POST", body)
                            .addHeader("Content-Type", "application/json")
                            .build();

                    client.newCall(request).enqueue(new Callback() {
                        @Override
                        public void onFailure(@NonNull Call call, @NonNull IOException e) {

                        }

                        @Override
                        public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                            String body=response.body().string();
                            Log.e("data from server", body);


                        }
                    });

                }
            }).start();


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void checkAuthorization() {
        try {

            OkHttpClient client = new OkHttpClient().newBuilder()
                    .build();
            MediaType mediaType = MediaType.parse("application/json");
            // Get the values from the EditText fields
            String username = loginUser.getText().toString();
            String password = loginPass.getText().toString();

            new Thread(new Runnable() {
                @Override
                public void run() {
                    RequestBody body = RequestBody.create(mediaType, "{\"username\":\"" + username + "\",\"password\":\"" + password + "\"}");
                    Request request = new Request.Builder()
                            .url("https://audace-ecomerce.herokuapp.com/auth/local")
                            .method("POST", body)
                            .addHeader("Content-Type", "application/json")
                            .build();

                    Call call = client.newCall(request);
                    Response response = null;
                    try{
                        response = call.execute();
                        JSONObject serverResponse = new JSONObject(response.body().string());
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    String accessToken = serverResponse.getString("accessToken");
                                    DataStorage.getInstance().setAccessToken(accessToken);
                                    Toast.makeText(LoginScreen.this, "Login successfully", Toast.LENGTH_SHORT).show();
                                    Intent t = new Intent(LoginScreen.this, HomepageActivity.class);
                                    startActivity(t);
                                } catch (JSONException e) {
                                    Toast.makeText(LoginScreen.this, "Incorrect username or password", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }catch (IOException e){
                        e.printStackTrace();
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                }
            }).start();



        } catch (Exception e) {
            e.printStackTrace();
        }
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