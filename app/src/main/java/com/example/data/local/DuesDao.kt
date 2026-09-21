package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.AppNotification
import com.example.data.model.ClusterLedgerEntry
import com.example.data.model.DuesBill
import com.example.data.model.Resident
import kotlinx.coroutines.flow.Flow

@Dao
interface DuesDao {
    // Resident operations
    @Query("SELECT * FROM residents ORDER BY blockNumber ASC")
    fun getAllResidents(): Flow<List<Resident>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertResidents(residents: List<Resident>)

    @Query("SELECT * FROM residents WHERE id = :id LIMIT 1")
    suspend fun getResidentById(id: String): Resident?

    // Dues Bill operations
    @Query("SELECT * FROM dues_bills WHERE residentId = :residentId ORDER BY periodIndex DESC")
    fun getBillsForResident(residentId: String): Flow<List<DuesBill>>

    @Query("SELECT * FROM dues_bills ORDER BY periodIndex DESC, blockNumber ASC")
    fun getAllBills(): Flow<List<DuesBill>>

    @Query("SELECT * FROM dues_bills WHERE residentId = :residentId AND isPaid = 0 ORDER BY periodIndex ASC")
    fun getUnpaidBillsForResident(residentId: String): Flow<List<DuesBill>>

    @Query("SELECT COUNT(*) FROM dues_bills WHERE residentId = :residentId AND isPaid = 0")
    fun getUnpaidCountForResident(residentId: String): Flow<Int>

    @Query("SELECT * FROM dues_bills WHERE id = :billId LIMIT 1")
    suspend fun getBillById(billId: Long): DuesBill?

    @Update
    suspend fun updateBill(bill: DuesBill)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBills(bills: List<DuesBill>)

    // Cluster Ledger operations (Transparency)
    @Query("SELECT * FROM cluster_ledger ORDER BY timestamp DESC")
    fun getAllLedgerEntries(): Flow<List<ClusterLedgerEntry>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLedgerEntry(entry: ClusterLedgerEntry)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLedgerEntries(entries: List<ClusterLedgerEntry>)

    // Notification operations
    @Query("SELECT * FROM app_notifications ORDER BY timestamp DESC")
    fun getAllNotifications(): Flow<List<AppNotification>>

    @Query("SELECT COUNT(*) FROM app_notifications WHERE isRead = 0")
    fun getUnreadNotificationCount(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notification: AppNotification)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotifications(notifications: List<AppNotification>)

    @Query("UPDATE app_notifications SET isRead = 1 WHERE id = :id")
    suspend fun markNotificationAsRead(id: Long)

    @Query("UPDATE app_notifications SET isRead = 1")
    suspend fun markAllNotificationsAsRead()
}
