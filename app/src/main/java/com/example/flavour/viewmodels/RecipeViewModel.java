package com.example.flavour.viewmodels;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.flavour.models.Recipe;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RecipeViewModel extends ViewModel {
    DatabaseReference users;
    MutableLiveData<Boolean> addFavorite = new MutableLiveData<>();
    FirebaseDatabase db;
    public void addFavorite(Recipe recipe, boolean is_favorite) {
        db = FirebaseDatabase.getInstance();
        users = db.getReference("users");
        if (!is_favorite) {
            users.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .child("favorites").child(recipe.getId().toString()).setValue(recipe.getId())
                    .addOnSuccessListener(aVoid -> addFavorite.postValue(true))
                    .addOnFailureListener(e -> addFavorite.postValue(false));
        }
        else {
            users.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .child("favorites").child(recipe.getId().toString()).removeValue()
                    .addOnSuccessListener(aVoid -> addFavorite.postValue(false))
                    .addOnFailureListener(e -> addFavorite.postValue(true));
        }
    }

    public MutableLiveData<Boolean> getAddFavorite() {
        return addFavorite;
    }
}
