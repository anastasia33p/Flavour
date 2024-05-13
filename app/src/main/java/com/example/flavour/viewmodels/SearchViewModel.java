package com.example.flavour.viewmodels;

import android.util.Log;

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

public class SearchViewModel extends ViewModel {
    DatabaseReference users;
    MutableLiveData<Boolean> addFavorite = new MutableLiveData<>();
    FirebaseDatabase db;
    MutableLiveData<User> user = new MutableLiveData<>();
    DatabaseReference recipes;
    MutableLiveData<List<Recipe>> recipe = new MutableLiveData<>();

    public void getRecipes() {
        db = FirebaseDatabase.getInstance();
        recipes = db.getReference("recipes");
        List<Recipe> recipesList = new ArrayList<>();
        recipes.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DataSnapshot data = task.getResult();
                for (DataSnapshot snapshot : data.getChildren()) {
                    Recipe val = snapshot.getValue(Recipe.class);
                    recipesList.add(val);
                }
                recipe.postValue(recipesList);
            }
        });
    }


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
    public MutableLiveData<User> getMUser() {
        return user;
    }

    public MutableLiveData<Boolean> getAddFavorite() {
        return addFavorite;
    }
    public MutableLiveData<List<Recipe>> getRecipe() {
        return recipe;
    }

}
