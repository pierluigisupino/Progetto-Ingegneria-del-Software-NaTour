package com.ingsw2122_n_03.natour.presentation.main;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.shape.CornerFamily;
import com.google.android.material.shape.MaterialShapeDrawable;
import com.ingsw2122_n_03.natour.R;
import com.ingsw2122_n_03.natour.application.AuthController;
import com.ingsw2122_n_03.natour.application.IterController;
import com.ingsw2122_n_03.natour.databinding.ActivityMainBinding;
import com.ingsw2122_n_03.natour.presentation.MessagesFragment;
import com.ingsw2122_n_03.natour.presentation.itinerary.addItinerary.AddItineraryActivity;
import com.ingsw2122_n_03.natour.presentation.support.BaseActivity;

public class MainActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener {


    private AuthController authController;
    private IterController iterController;

    private DrawerLayout drawerLayout;

    private MainFragment mainFragment;
    private final MessagesFragment messagesFragment = new MessagesFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        this.mainFragment = new MainFragment();

        Intent intent = getIntent();
        if(intent.hasExtra("itineraries")) {
            Bundle bundle = new Bundle();
            bundle.putSerializable("itineraries", intent.getSerializableExtra("itineraries"));
            mainFragment.setArguments(bundle);
        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragmentContainer, mainFragment);
        fragmentTransaction.commit();

        authController = AuthController.getInstance();
        authController.setMainActivity(this);

        iterController = IterController.getInstance();
        iterController.setMainActivity(this);

        MaterialToolbar materialToolbar = binding.toolbar;
        drawerLayout = binding.layout;
        NavigationView navigationView = binding.navView;

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, materialToolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setCheckedItem(R.id.home);
        navigationView.setNavigationItemSelectedListener(this);

        float radius = getResources().getDimension(R.dimen.corner_radious);
        MaterialShapeDrawable materialShapeDrawable = (MaterialShapeDrawable)materialToolbar.getBackground();
        materialShapeDrawable.setShapeAppearanceModel(materialShapeDrawable.getShapeAppearanceModel()
                .toBuilder()
                .setAllCorners(CornerFamily.ROUNDED, radius)
                .build());

        materialToolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.Add) {
                iterController.goToActivity(MainActivity.this, AddItineraryActivity.class);
                return true;
            }
            return false;
        });

    }

    @Override
    public void onBackPressed() {
        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        }else{
            super.onBackPressed();
        }
    }

    @Override
    public void onSuccess(String msg) {

    }

    @Override
    public void onFail(String msg) {

    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        switch (item.getItemId()) {

            case R.id.home:
                if (!mainFragment.isVisible()) {
                    fragmentTransaction.replace(R.id.fragmentContainer, mainFragment);
                    fragmentTransaction.commit();
                }
                break;

            case R.id.messages:
                if (!messagesFragment.isVisible()) {
                    fragmentTransaction.replace(R.id.fragmentContainer, messagesFragment);
                    fragmentTransaction.commit();
                }
                break;

            case R.id.logout:
                authController.signOut();
                break;

        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
}