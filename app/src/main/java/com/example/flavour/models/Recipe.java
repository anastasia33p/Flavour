package com.example.flavour.models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.ArrayList;

public class Recipe implements Parcelable {

    private long id;
    private String name;
    private String image;
    private ArrayList<String> ingredients = new ArrayList<>();
    private ArrayList<Step> steps = new ArrayList<>();
    private long duration = 0;
    private int difficulty = 0;
    private boolean favorite = false;


    public Recipe() {
    }

    public Recipe(String name, String image, ArrayList<String> ingredients, long id, ArrayList<Step> steps, long duration, int difficulty) {
        this.name = name;
        this.image = image;
        this.ingredients = ingredients;
        this.id = id;
        this.steps = steps;
        this.duration = duration;
        this.difficulty = difficulty;
    }

    protected Recipe(Parcel in) {
        id = in.readLong();
        name = in.readString();
        image = in.readString();
        ingredients = in.createStringArrayList();
        duration = in.readLong();
        difficulty = in.readInt();
        favorite = in.readByte() != 0;
    }

    public static final Creator<Recipe> CREATOR = new Creator<Recipe>() {
        @Override
        public Recipe createFromParcel(Parcel in) {
            return new Recipe(in);
        }

        @Override
        public Recipe[] newArray(int size) {
            return new Recipe[size];
        }
    };

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public ArrayList<String> getIngredients() {
        return ingredients;
    }

    public void setIngredients(ArrayList<String> ingredients) {
        this.ingredients = ingredients;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ArrayList<Step> getSteps() {
        return steps;
    }

    public void setSteps(ArrayList<Step> steps) {
        this.steps = steps;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public int getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(int difficulty) {
        this.difficulty = difficulty;
    }

    public boolean isFavorite() {
        return favorite;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeString(this.name);
        dest.writeString(this.image);
        dest.writeList(this.ingredients);
        dest.writeList(this.steps);
        dest.writeLong(this.duration);
        dest.writeInt(this.difficulty);
        dest.writeByte(this.favorite ? (byte) 1 : (byte) 0);
    }
}
