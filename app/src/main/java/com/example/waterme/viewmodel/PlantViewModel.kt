
package com.example.waterme.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.waterme.data.DataSource
import com.example.waterme.worker.WaterReminderWorker
import java.util.concurrent.TimeUnit

class PlantViewModel(application: Application): ViewModel() {

    val plants = DataSource.plants

    internal fun scheduleReminder(
        duration: Long,
        unit: TimeUnit,
        plantName: String
    ) {
        //  create a Data instance with the plantName passed to it
        val data = Data.Builder()
            .putString(WaterReminderWorker.nameKey, plantName)
            .build()

        //  Generate a OneTimeWorkRequest with the passed in duration, time unit, and data
        //  instance
        val waterReminderWorker = OneTimeWorkRequestBuilder<WaterReminderWorker>()
            .setInputData(data)
            .setInitialDelay(duration,unit)
            .build()

        //  Enqueue the request as a unique work request
        WorkManager.getInstance()
            .enqueueUniqueWork(plantName,ExistingWorkPolicy.REPLACE, waterReminderWorker)
    }
}

class PlantViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(PlantViewModel::class.java)) {
            PlantViewModel(application) as T
        } else {
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
