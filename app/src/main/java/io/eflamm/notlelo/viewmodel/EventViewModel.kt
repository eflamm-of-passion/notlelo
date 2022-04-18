package io.eflamm.notlelo.viewmodel

import androidx.lifecycle.*
import io.eflamm.notlelo.model.Event
import io.eflamm.notlelo.model.EventWithProducts
import io.eflamm.notlelo.repository.EventRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

interface IEventViewModel {
    val allEvents: LiveData<List<Event>>
    fun insert(event: Event): Job
}

class EventViewModel(private val repository: EventRepository): ViewModel(), IEventViewModel {
    override val allEvents = repository.allEvents.asLiveData()

    fun eventWithProducts(id: Long): LiveData<EventWithProducts> {
        return repository.eventWithProducts(id).asLiveData()
    }

    override fun insert(event: Event): Job = viewModelScope.launch {
        repository.insert(event)
    }

    fun removeByNames(eventNames: List<String>) = viewModelScope.launch {
        repository.removeByNames(eventNames)
    }
}

class MockEventViewModel(): ViewModel(), IEventViewModel {
    override val allEvents = liveData { emit(listOf( Event("Camp bleu"), Event("Camp rouge"))) }

    fun eventWithProducts(id: Long): LiveData<EventWithProducts> {
        // FIXME return some values
        return liveData {  }
    }

    override fun insert(event: Event): Job = viewModelScope.launch {
        // do nothing
    }

    fun removeByNames(eventNames: List<String>) = viewModelScope.launch {
        // do nothing
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