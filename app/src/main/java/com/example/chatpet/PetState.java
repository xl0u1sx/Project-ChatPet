package com.example.chatpet;
import androidx.annotation.NonNull;
public class PetState {
    private int happinessMeter=100;
    private int energyMeter=100;
    private int hungerMeter=100;
    private int petLevel=1; // Level starts at 1, max 3
    @NonNull private String petType;
    @NonNull private String userID;
    @NonNull private String  petName;

    public PetState(@NonNull String petCategory, @NonNull String userID, @NonNull String petName){
        this.petType =petCategory;
        this.userID=userID;
        this.petName=petName;
    }
    
    public int getPetLevel(){
        return petLevel;
    }
    
    public void setPetLevel(int level){
        this.petLevel = clamp(level, 1, 3); // Level is between 1 and 3
    }
    
    public boolean canLevelUp(){
        return petLevel < 3;
    }
    
    public void levelUp(){
        if(canLevelUp()){
            petLevel++;
        }
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
        this.happinessMeter=clamp(happinessMeter, 0, 100);
    }
    public void setEnergyMeter(int energyMeter){
        this.energyMeter=clamp(energyMeter, 0, 100);
    }
    public void setHungerMeter(int hungerMeter){
        this.hungerMeter=clamp(hungerMeter, 0, 100);
    }

    // Helper method to clamp values between min and max
    private int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }

    // Action methods for the three main interactions
    public void increaseHappiness(int amount) {
        setHappinessMeter(happinessMeter + amount);
    }

    public void increaseHunger(int amount) {
        setHungerMeter(hungerMeter + amount);
    }

    public void increaseEnergy(int amount) {
        setEnergyMeter(energyMeter + amount);
    }

    public void fillEnergy() {
        setEnergyMeter(100);
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
