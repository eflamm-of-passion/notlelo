package io.eflamm.notlelo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Spinner
import androidx.activity.viewModels
import io.eflamm.notlelo.model.Event
import io.eflamm.notlelo.viewmodel.EventViewModel
import io.eflamm.notlelo.viewmodel.EventViewModelFactory

class HomeActivity : AppCompatActivity() {

    private val eventViewModel: EventViewModel by viewModels {
        EventViewModelFactory((application as NotleloApplication).eventRepository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        /** TODO check if there are at least one camp, otherwise
            display add event layout
            grey the buttons
         **/
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        fillEventListSpinner()
    }

    private fun  fillEventListSpinner() {
        val spinner = this.findViewById<Spinner>(R.id.select_home_event)

         eventViewModel.allEvents.observe(this) { events ->
            var eventNameList: List<String> = events.map { event -> event.name }
            val adapter = ArrayAdapter<String>(this, R.layout.spinner_item, eventNameList)
            spinner.adapter = adapter
        }
    }

    fun onSelectEvent(view: View) {
        // TODO save the event in the state
    }

    fun onClickCameraButton (view : View) {
        val intent = Intent(this, CameraActivity::class.java).apply {

        }
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