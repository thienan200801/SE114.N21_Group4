package com.example.audace;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

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

public class ProfileScreen extends AppCompatActivity {


    TextView name, dob, emailText, phone;
    ImageView pic;

    ImageButton voucher,order,cart,info;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile);

        name = (TextView) findViewById(R.id.profile_name);
        dob = (TextView) findViewById(R.id.profile_dob);
        emailText = (TextView) findViewById(R.id.profile_email);
        phone = (TextView) findViewById(R.id.profile_phone);
        pic = (ImageView) findViewById(R.id.profile_pic);

        voucher = (ImageButton) findViewById(R.id.btnVoucher);
        order = (ImageButton) findViewById(R.id.btnOrder);
        cart = (ImageButton) findViewById(R.id.btnCart) ;
        info = (ImageButton) findViewById(R.id.btnEdit);

        voucher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProfileScreen.this,VoucherScreen.class);
                startActivity(intent);
            }
        });
        order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProfileScreen.this,VoucherScreen.class);
                startActivity(intent);
            }
        });
        cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProfileScreen.this,CartScreen.class);
                startActivity(intent);
            }
        });
        info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProfileScreen.this,InfoScreen.class);
                startActivity(intent);
            }
        });
        setupData();
        renderData();

    }

    private void renderData() {
        try {

            OkHttpClient client = new OkHttpClient().newBuilder()
                    .build();
            MediaType mediaType = MediaType.parse("application/json");
            RequestBody body = RequestBody.create(mediaType, "");
            // Get the values from the EditText fields




                    Request request = new Request.Builder()
                            .url("https://audace-ecomerce.herokuapp.com/users/me")
                            .method("POST", null)
                            .addHeader("Authorization", "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJfaWQiOiI2NDQxMTU4ZmVhZjQ5MmY0OGI0NzE3MzEiLCJpYXQiOjE2ODIwMDgxNjl9.Cgn99CpH3ypDpTVm_Fh_E1nn2anvJWZAnZHS4Qkwnn4")
                            .build();

                    client.newCall(request).enqueue(new Callback() {
                        @Override
                        public void onFailure(@NonNull Call call, @NonNull IOException e) {
                            Toast.makeText(ProfileScreen.this,"Sign Up failed",Toast.LENGTH_SHORT).show();

                        }

                        @Override
                        public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                            String body=response.body().string();
                            Log.e("data from server", body);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                    if (body.equals("")) {
                                        Toast.makeText(ProfileScreen.this, "Sign Up successfully", Toast.LENGTH_SHORT).show();

                                    }
                                    else {
                                        Toast.makeText(ProfileScreen.this, body, Toast.LENGTH_SHORT).show();

                                    }
                                }
                            });


                        }
                    });




        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setupData() {

        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
      /*  MediaType mediaType = MediaType.parse("text/plain");*/
        /*RequestBody body = RequestBody.create(mediaType, "");*/

        Request request = new Request.Builder()
                .url("https://audace-ecomerce.herokuapp.com/users/me/profile")
                .method("GET", null)
                .addHeader("Authorization", "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJfaWQiOiI2NDQxMTU4ZmVhZjQ5MmY0OGI0NzE3MzEiLCJpYXQiOjE2ODM3MDE4MDN9.dA-agPqUSJ-g2mdmw7lTBzzfszH7TUYpNAh-Lh9xQ24")
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.i("failure", e.toString());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                    if (response.isSuccessful()) {
                        String jsonData = response.body().string();
                        try {
                            JSONObject jsonObject = new JSONObject(jsonData);

                            // Extract the required data from the JSON response
                            String username = jsonObject.getString("username");
                            String address = jsonObject.getString("address");
                            String email = jsonObject.getString("email");
                            // Add more fields as needed

                            // Update the UI on the main thread
                            name.post (new Runnable() {
                                @Override
                                public void run() {
                                    name.setText(username);
                                    // Set other TextViews or update UI elements as needed
                                }
                            });

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        Log.e("response", "Error: " + response.code());
                    }

            }
        });
    }
}


