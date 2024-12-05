package com.tszhim.tszhimng_comp304lab4_ex1.ViewModels

import com.tszhim.tszhimng_comp304lab4_ex1.RoomDB.AppDatabase
import com.tszhim.tszhimng_comp304lab4_ex1.RoomDB.LocationLog
import com.tszhim.tszhimng_comp304lab4_ex1.RoomDB.LocationLogDao

class AppRepository(private val dao: LocationLogDao)
{
    suspend fun GetAllLogs() : List<LocationLog>
    {
        return dao.getAllLogs()
    }

    suspend fun InsertLog(l : LocationLog)
    {
        dao.insertLog(l)
    }
}