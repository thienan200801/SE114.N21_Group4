package com.example.audace;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.squareup.picasso.Picasso;

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

public class ProfileScreen extends Fragment {


    TextView nameText, genderText, emailText, phoneText;
    ImageView pic;

    ImageButton order,cart,info;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.activity_profile, container, false);
        nameText = (TextView) view.findViewById(R.id.profile_name);
        genderText = (TextView) view.findViewById(R.id.profile_gender);
        emailText = (TextView) view.findViewById(R.id.profile_email);
        phoneText = (TextView) view.findViewById(R.id.profile_phone);
        pic = (ImageView) view.findViewById(R.id.profile_pic);

        order = (ImageButton) view.findViewById(R.id.btnOrder);
        cart = (ImageButton) view.findViewById(R.id.btnCart) ;
        info = (ImageButton) view.findViewById(R.id.btnEdit);
        Picasso.get()
                .load(DataStorage.getInstance().getAvatarURl())
                .into(pic);

        order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadFragment(new HistoryScreen());
            }
        });
        cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity().getBaseContext(), CartScreen.class);
                startActivity(i);
            }
        });
        info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity().getBaseContext(),InfoScreen.class);
                startActivity(intent);
            }
        });
        view.findViewById(R.id.backButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });
        view.findViewById(R.id.btnVoucher).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadFragment(new FavoriteScreen());
            }
        });
        /*setupData();*/
        renderData();
        return view;
    }

    private void renderData() {
        try {

            OkHttpClient client = new OkHttpClient().newBuilder()
                    .build();
            MediaType mediaType = MediaType.parse("application/json");
            RequestBody body = RequestBody.create(mediaType, "");
            // Get the values from the EditText fields
                    Request request = new Request.Builder()
                            .url("https://audace-ecomerce.herokuapp.com/users/me/profile")
                            .method("GET", null)
                            .addHeader("Authorization", "Bearer "+ DataStorage.getInstance().getAccessToken())
                            .build();

                    client.newCall(request).enqueue(new Callback() {
                        @Override
                        public void onFailure(@NonNull Call call, @NonNull IOException e) {
                            Toast.makeText(getContext(),"Sign Up failed",Toast.LENGTH_SHORT).show();

                        }

                        @Override
                        public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                            String body=response.body().string();
                            Log.e("data from server", body);

                            new Handler(Looper.getMainLooper()).post(new Runnable() {
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
                                                genderText.setText(gender);
                                            }
                                            catch (JSONException e) {}
                                            try{
                                                String number = jsonObject.getString("phone");
                                                phoneText.setText(number);
                                            }
                                            catch (JSONException e) {}
                                        } else {
                                            Toast.makeText(getContext(), body, Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                    catch (JSONException e)
                                    { e.printStackTrace();
                                        Toast.makeText(getContext(), "Failed to parse profile data", Toast.LENGTH_SHORT).show(); }
                                    }
                            });


                        }
                    });




        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*private void setupData() {

        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
      *//*  MediaType mediaType = MediaType.parse("text/plain");*//*
        *//*RequestBody body = RequestBody.create(mediaType, "");*//*

        Request request = new Request.Builder()
                .url("https://audace-ecomerce.herokuapp.com/users/me/profile")
                .method("GET", null)
                .addHeader("Authorization", "Bearer "+DataStorage.getInstance().getAccessToken())
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
    }*/
    public void loadFragment(Fragment fragment) {
        // load fragment
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.bottomNavigationContainer, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}


