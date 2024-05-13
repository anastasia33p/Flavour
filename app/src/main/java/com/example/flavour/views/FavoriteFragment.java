package com.example.flavour.views;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.flavour.adapters.RecipeAdapter;
import com.example.flavour.databinding.FragmentFavoriteBinding;
import com.example.flavour.models.Recipe;
import com.example.flavour.viewmodels.FavoritesViewModel;

public class FavoriteFragment extends Fragment implements RecipeAdapter.RecipeEvents {
    private FragmentFavoriteBinding binding;
    private FavoritesViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentFavoriteBinding.inflate(inflater, container, false);
        RecipeAdapter adapter = new RecipeAdapter();
        adapter.setEvents(this);

        viewModel = new ViewModelProvider(this).get(FavoritesViewModel.class);
        viewModel.getFavorites().observe(getViewLifecycleOwner(), adapter::submitList);
        viewModel.getListFavorite();

        binding.list.setAdapter(adapter);
        binding.list.setLayoutManager(new LinearLayoutManager(getContext()));
        return binding.getRoot();
    }

    @Override
    public void openRecipe(Recipe recipe) {
        ((SecondActivity) requireActivity()).navigateToRecipeFragment(recipe);
    }

    @Override
    public void toFavorite(Recipe recipe, boolean is_favorite) {
        viewModel.addFavorite(recipe, is_favorite);
    }
}
