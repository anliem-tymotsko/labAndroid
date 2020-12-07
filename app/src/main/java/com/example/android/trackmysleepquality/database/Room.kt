

package com.example.android.trackmysleepquality.database

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface RestDao{

    @Query("select * from database_rest_table")
    fun getRest(): LiveData<List<DatabaseRest>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg rest: DatabaseRest)
}

@Database(entities = [DatabaseRest::class], version = 1, exportSchema = false)
abstract class RestDatabase: RoomDatabase(){
    abstract val restDao: RestDao
}

private lateinit var INSTANCE: RestDatabase

fun getDatabase(context: Context): RestDatabase {
    synchronized(RestDatabase::class.java){
        if (!::INSTANCE.isInitialized){
            INSTANCE = Room.databaseBuilder(context.applicationContext,
                    RestDatabase::class.java,
                    "rest").build()
        }
    }
    return INSTANCE
}


