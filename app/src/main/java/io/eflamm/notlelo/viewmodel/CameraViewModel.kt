package io.eflamm.notlelo.viewmodel

import kotlinx.coroutines.Job

interface ICameraViewModel {
    val cameraState: CameraState
    fun takePicture(): Job
}

data class CameraState(
    var takenPicturesPath : List<String>
)

//class CameraViewModel: ViewModel(), ICameraViewModel {
//
//}