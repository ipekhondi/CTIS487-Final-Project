package com.ctis487_fp.finalproject.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "medicine_table") // Define table name for Room
data class MedicineItem(
    @PrimaryKey(autoGenerate = true)
    @SerializedName("id") // Maps JSON key "id" to this property
    val id: Int = 0,

    @SerializedName("type") // Maps JSON key "type" to this property
    val type: Int = 1, // 1 for type one, 2 for type two

    @SerializedName("title") // Maps JSON key "title" to this property
    val title: String = "",

    @SerializedName("description") // Maps JSON key "description" to this property
    val description: String = "",

    @SerializedName("stock") // Maps JSON key "stock" to this property
    val stock: Int = 0,

    @SerializedName("imageUrl") // Maps JSON key "imageUrl" to this property
    val imageUrl: String = "",

    var isFavorite: Boolean = false // A field for marking the medicine as a favorite
) {
    override fun toString(): String {
        return "Medicine(id=$id, type=$type, title='$title', description='$description', stock=$stock, imageUrl='$imageUrl', isFavorite=$isFavorite)"
    }
}
