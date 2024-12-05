package com.tszhim.tszhimng_comp304lab4_ex1.RoomDB

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface LocationLogDao {
    @Insert
    suspend fun insertLog(locationLog: LocationLog)

    @Query("SELECT * FROM location_logs ORDER BY timestamp DESC")
    suspend fun getAllLogs(): List<LocationLog>
}
