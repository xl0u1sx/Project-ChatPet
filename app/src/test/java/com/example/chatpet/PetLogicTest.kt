package com.example.chatpet

import org.junit.Assert
import org.junit.Test
import java.util.Locale

/**
 * White-box tests written by: Aakanksha Peeru
 */
class PetLogicTest {
    // ----------------------------------------------------------
    // WHITE BOX TEST 1 — Feed only modifies hunger (Dragon)
    // ----------------------------------------------------------
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

    // ----------------------------------------------------------
    // WHITE BOX TEST 2 — Feed blocked when hunger = 100
    // ----------------------------------------------------------
    @Test
    fun testFeedBlockedAtFull() {
        val ps = PetState("Dragon", "u", "Pyro")
        val d = Dragon(ps)

        ps.setHungerMeter(100)
        ps.setEnergyMeter(40)
        ps.setHappinessMeter(60)

        val msg = d.feed()

        Assert.assertEquals(100, ps.getHungerMeter().toLong())
        Assert.assertEquals(40, ps.getEnergyMeter().toLong())
        Assert.assertEquals(60, ps.getHappinessMeter().toLong())
        Assert.assertTrue(msg.lowercase(Locale.getDefault()).contains("full"))
    }

    // ----------------------------------------------------------
    // WHITE BOX TEST 3 — tuckIn only modifies energy
    // ----------------------------------------------------------
    @Test
    fun testTuckInOnlyChangesEnergy() {
        val ps = PetState("Unicorn", "u", "Luna")
        val u = Unicorn(ps)

        ps.setEnergyMeter(20)
        ps.setHappinessMeter(55)
        ps.setHungerMeter(40)

        u.tuckIn()

        Assert.assertEquals(100, ps.getEnergyMeter().toLong())
        Assert.assertEquals(55, ps.getHappinessMeter().toLong())
        Assert.assertEquals(40, ps.getHungerMeter().toLong())
    }

    // ----------------------------------------------------------
    // WHITE BOX TEST 4 — tuck-in blocked when energy = 100
    // ----------------------------------------------------------
    @Test
    fun testTuckInBlockedAtFullEnergy() {
        val ps = PetState("Unicorn", "u", "Star")
        val un = Unicorn(ps)

        ps.setEnergyMeter(100)

        val msg = un.tuckIn()

        Assert.assertEquals(100, ps.getEnergyMeter().toLong())
        Assert.assertTrue(msg.lowercase(Locale.getDefault()).contains("rest"))
    }

    // ----------------------------------------------------------
    // WHITE BOX TEST 5 — special action reduces energy + hunger
    // ----------------------------------------------------------
    @Test
    fun testSpecialActionConsumesMeters_Dragon() {
        val ps = PetState("Dragon", "u", "Igneel")
        val d = Dragon(ps)

        ps.setEnergyMeter(50)
        ps.setHungerMeter(50)

        d.breatheFire() // internally: -15 energy, -7 hunger

        Assert.assertEquals(35, ps.getEnergyMeter().toLong())
        Assert.assertEquals(43, ps.getHungerMeter().toLong())
    }

}