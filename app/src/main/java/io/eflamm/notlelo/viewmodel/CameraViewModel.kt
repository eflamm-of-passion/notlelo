package io.eflamm.notlelo.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

interface ICameraViewModel {
    val cameraUiState: CameraUiState
    fun addPicture(path: String)
    fun removePicture(path: String)
    fun removeAllPictures()
    fun takePicture()
}

data class CameraUiState(
    var takenPicturesPath : MutableList<String>
)

class CameraViewModel: ViewModel(), ICameraViewModel {

    override var cameraUiState by mutableStateOf(CameraUiState(mutableStateListOf()))
        private set

    override fun addPicture(path: String) {
        cameraUiState.takenPicturesPath.add(path)
    }

    override fun removePicture(path: String) {
        cameraUiState.takenPicturesPath.remove(path)
    }

    override fun removeAllPictures() {
        // TODO remove them from the cache probably
        cameraUiState.takenPicturesPath.clear()
    }

    override fun takePicture() {
        TODO("Not yet implemented")
    }

}

class CameraViewModelFactory: ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return CameraViewModel() as T
    }
}