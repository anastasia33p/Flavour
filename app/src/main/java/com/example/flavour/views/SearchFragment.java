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
import com.example.flavour.databinding.FragmentSearchBinding;
import com.example.flavour.models.Recipe;
import com.example.flavour.viewmodels.SearchViewModel;

public class SearchFragment extends Fragment implements RecipeAdapter.RecipeEvents {
    FragmentSearchBinding binding;
    private SearchViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentSearchBinding.inflate(inflater, container, false);
        RecipeAdapter adapter = new RecipeAdapter();
        adapter.setEvents(this);

        viewModel = new ViewModelProvider(this).get(SearchViewModel.class);
        viewModel.getRecipe().observe(getViewLifecycleOwner(), adapter::submitList);
        viewModel.getRecipes();

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
        viewModel.getAddFavorite().observe(getViewLifecycleOwner(), aBoolean -> {
            if (aBoolean) {
                if (is_favorite) {
                    Toast.makeText(getContext(), "Рецепт удален из избранного", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Рецепт добавлен в избранное", Toast.LENGTH_SHORT).show();
                }
            }
        });
        viewModel.addFavorite(recipe, is_favorite);
    }
}
