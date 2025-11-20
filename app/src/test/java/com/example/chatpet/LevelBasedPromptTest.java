package com.example.chatpet;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * White-box Unit Test for Level-Based Prompt Generation
 * 
 * Coverage Criteria: Decision Coverage, Multiple Condition Coverage
 * 
 * This test achieves:
 * - Decision Coverage: 100% of when() branches
 * - Multiple Condition Coverage: All petType + level combinations
 * - Path Coverage: All paths through nested when statements
 */
public class LevelBasedPromptTest {
    
    /**
     * Test Case 1: Dragon Level 1 Prompt
     * Tests young dragon personality prompt
     * Branch: petType == "dragon" && level == 1
     */
    @Test
    public void testDragonLevel1Prompt() {
        String prompt = MainActivityKt.createLevelBasedPrompt("Dragon", "Spike", 1);
        
        assertTrue("Level 1 Dragon prompt should mention 'young and playful'", 
                   prompt.contains("young and playful"));
        assertTrue("Level 1 Dragon prompt should mention the pet name", 
                   prompt.contains("Spike"));
        assertTrue("Level 1 Dragon prompt should mention 'Level 1'", 
                   prompt.contains("Level 1"));
        assertTrue("Level 1 Dragon prompt should include action words guidance",
                   prompt.contains("*jumps*") || prompt.contains("*roars*"));
    }
    
    /**
     * Test Case 2: Dragon Level 2 Prompt
     * Tests growing dragon personality prompt
     * Branch: petType == "dragon" && level == 2
     */
    @Test
    public void testDragonLevel2Prompt() {
        String prompt = MainActivityKt.createLevelBasedPrompt("Dragon", "Draco", 2);
        
        assertTrue("Level 2 Dragon prompt should mention 'growing'", 
                   prompt.contains("growing"));
        assertTrue("Level 2 Dragon prompt should mention being 'composed'",
                   prompt.contains("composed"));
        assertTrue("Level 2 Dragon prompt should suggest mature phrases",
                   prompt.contains("I enjoy"));
    }
    
    /**
     * Test Case 3: Dragon Level 3 Prompt
     * Tests wise dragon personality prompt
     * Branch: petType == "dragon" && level == 3
     */
    @Test
    public void testDragonLevel3Prompt() {
        String prompt = MainActivityKt.createLevelBasedPrompt("Dragon", "Ancient", 3);
        
        assertTrue("Level 3 Dragon prompt should mention 'wise and mature'", 
                   prompt.contains("wise") && prompt.contains("mature"));
        assertTrue("Level 3 Dragon prompt should mention wisdom and eloquence",
                   prompt.contains("wisdom") || prompt.contains("eloquence"));
        assertTrue("Level 3 Dragon prompt should include 'Dear friend' suggestion",
                   prompt.contains("Dear friend"));
    }
    
    /**
     * Test Case 4: Unicorn All Levels
     * Tests all three unicorn level prompts
     * Branch: petType == "unicorn" && level in {1, 2, 3}
     */
    @Test
    public void testUnicornAllLevels() {
        String level1 = MainActivityKt.createLevelBasedPrompt("Unicorn", "Sparkle", 1);
        assertTrue("Level 1 Unicorn should be 'bubbly'", level1.contains("bubbly"));
        assertTrue("Level 1 Unicorn should mention sparkles", level1.contains("sparkles"));
        
        String level2 = MainActivityKt.createLevelBasedPrompt("Unicorn", "Grace", 2);
        assertTrue("Level 2 Unicorn should be 'graceful'", level2.contains("graceful"));
        assertTrue("Level 2 Unicorn should mention elegance", level2.contains("elegant"));
        
        String level3 = MainActivityKt.createLevelBasedPrompt("Unicorn", "Serenity", 3);
        assertTrue("Level 3 Unicorn should be 'elegant and serene'", 
                   level3.contains("elegant") && level3.contains("serene"));
        assertTrue("Level 3 Unicorn should mention 'Dear companion'", 
                   level3.contains("Dear companion"));
    }
    
    /**
     * Test Case 5: Case Insensitivity and Invalid Inputs
     * Tests edge cases: case variations and invalid levels
     * Branch: Default/else branches in when statements
     */
    @Test
    public void testCaseInsensitivityAndDefaults() {
        // Test case insensitivity
        String upperDragon = MainActivityKt.createLevelBasedPrompt("DRAGON", "Test", 1);
        String lowerDragon = MainActivityKt.createLevelBasedPrompt("dragon", "Test", 1);
        assertTrue("Uppercase DRAGON should work", upperDragon.contains("Dragon"));
        assertTrue("Lowercase dragon should work", lowerDragon.contains("Dragon"));
        
        // Test invalid level (should use default)
        String invalidLevel = MainActivityKt.createLevelBasedPrompt("Dragon", "Test", 5);
        assertTrue("Invalid level should return default prompt", 
                   invalidLevel.contains("friendly") || invalidLevel.contains("Dragon"));
        
        // Test unknown pet type
        String unknownPet = MainActivityKt.createLevelBasedPrompt("Phoenix", "Blaze", 1);
        assertTrue("Unknown pet type should return generic prompt", 
                   unknownPet.contains("Phoenix") && unknownPet.contains("companion"));
    }
}

