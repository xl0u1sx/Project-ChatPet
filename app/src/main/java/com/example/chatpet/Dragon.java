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
        return petState.getPetName()+" roars happily! Happiness increased by 15.";
    }
    
    @Override
    public String feed(){
        petState.increaseHunger(30);
        petState.increaseEnergy(10);
        Log.d("Dragon", "Feed - Happiness: "+petState.getHappinessMeter()+" Energy: "+petState.getEnergyMeter()+" Hunger: "+petState.getHungerMeter());
        return petState.getPetName()+" devours the food with delight! Hunger increased by 30.";
    }
    
    @Override
    public String tuckIn(){
        petState.fillEnergy();
        Log.d("Dragon", "Tuck-in - Happiness: "+petState.getHappinessMeter()+" Energy: "+petState.getEnergyMeter()+" Hunger: "+petState.getHungerMeter());
        return petState.getPetName()+" curls up in its cozy nest and falls asleep. Energy fully restored!";
    }
    
    // @Override
    // public String petAction(){
    //     update(15, -10, -5);
    //     return petState.getPetName()+" flies through the air with its wings wide open!!!";
    // }
    
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


}