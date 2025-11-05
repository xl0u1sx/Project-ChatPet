package com.example.chatpet;

public interface Pet {
    PetState.Meters getPetState();
    String getPetType();
    String getPetName();
    String getUserID();
    
    // Three main care actions
    String chat();
    String feed();
    String tuckIn();

}
