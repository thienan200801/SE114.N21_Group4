package com.example.audace;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
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
                            loadFragment(new ProfileScreen());
                            break;
                        }
                        case R.id.action_favourite:{
                            loadFragment(new FavoriteScreen());
                            break;
                        }
                        case R.id.action_history:{
                            loadFragment(new HistoryScreen());
                            break;
                        }
                        default:
                        {
                            loadFragment(new MainFragment());
                            break;
                        }

                    }
                    return true;
                }
            });
        }
        else
        {
            Intent t = new Intent(HomepageActivity.this, LoginScreen.class);
            startActivity(t);
            this.finishActivity(0);
        }

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
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
