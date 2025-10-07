package com.example.tanimart.data.model;

public class User {
    private String id;
    private String name;
    private String email;
    private String role;
    private String photoUrl;

    public User() {}

    // Constructor lengkap
    public User(String id, String name, String email, String role, String photoUrl) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.role = role;
        this.photoUrl = photoUrl;
    }

    // Constructor ringkas (untuk kompatibilitas lama)
    public User(String name, String email, String role) {
        this.name = name;
        this.email = email;
        this.role = role;
        this.photoUrl = "default"; // supaya tidak null
    }

    // Getter & Setter
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getPhotoUrl() { return photoUrl; }
    public void setPhotoUrl(String photoUrl) { this.photoUrl = photoUrl; }
}
