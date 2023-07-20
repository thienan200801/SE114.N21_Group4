package com.example.audace;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class InfoScreen extends AppCompatActivity implements AdapterView.OnItemSelectedListener{
    Button btnUpdate;
    EditText nameText,phoneText,emailText;
    Spinner genderSpinner;
    TextView userIdText, passwordText;
    String text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.info);

        btnUpdate=findViewById(R.id.btnUpdate);
        nameText=findViewById(R.id.profile_name);
        phoneText=findViewById(R.id.profile_phone);
        emailText=findViewById(R.id.profile_email);
        genderSpinner=findViewById(R.id.profile_gender);
        userIdText= findViewById(R.id.profile_id);
        passwordText=findViewById(R.id.profile_pass);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,R.array.gender, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        genderSpinner.setAdapter(adapter);
        genderSpinner.setOnItemSelectedListener(this);

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (nameText.getText().toString().isEmpty() || phoneText.getText().toString().isEmpty() || emailText.getText().toString().isEmpty())
                {
                    Toast.makeText(InfoScreen.this, "Vui lòng điền đủ thông tin", Toast.LENGTH_SHORT).show();
                }
                else{
                    updateInfo();

                }
            }
        });
        renderData();
        findViewById(R.id.backButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    private void renderData() {
        try {

            OkHttpClient client = new OkHttpClient().newBuilder()
                    .build();
            MediaType mediaType = MediaType.parse("application/json");

            Request request = new Request.Builder()
                    .url("https://audace-ecomerce.herokuapp.com/users/me/profile")
                    .method("GET", null)
                    .addHeader("Authorization", "Bearer " + DataStorage.getInstance().getAccessToken())
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(InfoScreen.this, "Send request failed", Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    String body = response.body().string();
                    Log.e("data from server", body);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                if (response.isSuccessful()) {
                                    JSONObject jsonObject = new JSONObject(body);
                                    try{
                                        String email = jsonObject.getString("email");
                                        emailText.setText(email);
                                    }
                                    catch (JSONException e)
                                    {}
                                    try{
                                        String fullName = jsonObject.getString("fullname");
                                        nameText.setText(fullName);
                                    }
                                    catch (JSONException e) {}
                                    try{
                                        String gender = jsonObject.getString("gender");
                                        int genderPosition = getGenderPosition(gender);
                                        genderSpinner.setSelection(genderPosition);

                                    }
                                    catch (JSONException e) {}
                                    try{
                                        String number = jsonObject.getString("phone");
                                        phoneText.setText(number);
                                    }
                                    catch (JSONException e) {}
                                    try{
                                        String id = jsonObject.getString("_id");
                                        userIdText.setText(id);
                                    }
                                    catch (JSONException e) {}
                                    try{
                                        String password = jsonObject.getString("password");
                                        passwordText.setText(password);
                                    }
                                    catch (JSONException e) {}

                                } else {
                                    Toast.makeText(InfoScreen.this, body, Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                Toast.makeText(InfoScreen.this, "Failed to parse profile data", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private int getGenderPosition(String gender) {
        String[] gendersArray = getResources().getStringArray(R.array.gender);
        for (int i = 0; i < gendersArray.length; i++) {
            if (gendersArray[i].equals(gender)) {
                return i;
            }
        }
        return 0;
    }


    private  void updateInfo(){
        try {
            OkHttpClient client = new OkHttpClient().newBuilder().build();

            String updatedFullName = nameText.getText().toString();
            String updatedPhone = phoneText.getText().toString();
            String updatedEmail = emailText.getText().toString();

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("fullname", updatedFullName);
            jsonObject.put("phone", updatedPhone);
            jsonObject.put("email", updatedEmail);
            jsonObject.put("gender", text);

            RequestBody body = RequestBody.create(MediaType.parse("application/json"), jsonObject.toString());

            Request request = new Request.Builder()
                    .url("https://audace-ecomerce.herokuapp.com/users/me/profile")
                    .method("PATCH", body)
                    .addHeader("Authorization", "Bearer " + DataStorage.getInstance().getAccessToken())
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        text = adapterView.getItemAtPosition(i).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        text = adapterView.getItemAtPosition(0).toString();
    }
}