package com.example

import com.example.data.model.ChildProfileEntity
import com.example.util.OnboardingRules
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class OnboardingRulesTest {
    @Test
    fun demoProfileIsDetectedForEmptyProfile() {
        assertTrue(OnboardingRules.isDemoProfile(ChildProfileEntity()))
    }

    @Test
    fun childWithoutFamilyCodeRequiresCode() {
        assertTrue(OnboardingRules.requiresFamilyCode(15, false))
        assertFalse(OnboardingRules.requiresFamilyCode(15, true))
        assertFalse(OnboardingRules.requiresFamilyCode(35, false))
    }

    @Test
    fun familyCodeIsNormalized() {
        assertEquals("FAM-1234", OnboardingRules.normalizeFamilyCode(" fam-1234 "))
    }

    @Test
    fun familyCodeMatchesAcrossFormatting() {
        assertTrue(OnboardingRules.matchesFamilyCode("fam-1234", "FAM-1234"))
        assertTrue(OnboardingRules.matchesFamilyCode("FAM-1234", "1234"))
        assertFalse(OnboardingRules.matchesFamilyCode("FAM-9999", "FAM-1234"))
    }
}
