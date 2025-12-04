package com.example.chatpet;

public class User {
    // regular Java class for user metadata
    private String username;
    private String password;
    private String firstName;
    private String lastName;
    private String email;
    private byte[] avatarImage;

    public User(String username, String password, String firstName, String lastName) {
        this.username = username;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = null;
        this.avatarImage = null;
    }

    public User(String username, String password, String firstName, String lastName, String email, byte[] avatarImage) {
        this.username = username;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.avatarImage = avatarImage;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public byte[] getAvatarImage() {
        return avatarImage;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setAvatarImage(byte[] avatarImage) {
        this.avatarImage = avatarImage;
    }
}
