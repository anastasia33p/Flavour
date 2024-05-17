package com.example.flavour.views;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.flavour.databinding.FragmentSignBinding;
import com.example.flavour.views.MainActivity;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignFragment extends Fragment {
    FragmentSignBinding binding;
    FirebaseAuth auth;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference users;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentSignBinding.inflate(getLayoutInflater());
        auth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        users = firebaseDatabase.getReference("users");
        setButtons();
        return binding.getRoot();
    }

    private void setButtons() {
        binding.register.setOnClickListener(v -> {
            ((MainActivity) requireActivity()).navigateToSecondFragment();
        });
        binding.sign.setOnClickListener(v -> {
            if (TextUtils.isEmpty(binding.emailInp.getText().toString())) {
                Snackbar.make(binding.getRoot(), "Введите почту", Snackbar.LENGTH_SHORT).show();
                return;
            }
            if (binding.passwordInp.getText().toString().length() < 6) {
                Snackbar.make(binding.getRoot(), "Пароль должен содержать не менее 6 символов", Snackbar.LENGTH_SHORT).show();
                return;
            }
            auth.signInWithEmailAndPassword(binding.emailInp.getText().toString(), binding.passwordInp.getText().toString())
                    .addOnSuccessListener(authResult -> {
                        ((MainActivity) requireActivity()).toSecondActivity();
                    })
                    .addOnFailureListener(e -> Snackbar.make(binding.getRoot(), "Ошибка в логине или пароле", Snackbar.LENGTH_SHORT).show());
        });
    }
}

