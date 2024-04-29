package com.example.flavour.views;

import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.PopupMenu;
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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        setData();
        binding.menu.setOnClickListener(v -> {
            setMenu(inflater);
        });
        return binding.getRoot();
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
                .setMessage("• Программа представляет собой кулинарную книгу, в которой вы можете искать рецепты и добавлять их в избранное. " +
                        "Вы можете создавать свои рецепты если являетесь поваром\n" +
                        "• Инструкция - для использования программы, начните с выбора вкладки Рецепты. " +
                        "Здесь вы можете использовать поиск по ключевым словам, чтобы найти нужное блюдо. " +
                        "После того, как вы нашли интересующий вас рецепт, вы можете добавить его в избранное, нажав на соответствующую кнопку. " +
                        "В разделе Избранные вы сможете быстро найти сохраненные рецепты. Во вкладке Профиль вы можете найти информацию о программе." )
                .setPositiveButton("Ok", null);
        builder.show();
    }

    public void setMenu(LayoutInflater inflater) {
        PopupMenu popupMenu = new PopupMenu(inflater.getContext(), binding.menu);
        popupMenu.getMenuInflater().inflate(R.menu.profile_menu, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.author) {
                showAuthor(inflater);
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
        viewModel = new ViewModelProvider(this).get(ProfileViewModel.class);
        viewModel.getMUser().observe(getViewLifecycleOwner(), user -> {
            binding.name.setText(user.getName());
            binding.role.setText(user.getRole());
        });
        viewModel.getUser();
    }
}
