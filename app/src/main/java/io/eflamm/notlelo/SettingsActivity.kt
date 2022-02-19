package io.eflamm.notlelo

import android.content.res.ColorStateList
import android.os.Bundle
import android.widget.CheckBox
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.children
import androidx.core.widget.CompoundButtonCompat
import io.eflamm.notlelo.databinding.SettingsActivityBinding
import io.eflamm.notlelo.viewmodel.EventViewModel
import io.eflamm.notlelo.viewmodel.EventViewModelFactory

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding : SettingsActivityBinding
    private lateinit var eventsToDelete: MutableList<String>

    private val eventViewModel: EventViewModel by viewModels {
        EventViewModelFactory((application as NotleloApplication).eventRepository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = SettingsActivityBinding.inflate(layoutInflater)

        eventsToDelete = mutableListOf()

        setContentView(binding.root)
        loadEventListLayout()
        binding.deleteEventsButton.setOnClickListener {
            onClickDeleteEvents()
        }
    }

     private fun loadEventListLayout() {
         val context = this
         eventViewModel.allEvents.observe(this){ events ->
             events.forEach {
                val checkbox = CheckBox(this)

                 checkbox.setTextColor(ContextCompat.getColor(context, R.color.secondary))
                 CompoundButtonCompat.setButtonTintList(checkbox, ColorStateList.valueOf(ContextCompat.getColor(context, R.color.secondary)));
                 checkbox.text = it.name

                 checkbox.setOnCheckedChangeListener { cb, checked ->
                     when(checked) {
                         true -> eventsToDelete.add(cb.text.toString())
                         false -> eventsToDelete.remove(cb.text.toString())
                     }
                 }

                 binding.settingsEventList.addView(checkbox)
             }
         }
     }

    private fun onClickDeleteEvents() {
        eventViewModel.removeByNames(eventsToDelete).invokeOnCompletion {
            // FIXME I don't know why it is deleting only one element
            binding.settingsEventList.removeAllViews()
            Toast.makeText(this, binding.settingsEventList.children.count().toString(), Toast.LENGTH_LONG).show()
        }
    }
}