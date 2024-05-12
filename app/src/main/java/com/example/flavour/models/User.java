package com.example.flavour.models;

import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class User {

    private String email;
    private String role;
    private String name;
    private String id;
    private ArrayList<Long> favorites;

    public User() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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
        if (favorites.size() == 0) return new ArrayList<>();
        return favorites;
    }

    public void setFavorites(Object favorites) {
        if (favorites instanceof Map) {
            this.favorites = new ArrayList<>(((HashMap<String, Long>) favorites).values());
        } else {
            this.favorites = (ArrayList<Long>) favorites;
        }
    }
}
