package com.example.chatpet;
import android.util.Log;
public class Unicorn implements Pet {
    private final PetState petState;

    public Unicorn(PetState petState){
        this.petState=petState;
        petState.setPetType("Unicorn");
    }
    private void update(int happinessMeter, int energyMeter, int hungerMeter){
        petState.setHappinessMeter(petState.getHappinessMeter()+happinessMeter);
        petState.setEnergyMeter(petState.getEnergyMeter()+energyMeter);
        petState.setHungerMeter(petState.getHungerMeter()+hungerMeter);
        Log.d("Unicorn", "Happiness: "+petState.getHappinessMeter()+" Energy: "+petState.getEnergyMeter()+" Hunger: "+petState.getHungerMeter());
    }
    
    @Override
    public String chat(){
        petState.increaseHappiness(15);
        Log.d("Unicorn", "Chat - Happiness: "+petState.getHappinessMeter()+" Energy: "+petState.getEnergyMeter()+" Hunger: "+petState.getHungerMeter());
        return petState.getPetName()+" sparkles with joy! Happiness increased by 15.";
    }
    
    @Override
    public String feed(){
        petState.increaseHunger(30);
        petState.increaseEnergy(10);
        Log.d("Unicorn", "Feed - Happiness: "+petState.getHappinessMeter()+" Energy: "+petState.getEnergyMeter()+" Hunger: "+petState.getHungerMeter());
        return petState.getPetName()+" munches on magical treats! Hunger increased by 30.";
    }
    
    @Override
    public String tuckIn(){
        petState.fillEnergy();
        Log.d("Unicorn", "Tuck-in - Happiness: "+petState.getHappinessMeter()+" Energy: "+petState.getEnergyMeter()+" Hunger: "+petState.getHungerMeter());
        return petState.getPetName()+" nestles down in a bed of clouds. Energy fully restored!";
    }
    
    // @Override
    // public String petAction(){
    //     update(+12, -10, -5);
    //     return petState.getPetName()+" walks around the magical forest with its magical unicorn horn!!!";
    // }
    public String tellMagicalStory(){
        update(+10, -15, -7);
        return petState.getPetName()+" narrates a magical story with its magical unicorn horn!!!";
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