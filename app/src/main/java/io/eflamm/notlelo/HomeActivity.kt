package io.eflamm.notlelo

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import io.eflamm.notlelo.databinding.HomeActivityBinding
import io.eflamm.notlelo.model.Event
import io.eflamm.notlelo.viewmodel.EventViewModel
import io.eflamm.notlelo.viewmodel.EventViewModelFactory
import io.eflamm.notlelo.views.EventSpinnerAdapter

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: HomeActivityBinding
    private lateinit var selectedEvent: Event
    private val eventViewModel: EventViewModel by viewModels {
        EventViewModelFactory((application as NotleloApplication).eventRepository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        /** TODO check if there are at least one camp, otherwise
            display add event layout
            grey the buttons
         **/
        super.onCreate(savedInstanceState)
        binding = HomeActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fillEventListSpinner()
        binding.buttonHomeCamera.setOnClickListener { onClickCameraButton() }
        binding.buttonHomeLibrary.setOnClickListener { onClickLibraryButton() }
        binding.buttonHomeSettings.setOnClickListener { onClickSettingsButton() }
    }

    private fun  fillEventListSpinner() {
        val spinner = binding.selectHomeEvent
        val context = this

         eventViewModel.allEvents.observe(context) { events ->
            val adapter = EventSpinnerAdapter(this, R.layout.spinner_item, events)
            spinner.adapter = adapter
             spinner?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
                 override fun onNothingSelected(parent: AdapterView<*>?) {
                    // not yet implemented
                 }

                 override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                     selectedEvent = adapter.getItem(position)
                 }
             }
        }
    }

    private fun onClickCameraButton () {
        val intent = Intent(this, CameraActivity::class.java)
        val bundle = Bundle()
        bundle.putSerializable(getString(R.string.selected_event_key), selectedEvent)
        intent.putExtras(bundle)
        startActivity(intent)
    }

    private fun onClickLibraryButton () {
        val intent = Intent(this, LibraryActivity::class.java)
        val bundle = Bundle()
        bundle.putSerializable(getString(R.string.selected_event_key), selectedEvent)
        intent.putExtras(bundle)
        startActivity(intent)
    }

    private fun onClickSettingsButton () {
        val intent = Intent(this, SettingsActivity::class.java)

        startActivity(intent)
    }

    fun onClickAddEventButton(view: View) {
        this.toggleLayoutsVisibility()
    }

    fun onClickValidateAddEventButton(view: View) {
        val inputText = this.findViewById<EditText>(R.id.text_input_home_camp)
        val eventName = inputText.text.toString()
        val newEvent = Event(eventName)

        eventViewModel.insert(newEvent)

        inputText.text.clear()
        this.toggleLayoutsVisibility()
    }

    fun onClickCancelAddEventButton(view: View) {
        val inputText = this.findViewById<EditText>(R.id.text_input_home_camp)
        inputText.text.clear()
        this.toggleLayoutsVisibility()
    }

    private fun toggleLayoutsVisibility() {
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