package com.example.chatpet;
import android.util.Log;
public class Dragon implements Pet {
    private final PetState petState;

    public Dragon(PetState petState){
        this.petState=petState;
        petState.setPetType("Dragon");
    }
    private void update(int happinessMeter, int energyMeter, int hungerMeter){
        petState.setHappinessMeter(petState.getHappinessMeter()+happinessMeter);
        petState.setEnergyMeter(petState.getEnergyMeter()+energyMeter);
        petState.setHungerMeter(petState.getHungerMeter()+hungerMeter);
        Log.d("Dragon", "Happiness: "+petState.getHappinessMeter()+" Energy: "+petState.getEnergyMeter()+" Hunger: "+petState.getHungerMeter());
    }
    
    @Override
    public String chat(){
        petState.increaseHappiness(15);
        Log.d("Dragon", "Chat - Happiness: "+petState.getHappinessMeter()+" Energy: "+petState.getEnergyMeter()+" Hunger: "+petState.getHungerMeter());
        return getChatMessage();
    }
    
    @Override
    public String feed(){
        return feedMeal();
    }
    public String feedSnack() {
        if (petState.getHungerMeter() >= 100) {
            return petState.getPetName() + " is already full and can't eat more!";
        }
        int before = petState.getHungerMeter();
        petState.increaseHunger(10); // small snack
        int after = petState.getHungerMeter();

        Log.d("Dragon", "Snack - Happiness: " + petState.getHappinessMeter() +
                " Energy: " + petState.getEnergyMeter() +
                " Hunger: " + petState.getHungerMeter());

        return getFeedMessage(after - before);
    }

    public String feedMeal() {
        if (petState.getHungerMeter() >= 100) {
            return petState.getPetName() + " is already full and can't eat more!";
        }
        int before = petState.getHungerMeter();
        petState.increaseHunger(20);
        int after = petState.getHungerMeter();

        Log.d("Dragon", "Meal - Happiness: " + petState.getHappinessMeter() +
                " Energy: " + petState.getEnergyMeter() +
                " Hunger: " + petState.getHungerMeter());

        return getFeedMessage(after - before);
    }


    public String feedFeast() {
        if (petState.getHungerMeter() >= 100) {
            return petState.getPetName() + " is already full and can't eat more!";
        }
        int before = petState.getHungerMeter();
        petState.increaseHunger(35);
        int after = petState.getHungerMeter();

        Log.d("Dragon", "Feast - Happiness: " + petState.getHappinessMeter() +
                " Energy: " + petState.getEnergyMeter() +
                " Hunger: " + petState.getHungerMeter());

        return getFeedMessage(after - before);
    }
    
    @Override
    public String tuckIn(){
        petState.fillEnergy();
        Log.d("Dragon", "Tuck-in - Happiness: "+petState.getHappinessMeter()+" Energy: "+petState.getEnergyMeter()+" Hunger: "+petState.getHungerMeter());
        return getTuckInMessage();
    }
    
    // Level-based personality messages
    private String getChatMessage(){
        switch(petState.getPetLevel()){
            case 1:
                return petState.getPetName()+" roars happily! *jumps excitedly* Happiness +15";
            case 2:
                return petState.getPetName()+" rumbles contentedly. \"I enjoy our conversations.\" Happiness +15";
            case 3:
                return petState.getPetName()+" speaks with wisdom: \"Your presence brings me great joy, dear friend.\" Happiness +15";
            default:
                return petState.getPetName()+" roars happily! Happiness +15";
        }
    }
    
    private String getFeedMessage(int hungerGained){
        switch(petState.getPetLevel()){
            case 1:
                return petState.getPetName()+" devours the food messily! *crumbs everywhere* Hunger "+hungerGained;
            case 2:
                return petState.getPetName()+" eats gracefully. \"This is quite delicious, thank you.\" Hunger "+hungerGained;
            case 3:
                return petState.getPetName()+" savors the meal thoughtfully. \"Your care is much appreciated.\" Hunger "+hungerGained;
            default:
                return petState.getPetName()+" devours the food! Hunger "+hungerGained;
        }
    }
    
    private String getTuckInMessage(){
        switch(petState.getPetLevel()){
            case 1:
                return petState.getPetName()+" yawns widely and curls up. *snores loudly* Energy fully restored!";
            case 2:
                return petState.getPetName()+" settles down peacefully. \"Rest is essential for growth.\" Energy restored!";
            case 3:
                return petState.getPetName()+" rests with dignity. \"I shall dream of our adventures together.\" Energy restored!";
            default:
                return petState.getPetName()+" falls asleep. Energy restored!";
        }
    }
    

    public String breatheFire(){
        update(10, -15, -7);
        return petState.getPetName()+" breathes fire in the air!!!";
    }
    @Override
    public PetState.Meters getPetState(){
        return petState.getMeters();
    }
    
    @Override
    public PetState getPetStateObject(){
        return petState;
    }
    
    @Override
    public String getPetType(){
        return petState.getPetType();
    }
    @Override
    public String getPetName(){
        return petState.getPetName();

    }

    @Override
    public String getUserID(){
        return petState.getUserID();
    }
    
    @Override
    public int getPetLevel(){
        return petState.getPetLevel();
    }


}