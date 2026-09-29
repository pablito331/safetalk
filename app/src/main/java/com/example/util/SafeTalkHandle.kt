package com.example.util

/**
 * Identidade pública SafeTalk: @usuário com prefixo da família.
 *
 * Permite que famílias diferentes se conectem SEM compartilhar telefone ou
 * e-mail. O handle segue o padrão:  nome.fam-XXXXXXXX
 *   ex: ana.fam-7k4q9x2m  (Ana, da família FAM-7K4Q9X2M)
 *
 * Fluxo entre famílias:
 *  1. Um responsável gera o convite (código da família ou @usuário).
 *  2. O outro responsável adiciona o @usuário como contato.
 *  3. O contato nasce PENDENTE nos dois lados e só conversa após aprovação
 *     dos responsáveis (ContactSafetyRules.shouldAllowConversation).
 */
object SafeTalkHandle {

    const val PREFIX = "@"
    private val VALID_BODY = Regex("^[a-z0-9]+\\.fam-[a-z0-9]{8}$")

    /** Gera o handle público: nome + código da família. */
    fun generate(displayName: String, familyCode: String): String {
        val slug = slugify(displayName)
        val code = OnboardingRules.normalizeFamilyCode(familyCode)
            .removePrefix("FAM-")
            .lowercase()
        if (slug.isBlank() || code.isBlank()) return ""
        return "$slug.fam-$code"
    }

    /** Extrai o código da família (FAM-XXXX) de um handle. */
    fun familyCodeOf(handle: String): String {
        val body = handle.trim().removePrefix(PREFIX).lowercase()
        val codePart = body.substringAfter(".fam-", "")
        return if (codePart.isNotBlank()) "FAM-${codePart.uppercase()}" else ""
    }

    /** Valida formato de handle: @nome.fam-xxxxxxxx */
    fun isValid(handle: String): Boolean {
        val body = handle.trim().removePrefix(PREFIX).lowercase()
        return VALID_BODY.matches(body)
    }

    /** Aceita @handle, e-mail ou telefone — o que a família preferir compartilhar. */
    fun normalizeContactInput(raw: String): String {
        val trimmed = raw.trim()
        return when {
            trimmed.startsWith(PREFIX) -> trimmed.lowercase()
            trimmed.contains("@") && trimmed.contains(".") -> trimmed.lowercase() // e-mail
            else -> trimmed // telefone
        }
    }

    private fun slugify(name: String): String {
        val normalized = name.trim().lowercase()
            .replace(Regex("[áàãâä]"), "a").replace(Regex("[éèêë]"), "e")
            .replace(Regex("[íìîï]"), "i").replace(Regex("[óòõôö]"), "o")
            .replace(Regex("[úùûü]"), "u").replace("ç", "c")
        val slug = normalized.filter { it.isLetterOrDigit() }
        return slug.take(12)
    }
}
