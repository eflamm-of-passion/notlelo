package io.eflamm.notlelo

import android.os.Bundle
import android.widget.CheckBox
import android.widget.LinearLayout
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import io.eflamm.notlelo.viewmodel.EventViewModel
import io.eflamm.notlelo.viewmodel.EventViewModelFactory

class SettingsActivity : AppCompatActivity() {

    private val eventViewModel: EventViewModel by viewModels {
        EventViewModelFactory((application as NotleloApplication).eventRepository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        fillDeleteEventLayout()
    }

     private fun fillDeleteEventLayout() {
         val eventList = findViewById<LinearLayout>(R.id.settingsEventList)
         eventViewModel.allEvents.observe(this){ events ->
             events.forEach {
                val checkbox = CheckBox(this)
                 checkbox.text = it.name
                 eventList.addView(checkbox)

             }
         }
     }
}