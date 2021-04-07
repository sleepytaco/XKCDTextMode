package com.abukh.xkcdtextmode;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import android.os.Bundle;
import android.view.MenuItem;

import com.abukh.xkcdtextmode.fragments.FavoritesFragment;
import com.abukh.xkcdtextmode.fragments.HistoryFragment;
import com.abukh.xkcdtextmode.fragments.HomeFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = MainActivity.class.getSimpleName();

    final FragmentManager fragmentManager = getSupportFragmentManager(); // will be responsible for changing the fragment that will be shown in the frame layout

    private BottomNavigationView bottomNavigationView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottomNavigation);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment fragment;

                switch (item.getItemId()) {
                    case R.id.action_home:
                        fragment = new HomeFragment();
                        break;
                    case R.id.action_favorites:
                        fragment = new FavoritesFragment();
                        break;
                    case R.id.action_history:
                    default:
                        fragment = new HistoryFragment();
                        break;
                }

                fragmentManager.beginTransaction().replace(R.id.flContainer, fragment).commit();
                return true;

            }
        });

        // Sets the default item in the frame layout to be selected be action home
        bottomNavigationView.setSelectedItemId(R.id.action_home);
    }

}