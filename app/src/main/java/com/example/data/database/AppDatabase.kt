package com.example.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.dao.FamilyDao
import com.example.data.model.ChatMessageEntity
import com.example.data.model.ChildProfileEntity
import com.example.data.model.ContactEntity
import com.example.data.model.FamilyGroupEntity
import com.example.data.model.FamilyHighlightEntity
import com.example.data.model.FamilyTaskEntity
import com.example.data.model.FinancialTransactionEntity
import com.example.data.model.GroupInviteEntity
import com.example.data.model.MonthlyRewardEntity
import com.example.data.model.SiblingAccountEntity
import com.example.data.model.WithdrawalRequestEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        ContactEntity::class,
        ChatMessageEntity::class,
        FamilyTaskEntity::class,
        ChildProfileEntity::class,
        MonthlyRewardEntity::class,
        FinancialTransactionEntity::class,
        FamilyHighlightEntity::class,
        FamilyGroupEntity::class,
        GroupInviteEntity::class,
        WithdrawalRequestEntity::class,
        SiblingAccountEntity::class
    ],
    version = 6,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun familyDao(): FamilyDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "family_safe_db"
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(DatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        populateInitialData(database.familyDao())
                    }
                }
            }
        }

        suspend fun populateInitialData(dao: FamilyDao) {
            // Perfil inicial padrão funcional
            val initialProfile = ChildProfileEntity(
                id = 1L,
                name = "Pedro",
                age = 10,
                userRole = "CHILD",
                familyRole = "FILHO",
                loginIdentifier = "pedro@familia.com",
                profileStatus = "ATIVO",
                familyCode = "FAM-7749",
                parentPin = "1234",
                balance = 35.50,
                monthlyPoints = 120,
                monthlyTasksCompleted = 4,
                monthlyTasksTarget = 10,
                batteryPercent = 88,
                lastLocationUpdate = "Em casa (Seguro)",
                isInSafeZone = true,
                monitoringEnabled = true,
                funnyFilterEnabled = true
            )
            dao.insertProfile(initialProfile)

            // Contatos padrão (Família + Amigos)
            val initialContacts = listOf(
                ContactEntity(
                    id = 1L,
                    name = "Mãe",
                    phone = "(11) 98888-1111",
                    relationship = "Mãe",
                    isApprovedByParent = true,
                    safetyStatus = "APROVADO",
                    relationshipType = "FAMILY",
                    isOnline = true,
                    lastSeen = "Online",
                    avatarColor = 0xFF0D9488
                ),
                ContactEntity(
                    id = 2L,
                    name = "Pai (Carlos)",
                    phone = "(11) 98888-2222",
                    relationship = "Pai",
                    isApprovedByParent = true,
                    safetyStatus = "APROVADO",
                    relationshipType = "FAMILY",
                    isOnline = true,
                    lastSeen = "Visto por último hoje às 12:40",
                    avatarColor = 0xFF1E3A8A
                ),
                ContactEntity(
                    id = 3L,
                    name = "Mariana (Irmã)",
                    phone = "(11) 98888-3333",
                    relationship = "Irmã",
                    isApprovedByParent = true,
                    safetyStatus = "APROVADO",
                    relationshipType = "FAMILY",
                    isOnline = true,
                    lastSeen = "Online",
                    avatarColor = 0xFFEC4899
                ),
                ContactEntity(
                    id = 4L,
                    name = "Lucas (Amigo da Escola)",
                    phone = "(11) 99999-4444",
                    relationship = "Amigo",
                    isApprovedByParent = true,
                    safetyStatus = "APROVADO",
                    relationshipType = "EXTERNAL",
                    isOnline = false,
                    lastSeen = "Visto ontem",
                    avatarColor = 0xFF8B5CF6
                )
            )
            dao.insertContacts(initialContacts)

            // Mensagens iniciais de boas-vindas no chat com a Mãe
            val initialMessages = listOf(
                ChatMessageEntity(
                    contactId = 1L,
                    sender = "PARENT",
                    text = "Oi, Pedro! Tudo bem? Não esqueça da lição de casa depois do almoço.",
                    mediaType = "TEXT",
                    formattedTime = "11:30"
                ),
                ChatMessageEntity(
                    contactId = 1L,
                    sender = "ME",
                    text = "Oi, mãe! Já cheguei da escola e fiz meu check-in de segurança.",
                    mediaType = "TEXT",
                    formattedTime = "11:35"
                ),
                ChatMessageEntity(
                    contactId = 1L,
                    sender = "SYSTEM",
                    text = "🛡️ SafeTalk Proteção: Mensagens criptografadas e supervisionadas com amor.",
                    mediaType = "TEXT",
                    formattedTime = "11:36"
                )
            )
            dao.insertMessages(initialMessages)

            // Contas de Irmãos
            val initialSiblings = listOf(
                SiblingAccountEntity(name = "Pedro", age = 10, balance = 35.50, relationship = "Irmão", avatarDrawableResName = "avatar_pedro"),
                SiblingAccountEntity(name = "Mariana", age = 12, balance = 50.00, relationship = "Irmã", avatarDrawableResName = "avatar_mariana"),
                SiblingAccountEntity(name = "Lucas", age = 8, balance = 20.00, relationship = "Irmão", avatarDrawableResName = "avatar_lucas")
            )
            dao.insertSiblingAccounts(initialSiblings)

            // Tarefas iniciais
            val initialTasks = listOf(
                FamilyTaskEntity(
                    title = "Arrumar a cama e organizar o quarto",
                    description = "Deixar tudo em ordem antes de jogar videogame",
                    rewardAmount = 5.00,
                    rewardPoints = 20,
                    penaltyAmount = 2.00,
                    dueDate = "Hoje às 14:00",
                    category = "Quarto",
                    status = "PENDENTE",
                    assignedChildName = "Pedro",
                    recurrence = "DIARIA"
                ),
                FamilyTaskEntity(
                    title = "Lição de casa de Matemática",
                    description = "Fazer exercícios da página 42 no caderno",
                    rewardAmount = 10.00,
                    rewardPoints = 50,
                    penaltyAmount = 3.00,
                    dueDate = "Hoje às 18:00",
                    category = "Estudos",
                    status = "PENDENTE",
                    assignedChildName = "Pedro",
                    requiresPhotoEvidence = true,
                    recurrence = "DIAS_UTEIS"
                ),
                FamilyTaskEntity(
                    title = "Guardar a louça do almoço",
                    description = "Ajudar a família após a refeição",
                    rewardAmount = 4.00,
                    rewardPoints = 15,
                    penaltyAmount = 1.50,
                    dueDate = "Hoje",
                    category = "Cozinha",
                    status = "CONCLUIDO_FILHO",
                    assignedChildName = "Pedro",
                    recurrence = "DIARIA"
                )
            )
            dao.insertTasks(initialTasks)

            // Metas / Recompensas do Mês
            val initialRewards = listOf(
                MonthlyRewardEntity(
                    title = "Passeio no Cinema com Pipoca",
                    description = "Escolha o filme da semana!",
                    requiredPercentage = 80,
                    bonusAmount = 20.0,
                    tier = "PRATA",
                    isUnlocked = false
                ),
                MonthlyRewardEntity(
                    title = "Bônus no Cofrinho",
                    description = "Meta de cumprimento de 100% das tarefas!",
                    requiredPercentage = 100,
                    bonusAmount = 30.0,
                    tier = "OURO",
                    isUnlocked = false
                )
            )
            dao.insertMonthlyRewards(initialRewards)

            // Transações Financeiras do Cofrinho
            val initialTransactions = listOf(
                FinancialTransactionEntity(
                    title = "Semana de Tarefas Cumpridas",
                    amount = 25.00,
                    type = "CREDITO",
                    date = "Ontem",
                    category = "Mesada / Tarefas",
                    description = "Recompensa semanal aprovada pelos pais"
                ),
                FinancialTransactionEntity(
                    title = "Lanche na Cantina",
                    amount = 10.50,
                    type = "DEBITO",
                    date = "2 dias atrás",
                    category = "Saque / Gastos",
                    description = "Saque em mãos autorizado"
                )
            )
            dao.insertTransactions(initialTransactions)

            // Destaques / Stories da Família
            val initialHighlights = listOf(
                FamilyHighlightEntity(
                    authorName = "Mãe",
                    authorRole = "PARENT",
                    avatarDrawableResName = "avatar_mae",
                    title = "Passeio de Domingo no Parque 🌳",
                    textContent = "Dia incrível em família! Todo mundo de parabéns pela cooperação da semana.",
                    audience = "FAMILY",
                    mediaType = "TEXT",
                    timeAgo = "Há 2 horas",
                    moodEmoji = "🌟",
                    likesCount = 3
                )
            )
            dao.insertHighlights(initialHighlights)
        }
    }
}
