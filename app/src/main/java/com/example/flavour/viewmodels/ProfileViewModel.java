package com.example.flavour.viewmodels;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.flavour.models.Recipe;
import com.example.flavour.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class ProfileViewModel extends ViewModel {
    DatabaseReference users;
    MutableLiveData<User> user = new MutableLiveData<>();
    FirebaseDatabase db;

    public void getUser() {
        db = FirebaseDatabase.getInstance();
        users = db.getReference("users");
        users.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DataSnapshot data = task.getResult();
                for (DataSnapshot snapshot : data.getChildren()) {
                    User val = snapshot.getValue(User.class);
                    if (val.getId().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                        user.postValue(val);
                    }
                }
            }
        });
    }
    public void exit() {
        FirebaseAuth.getInstance().signOut();
    }
    public MutableLiveData<User> getMUser() {
        return user;
    }
}
