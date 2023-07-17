package com.example.audace;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class HomepageActivity extends AppCompatActivity {
    boolean doubleBackToExitPressedOnce = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(DataStorage.getInstance().getAccessToken() != null)
        {
            setContentView(R.layout.activity_homepage);
            loadFragment(new MainFragment());
            BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);
            bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    Log.i("message", String.format("%d",item.getItemId()));
                    switch (item.getItemId())
                    {

                        case R.id.action_profile: {
                            Intent intent = new Intent(HomepageActivity.this, ProfileScreen.class);
                            startActivity(intent);
                            break;
                        }
                        case R.id.action_favourite:{
                            Intent intent = new Intent(HomepageActivity.this, FavoriteScreen.class);
                            startActivity(intent);
                            break;
                        }
                        case R.id.action_history:{
                            Intent intent = new Intent(HomepageActivity.this, HistoryScreen.class);
                            startActivity(intent);
                            break;
                        }
                        default:
                        {
                            break;
                        }

                    }
                    return true;
                }
            });
        }
        else
        {
            SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("token", Context.MODE_PRIVATE);
            String token = sharedPreferences.getString("token", "");
            Log.i("token from device", token);
            if(!token.equals(""))
            {
                DataStorage.getInstance().tokenValidation(token);
                Intent t = new Intent(HomepageActivity.this, HomepageActivity.class);
                startActivity(t);
                this.finishActivity(0);
            }
            else
            {

                boolean firstTime = sharedPreferences.getBoolean("firstTime", true);
                if(firstTime)
                {
                    Intent t = new Intent(HomepageActivity.this, IntroductionActivity1.class);
                    startActivity(t);
                    this.finishActivity(0);
                }
                else
                {
                    Intent t = new Intent(HomepageActivity.this, LoginScreen.class);
                    startActivity(t);
                    this.finishActivity(0);
                }
            }
        }

    }

    @Override
    public void onBackPressed() {
        if(getSupportFragmentManager().findFragmentById(R.id.bottomNavigationContainer) instanceof MainFragment)
        {
            if(doubleBackToExitPressedOnce)
                this.finishAffinity();
            Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();
            this.doubleBackToExitPressedOnce = true;
            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    doubleBackToExitPressedOnce=false;
                }
            }, 2000);
        }
        else {
            super.onBackPressed();
        }
    }

    public void loadFragment(Fragment fragment) {
        // load fragment
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.bottomNavigationContainer, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
