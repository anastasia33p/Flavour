package com.example.flavour.viewmodels;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.flavour.models.Recipe;
import com.example.flavour.models.Step;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.List;

public class EditorViewModel extends ViewModel {
    FirebaseDatabase firebaseDatabase;
    DatabaseReference recipes;
    FirebaseStorage storage;
    StorageReference storageRef;
    Recipe recipe;
    MutableLiveData<Boolean> success = new MutableLiveData<>(false);
    private Uri imageUri;

    public void addRecipe(Recipe new_recipe){
        this.recipe = new_recipe;
        firebaseDatabase = FirebaseDatabase.getInstance();
        recipes = firebaseDatabase.getReference("recipes");
        String recipe_image=uploadImage(Uri.parse(recipe.getImage()));
        StorageReference imagesRef = storageRef.child(recipe_image);
        imagesRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                recipe.setImage(uri.toString());
                loadImage(0,recipe.getSteps());
            }
        });


    }
    private void loadImage(int iterator, List<Step> steps) {
        Step step = steps.get(iterator);
        StorageReference imagesRef;
        String step_image=uploadImage(Uri.parse(step.getImage()));
        imagesRef = storageRef.child(step_image);
        imagesRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                step.setImage(uri.toString());
                if (iterator < steps.size() - 1) {
                    loadImage(iterator + 1, steps);
                }
                else {
                    pushRecipe(steps);
                }
            }
        });
    }
    public void pushRecipe(List<Step> steps) {
        //recipe.setSteps((ArrayList<Step>) steps);
        recipes.push().setValue(recipe).
                addOnSuccessListener(aVoid -> {
                    success.setValue(true);
                })
                .addOnFailureListener(aVoid ->{
                    success.setValue(false);
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

    public String uploadImage(Uri imageUri) {
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();

        String imageName = imageUri.getLastPathSegment();

        StorageReference imagesRef = storageRef.child("images/" + imageName);

        imagesRef.putFile(imageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Log.d("UPLOAD", "Image uploaded successfully");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        Log.d("UPLOAD", "Image upload failed", exception);
                    }
                });
        return "images/" + imageName;
    }
    public MutableLiveData<Boolean> getSuccess() {
        return success;
    }
}
