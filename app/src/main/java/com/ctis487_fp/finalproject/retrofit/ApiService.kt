package com.ctis487_fp.finalproject.retrofit

import com.ctis487_fp.finalproject.model.Medicine
import com.ctis487_fp.finalproject.model.MedicineItem
import retrofit2.Call
import retrofit2.http.GET

interface ApiService {
    @GET("Y3VN")
    fun getMedicines(): Call<List<MedicineItem>>

}