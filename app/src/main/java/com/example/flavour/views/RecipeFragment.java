package com.example.flavour.views;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.flavour.adapters.StepAdapter;
import com.example.flavour.databinding.FragmentRecipeBinding;
import com.example.flavour.models.Recipe;
import com.example.flavour.viewmodels.RecipeViewModel;
import com.example.flavour.viewmodels.SearchViewModel;

public class RecipeFragment extends Fragment implements StepAdapter.StepEvents {
    FragmentRecipeBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentRecipeBinding.inflate(inflater, container, false);

        if (getArguments().getParcelable("recipe") == null) navigateBack();

        Recipe recipe = (Recipe) getArguments().getParcelable("recipe");
        binding.name.setText(recipe.getName());
        binding.back.setOnClickListener(v -> navigateBack());
        initRecycler(recipe);

        disableBottomNav();
        return binding.getRoot();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        enableBottomNav();
    }
    public void initRecycler(Recipe recipe) {
        Log.d("TAG", "initRecycler: " + recipe.getName()+" "+recipe.getDifficulty());
        StepAdapter adapter = new StepAdapter(recipe);
        adapter.setEvents(this);
        adapter.submitList(recipe.getSteps());
        binding.steps.setAdapter(adapter);
        binding.steps.setLayoutManager(new LinearLayoutManager(getContext()));
    }
    private void navigateBack(){
        requireActivity().getSupportFragmentManager().popBackStack();
    }
    private void disableBottomNav() {
        ((SecondActivity) requireActivity()).disableBottomNav();
    }

    private void enableBottomNav() {
        ((SecondActivity) requireActivity()).enableBottomNav();
    }

    @Override
    public void toFavorite(Recipe recipe, boolean is_favorite) {
        RecipeViewModel userViewModel = new ViewModelProvider(this).get(RecipeViewModel.class);
        userViewModel.addFavorite(recipe, is_favorite);
    }
}
