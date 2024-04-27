package com.example.flavour.viewmodels;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.flavour.models.Recipe;
import com.example.flavour.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class FavoritesViewModel extends ViewModel {
    MutableLiveData<Boolean> addFavorite = new MutableLiveData<>();
    FirebaseDatabase firebaseDatabase;
    DatabaseReference users;
    DatabaseReference recipes;
    MutableLiveData<List<Recipe>> favorites = new MutableLiveData<>();
    List<Long> favoritesList = new ArrayList<>();

    public void getListFavorite() {
        getIdFavorite();
        firebaseDatabase = FirebaseDatabase.getInstance();
        recipes = firebaseDatabase.getReference("recipes");
        List<Recipe> recipesList = new ArrayList<>();
        recipes.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DataSnapshot data = task.getResult();
                for (DataSnapshot snapshot : data.getChildren()) {
                    Recipe val = snapshot.getValue(Recipe.class);
                    if (val != null && favoritesList.contains(val.getId()))
                        recipesList.add(val);
                }
                favorites.postValue(recipesList);
            }
        });
    }

    public void getIdFavorite() {
        firebaseDatabase = FirebaseDatabase.getInstance();
        users = firebaseDatabase.getReference("users");
        users.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).get()
                .addOnCompleteListener(snapshotTask -> {
                    User current = snapshotTask.getResult().getValue(User.class);
                    if (current != null && current.getFavorites() != null) {
                        favoritesList = current.getFavorites();
                    }
                });
    }
    public void addFavorite(Recipe recipe, boolean is_favorite) {
        firebaseDatabase = FirebaseDatabase.getInstance();
        users = firebaseDatabase.getReference("users");
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
    public MutableLiveData<List<Recipe>> getFavorites() {
        return favorites;
    }
}
