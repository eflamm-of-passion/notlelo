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
import io.eflamm.notlelo.model.Product
import io.eflamm.notlelo.repository.EventRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.io.File

/*
TODO : implement a fuzzy search for adding product and give product suggestions to the user
using FTS (not straightforward solution) : https://stackoverflow.com/questions/49656009/implementing-search-with-room
 */

interface IEventViewModel {
    val uiState: EventUiState
    fun updateSelectedEventWithDays(event: EventWithDays?)
    fun updateSelectedPicture(picturePath: String?)
    val allEvents: LiveData<List<Event>>
    val allEventsWithDays: LiveData<List<EventWithDays>>
    fun eventWithProducts(id: Long): LiveData<EventWithDays>
    fun insertEvent(eventToCreate: Event): Job
    fun insertFullProduct(eventId: Long, mealName: String, productName: String, picturePaths: List<String>): Job
    fun shareEvent(context: Context, eventWithProducts: EventWithDays): Intent
    fun deleteEvents(events: List<Event>): Job
    fun deleteProduct(product: Product): Job
    fun clearCache(cacheDirectory: File): Job
    fun getProductSuggestions(inputString: String, numberOfNames: Int): LiveData<List<String>>
}

data class EventUiState(
    var selectedEventWithDays: EventWithDays?,
    var selectedPicturePath : String?
)

class EventViewModel(private val eventRepository: EventRepository ): ViewModel(), IEventViewModel {

    override var uiState by mutableStateOf(EventUiState(null, null))
        private set

    override fun updateSelectedEventWithDays(event: EventWithDays?) {
        uiState.selectedEventWithDays = event
    }
    override fun updateSelectedPicture(picturePath: String?) {
        uiState.selectedPicturePath = picturePath
    }

    override val allEvents = eventRepository.allEvents.asLiveData()
    override val allEventsWithDays = eventRepository.allEventsWithDays.asLiveData()

    override fun eventWithProducts(id: Long): LiveData<EventWithDays> {
        return eventRepository.eventWithProducts(id).asLiveData()
    }

    override fun insertEvent(eventToCreate: Event): Job = viewModelScope.launch {
        val eventCreatedId = eventRepository.insertEvent(eventToCreate)
        val createdEventWithDays = EventWithDays(Event(eventCreatedId, eventToCreate), emptyList())
        updateSelectedEventWithDays(createdEventWithDays)
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

    override fun deleteProduct(product: Product): Job = viewModelScope.launch {
        eventRepository.deleteProduct(product)
    }

    override fun clearCache(cacheDirectory: File): Job = viewModelScope.launch {
        cacheDirectory.deleteRecursively()
    }

    override fun getProductSuggestions(inputString: String, numberOfNames: Int): LiveData<List<String>> {
        return eventRepository.getProductNameOccurrence(numberOfNames).map { productList ->
            productList.filter { productName -> productName.contains(inputString) || inputString.isNullOrEmpty() }
            .take(numberOfNames)
        }.asLiveData()
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

@Suppress("UNREACHABLE_CODE")
class MockEventViewModel: ViewModel(), IEventViewModel {
    override val uiState = EventUiState(null, null)

    override fun updateSelectedEventWithDays(event: EventWithDays?) {
        TODO("Not yet implemented")
    }

    override fun updateSelectedPicture(picturePath: String?) {
        TODO("Not yet implemented")
    }

    override val allEvents = liveData { emit(listOf( Event("Camp bleu"), Event("Camp rouge"))) }
    override val allEventsWithDays: LiveData<List<EventWithDays>>
        get() = TODO("Not yet implemented")

    override fun eventWithProducts(id: Long): LiveData<EventWithDays> {
        // FIXME return some values
        return liveData {  }
    }

    override fun insertEvent(eventToCreate: Event): Job = viewModelScope.launch {
        // do nothing
    }

    override fun insertFullProduct(eventId: Long, mealName: String, productName: String, picturePaths: List<String>): Job = viewModelScope.launch {
        // do nothing
    }

    override fun shareEvent(context: Context, eventWithProducts: EventWithDays): Intent {
        TODO("Not yet implemented")
        return Intent()
    }

    override fun deleteEvents(events: List<Event>): Job = viewModelScope.launch {
        TODO("Not yet implemented")
    }

    override fun deleteProduct(product: Product): Job {
        TODO("Not yet implemented")
    }

    override fun clearCache(cacheDirectory: File): Job {
        TODO("Not yet implemented")
    }

    override fun getProductSuggestions(
        inputString: String,
        numberOfNames: Int
    ): LiveData<List<String>> {
        TODO("Not yet implemented")
    }

}

@Suppress("UNCHECKED_CAST")
class EventViewModelFactory(private val eventRepository: EventRepository): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        // TODO should I let the selected event to null, instead of passing it in the parameters
        if(modelClass.isAssignableFrom(EventViewModel::class.java))
            return EventViewModel(eventRepository) as T
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}