package com.example.chatpet;

public interface Pet {
    PetState.Meters getPetState();
    PetState getPetStateObject();
    String getPetType();
    String getPetName();
    String getUserID();
    int getPetLevel();
    
    // Three main care actions
    String chat();
    String feed();
    String tuckIn();

}
