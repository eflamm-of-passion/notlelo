package io.eflamm.notlelo

import android.app.Application
import io.eflamm.notlelo.database.NotleloDatabase
import io.eflamm.notlelo.repository.EventRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

// source : https://developer.android.com/codelabs/android-room-with-a-view-kotlin#12

class NotleloApplication: Application() {
    // No need to cancel this scope as it'll be torn down with the process
    val applicationScope = CoroutineScope(SupervisorJob())

    // Using by lazy so the database and the repository are only created when they're needed
    // rather than when the application starts
    val database by lazy { NotleloDatabase.getInstance(this)}
    val eventRepository by lazy { EventRepository(database.eventDao(), database.dateDao(), database.mealDao(), database.productDao(), database.pictureDao()) }
}