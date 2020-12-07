

package com.example.android.trackmysleepquality.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.android.trackmysleepquality.network.MarsProperty

@Entity(tableName = "database_rest_table")
data class DatabaseRest constructor(

        @PrimaryKey
        val id: String,
        val imgSrcUrl: String,
        val type: String,
        val price: Double)

fun List<DatabaseRest>.asDomainModel(): List<MarsProperty>{
        return map {
                MarsProperty(
                        id = it.id,
                        imgSrcUrl = it.imgSrcUrl,
                        type = it.type,
                        price = it.price
                )
        }
}
