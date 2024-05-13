package com.example.flavour.views;

import android.Manifest;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.flavour.adapters.ItemAdapter;
import com.example.flavour.databinding.FragmentEditorBinding;
import com.example.flavour.models.Recipe;
import com.example.flavour.models.Step;
import com.example.flavour.viewmodels.EditorViewModel;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import kotlin.Pair;

public class EditorFragment extends Fragment implements ItemAdapter.ItemEvents {

    private FragmentEditorBinding binding;
    private ItemAdapter adapter;
    private EditorViewModel viewModel;
    private int position;
    ArrayList<Step> list;

    private final ActivityResultLauncher<Uri> takePictureLauncher = registerForActivityResult(
            new ActivityResultContracts.TakePicture(),
            result -> {
                if (result) {
                    adapter.setImage(viewModel.getImageUri().toString(), position);
                } else {
                    viewModel.setImageUri(null);
                }
            }
    );

    private final ActivityResultLauncher<String> pickImageLauncher = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            uri -> {
                if (uri != null) {
                    adapter.setImage(uri.toString(), position);
                }
            }
    );

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentEditorBinding.inflate(inflater, container, false);
        disableBottomNav();
        Recipe recipe = new Recipe();
        viewModel = new ViewModelProvider(this).get(EditorViewModel.class);
        setRecycler();
        return binding.getRoot();
    }

    public void setRecycler() {
        adapter = new ItemAdapter(new Recipe());
        list = new ArrayList<>();
        list.add(new Step(1));
        adapter.setEvents(this);
        adapter.submitList(list);
        binding.list.setAdapter(adapter);
        binding.list.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    @Override
    public void showTimePickerDialog() {
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(),
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int selectedHour, int selectedMinute) {
                        adapter.updateTime(selectedHour, selectedMinute);
                    }
                }, hour, minute, true);
        timePickerDialog.setTitle("Выберите время");
        timePickerDialog.show();
    }

    @Override
    public void SaveData(Recipe recipe, ArrayList<Step> steps) {
        recipe.setSteps(steps);
/*        viewModel.getSuccess().observe(getViewLifecycleOwner(), aBoolean -> {
            if (aBoolean) {
                Toast.makeText(getContext(), "Рецепт сохранен", Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(getContext(), "Рецепт не сохранен", Toast.LENGTH_SHORT).show();
            }
        });*/
        viewModel.addRecipe(recipe);
        requireActivity().getSupportFragmentManager().popBackStack();
    }

    @Override
    public void chooseImage(int position) {
        this.position = position;
        showPictureDialog();
    }

    @Override
    public void addStep() {
        list.add(new Step(list.size()+1));
        adapter.notifyItemInserted(list.size());
    }


    private void showPictureDialog() {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getContext());
        builder.setTitle("Выберите источник изображения");
        String[] items = {"Галерея", "Камера"};
        builder.setItems(items, (dialog, which) -> {
            switch (which) {
                case 0:
                    pickImageLauncher.launch("image/*");
                    break;
                case 1:
                    captureImage();
                    break;
            }
        });
        builder.show();
    }
    private void captureImage() {
        viewModel.createImageUri(requireContext().getContentResolver());
        if (viewModel.getImageUri() != null) {
            takePictureLauncher.launch(viewModel.getImageUri());
        } else {
            Log.e(getClass().getCanonicalName(), "Error creating image URI");
        }
    }
    private void disableBottomNav() {
        ((SecondActivity) requireActivity()).disableBottomNav();
    }

    private void enableBottomNav() {
        ((SecondActivity) requireActivity()).enableBottomNav();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        enableBottomNav();
    }
}
