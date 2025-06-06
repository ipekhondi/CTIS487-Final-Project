package com.ctis487_fp.finalproject.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [MedicineStock::class], version = 1)
abstract class MedicineStockRoomDatabase : RoomDatabase() {
    abstract fun medicineStockDao(): MedicineStockDAO
}


