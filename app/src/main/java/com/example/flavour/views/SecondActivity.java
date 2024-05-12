package com.example.flavour.views;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.flavour.R;
import com.example.flavour.databinding.ActivitySecondBinding;
import com.example.flavour.models.Recipe;

public class SecondActivity extends AppCompatActivity {
    ActivitySecondBinding binding;
    private NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySecondBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.fragment_container_view_2);
        navController = navHostFragment.getNavController();
        selectNavItem();
    }
    public void selectNavItem() {
        binding.bottomNavigation.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            int currentDestinationId = navController.getCurrentDestination().getId();

            if (itemId == R.id.main) {
                if (currentDestinationId != R.id.searchFragment) {
                    navigateToSearchFragment();
                }
                return true;
            } else if (itemId == R.id.fav) {
                if (currentDestinationId != R.id.favoriteFragment) {
                    navigateToFavoriteFragment();
                }
                return true;
            } else if (itemId == R.id.profile) {
                if (currentDestinationId != R.id.profileFragment) {
                    navigateToProfileFragment();
                }
                return true;
            }
            return false;
        });
    }

    public void navigateToProfileFragment() {
        navController.navigate(R.id.profileFragment);
    }
    public void navigateToSearchFragment() {
        navController.navigate(R.id.searchFragment);
    }

    public void navigateToFavoriteFragment() {
        navController.navigate(R.id.favoriteFragment);
    }

    public void navigateToRecipeFragment(Recipe recipe) {
        Bundle bundle = new Bundle();
        bundle.putParcelable("recipe", recipe);
        navController.navigate(R.id.recipeFragment, bundle);
    }
    public void navigateToMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
    public void navigateToEditorFragment(){
        navController.navigate(R.id.editorFragment);
    }

    public void disableBottomNav() {
        binding.bottomNavigation.setVisibility(View.GONE);
    }

    public void enableBottomNav() {
        binding.bottomNavigation.setVisibility(View.VISIBLE);
    }
}