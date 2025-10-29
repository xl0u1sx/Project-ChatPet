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
    public String petActivity(){
        update(+12, -10, -5);
        return petState.getPetName()+" walks around the magical forest with its magical unicorn horn!!!";
    }
    public String tellMagicalStory(){
        update(+10, -15, -7);
        return petState.getPetName()+"narrates a magical story with its magical unicorn horn!!!";
    }
    @Override
    public PetState.Meters getPetState(){
        return petState.getMeters();
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