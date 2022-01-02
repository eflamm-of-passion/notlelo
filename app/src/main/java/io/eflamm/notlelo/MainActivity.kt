package io.eflamm.notlelo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout

const val CAMERA_INTENT =  "io.eflamm.notlelo.CAMERA"

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        /** TODO check if there are at least one camp, otherwise
            display add event layout
            grey the buttons
         **/
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

    }

    fun onClickCameraButton (view : View) {
        val intent = Intent(this, CameraActivity::class.java).apply {

        }
        startActivity(intent)
    }

    fun toggleLayoutsVisibility( view: View) {
        val layoutSelectEvent = this.findViewById<LinearLayout>(R.id.layout_select_camp)
        val layoutAddEvent = this.findViewById<LinearLayout>(R.id.layout_create_camp)

        layoutSelectEvent.visibility = when (View.VISIBLE == layoutSelectEvent.visibility) {
            true -> View.GONE
            false ->  View.VISIBLE
        }
        layoutAddEvent.visibility = when (View.VISIBLE == layoutAddEvent.visibility) {
            true -> View.GONE
            false ->  View.VISIBLE
        }
    }
}