package com.ctis487_fp.finalproject.model

data class Medicine(
    // 1 type acil type 1 kırmızı
    // 2 type normal type 2 yeşil

    val type: Int, // 1 for type one, 2 for type two
    val title: String? = null,
    val description: String? = null,
    //val imageRes: Int? = null
    val stock: Int? = null
)
{
    // Empty constructor required for Firebase
    constructor() : this(0, "", "",0)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Medicine) return false
        return type == other.type &&
                title == other.title &&
                description == other.description
        stock == other.stock
    }

    override fun hashCode(): Int {
        var result = type
        result = 31 * result + title.hashCode()
        result = 31 * result + description.hashCode()
        result = 31 * result + (stock ?: 0)
        return result
    }
}