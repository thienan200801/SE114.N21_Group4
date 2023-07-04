package com.example.audace;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class HomepageActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
                        Intent intent = new Intent(HomepageActivity.this, ProfileActivity.class);
                        startActivity(intent);
                        break;
                    }
                    case R.id.action_favourite:{
                        Intent intent = new Intent(HomepageActivity.this, Favourite.class);
                        startActivity(intent);
                        break;
                    }
                    case R.id.action_history:{
                        Intent intent = new Intent(HomepageActivity.this, HistoryActivity.class);
                        startActivity(intent);
                        break;
                    }
                    default:
                    {
                        Intent intent = new Intent(HomepageActivity.this, HomepageActivity.class);
                        startActivity(intent);
                        break;
                    }

                }
                return true;
            }
        });

    }
    private void loadFragment(Fragment fragment) {
        // load fragment
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.bottomNavigationContainer, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
