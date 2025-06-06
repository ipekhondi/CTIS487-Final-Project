package com.ctis487_fp.finalproject.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.ctis487_fp.finalproject.util.Constants

@Dao
interface MedicineStockDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertMedicineStock(customer: MedicineStock):Long

    @Update
    fun updateMedicineStock(customer: MedicineStock):Int

    @Delete
    fun deleteMedicineStock(customer: MedicineStock):Int

    @Query("DELETE FROM ${Constants.TABLENAME}")
    fun deleteAllMedicineStock()

    @Query("SELECT * FROM ${Constants.TABLENAME} ORDER BY id DESC")
    fun getAllMedicineStock():MutableList<MedicineStock>

    @Query("SELECT * FROM ${Constants.TABLENAME} WHERE id =:id")
    fun getMedicineStockById(id:Int):MedicineStock

    @Query("SELECT * FROM ${Constants.TABLENAME} WHERE uidFirebase LIKE :name")
    fun getMedicineStockByuidFirebase(name:String):MutableList<MedicineStock>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllCustomer(customers: ArrayList<MedicineStock>){
        customers.forEach{
            insertMedicineStock(it)
        }
    }
}