package com.ctis487_fp.finalproject

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters

class BackgroundChangeWorker(context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {

    override fun doWork(): Result {
        // Spinner'dan gelen index'i alın
        val index = inputData.getInt("index", -1)

        // Geçersiz index değerine karşı önlem al
        if (index < 0 || index > 4) {
            return Result.failure()  // Hatalı index, işlem başarısız
        }

        // Animasyon dosyasını belirleyin
        val animationFile = when (index) {
            0 -> "raw/blue_anim.json"
            1 -> "raw/purple_anim.json"
            2 -> "raw/green_anim.json"
            3 -> "raw/red_anim.json"
            else -> "raw/yellow_anim.json"
        }

        // Animasyon dosyasını kaydedin
        saveAnimationFile(animationFile)

        return Result.success()
    }

    private fun saveAnimationFile(animationFile: String) {
        // Animasyon dosyasını SharedPreferences'a kaydedin
        val sharedPref = applicationContext.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        sharedPref.edit().putString("animation_file", animationFile).apply()
    }
}

