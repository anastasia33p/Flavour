package com.example.flavour.models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.ArrayList;

public class Step implements Parcelable {
    private int step;
    private String description;
    private String image;

    public Step() {
    }

    public Step(int step) {
        this.step = step;
    }

    public Step(int step, String description, String image) {
        this.step = step;
        this.description = description;
        this.image = image;
    }

    protected Step(Parcel in) {
        step = in.readInt();
        description = in.readString();
        image = in.readString();
    }

    public static final Creator<Step> CREATOR = new Creator<Step>() {
        @Override
        public Step createFromParcel(Parcel in) {
            return new Step(in);
        }

        @Override
        public Step[] newArray(int size) {
            return new Step[size];
        }
    };

    public int getStep() {
        return step;
    }

    public void setStep(int step) {
        this.step = step;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeInt(this.step);
        dest.writeString(this.description);
        dest.writeString(this.image);
    }
}
