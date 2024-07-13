package com.example.hellomich;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.hellomich.databinding.ActivityMainPageBinding;
import com.example.hellomich.databinding.ActivityRegisterBinding;
import com.google.android.material.navigation.NavigationBarView;

public class MainPageActivity extends AppCompatActivity {

    private ActivityMainPageBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainPageBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        replaceFragment(new HomeFragment());



        binding.bottomNavigationView.setOnItemSelectedListener(item -> {

            if (item.getItemId()==R.id.home) {

                replaceFragment(new HomeFragment());
            } else if (item.getItemId()==R.id.profile) {

                replaceFragment(new ProfileFragment());
            } else if (item.getItemId()==R.id.settings) {

                replaceFragment(new SettingsFragment());
            }
            return true;
        });
    }

    public void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.mainPageFrameLayout,fragment).commit();
    }
}