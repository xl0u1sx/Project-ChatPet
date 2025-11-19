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
        return getChatMessage();
    }
    
    @Override
    public String feed(){
        if (petState.getHungerMeter() >= 100) {
            return petState.getPetName() + " is already full and can't eat more!";
        }
        int before=petState.getHungerMeter();
        petState.increaseHunger(20);
        int after=petState.getHungerMeter();

        Log.d("Unicorn", "Feed - Happiness: "+petState.getHappinessMeter()+" Energy: "+petState.getEnergyMeter()+" Hunger: "+petState.getHungerMeter());
        return getFeedMessage(after-before);
    }
    
    @Override
    public String tuckIn(){
        petState.fillEnergy();
        Log.d("Unicorn", "Tuck-in - Happiness: "+petState.getHappinessMeter()+" Energy: "+petState.getEnergyMeter()+" Hunger: "+petState.getHungerMeter());
        return getTuckInMessage();
    }
    
    // Level-based personality messages
    private String getChatMessage(){
        switch(petState.getPetLevel()){
            case 1:
                return petState.getPetName()+" sparkles with joy! *prances around* Happiness +15";
            case 2:
                return petState.getPetName()+" glows warmly. \"I cherish our time together.\" Happiness +15";
            case 3:
                return petState.getPetName()+" radiates serenity: \"Your kindness enriches my spirit, dear companion.\" Happiness +15";
            default:
                return petState.getPetName()+" sparkles with joy! Happiness +15";
        }
    }
    
    private String getFeedMessage(int hungerGained){
        switch(petState.getPetLevel()){
            case 1:
                return petState.getPetName()+" munches eagerly! *magical crumbs float away* Hunger "+hungerGained;
            case 2:
                return petState.getPetName()+" eats delicately. \"These treats are wonderful.\" Hunger "+hungerGained;
            case 3:
                return petState.getPetName()+" dines with elegance. \"Your generosity warms my heart.\" Hunger "+hungerGained;
            default:
                return petState.getPetName()+" munches on treats! Hunger "+hungerGained;
        }
    }
    
    private String getTuckInMessage(){
        switch(petState.getPetLevel()){
            case 1:
                return petState.getPetName()+" nestles in clouds and giggles. *sparkles while sleeping* Energy restored!";
            case 2:
                return petState.getPetName()+" rests gracefully. \"Dreams await me in the starlight.\" Energy restored!";
            case 3:
                return petState.getPetName()+" slumbers peacefully. \"May my dreams be as beautiful as our friendship.\" Energy restored!";
            default:
                return petState.getPetName()+" nestles down. Energy restored!";
        }
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
    
    @Override
    public int getPetLevel(){
        return petState.getPetLevel();
    }


}