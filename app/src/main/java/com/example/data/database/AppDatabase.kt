package com.example.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
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
    version = 8, // v8: +mediaBase64/senderName/familyCode em chat_messages (mídia inline)
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun familyDao(): FamilyDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context, scope: kotlinx.coroutines.CoroutineScope): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "family_safe_db"
                )
                    // v7: removed all demo/seed data — real families register from scratch.
                    // v8: mídia inline (mediaBase64/senderName/familyCode) — rebuild local.
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
