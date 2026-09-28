package com.example

import com.example.data.model.ContactEntity
import com.example.util.ContactSafetyRules
import com.example.util.FamilyHighlightRules
import com.example.util.FunProfanityFilter
import org.junit.Assert.*
import org.junit.Test

/**
 * Unit tests for SafeTalk functionality.
 */
class ExampleUnitTest {
  @Test
  fun addition_isCorrect() {
    assertEquals(4, 2 + 2)
  }

  @Test
  fun profanityFilter_replacesMerdaAndCu() {
    val input = "que merda cara"
    val result = FunProfanityFilter.filter(input)
    assertTrue(result.wasFiltered)
    assertTrue(result.sanitizedText.contains("💩"))

    val input2 = "vai tomar no cu"
    val result2 = FunProfanityFilter.filter(input2)
    assertTrue(result2.wasFiltered)
    assertTrue(result2.sanitizedText.contains("churros"))
  }

  @Test
  fun profanityFilter_keepsCleanMessagesIntact() {
    val clean = "Oi Mariana, vamos estudar para a prova amanhã?"
    val result = FunProfanityFilter.filter(clean)
    assertFalse(result.wasFiltered)
    assertEquals(clean, result.sanitizedText)
  }

  @Test
  fun familyContactsCanChatWithoutExtraApproval() {
    val familyContact = ContactEntity(
      id = 1L,
      name = "Mãe",
      phone = "111",
      relationship = "Mãe / Família",
      relationshipType = "FAMILY",
      isApprovedByParent = true,
      safetyStatus = "APROVADO"
    )

    assertTrue(ContactSafetyRules.shouldAllowConversation(familyContact))
  }

  @Test
  fun blockedContactsCannotChatAndUnviewedHighlightsStayFirst() {
    val blockedContact = ContactEntity(
      id = 2L,
      name = "Contato bloqueado",
      phone = "222",
      relationship = "Amigo",
      relationshipType = "EXTERNAL",
      isApprovedByParent = false,
      safetyStatus = "BLOQUEADO"
    )

    assertFalse(ContactSafetyRules.shouldAllowConversation(blockedContact))

    val highlights = listOf(
      com.example.data.model.FamilyHighlightEntity(
        id = 1L,
        authorName = "A",
        title = "VISTA",
        textContent = "Vista",
        isViewed = true
      ),
      com.example.data.model.FamilyHighlightEntity(
        id = 2L,
        authorName = "B",
        title = "NOVA",
        textContent = "Nova",
        isViewed = false
      )
    )

    val ordered = FamilyHighlightRules.sortForDisplay(highlights)
    assertEquals("NOVA", ordered.first().title)
    assertEquals("VISTA", ordered.last().title)
  }
}

