package com.example.flavour.views;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.flavour.databinding.FragmentRegisterBinding;
import com.example.flavour.models.User;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterFragment extends Fragment {
    private FragmentRegisterBinding binding;
    FirebaseAuth auth;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference users;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentRegisterBinding.inflate(getLayoutInflater());
        auth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        users = firebaseDatabase.getReference("users");
        binding.sign.setOnClickListener(v -> {
            if (TextUtils.isEmpty(binding.nameInp.getText().toString())) {
                Snackbar.make(binding.getRoot(), "Введите имя", Snackbar.LENGTH_SHORT).show();
                return;
            }
            if (TextUtils.isEmpty(binding.role.getText().toString())) {
                Snackbar.make(binding.getRoot(), "Выберите роль", Snackbar.LENGTH_SHORT).show();
                return;
            }
            if (binding.passwordInp.getText().toString().length() < 6) {
                Snackbar.make(binding.getRoot(), "Пароль должен содержать не менее 6 символов", Snackbar.LENGTH_SHORT).show();
                return;
            }
            if (!binding.passwordInp2.getText().toString().equals(binding.passwordInp.getText().toString())) {
                Snackbar.make(binding.getRoot(), "Пароли не совпадают", Snackbar.LENGTH_SHORT).show();
                return;
            }
            auth.createUserWithEmailAndPassword(binding.emailInp.getText().toString(), binding.passwordInp.getText().toString())
                    .addOnSuccessListener(authResult -> {
                        User user = new User();
                        user.setEmail(binding.emailInp.getText().toString());
                        user.setName(binding.nameInp.getText().toString());
                        user.setRole(binding.role.getText().toString());
                        user.setId(authResult.getUser().getUid());
                        users.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(user)
                                .addOnSuccessListener(aVoid -> {
                                    ((MainActivity) requireActivity()).toSecondActivity();
                                });
                    })
                    .addOnFailureListener(e -> Snackbar.make(binding.getRoot(), e.getMessage(), Snackbar.LENGTH_SHORT).show());

        });
        return binding.getRoot();
    }
}
