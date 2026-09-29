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

    /**
     * Gera um código familiar forte e praticamente único (ex: FAM-7K4Q9X2M).
     * 8 caracteres de um alfabeto sem dígitos ambíguos → 32^8 ≈ 1,1 trilhão de
     * combinações; colisão entre famílias é improvável e cada família também
     * confere o nome de quem entra, então um choque acidental não dá acesso à
     * conversa.
     */
    fun generateFamilyCode(): String {
        val alphabet = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789" // sem I, O, 0, 1 (ambíguos)
        val code = buildString {
            repeat(8) { append(alphabet.random()) }
        }
        return "FAM-$code"
    }
}
