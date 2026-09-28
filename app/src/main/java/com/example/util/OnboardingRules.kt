package com.example.util

import com.example.data.model.ChildProfileEntity

object OnboardingRules {
    fun isDemoProfile(profile: ChildProfileEntity?): Boolean {
        if (profile == null) return true
        return profile.name.isBlank() ||
            (profile.name == "Pedro" &&
                profile.age == 11 &&
                profile.loginIdentifier == "pedro@escola.com" &&
                profile.parentPin == "1234")
    }

    fun requiresFamilyCode(age: Int, isAutonomousChild: Boolean): Boolean {
        return age < 18 && !isAutonomousChild
    }

    fun normalizeFamilyCode(rawCode: String): String {
        val clean = rawCode.trim().uppercase()
        if (clean.isBlank()) return ""
        val withoutPrefix = clean.removePrefix("FAM-").filter { it.isLetterOrDigit() }
        return if (withoutPrefix.isBlank()) "" else "FAM-$withoutPrefix"
    }

    fun matchesFamilyCode(input: String?, expected: String?): Boolean {
        if (input == null || expected == null) return false
        val normalizedInput = normalizeFamilyCode(input)
        val normalizedExpected = normalizeFamilyCode(expected)
        return normalizedInput.isNotBlank() && normalizedInput == normalizedExpected
    }

    fun generateFamilyCode(): String {
        val random = (1000..9999).random()
        return "FAM-$random"
    }
}
