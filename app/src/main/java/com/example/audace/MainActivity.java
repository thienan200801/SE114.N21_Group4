package com.example.audace;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    final DataStorage storage = DataStorage.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_startup);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        Intent i = getIntent();
        Uri data = i.getData();
        ((TextView)findViewById(R.id.loadingText)).setText("Đang tìm kiếm token");
        String uriToken = null;
        if(data != null)
        {
            uriToken = data.getQueryParameter("access_token");
            Log.i("message", data.toString());
        }
        if(uriToken != null)
        {
            DataStorage.getInstance().setAccessToken(uriToken);
            SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("token", Context.MODE_PRIVATE);
            @SuppressLint("CommitPrefEdits") SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("token",uriToken);
            editor.apply();
            GetData();
            changeActivity();
        }
        else
        {
            SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("token", Context.MODE_PRIVATE);
            String token = sharedPreferences.getString("token", "");
            Log.i("token from device", token);
            if(!token.equals(""))
            {
                DataStorage.getInstance().setAccessToken(token);
                GetData();
                changeActivity();
            }
            else {

                boolean firstTime = sharedPreferences.getBoolean("firstTime", true);
                if (firstTime) {
                    Intent t = new Intent(getBaseContext(), IntroductionActivity1.class);
                    startActivity(t);
                    this.finishActivity(0);
                }
                else
                {
                    Intent t = new Intent(getBaseContext(), LoginScreen.class);
                    startActivity(t);
                    this.finishActivity(0);
                }
            }
        }
    }

    public void GetData()
    {
        ((TextView)findViewById(R.id.loadingText)).setText("Đang truy xuất dữ liệu...");
        getUserAvatar();
    }

    private void getUserAvatar() {
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        MediaType mediaType = MediaType.parse("application/json");
        Request request = new Request.Builder()
                .url("https://audace-ecomerce.herokuapp.com/users/me/profile")
                .method("GET", null)
                .addHeader("Authorization", "Bearer "+ DataStorage.getInstance().getAccessToken())
                .addHeader("Content-Type", "application/json")
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.i("message", "Fail to fetch data");
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {

                try {
                    JSONObject jsonObject = new JSONObject(response.body().string());
                    Log.i("user",jsonObject.getJSONArray("cart").toString());
                    DataStorage.getInstance().setUserName(jsonObject.getString("fullname"));
                    DataStorage.getInstance().setCartCount(jsonObject.getJSONArray("cart").length());
                    DataStorage.getInstance().setAvatarURl(jsonObject.getString("imageURL"));

                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }

            }
        });
    }
    public void changeActivity(){
        if(DataStorage.getInstance().getAvatarURl() == null || DataStorage.getInstance().getCatagoryArrayList() == null)
            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    changeActivity();
                }
            }, 200);
        else
        {
            Intent t = new Intent(getBaseContext(), HomepageActivity.class);
            startActivity(t);
            this.finishActivity(0);
        }
    }
}