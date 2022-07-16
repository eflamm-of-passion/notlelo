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
import io.eflamm.notlelo.model.EventWithProducts
import io.eflamm.notlelo.model.Picture
import io.eflamm.notlelo.model.Product
import io.eflamm.notlelo.repository.EventRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.io.File


interface IEventViewModel {
    val uiState: EventUiState
    fun updateSelectedEvent(event: Event)
    val allEvents: LiveData<List<Event>>
    fun eventWithProducts(id: Long): LiveData<EventWithProducts>
    fun insertEvent(event: Event): Job
    fun insertProductWithPictures(product: Product, pictures: List<Picture>): Job
    fun shareEvent(context: Context, eventWithProducts: EventWithProducts): Intent
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

    override fun eventWithProducts(id: Long): LiveData<EventWithProducts> {
        return eventRepository.eventWithProducts(id).asLiveData()
    }

    override fun insertEvent(eventToCreate: Event): Job = viewModelScope.launch {
        val eventCreatedId = eventRepository.insertEvent(eventToCreate)
        val createdEvent = Event(eventCreatedId, eventToCreate)
        updateSelectedEvent(createdEvent)
    }

    override fun insertProductWithPictures(product: Product, pictures: List<Picture>): Job = viewModelScope.launch {
        eventRepository.insertProductWithPictures(product, pictures)
    }

    override fun shareEvent(context: Context, eventWithProducts: EventWithProducts): Intent {
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

    private fun zipEvent(context: Context, eventWithProducts: EventWithProducts): File {
        val temporaryPictureFiles = mutableListOf<File>()
        val eventName = eventWithProducts.event.name
        eventWithProducts.products.forEach { productWithPictures ->
            val productName = productWithPictures.product.name
            productWithPictures.pictures.forEach { picture ->
                // FIXME give the real filename
                val targetPictureFileName = picture.uuid + ".jpg"
                val temporaryPictureFile = StorageUtils.insertPictureInTemporaryFolder(context, eventName, productName, targetPictureFileName, File(picture.path))
                temporaryPictureFiles.add(temporaryPictureFile)
            }
        }
        val zippedFileName = "$eventName.zip"
        val zippedEventTemporaryFile = StorageUtils.zipFolder(context.cacheDir, eventName, zippedFileName, temporaryPictureFiles)
        return zippedEventTemporaryFile
    }

}

class MockEventViewModel(): ViewModel(), IEventViewModel {
    override val uiState = EventUiState(null)

    override fun updateSelectedEvent(event: Event) {
        // do nothing
    }

    override val allEvents = liveData { emit(listOf( Event("Camp bleu"), Event("Camp rouge"))) }

    override fun eventWithProducts(id: Long): LiveData<EventWithProducts> {
        // FIXME return some values
        return liveData {  }
    }

    override fun insertEvent(event: Event): Job = viewModelScope.launch {
        // do nothing
    }

    override fun insertProductWithPictures(product: Product, pictures: List<Picture>): Job = viewModelScope.launch {
        // do nothing
    }

    override fun shareEvent(context: Context, eventWithProducts: EventWithProducts): Intent {
        TODO("Not yet implemented")
        return Intent()
    }

    fun removeByNames(eventNames: List<String>) = viewModelScope.launch {
        // do nothing
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