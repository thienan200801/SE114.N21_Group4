package com.example.audace;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class InfoScreen extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.info);

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
                    .addHeader("Authorization", "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJfaWQiOiI2NDQxMTU4ZmVhZjQ5MmY0OGI0NzE3MzEiLCJpYXQiOjE2ODIwMDgxNjl9.Cgn99CpH3ypDpTVm_Fh_E1nn2anvJWZAnZHS4Qkwnn4")
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    Toast.makeText(InfoScreen.this, "Send request failed", Toast.LENGTH_SHORT).show();

                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    String body = response.body().string();
                    Log.e("data from server", body);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                                Toast.makeText(InfoScreen.this, body, Toast.LENGTH_SHORT).show();


                        }
                    });


                }
            });


        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}