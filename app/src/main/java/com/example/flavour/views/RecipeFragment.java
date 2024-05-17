package com.example.flavour.views;

import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.flavour.R;
import com.example.flavour.adapters.StepAdapter;
import com.example.flavour.databinding.FragmentRecipeBinding;
import com.example.flavour.models.Recipe;
import com.example.flavour.models.Step;
import com.example.flavour.viewmodels.RecipeViewModel;
import com.example.flavour.viewmodels.SearchViewModel;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;

public class RecipeFragment extends Fragment implements StepAdapter.StepEvents {
    FragmentRecipeBinding binding;
    private String text;
    private Recipe recipe;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentRecipeBinding.inflate(inflater, container, false);

        if (getArguments().getParcelable("recipe") == null) navigateBack();

        recipe = (Recipe) getArguments().getParcelable("recipe");
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
        Log.d("TAG", "initRecycler: " + recipe.getName() + " " + recipe.getDifficulty());
        StepAdapter adapter = new StepAdapter(recipe);
        adapter.setEvents(this);
        adapter.submitList(recipe.getSteps());
        binding.steps.setAdapter(adapter);
        binding.steps.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    private void navigateBack() {
        requireActivity().getSupportFragmentManager().popBackStack();
    }

    private void disableBottomNav() {
        ((SecondActivity) requireActivity()).disableBottomNav();
    }

    private void enableBottomNav() {
        ((SecondActivity) requireActivity()).enableBottomNav();
    }

    @Override
    public void toFavorite(boolean is_favorite) {
        RecipeViewModel userViewModel = new ViewModelProvider(this).get(RecipeViewModel.class);
        userViewModel.addFavorite(recipe, is_favorite);
    }

    @Override
    public void saveFile() {
        text = recipe.getName() + "\n" + "Сложность: " + recipe.getDifficulty() + "\n" + "Время приготовления: " + recipe.getDuration()
                + "\n" + "Ингредиенты:\n" + recipe.getIngredients().toString() + "\n";
        for (Step step : recipe.getSteps()) {
            text += "Шаг " + step.getStep() + ": " + step.getDescription() + "\n";
        }
        showSaveDialog();
    }

    private void showSaveDialog() {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireContext());
        builder.setTitle("Скачать рецепт?");
        builder.setPositiveButton("Да", (dialog, which) -> {
            launcher.launch("recipe.txt");
        });
        builder.setNegativeButton("Нет", null);
        builder.show();
    }

    ActivityResultLauncher<String> launcher = registerForActivityResult(
            new ActivityResultContracts.CreateDocument("text/plain"),
            uri -> {
                if (uri != null) {
                    try (OutputStream outputStream = requireActivity().getContentResolver().openOutputStream(uri)) {
                        outputStream.write(text.getBytes());
                        outputStream.flush();
                    } catch (IOException e) {
                        Log.e(getClass().getCanonicalName(), e.toString());
                    }
                }
            }
    );
}
