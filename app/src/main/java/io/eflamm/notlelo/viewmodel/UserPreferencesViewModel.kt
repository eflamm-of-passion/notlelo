package io.eflamm.notlelo.viewmodel

import androidx.lifecycle.*
import io.eflamm.notlelo.repository.UserPreferences
import io.eflamm.notlelo.repository.UserPreferencesRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

interface IUserPreferencesViewModel {
    val userPreferences: LiveData<UserPreferences>
    val pictureResolution: LiveData<Int>
    fun updatePictureResolution(resolution: Int): Job
}

class UserPreferencesViewModel(private val userPreferencesRepository: UserPreferencesRepository): ViewModel(), IUserPreferencesViewModel {
    override val userPreferences = userPreferencesRepository.userPreferencesFlow.asLiveData()

    override val pictureResolution: LiveData<Int> = userPreferences.map { userPreferences ->
        when(userPreferences.pictureResolution) {
            480 -> 480
            720 -> 720
            1080 -> 1080
            else -> 480
        }
    }

    override fun updatePictureResolution(resolution: Int): Job = viewModelScope.launch {
        val processedResolution = when(resolution) {
            480 -> 480
            720 -> 720
            1080 -> 1080
            else -> 480
        }
        userPreferencesRepository.updatePictureResolution(processedResolution)
    }
}

class MockUserPreferencesViewModel(): ViewModel(), IUserPreferencesViewModel {
    override val userPreferences: LiveData<UserPreferences> = liveData { UserPreferences(480) }

    override val pictureResolution: LiveData<Int> = userPreferences.map { userPreferences ->
        when(userPreferences.pictureResolution) {
            480 -> 480
            720 -> 720
            1080 -> 1080
            else -> 480
        }
    }

    override fun updatePictureResolution(resolution: Int): Job = viewModelScope.launch {
        // do nothing
    }
}

class UserPreferencesViewModelFactory(private val userPreferencesRepository: UserPreferencesRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(UserPreferencesViewModel::class.java))
            return UserPreferencesViewModel(userPreferencesRepository) as T
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}