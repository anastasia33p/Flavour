package com.example.flavour.views;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.flavour.R;
import com.example.flavour.databinding.FragmentProfileBinding;
import com.example.flavour.viewmodels.FavoritesViewModel;
import com.example.flavour.viewmodels.ProfileViewModel;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class ProfileFragment extends Fragment {
    private FragmentProfileBinding binding;
    private ProfileViewModel viewModel;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    private final ActivityResultLauncher<String> requestPermission = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(),
            isGranted -> {
                if (isGranted) {
                    editor.putBoolean("notificationsAllowed", true).commit();
                    binding.notswitch.setChecked(true);
                } else {
                    editor.putBoolean("notificationsAllowed", false).commit();
                    binding.notswitch.setChecked(false);
                }
            });

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        viewModel = new ViewModelProvider(this).get(ProfileViewModel.class);

        preferences = requireActivity().getSharedPreferences("settings", Context.MODE_PRIVATE);
        editor = preferences.edit();

        binding.menu.setOnClickListener(v -> {
            setMenu(inflater);
        });
        binding.exit.setOnClickListener(v -> {
            MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireContext());
            builder
                    .setTitle("Выход")
                    .setMessage("Вы хотите выйти из аккаунта?")
                    .setPositiveButton("Да", (dialog, which) -> {
                        viewModel.exit();
                        editor.remove("name").commit();
                        editor.remove("role").commit();
                        ((SecondActivity) requireActivity()).navigateToMainActivity();
                    })
                    .setNegativeButton("Нет", null);
            builder.show();
        });
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        if (preferences.getString("name", null) == null || preferences.getString("role", null) == null) {
            setData();
        } else {
            binding.name.setText(preferences.getString("name", ""));
            binding.role.setText(preferences.getString("role", ""));
        }
        binding.notswitch.setChecked(preferences.getBoolean("notificationOn", false));

        //Toast.makeText(requireContext(), "Уведомления " + (preferences.getBoolean("notificationOn", false) ? "включены" : "выключены"), Toast.LENGTH_SHORT).show();
        binding.notswitch.setOnClickListener(buttonView -> setNotifications(binding.notswitch.isChecked()));
        super.onViewCreated(view, savedInstanceState);
    }

    private void setNotifications(boolean isChecked) {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            requestPermission.launch(Manifest.permission.POST_NOTIFICATIONS);

        } else {

            editor.putBoolean("notificationsAllowed", true).commit();
            editor.putBoolean("notificationOn", isChecked).commit();
        }
    }

    public void showAuthor(LayoutInflater inflater) {
        ImageView view = new ImageView(inflater.getContext());
        view.setImageResource(R.drawable.ava);
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(inflater.getContext());
        builder
                .setTitle("Об авторе")
                .setView(view)
                .setMessage("Петухова А.С.\nИКБО-03-22 \n2024\nanastasiamountain2008@gmail.com")
                .setPositiveButton("Ok", null);
        builder.show();
    }

    public void showProgram(LayoutInflater inflater) {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(inflater.getContext());
        builder
                .setTitle("О программе")
                .setMessage(getString(R.string.program_about))
                .setPositiveButton("Ok", null);
        builder.show();
    }

    public void setMenu(LayoutInflater inflater) {
        PopupMenu popupMenu = new PopupMenu(inflater.getContext(), binding.menu);
        popupMenu.getMenuInflater().inflate(R.menu.profile_menu, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.author) {
                showProgram(inflater);
                return true;
            } else if (itemId == R.id.program) {
                showProgram(inflater);
                return true;
            } else {
                return false;
            }
        });
        popupMenu.show();
    }

    public void setData() {

        viewModel.getMUser().observe(getViewLifecycleOwner(), user -> {
            binding.name.setText(user.getName());
            binding.role.setText(user.getRole());


            editor.putString("name", binding.name.getText().toString()).commit();
            editor.putString("role", binding.role.getText().toString()).commit();
        });
        viewModel.getUser();
    }
}
