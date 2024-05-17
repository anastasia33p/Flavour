package com.example.flavour.views;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.example.flavour.adapters.RecipeAdapter;
import com.example.flavour.databinding.FragmentSearchBinding;
import com.example.flavour.models.Recipe;
import com.example.flavour.viewmodels.SearchViewModel;
import com.example.flavour.workers.NotificationWorker;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class SearchFragment extends Fragment implements RecipeAdapter.RecipeEvents {
    FragmentSearchBinding binding;
    private SearchViewModel viewModel;
    private SharedPreferences preferences;
    private MaterialAlertDialogBuilder builder;
    private RecipeAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentSearchBinding.inflate(inflater, container, false);

        showProgress();
        AlertDialog alertDialog = builder.create();
        alertDialog.show();

        preferences = getActivity().getSharedPreferences("settings", Context.MODE_PRIVATE);

        adapter = new RecipeAdapter();
        adapter.setEvents(this);

        workViewModel(alertDialog);

        setListeners();
        binding.list.setAdapter(adapter);
        binding.list.setLayoutManager(new LinearLayoutManager(getContext()));

        return binding.getRoot();
    }

    public void setListeners() {
        binding.add.setOnClickListener(v -> {
            ((SecondActivity) requireActivity()).navigateToEditorFragment();
        });
        binding.searchField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {            }

            @Override
            public void afterTextChanged(Editable s) {
                adapter.search(s);
            }
        });
    }

    public void workViewModel(AlertDialog alertDialog) {
        viewModel = new ViewModelProvider(this).get(SearchViewModel.class);
        viewModel.getRecipe().observe(getViewLifecycleOwner(), recipes -> {
            adapter.submitList(recipes);
            alertDialog.dismiss();
        });
        createWorker();
        viewModel.getRecipes();
        viewModel.getMUser().observe(getViewLifecycleOwner(), user -> {
            if (Objects.equals(user.getRole(), "Любитель")) {
                Log.d("USER", user.getRole()+" "+user.getName());
                binding.add.setVisibility(View.GONE);
            }
            if (Objects.equals(user.getRole(), "Проффесионал")) {
                Log.d("USER", user.getRole()+" "+user.getName());
                binding.add.setVisibility(View.VISIBLE);
            }
        });
        viewModel.getUser();
    }

    @Override
    public void openRecipe(Recipe recipe) {
        ((SecondActivity) requireActivity()).navigateToRecipeFragment(recipe);
    }

    @Override
    public void toFavorite(Recipe recipe, boolean is_favorite) {
        viewModel.addFavorite(recipe, is_favorite);
    }

    public void createWorker() {
        if (preferences.getBoolean("notificationOn", false)){
            Log.d("Notifications", "createWorker: on");
            PeriodicWorkRequest myWorkRequest = new PeriodicWorkRequest.Builder(NotificationWorker.class, 30, TimeUnit.MINUTES)
                    .build();
            WorkManager.getInstance(requireContext()).enqueue(myWorkRequest);
        }
    }
    private void showProgress() {
        builder = new MaterialAlertDialogBuilder(getContext());
        builder.setTitle("Загрузка...");
        builder.setMessage("Пожалуйста, ожидайте...");

        ProgressBar progressBar = new ProgressBar(getContext());
        builder.setView(progressBar);
    }
}
