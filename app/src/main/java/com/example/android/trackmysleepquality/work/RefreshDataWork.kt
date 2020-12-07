
package com.example.android.trackmysleepquality.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.android.trackmysleepquality.database.getDatabase
import com.example.android.trackmysleepquality.repository.VideosRepository
import retrofit2.HttpException

class RefreshDataWork (appContext: Context, params: WorkerParameters):
        CoroutineWorker(appContext,params){

    companion object{
        const val WORK_NAME = "RefreshDataWorker"
    }

    /**
     * A coroutine-friendly method to do work
     */
    override suspend fun doWork(): Payload {
        val database = getDatabase(applicationContext)
        val repository = VideosRepository(database)

        return try {
            repository.refreshRest()
            Payload(Result.SUCCESS)
        }catch (exception: HttpException){
            Payload(Result.RETRY)
        }
    }

}