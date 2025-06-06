package com.ctis487_fp.finalproject.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.ctis487_fp.finalproject.util.Constants

@Entity(tableName = Constants.TABLENAME)
class MedicineStock(
    @PrimaryKey(autoGenerate = true)
    var id:Int = 0,
    var uidFirebase: String,
    var stockMedicine: Int
)
{
    override fun toString(): String {
        return "MedicineStock(id=$id, name='$uidFirebase', stock=$stockMedicine)"
    }
}