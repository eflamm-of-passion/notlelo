package io.eflamm.notlelo.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.*
import io.eflamm.notlelo.model.Event
import io.eflamm.notlelo.model.EventWithProducts
import io.eflamm.notlelo.model.Picture
import io.eflamm.notlelo.model.Product
import io.eflamm.notlelo.repository.EventRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch


interface IEventViewModel {
    val uiState: EventUiState
    fun updateSelectedEvent(event: Event)
    val allEvents: LiveData<List<Event>>
    fun eventWithProducts(id: Long): LiveData<EventWithProducts>
    fun insertEvent(event: Event): Job
    fun insertProductWithPictures(product: Product, pictures: List<Picture>): Job
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