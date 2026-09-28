package com.example.util

import java.util.regex.Pattern

/**
 * Filtro Divertido SafeTalk:
 * Substitui palavrões e xingamentos por palavras engraçadas e emojis bem-humorados,
 * mantendo o sentido cômico sem nenhuma grosseria.
 */
object FunProfanityFilter {

    data class FilterResult(
        val originalText: String,
        val sanitizedText: String,
        val wasFiltered: Boolean,
        val replacedItems: List<Pair<String, String>>
    )

    // Dicionário de substituições hilárias
    // Frases compostas primeiro para evitar substituições parciais
    private val phraseReplacements: List<Pair<Regex, String>> = listOf(
        Regex("(?i)\\bvai\\s+tomar\\s+no\\s+c[uú]\\b") to "vai comprar churros na feira 🥨",
        Regex("(?i)\\bvtnc\\b") to "vai tomar milkshake de baunilha 🥤",
        Regex("(?i)\\bvai\\s+se\\s+foder\\b") to "vai tomar um sorvete bem gostoso 🍦",
        Regex("(?i)\\bvsf\\b") to "vai soltar pipa no parque 🪁",
        Regex("(?i)\\bfilh[oa]\\s+da\\s+puta\\b") to "filhote de chocadeira 🐣",
        Regex("(?i)\\bfdp\\b") to "fã de paçoca 🥜",
        Regex("(?i)\\bque\\s+porra\\s+[eé]\\s+essa\\b") to "que carambolas 🍋 é essa",
        Regex("(?i)\\bputa\\s+que\\s+pariu\\b") to "pombinha 🕊️ na pracinha"
    )

    // Palavras individuais com limite de palavra (\b) ou acentuação
    private val wordReplacements: List<Pair<Regex, String>> = listOf(
        Regex("(?i)\\bmerda[s]?\\b") to "💩 cocô",
        Regex("(?i)\\bbosta[s]?\\b") to "💩 tortinha de lama",
        Regex("(?i)\\bc[uú][s]?\\b") to "máquina de churros 🥯",
        Regex("(?i)\\bporra[s]?\\b") to "carambolas 🍋",
        Regex("(?i)\\bcaralho[s]?\\b") to "paralelepípedo 🧱",
        Regex("(?i)\\bcacete[s]?\\b") to "pão francês quentinho 🥖",
        Regex("(?i)\\bputa[s]?\\b") to "pombinha 🕊️",
        Regex("(?i)\\bputo[s]?\\b") to "pombinho 🕊️",
        Regex("(?i)\\bfoder\\b") to "fazer bagunça 🎪",
        Regex("(?i)\\bfod[eê]u\\b") to "danou-se tudo 🚀",
        Regex("(?i)\\bfud[eê]u\\b") to "danou-se tudo 🚀",
        Regex("(?i)\\bfoda[s]?\\b") to "casca de banana 🍌",
        Regex("(?i)\\barrombad[oa][s]?\\b") to "astronauta sem capacete 🧑‍🚀",
        Regex("(?i)\\bot[aá]ri[oa][s]?\\b") to "patinho na lagoa 🦆",
        Regex("(?i)\\bidiota[s]?\\b") to "gênio ao contrário 🧠",
        Regex("(?i)\\bburr[oa][s]?\\b") to "enciclopédia invertida 📚",
        Regex("(?i)\\bimbecil[es]?\\b") to "campeão da trapalhada 🎪",
        Regex("(?i)\\bbuceta[s]?\\b") to "pastel de vento 🥟",
        Regex("(?i)\\bdesgra[cç]a[s]?\\b") to "pipoca queimada 🍿",
        Regex("(?i)\\bdroga[s]?\\b") to "jiló azedo 🥦",
        Regex("(?i)\\binferno\\b") to "forno de pizza 🍕",
        Regex("(?i)\\bviado[s]?\\b") to "camarada alegre 🦄",
        Regex("(?i)\\bbabaca[s]?\\b") to "bananada mole 🍌",
        Regex("(?i)\\bcorno[s]?\\b") to "unicórnio alado 🦄",
        Regex("(?i)\\bpinto[s]?\\b") to "pipoca estourada 🍿",
        Regex("(?i)\\bpiroc[aa][s]?\\b") to "pirulito gigante 🍭",
        Regex("(?i)\\bvagabund[oa][s]?\\b") to "dorminhoco de rede 😴",
        Regex("(?i)\\bmaldit[oa][s]?\\b") to "mosquito de verão 🦟"
    )

    /**
     * Aplica o filtro sobre o texto fornecido.
     * Retorna o texto higienizado com emojis e termos engraçados.
     */
    fun filter(input: String): FilterResult {
        var currentText = input
        val replaced = mutableListOf<Pair<String, String>>()

        // 1. Processar frases primeiro
        for ((regex, replacement) in phraseReplacements) {
            val matches = regex.findAll(currentText).toList()
            if (matches.isNotEmpty()) {
                for (match in matches) {
                    replaced.add(match.value to replacement)
                }
                currentText = regex.replace(currentText, replacement)
            }
        }

        // 2. Processar palavras individuais
        for ((regex, replacement) in wordReplacements) {
            val matches = regex.findAll(currentText).toList()
            if (matches.isNotEmpty()) {
                for (match in matches) {
                    replaced.add(match.value to replacement)
                }
                currentText = regex.replace(currentText, replacement)
            }
        }

        val wasFiltered = replaced.isNotEmpty()
        return FilterResult(
            originalText = input,
            sanitizedText = currentText,
            wasFiltered = wasFiltered,
            replacedItems = replaced
        )
    }

    /**
     * Exemplos hilários para exibir no painel dos pais ou das crianças
     */
    val funExamples = listOf(
        "merda" to "💩 cocô",
        "cu" to "máquina de churros 🥯",
        "porra" to "carambolas 🍋",
        "caralho" to "paralelepípedo 🧱",
        "cacete" to "pão francês quentinho 🥖",
        "vai se foder" to "vai tomar um sorvete bem gostoso 🍦",
        "vai tomar no cu" to "vai comprar churros na feira 🥨",
        "arrombado" to "astronauta sem capacete 🧑‍🚀",
        "otário" to "patinho na lagoa 🦆"
    )
}
