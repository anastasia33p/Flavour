package com.example.flavour.models;

import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;

public class User {

    private String email;
    private String password;
    private String role;
    private String name;
    private ArrayList<Long> favorites;

    public User() {}
    public User(String email, String password, String role, String name) {
        this.email = email;
        this.password = password;
        this.role = role;
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<Long> getFavorites() {
        return favorites;
    }

    public void setFavorites(ArrayList<Long> favorites) {
        this.favorites = favorites;
    }
}
