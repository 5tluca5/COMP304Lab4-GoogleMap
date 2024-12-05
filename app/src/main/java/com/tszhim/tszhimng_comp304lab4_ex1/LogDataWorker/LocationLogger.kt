package com.tszhim.tszhimng_comp304lab4_ex1.LogDataWorker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.tszhim.tszhimng_comp304lab4_ex1.RoomDB.AppDatabase
import com.tszhim.tszhimng_comp304lab4_ex1.RoomDB.LocationLog

class LocationLogger(private val appContext: Context,
                     private val param: WorkerParameters) : CoroutineWorker(appContext, param) {

    override suspend fun doWork(): Result {

        var database = AppDatabase.getInstance(applicationContext)
        var done = 0;
        var ids = param.inputData.getIntArray("ids");
        var latitudes = param.inputData.getDoubleArray("latitudes");
        var longitudes = param.inputData.getDoubleArray("longitudes");
        var timestamps = param.inputData.getLongArray("timestamps");

        var counter = 0
        if (ids != null) {
            while (counter < ids.size){
                val locationLog = LocationLog(
                    id = ids[counter],
                    latitude = latitudes?.get(counter) ?: 0.0,
                    longitude = longitudes?.get(counter) ?: 0.0,
                    timestamp = timestamps?.get(counter) ?: 0
                )

                database.locationLogDao.insertLog(locationLog)
                done = 1;
                counter++
            }
        }
        if (done == 1) {
            return Result.success()
        }else
            return  Result.failure()
    }

}
