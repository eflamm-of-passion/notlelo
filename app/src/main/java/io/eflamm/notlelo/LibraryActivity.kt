package io.eflamm.notlelo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.TextView
import androidx.activity.viewModels
import io.eflamm.notlelo.databinding.LibraryActivityBinding
import io.eflamm.notlelo.model.Event
import io.eflamm.notlelo.viewmodel.EventViewModel
import io.eflamm.notlelo.viewmodel.EventViewModelFactory
import io.eflamm.notlelo.viewmodel.ProductViewModel
import io.eflamm.notlelo.viewmodel.ProductViewModelFactory

class LibraryActivity : AppCompatActivity() {
    private lateinit var binding: LibraryActivityBinding
    private lateinit var selectedEvent: Event
    private val eventViewModel: EventViewModel by viewModels {
        EventViewModelFactory((application as NotleloApplication).eventRepository)
    }
    private val productViewModel: ProductViewModel by viewModels {
        ProductViewModelFactory((application as NotleloApplication).productRepository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = LibraryActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val bundle: Bundle? = this.intent.extras
        selectedEvent = bundle?.getSerializable(getString(R.string.selected_event_key)) as Event

        fillLibrary(selectedEvent.id)
    }

    private fun fillLibrary(eventId: Long) {
        eventViewModel.eventWithProducts(eventId).observe(this){ eventWithProducts ->
            eventWithProducts.products.forEach { product ->
                val textView = TextView(this)
                textView.text = product.name
                binding.root.addView(textView)
            }
        }
    }
}