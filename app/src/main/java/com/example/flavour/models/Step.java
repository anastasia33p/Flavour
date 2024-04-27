package com.example.flavour.models;

import java.util.ArrayList;

public class Step {
    private int step;
    private String description;
    private String image;
    public Step() {}
    public Step(int step, String description, String image) {
        this.step = step;
        this.description = description;
        this.image = image;
    }

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
}
