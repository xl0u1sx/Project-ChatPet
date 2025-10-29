package com.example.chatpet;
import androidx.annotation.NonNull;
public class PetState {
    private int happinessMeter=100;
    private int energyMeter=100;
    private int hungerMeter=100;
    @NonNull private String petType;
    @NonNull private String userID;
    @NonNull private String  petName;

    public PetState(@NonNull String petCategory, @NonNull String userID, @NonNull String petName){
        this.petType =petCategory;
        this.userID=userID;
        this.petName=petName;
    }
    public int getHappinessMeter(){
        return happinessMeter;
    }
    public int getEnergyMeter(){
        return energyMeter;
    }
    public int getHungerMeter(){
        return hungerMeter;
    }
    public void setHappinessMeter(int happinessMeter){
        this.happinessMeter=happinessMeter;
    }
    public void setEnergyMeter(int energyMeter){
        this.energyMeter=energyMeter;
    }
    public void setHungerMeter(int hungerMeter){
        this.hungerMeter=hungerMeter;
    }
    @NonNull public String getPetType(){
        return petType;
    }
    @NonNull public String getUserID(){
        return userID;
    }
    @NonNull public String getPetName(){
        return petName;
    }
    public void setPetType(@NonNull String petType){
        this.petType = petType;
    }
    public void setPetName(@NonNull String petName){
        this.petName=petName;
    }
    public void setUserID(@NonNull String userID){
        this.userID=userID;
    }


    public static final class Meters{
        public final int happiness;
        public final int energy;
        public final int hunger;
        Meters(int happiness, int energy, int hunger){
            this.happiness=happiness;
            this.energy=energy;
            this.hunger=hunger;
        }
        @NonNull
        @Override public String toString(){
            return "Happiness: " + happiness + " Energy: " + energy + " Hunger: " + hunger;
        }
    }
    public Meters getMeters(){
        return new Meters(happinessMeter,energyMeter,hungerMeter);
    }
}
