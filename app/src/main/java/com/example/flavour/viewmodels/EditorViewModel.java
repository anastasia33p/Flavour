package com.example.flavour.viewmodels;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.flavour.models.Recipe;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class EditorViewModel extends ViewModel {
    FirebaseDatabase firebaseDatabase;
    DatabaseReference recipes;
    private Uri imageUri;

    public void addRecipe(Recipe recipe){
        firebaseDatabase = FirebaseDatabase.getInstance();
        recipes = firebaseDatabase.getReference("recipes");
        recipes.push().setValue(recipe).
                addOnSuccessListener(aVoid -> {
                    Log.d("ADD","added");
                })
                .addOnFailureListener(aVoid ->{
                    Log.d("ADD","failed");
                });
    }
    public void createImageUri(ContentResolver resolver) {
        String imageFileName = "photo";
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, imageFileName);
        contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg");
        Uri imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
        setImageUri(imageUri);
    }
    public Uri getImageUri() {
        return imageUri;
    }

    public void setImageUri(Uri imageUri) {
        this.imageUri = imageUri;
    }
}
