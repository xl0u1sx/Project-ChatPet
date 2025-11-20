package com.example.chatpet

import org.junit.Assert
import org.junit.Test
import java.util.Locale

// WHITE BOX TEST CASES FOR TESTING PET ACTIVITY

class PetLogicTest {

    // TEST CASE 1: Feeding the pet should only alter the hunger meter, leaving energy and
    // happiness meter unchanged
    @Test
    fun testFeedOnlyChangesHunger_Dragon() {
        val ps = PetState("Dragon", "u", "Draco")
        val d = Dragon(ps)

        ps.setHappinessMeter(50)
        ps.setEnergyMeter(40)
        ps.setHungerMeter(30)

        d.feed()

        Assert.assertEquals(50, ps.getHappinessMeter().toLong())
        Assert.assertEquals(40, ps.getEnergyMeter().toLong())
        Assert.assertTrue(ps.getHungerMeter() > 30)
    }



    // TEST CASE 2: Feeding the pet is invalid when the hunger is already at maximum = 100

    @Test
    fun testFeedBlockedAtFull() {
        val ps = PetState("Dragon", "u1", "P123")
        val d = Dragon(ps)

        ps.setHungerMeter(100)
        ps.setEnergyMeter(80)
        ps.setHappinessMeter(80)

        val msg = d.feed()

        Assert.assertEquals(100, ps.getHungerMeter().toLong())
        Assert.assertEquals(80, ps.getEnergyMeter().toLong())
        Assert.assertEquals(80, ps.getHappinessMeter().toLong())
        Assert.assertTrue(msg.lowercase(Locale.getDefault()).contains("full"))
    }

    // TEST CASE 3: Tucking in the pet should only alter the energy meter,
    // leaving happiness and hunger unchanged
    @Test
    fun testTuckInOnlyChangesEnergy() {
        val ps = PetState("Unicorn", "u2", "L123")
        val u = Unicorn(ps)

        ps.setEnergyMeter(20)
        ps.setHappinessMeter(50)
        ps.setHungerMeter(50)

        u.tuckIn()

        Assert.assertEquals(100, ps.getEnergyMeter().toLong())
        Assert.assertEquals(50, ps.getHappinessMeter().toLong())
        Assert.assertEquals(50, ps.getHungerMeter().toLong())
    }


    // TEST CASE 4: Tucking in the pet is blocked when the energy is already at maximum = 100
    @Test
    fun testTuckInBlockedAtFullEnergy() {
        val ps = PetState("Unicorn", "u3", "S123")
        val un = Unicorn(ps)

        ps.setEnergyMeter(100)

        val msg = un.tuckIn()

        Assert.assertEquals(100, ps.getEnergyMeter().toLong())
        Assert.assertTrue(msg.lowercase(Locale.getDefault()).contains("rest"))
    }

    // TEST CASE 5: The petAction button alters the hunger and energy meter by decrements
    @Test
    fun testSpecialActionConsumesMeters_Dragon() {
        val ps = PetState("Dragon", "u4", "I123")
        val d = Dragon(ps)

        ps.setEnergyMeter(100)
        ps.setHungerMeter(100)

        d.breatheFire() // internally: -15 energy, -7 hunger

        Assert.assertEquals(85, ps.getEnergyMeter().toLong())
        Assert.assertEquals(93, ps.getHungerMeter().toLong())
    }

}