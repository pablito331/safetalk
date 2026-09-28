package com.example.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
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
import kotlinx.coroutines.flow.Flow

@Dao
interface FamilyDao {

    // Contacts
    @Query("SELECT * FROM contacts ORDER BY isApprovedByParent DESC, name ASC")
    fun getAllContacts(): Flow<List<ContactEntity>>

    @Query("SELECT * FROM contacts WHERE id = :contactId LIMIT 1")
    fun getContactById(contactId: Long): Flow<ContactEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertContact(contact: ContactEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertContacts(contacts: List<ContactEntity>)

    @Update
    suspend fun updateContact(contact: ContactEntity)

    @Delete
    suspend fun deleteContact(contact: ContactEntity)

    // Chat Messages
    @Query("SELECT * FROM chat_messages WHERE contactId = :contactId ORDER BY timestamp ASC")
    fun getMessagesForContact(contactId: Long): Flow<List<ChatMessageEntity>>

    @Query("SELECT * FROM chat_messages ORDER BY timestamp DESC")
    fun getAllMessages(): Flow<List<ChatMessageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: ChatMessageEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessages(messages: List<ChatMessageEntity>)

    // Tasks
    @Query("SELECT * FROM family_tasks ORDER BY status ASC, id DESC")
    fun getAllTasks(): Flow<List<FamilyTaskEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: FamilyTaskEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTasks(tasks: List<FamilyTaskEntity>)

    @Update
    suspend fun updateTask(task: FamilyTaskEntity)

    @Delete
    suspend fun deleteTask(task: FamilyTaskEntity)

    // Profile
    @Query("SELECT * FROM child_profile WHERE id = 1 LIMIT 1")
    fun getChildProfile(): Flow<ChildProfileEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProfile(profile: ChildProfileEntity)

    @Update
    suspend fun updateProfile(profile: ChildProfileEntity)

    // Monthly Rewards
    @Query("SELECT * FROM monthly_rewards ORDER BY requiredPercentage ASC")
    fun getMonthlyRewards(): Flow<List<MonthlyRewardEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMonthlyReward(reward: MonthlyRewardEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMonthlyRewards(rewards: List<MonthlyRewardEntity>)

    @Update
    suspend fun updateMonthlyReward(reward: MonthlyRewardEntity)

    // Financial Transactions
    @Query("SELECT * FROM financial_transactions ORDER BY id DESC")
    fun getAllTransactions(): Flow<List<FinancialTransactionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(tx: FinancialTransactionEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransactions(txs: List<FinancialTransactionEntity>)

    // Family Highlights / Stories ("Manchetes da Família")
    @Query("SELECT * FROM family_highlights ORDER BY timestamp DESC")
    fun getAllHighlights(): Flow<List<FamilyHighlightEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHighlight(highlight: FamilyHighlightEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHighlights(highlights: List<FamilyHighlightEntity>)

    @Update
    suspend fun updateHighlight(highlight: FamilyHighlightEntity)

    @Delete
    suspend fun deleteHighlight(highlight: FamilyHighlightEntity)

    // Family Group Approval Flow
    @Query("SELECT * FROM family_groups ORDER BY createdAt DESC")
    fun getAllGroups(): Flow<List<FamilyGroupEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGroup(group: FamilyGroupEntity): Long

    @Update
    suspend fun updateGroup(group: FamilyGroupEntity)

    @Query("SELECT * FROM group_invites ORDER BY createdAt DESC")
    fun getAllGroupInvites(): Flow<List<GroupInviteEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGroupInvite(invite: GroupInviteEntity): Long

    @Update
    suspend fun updateGroupInvite(invite: GroupInviteEntity)

    // Withdrawal Requests ("Saque em Mãos / Dívida Paga")
    @Query("SELECT * FROM withdrawal_requests ORDER BY id DESC")
    fun getAllWithdrawalRequests(): Flow<List<WithdrawalRequestEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWithdrawalRequest(req: WithdrawalRequestEntity): Long

    @Update
    suspend fun updateWithdrawalRequest(req: WithdrawalRequestEntity)

    // Sibling Accounts & Balances
    @Query("SELECT * FROM sibling_accounts ORDER BY name ASC")
    fun getAllSiblingAccounts(): Flow<List<SiblingAccountEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSiblingAccount(sibling: SiblingAccountEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSiblingAccounts(siblings: List<SiblingAccountEntity>)

    @Update
    suspend fun updateSiblingAccount(sibling: SiblingAccountEntity)
}
