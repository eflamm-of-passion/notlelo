package io.eflamm.notlelo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View

const val CAMERA_INTENT =  "io.eflamm.notlelo.CAMERA"

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun onClickCameraButton (view : View) {
        val intent = Intent(this, CameraActivity::class.java).apply {

        }
        startActivity(intent)
    }
}