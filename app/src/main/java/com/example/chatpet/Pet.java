package com.example.chatpet;

public interface Pet {
    String petActivity();
    PetState.Meters getPetState();
    String getPetType();
    String getPetName();
    String getUserID();

}
