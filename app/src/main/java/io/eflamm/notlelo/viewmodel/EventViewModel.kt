package io.eflamm.notlelo.viewmodel

import androidx.lifecycle.*
import io.eflamm.notlelo.model.Event
import io.eflamm.notlelo.model.EventWithProducts
import io.eflamm.notlelo.repository.EventRepository
import kotlinx.coroutines.launch
import java.lang.IllegalArgumentException

class EventViewModel(private val repository: EventRepository): ViewModel() {
    val allEvents = repository.allEvents.asLiveData()

    fun eventWithProducts(id: Long): LiveData<EventWithProducts> {
        return repository.eventWithProducts(id).asLiveData()
    }

    fun insert(event: Event) = viewModelScope.launch {
        repository.insert(event)
    }

    fun removeByNames(eventNames: List<String>) = viewModelScope.launch {
        repository.removeByNames(eventNames)
    }
}

class EventViewModelFactory(private val repository: EventRepository): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(EventViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return EventViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}