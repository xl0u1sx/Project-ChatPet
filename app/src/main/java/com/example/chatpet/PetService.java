package com.example.chatpet;
import android.util.Log;
import androidx.annotation.Nullable;

import java.lang.reflect.ParameterizedType;
import java.util.Locale;

public class PetService {
    private PetState petState;
    @Nullable private Pet pet;
    public Pet createPet(String petType, String petName, String userID) {
        this.petState = new PetState(petType, userID, petName);
        String my_pet=petType.toLowerCase(Locale.ROOT).trim();
        switch(my_pet){
            case "dragon":
                pet=new Dragon(petState);
                break;
            case "unicorn":
                pet=new Unicorn(petState);
                break;
            default:
                pet=new Unicorn(petState);
                Log.w("PetService", "Unknown petType '" + petType + "'. Defaulting to Unicorn.");

        }
        return pet;
    }
    public PetState.Meters getPetState(){
        if(pet==null)
            return new PetState("N/A","N/A", "N/A").getMeters();

        return petState.getMeters();
    }
    @Nullable public Pet getPet(){
        return pet;
    }



}
