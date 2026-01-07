package com.example.jadwalkambing.data.database

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.jadwalkambing.data.database.KambingDao
import com.example.jadwalkambing.data.entity.DataKambingEntity
import kotlinx.coroutines.flow.Flow
import androidx.room.Update

@Database(
    entities = [DataKambingEntity::class],
    version = 1
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun kambingDao(): KambingDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "ternak_db"
                ).build().also {
                    INSTANCE = it
                }
            }
        }
    }
}

@Dao
interface KambingDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(data: DataKambingEntity)

    @Update
    suspend fun update(data: DataKambingEntity)

    @Delete
    suspend fun delete(data: DataKambingEntity)

    @Query("SELECT * FROM kambing ORDER BY id DESC")
    fun getAll(): Flow<List<DataKambingEntity>>
}
