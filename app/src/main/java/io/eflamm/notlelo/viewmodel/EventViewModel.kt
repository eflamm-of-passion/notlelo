package io.eflamm.notlelo.viewmodel

import android.content.Context
import android.content.Intent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.content.FileProvider
import androidx.lifecycle.*
import io.eflamm.notlelo.StorageUtils
import io.eflamm.notlelo.model.Event
import io.eflamm.notlelo.model.EventWithDays
import io.eflamm.notlelo.repository.EventRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.io.File


interface IEventViewModel {
    val uiState: EventUiState
    fun updateSelectedEvent(event: Event)
    val allEvents: LiveData<List<Event>>
    fun eventWithProducts(id: Long): LiveData<EventWithDays>
    fun insertEvent(event: Event): Job
    fun insertFullProduct(eventId: Long, mealName: String, productName: String, picturePaths: List<String>): Job
    fun shareEvent(context: Context, eventWithProducts: EventWithDays): Intent
    fun deleteEvents(event: List<Event>): Job
}

data class EventUiState(
    var selectedEvent : Event?
)

class EventViewModel(private val eventRepository: EventRepository ): ViewModel(), IEventViewModel {

    override var uiState by mutableStateOf(EventUiState(selectedEvent = null))
        private set

    override fun updateSelectedEvent(event: Event) {
            uiState.selectedEvent = event
    }

    override val allEvents = eventRepository.allEvents.asLiveData()

    override fun eventWithProducts(id: Long): LiveData<EventWithDays> {
        return eventRepository.eventWithProducts(id).asLiveData()
    }

    override fun insertEvent(eventToCreate: Event): Job = viewModelScope.launch {
        val eventCreatedId = eventRepository.insertEvent(eventToCreate)
        val createdEvent = Event(eventCreatedId, eventToCreate)
        updateSelectedEvent(createdEvent)
    }

    override fun insertFullProduct(eventId: Long, mealName: String, productName: String, picturePaths: List<String>): Job = viewModelScope.launch {
        eventRepository.insertFullProduct(eventId, mealName, productName, picturePaths)
    }

    override fun shareEvent(context: Context, eventWithProducts: EventWithDays): Intent {
        val zippedEvent: File = zipEvent(context, eventWithProducts)
        // TODO change the authority by a global variable
        val fileUriWithPermissionGranted = FileProvider.getUriForFile(context, "io.eflamm.notlelo.fileprovider", zippedEvent)
        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_STREAM, fileUriWithPermissionGranted)
            type = "application/zip"
        }
        return Intent.createChooser(sendIntent, null)
    }

    override fun deleteEvents(events: List<Event>): Job = viewModelScope.launch {
        eventRepository.deleteEvents(events)
    }

    private fun zipEvent(context: Context, eventWithProducts: EventWithDays): File {
        val temporaryPictureFiles = mutableListOf<File>()
        // FIXME is there a way to have a better complexity. Using a tree, recursive, coroutine ?
        val eventName = eventWithProducts.event.name
        eventWithProducts.days.forEach { dayWithMeals ->
            val dayAsString = dayWithMeals.day.date.toString()
            dayWithMeals.meals.forEach { mealWithProducts ->
                val mealName = mealWithProducts.meal.name
                mealWithProducts.products.forEach { productWithPictures ->
                    val productName = productWithPictures.product.name
                    productWithPictures.pictures.forEach { picture ->
                        // TODO get the real name instead
                        val targetPictureFileName = picture.uuid + ".jpg"
                        val temporaryPictureFile = StorageUtils.insertPictureInTemporaryFolder(
                            context,
                            eventName,
                            dayAsString,
                            mealName,
                            productName,
                            targetPictureFileName,
                            File(picture.path)
                        )
                        temporaryPictureFiles.add(temporaryPictureFile)
                    }
                }
            }
        }
        val zippedFileName = "$eventName.zip"
        return StorageUtils.zipFolder(
            context.cacheDir,
            eventName,
            zippedFileName,
            temporaryPictureFiles
        )
    }

}

class MockEventViewModel(): ViewModel(), IEventViewModel {
    override val uiState = EventUiState(null)

    override fun updateSelectedEvent(event: Event) {
        // do nothing
    }

    override val allEvents = liveData { emit(listOf( Event("Camp bleu"), Event("Camp rouge"))) }

    override fun eventWithProducts(id: Long): LiveData<EventWithDays> {
        // FIXME return some values
        return liveData {  }
    }

    override fun insertEvent(event: Event): Job = viewModelScope.launch {
        // do nothing
    }

    override fun insertFullProduct(eventId: Long, mealName: String, productName: String, picturePaths: List<String>): Job = viewModelScope.launch {
        // do nothing
    }

    override fun shareEvent(context: Context, eventWithProducts: EventWithDays): Intent {
        TODO("Not yet implemented")
        return Intent()
    }

    override fun deleteEvents(event: List<Event>): Job = viewModelScope.launch {
        TODO("Not yet implemented")
    }

}

class EventViewModelFactory(private val eventRepository: EventRepository): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(EventViewModel::class.java)) {
            // TODO should I let the selected event to null, instead of passing it in the parameters
            @Suppress("UNCHECKED_CAST")
            return EventViewModel(eventRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}